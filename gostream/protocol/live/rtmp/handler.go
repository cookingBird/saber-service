package rtmp

import (
    "fmt"
    cmap "github.com/orcaman/concurrent-map"
    log "github.com/sirupsen/logrus"
    "gostream/config"
    "gostream/protocol"
    "gostream/protocol/live/rtmp/cache"
    "strings"
    "time"
)

type StreamHandler struct {
    streams cmap.ConcurrentMap
}

func NewStreamHandler() *StreamHandler {
    ret := &StreamHandler{
        streams: cmap.New(),
    }
    go ret.CheckAlive()
    return ret
}

func (streamHandler *StreamHandler) HandleReader(reader protocol.ReadCloser) {
    info := reader.Info()
    log.Debugf("HandleReader: info[%v]", info)

    var stream *Stream
    val, ok := streamHandler.streams.Get(info.Key)
    if stream, ok = val.(*Stream); ok {
        stream.Stop() // TODO stop channel，阻塞等待stop完成
        id := stream.reader.Info().UID
        if id != "" && id != info.UID {
            newStream := NewStream()
            stream.Copy(newStream)
            newStream.info = info
            stream = newStream
            streamHandler.streams.Set(info.Key, newStream)
        }
    } else {
        stream = NewStream()
        streamHandler.streams.Set(info.Key, stream)
        stream.info = info
    }

    stream.reader = reader
}

func (streamHandler *StreamHandler) StartTransmit(key string) {
    val, _ := streamHandler.streams.Get(key)
    go val.(*Stream).Transmit()
}

func (streamHandler *StreamHandler) HandleWriter(w protocol.WriteCloser) {
    info := w.Info()
    log.Debugf("HandleWriter: info[%v]", info)

    var stream *Stream
    ok := streamHandler.streams.Has(info.Key)
    if !ok {
        stream = NewStream()
        streamHandler.streams.Set(info.Key, stream)
        stream.info = info
    } else {
        item, ok := streamHandler.streams.Get(info.Key)
        if ok {
            stream = item.(*Stream)
            stream.writerMap.Set(w.Info().UID, &WriteCloser{w: w})
        }
    }
}

func (streamHandler *StreamHandler) GetStreams() cmap.ConcurrentMap {
    return streamHandler.streams
}

func (streamHandler *StreamHandler) CheckAlive() {
    for {
        <-time.After(5 * time.Second)
        for item := range streamHandler.streams.IterBuffered() {
            v := item.Val.(*Stream)
            if v.CheckAlive() == 0 {
                streamHandler.streams.Remove(item.Key)
            }
        }
    }
}

type Stream struct {
    isStart        bool
    cache          *cache.Cache
    reader         protocol.ReadCloser
    writerMap      cmap.ConcurrentMap
    info           protocol.Info
    staticPushList []*StaticPush
}

func NewStream() *Stream {
    return &Stream{
        cache:     cache.NewCache(),
        writerMap: cmap.New(),
    }
}

func (stream *Stream) GetReader() protocol.ReadCloser {
    return stream.reader
}

func (stream *Stream) CheckAlive() (n int) {
    if stream.reader != nil && stream.isStart {
        if stream.reader.Alive() {
            n++
        } else {
            stream.reader.Close(fmt.Errorf("read timeout"))
        }
    }
    for item := range stream.writerMap.IterBuffered() {
        v := item.Val.(*WriteCloser)
        if v.w != nil {
            if !v.w.Alive() && stream.isStart {
                stream.writerMap.Remove(item.Key)
                v.w.Close(fmt.Errorf("write timeout"))
                continue
            }
            n++
        }

    }
    return
}

func (stream *Stream) Copy(dst *Stream) {
    for item := range stream.writerMap.IterBuffered() {
        v := item.Val.(*WriteCloser)
        stream.writerMap.Remove(item.Key)
        v.w.CalcBaseTimestamp()
        dst.writerMap.Set(v.w.Info().UID, &WriteCloser{w: v.w})

    }
}

func (stream *Stream) Transmit() {
    defer func() {
        if r := recover(); r != nil {
            log.Error("rtmp handler Transmit panic: ", r)
        }
    }()
    stream.isStart = true
    var p protocol.Packet

    log.Debugf("TransStart: %v", stream.info)

    stream.StartStaticPush()

    for {
        if !stream.isStart {
            stream.close()
            return
        }
        err := stream.reader.Read(&p)
        if err != nil {
            stream.close()
            stream.isStart = false
            return
        }

        stream.SendStaticPush(p)

        stream.cache.Save(p)

        for item := range stream.writerMap.IterBuffered() {
            v := item.Val.(*WriteCloser)
            if !v.sendHeader {
                //log.Debugf("cache.send: %v", v.w.Info())
                if err = stream.cache.Send(v.w); err != nil {
                    log.Debugf("[%s] send cache packet error: %v, remove", v.w.Info(), err)
                    stream.writerMap.Remove(item.Key)
                    v.w.Close(fmt.Errorf("send err"))
                    continue
                }
                v.sendHeader = true
            } else {
                newPacket := p
                //log.Debugf("w.Write: type=%v, %v", writeType, v.w.Info())
                if err = v.w.Write(&newPacket); err != nil {
                    log.Debugf("[%s] write packet error: %v, remove", v.w.Info(), err)
                    stream.writerMap.Remove(item.Key)
                    v.w.Close(fmt.Errorf("send err"))
                }
            }
        }
    }
}

func (stream *Stream) Stop() {
    log.Debugf("TransStop: %s", stream.info.Key)

    if stream.isStart && stream.reader != nil {
        stream.reader.Close(fmt.Errorf("stop old"))
    }

    stream.isStart = false
}

func GetStaticPushUrls(appName string) ([]string, error) {
    for _, app := range config.GetConfig().Apps {
        if app.Name == appName {
            if len(app.PushUrls) > 0 {
                return app.PushUrls, nil
            } else {
                return nil, fmt.Errorf("no static push url")
            }
        }
    }
    return nil, fmt.Errorf("no static push url")
}

func (stream *Stream) StartStaticPush() {
    key := stream.info.Key
    keyArr := strings.SplitN(key, "/", 2)
    if len(keyArr) < 2 {
        return
    }
    appName := keyArr[0]
    steamName := keyArr[1]
    pushUrls, err := GetStaticPushUrls(appName)
    if err != nil {
        return
    }
    stream.staticPushList = make([]*StaticPush, 0)
    for _, pushUrl := range pushUrls {
        staticPush := NewStaticPush(pushUrl + "/" + steamName)
        stream.staticPushList = append(stream.staticPushList, staticPush)
        err := staticPush.Start()
        if err != nil {
            log.Debugf("StartStaticPush:%s error=%v", staticPush.RtmpUrl, err)
        } else {
            log.Debugf("StartStaticPush:%s ok", staticPush.RtmpUrl)
        }
    }
}

func (stream *Stream) StopStaticPush() {
    if stream.staticPushList == nil || len(stream.staticPushList) == 0 {
        return
    }
    for _, staticPush := range stream.staticPushList {
        staticPush.Stop()
    }
}

func (stream *Stream) SendStaticPush(packet protocol.Packet) {
    if stream.staticPushList == nil || len(stream.staticPushList) == 0 {
        return
    }
    for _, staticPush := range stream.staticPushList {
        staticPush.WritePacket(&packet)
    }
}

func (stream *Stream) close() {
    if stream.reader != nil {
        stream.StopStaticPush()
        log.Debugf("[%v] publisher closed", stream.reader.Info())
    }

    for item := range stream.writerMap.IterBuffered() {
        v := item.Val.(*WriteCloser)
        if v.w != nil {
            v.w.Close(fmt.Errorf("closed"))
            stream.writerMap.Remove(item.Key)
            log.Debugf("[%v] player closed and remove\n", v.w.Info())
        }
    }
}

type WriteCloser struct {
    sendHeader bool
    w          protocol.WriteCloser
}

func (p *WriteCloser) GetWriter() protocol.WriteCloser {
    return p.w
}
