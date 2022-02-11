package flv

import (
    "encoding/base64"
    "encoding/binary"
    "fmt"
    log "github.com/sirupsen/logrus"
    "gostream/api"
    "gostream/config"
    "gostream/data/hbase"
    "gostream/data/minio"
    "gostream/protocol"
    "gostream/protocol/amf"
    "gostream/protocol/common"
    binaryUtil "gostream/utils/binary"
    "gostream/utils/ffmpeg"
    "gostream/utils/uuid"
    "math/rand"
    "os"
    "path"
    "strconv"
    "strings"
    "time"
)

const headerLen = 11

var flvHeader = []byte{0x46, 0x4c, 0x56, 0x01, 0x05, 0x00, 0x00, 0x00, 0x09}

type videoCache struct {
    RowKey    []byte
    RowKeyStr string
}

type Writer struct {
    Uid string
    common.BaseReadWriter
    app, title, url string
    buf             []byte
    closed          chan struct{}
    ctx             *os.File
    createTime      time.Time
    fileName        string
    Video           *videoCache
    appName         string
    key             string
    uavInfo         *api.UavDevInfo
}

func NewWriter(info protocol.Info, key string, app string, uavInfo *api.UavDevInfo) *Writer {
    nowTime := time.Now()
    paths := strings.SplitN(info.Key, "/", 2)
    if len(paths) != 2 {
        log.Warning("invalid info")
        return nil
    }

    flvDir := config.GetConfig().FileDir

    err := os.MkdirAll(path.Join(flvDir, paths[0]), 0755)
    if err != nil {
        log.Error("mkdir error: ", err)
        return nil
    }

    fileName := fmt.Sprintf("%s_%d.%s", path.Join(flvDir, info.Key), nowTime.Unix(), "flv")
    log.Debug("flv save stream to: ", fileName)
    w, err := os.OpenFile(fileName, os.O_CREATE|os.O_RDWR, os.ModePerm)
    if err != nil {
        log.Error("open file error: ", err)
        return nil
    }
    var vc *videoCache
    if app == protocol.AppNameUav {
        rowKey := make([]byte, 8)
        binary.BigEndian.PutUint32(rowKey[:4], uint32(nowTime.Unix()))
        randInt := rand.Int31()
        binary.BigEndian.PutUint32(rowKey[4:], uint32(randInt))
        rowKeyString := strconv.Itoa(int(nowTime.Unix())) + strconv.Itoa(int(randInt))
        vc = &videoCache{RowKey: rowKey, RowKeyStr: rowKeyString}
    }

    writer := &Writer{
        Uid:            uuid.NewId(),
        app:            paths[0],
        title:          paths[1],
        url:            info.URL,
        ctx:            w,
        BaseReadWriter: common.NewBaseReadWriter(time.Second * 10),
        closed:         make(chan struct{}),
        buf:            make([]byte, headerLen),
        createTime:     nowTime,
        fileName:       fileName,
        Video:          vc,
        appName:        app,
        key:            key,
        uavInfo:        uavInfo,
    }

    _, _ = writer.ctx.Write(flvHeader)
    //binary.BigEndian.PutUint32(writer.buf[:4], 0)
    binaryUtil.PutI32BE(writer.buf[:4], 0)
    _, _ = writer.ctx.Write(writer.buf[:4])

    log.Debug("new flv writer: ", writer.Info())
    return writer
}

func (writer *Writer) Write(packet *protocol.Packet) error {
    writer.BaseReadWriter.SetPreTime()
    h := writer.buf[:headerLen]
    typeID := TagVideo
    if !packet.IsVideo {
        if packet.IsMetadata {
            var err error
            typeID = TagScriptDataAMF0
            packet.Data, err = amf.MetaDataReform(packet.Data, amf.DEL)
            if err != nil {
                return err
            }
        } else {
            typeID = TagAudio
        }
    }
    dataLen := len(packet.Data)
    timestamp := packet.TimeStamp
    timestamp += writer.BaseTimeStamp()
    writer.BaseReadWriter.RecTimeStamp(timestamp, uint32(typeID))

    preDataLen := dataLen + headerLen
    timestampBase := timestamp & 0xffffff
    timestampExt := timestamp >> 24 & 0xff

    //h[0] = uint8(typeID)
    binaryUtil.PutU8(h[0:1], uint8(typeID))
    binaryUtil.PutInt24ByBigEndian(h[1:4], int32(dataLen))
    binaryUtil.PutInt24ByBigEndian(h[4:7], int32(timestampBase))
    //h[7] = uint8(timestampExt)
    binaryUtil.PutU8(h[7:8], uint8(timestampExt))

    if _, err := writer.ctx.Write(h); err != nil {
        return err
    }

    if _, err := writer.ctx.Write(packet.Data); err != nil {
        return err
    }

    binary.BigEndian.PutUint32(h[:4], uint32(preDataLen))
    if _, err := writer.ctx.Write(h[:4]); err != nil {
        return err
    }

    return nil
}

func (writer *Writer) Close(error) {
    _ = writer.ctx.Close()
    close(writer.closed)
    if writer.appName == protocol.AppNameUav {
        go api.SendLiveNotify(writer.key, writer.url, "0")
        go writer.saveUavVideo()
    } else if writer.appName == protocol.AppNameAi {
        go writer.saveAiVideo()
    }
}

func (writer *Writer) Info() (info protocol.Info) {
    info.UID = writer.Uid
    info.URL = writer.url
    info.Key = writer.app + "/" + writer.title
    return
}

func (writer *Writer) Wait() {
    select {
    case <-writer.closed:
        return
    }
}

func (writer *Writer) saveUavVideo() {
    uavCode := writer.key
    dateStr := writer.createTime.Format("20060102")
    bucketName := "u" + strconv.FormatInt(writer.uavInfo.UavId, 10)
    info, err := ffmpeg.GetInfo(writer.fileName)
    if err != nil {
        log.Errorf("获取视频文件信息错误,fileName:%s, err:%v", writer.fileName, err)
    }
    log.Info("开始转码 ", writer.fileName)
    file1080p, file720p, file480p, err := ffmpeg.ConvertResolutionFile(writer.fileName)
    if err != nil {
        log.Errorf("转码失败,fileName:%s, err:%v", writer.fileName, err)
        return
    } else {
        log.Info("转码结束 ", writer.fileName)
    }

    coverFile, err := ffmpeg.Screenshot(writer.fileName)
    if err != nil {
        log.Errorf("获取封面失败,fileName:%s, err:%v", writer.fileName, err)
        return
    } else {
        log.Info("获取封面成功 ", coverFile)
    }

    err = os.Remove(writer.fileName)
    if err != nil {
        log.Errorf("del file:%s, err:%v", writer.fileName, err)
    }
    
    objectName1080p := dateStr + "/" + writer.Video.RowKeyStr + "-1080p.mp4"
    writer.saveMinIO(bucketName, objectName1080p, file1080p, true)

    objectName720p := dateStr + "/" + writer.Video.RowKeyStr + "-720p.mp4"
    writer.saveMinIO(bucketName, objectName720p, file720p, true)

    objectName480p := dateStr + "/" + writer.Video.RowKeyStr + "-480p.mp4"
    writer.saveMinIO(bucketName, objectName480p, file480p, true)

    coverObjectName := dateStr + "/" + writer.Video.RowKeyStr + ".jpg"
    writer.saveMinIO(bucketName, coverObjectName, coverFile, true)

    uavInfo, err := api.GetUavInfo(writer.key)
    if err != nil {
        log.Errorf("查询不到无人机, uavDevCode:%s, err:%v", writer.key, err)
    }

    uavId := ""
    eventId := ""
    userId := ""
    var taskId int64 = 0
    if uavInfo != nil {
        if uavInfo.UavId > 0 {
            uavId = strconv.FormatInt(uavInfo.UavId, 10)
        }
        if uavInfo.EventId > 0 {
            eventId = strconv.FormatInt(uavInfo.EventId, 10)
        }
        if uavInfo.UserId > 0 {
            userId = strconv.FormatInt(uavInfo.UserId, 10)
        }
        if uavInfo.TaskId > 0 {
            taskId = uavInfo.TaskId
        }
    }
    value := map[string]map[string][]byte{
        "info": {
            "eventId":             []byte(eventId),
            "uavId":               []byte(uavId),
            "uavCode":             []byte(uavCode),
            "source":              []byte("0"),
            "userId":              []byte(userId),
            "startTime":           []byte(writer.createTime.Format("2006-01-02 15:04:05")),
            "endTime":             []byte(writer.createTime.Add(time.Second * time.Duration(info.Duration)).Format("2006-01-02 15:04:05")),
            "duration":            []byte(strconv.Itoa(info.Duration)),
            "size":                []byte(strconv.FormatInt(info.Size, 10)),
            "videoCodec":          []byte(info.VideoCodec),
            "fps":                 []byte(info.Fps),
            "audioCodec":          []byte(info.AudioCodec),
            "samplerate":          []byte(info.Samplerate),
            "format":              []byte("1"),
            "bitrate":             []byte(info.Bitrate),
            "bucketName480p":      []byte(bucketName),
            "objectName480p":      []byte(objectName480p),
            "bucketName720p":      []byte(bucketName),
            "objectName720p":      []byte(objectName720p),
            "bucketName1080p":     []byte(bucketName),
            "objectName1080p":     []byte(objectName1080p),
            "coverFileBucketName": []byte(bucketName),
            "coverFileObjectName": []byte(coverObjectName),
        },
    }
    err = hbase.Put(hbase.VideoTable, writer.Video.RowKey, value)
    if err != nil {
        log.Errorf("save to HBase fail, rowKey:%s, err:%v", writer.Video.RowKey, err)
        return
    }
    log.Info("save to HBase success, RowKeyStr:", writer.Video.RowKey)
    if taskId == 0 {
        return
    }
    videoTaskValue := map[string]map[string][]byte{
        "info": {
            "eventId": []byte(eventId),
            "uavId":   []byte(uavId),
            "uavCode": []byte(uavCode),
            "videoId": []byte(base64.StdEncoding.EncodeToString(writer.Video.RowKey)),
        },
    }
    taskRowKey := make([]byte, 16)
    copy(taskRowKey[8:], writer.Video.RowKey)
    binary.BigEndian.PutUint64(taskRowKey[:8], uint64(taskId))
    err = hbase.Put(hbase.VideoTaskTable, taskRowKey, videoTaskValue)
    if err != nil {
        log.Errorf("update HBase video-task fail, rowKey:%s, err:%v", taskRowKey, err)
        return
    }
    log.Info("update HBase video-task success, RowKeyStr:", taskRowKey)
}

func (writer *Writer) saveAiVideo() {
    bucketName := "u" + strconv.FormatInt(writer.uavInfo.UavId, 10)
    log.Info("开始转码", writer.fileName)
    file1080p, file720p, file480p, err := ffmpeg.ConvertResolutionFile(writer.fileName)
    if err != nil {
        log.Errorf("转码失败,fileName:%s, err:%v", writer.fileName, err)
        return
    } else {
        log.Info("转码结束 ", writer.fileName)
    }
    // writer.key = "rtmp#" + name + "#" + flvWriter.Video.RowKeyStr
    arr := strings.Split(writer.key, "#")
    if len(arr) != 3 {
        log.Error("非法AI任务ID", writer.key)
        return
    }
    rowKeyStr := arr[2]
    rowKey := make([]byte, 8)
    timeVal, _ := strconv.ParseUint(rowKeyStr[:10], 10, 32)
    dateStr := time.Unix(int64(timeVal), 0).Format("20060102")
    binary.BigEndian.PutUint32(rowKey[:4], uint32(timeVal))
    randVal, _ := strconv.Atoi(rowKeyStr[10:])
    binary.BigEndian.PutUint32(rowKey[4:], uint32(randVal))

    objectName1080p := dateStr + "/" + rowKeyStr + "-ai-1080p.mp4"
    writer.saveMinIO(bucketName, objectName1080p, file1080p, false)

    objectName720p := dateStr + "/" + rowKeyStr + "-ai-720p.mp4"
    writer.saveMinIO(bucketName, objectName720p, file720p, false)

    objectName480p := dateStr + "/" + rowKeyStr + "-ai-480p.mp4"
    writer.saveMinIO(bucketName, objectName480p, file480p, false)

    writer.saveAiHBase(bucketName, objectName480p, objectName720p, objectName1080p, rowKey)
}

func (writer *Writer) saveMinIO(bucketName string, objectName string, fileName string, delSrc bool) {
    err := minio.FPutObject(bucketName, objectName, fileName)
    if err != nil {
        log.Errorf("save to MinIO fail, fileName:%s, bucketName:%s, objectName:%s, err:%v", fileName, bucketName, objectName, err)
    } else {
        log.Infof("save to MinIO success, fileName:%s, bucketName:%s, objectName:%s", fileName, bucketName, objectName)
    }
    if delSrc {
        // 删除本地文件
        err = os.Remove(fileName)
        if err != nil {
            log.Errorf("del file:%s, err:%v", fileName, err)
        }
    }
}

func (writer *Writer) saveAiHBase(bucketName string, objectName480p string, objectName720p string, objectName1080p string, rowKey []byte) {
    videoValue := map[string]map[string][]byte{
        "info": {
            "aiBucketName480p":  []byte(bucketName),
            "aiObjectName480p":  []byte(objectName480p),
            "aiBucketName720p":  []byte(bucketName),
            "aiObjectName720p":  []byte(objectName720p),
            "aiBucketName1080p": []byte(bucketName),
            "aiObjectName1080p": []byte(objectName1080p),
        },
    }
    err := hbase.Put(hbase.VideoTable, rowKey, videoValue)
    if err != nil {
        log.Errorf("update HBase video fail, rowKey:%s, err:%v", rowKey, err)
        return
    }
    log.Info("update HBase video success, RowKeyStr:", rowKey)
}
