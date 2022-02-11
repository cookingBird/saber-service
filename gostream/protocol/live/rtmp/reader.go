package rtmp

import (
    log "github.com/sirupsen/logrus"
    "gostream/config"
    "gostream/protocol"
    "gostream/protocol/common"
    "gostream/protocol/container/flv"
    "gostream/utils/uuid"
    "net/url"
    "strings"
    "time"
)

type Reader struct {
    Uid string
    common.BaseReadWriter
    conn      StreamReadWriteCloser
    flvReader *flv.Reader
    //ReadBWInfo StaticsBW
}

func (reader *Reader) Read(packet *protocol.Packet) (err error) {
    defer func() {
        if r := recover(); r != nil {
            log.Warning("rtmp read packet panic: ", r)
        }
    }()

    reader.SetPreTime()
    var cs ChunkStream
    for {
        err = reader.conn.Read(&cs)
        if err != nil {
            return err
        }
        if cs.TypeID == flv.TagAudio ||
            cs.TypeID == flv.TagVideo ||
            cs.TypeID == flv.TagScriptDataAMF0 ||
            cs.TypeID == flv.TagScriptDataAMF3 {
            break
        }
    }

    packet.IsAudio = cs.TypeID == flv.TagAudio
    packet.IsVideo = cs.TypeID == flv.TagVideo
    packet.IsMetadata = cs.TypeID == flv.TagScriptDataAMF0 || cs.TypeID == flv.TagScriptDataAMF3
    packet.StreamID = cs.StreamID
    packet.Data = cs.Data
    packet.TimeStamp = cs.Timestamp

    //reader.SaveStatics(packet.StreamID, uint64(len(packet.Data)), packet.IsVideo)
    _ = reader.flvReader.ReadHeader(packet)
    return err
}

func NewReader(conn StreamReadWriteCloser) *Reader {
    return &Reader{
        Uid:            uuid.NewId(),
        conn:           conn,
        BaseReadWriter: common.NewBaseReadWriter(time.Second * time.Duration(config.GetConfig().ReadTimeout)),
        flvReader:      flv.NewReader(),
        //ReadBWInfo: StaticsBW{0, 0, 0, 0, 0, 0, 0, 0},
    }
}

func (reader *Reader) Info() (ret protocol.Info) {
    ret.UID = reader.Uid
    _, _, URL := reader.conn.GetInfo()
    ret.URL = URL
    _url, err := url.Parse(URL)
    if err != nil {
        log.Warning(err)
    }
    ret.Key = strings.TrimLeft(_url.Path, "/")
    return
}

func (reader *Reader) Close(err error) {
    log.Debug("publisher ", reader.Info(), "closed: "+err.Error())
    reader.conn.Close(err)
}
