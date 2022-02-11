package rtmp

import (
    "fmt"
    log "github.com/sirupsen/logrus"
    "gostream/protocol"
    "gostream/protocol/container/flv"
)

type StaticPush struct {
    RtmpUrl    string
    packetChan chan *protocol.Packet
    ctrlChan   chan string
    connClient *ConnClient
    isRunning  bool
}

func NewStaticPush(rtmpUrl string) *StaticPush {
    return &StaticPush{
        RtmpUrl:    rtmpUrl,
        packetChan: make(chan *protocol.Packet, 500),
        ctrlChan:   make(chan string),
        connClient: nil,
        isRunning:  false,
    }
}

func (staticPush *StaticPush) Start() error {
    if staticPush.isRunning {
        return fmt.Errorf("StaticPush already start %s", staticPush.RtmpUrl)
    }

    staticPush.connClient = NewClient()

    log.Debugf("static publish server addr:%v starting....", staticPush.RtmpUrl)
    err := staticPush.connClient.Start(staticPush.RtmpUrl, "publish")
    if err != nil {
        log.Debugf("connectClient.Start url=%v error", staticPush.RtmpUrl)
        return err
    }
    log.Debugf("static publish server addr:%v started, streamid=%d", staticPush.RtmpUrl, staticPush.connClient.streamId)
    go staticPush.HandlePacket()

    staticPush.isRunning = true
    return nil
}

func (staticPush *StaticPush) Stop() {
    if !staticPush.isRunning {
        return
    }

    log.Debugf("StaticPush Stop: %s", staticPush.RtmpUrl)
    staticPush.ctrlChan <- staticCtrlCmdStop
    staticPush.isRunning = false
}

func (staticPush *StaticPush) HandlePacket() {
    if !staticPush.isRunning {
        log.Debugf("static push %s not started", staticPush.RtmpUrl)
        return
    }

    for {
        select {
        case packet := <-staticPush.packetChan:
            staticPush.sendPacket(packet)
        case ctrlCmd := <-staticPush.ctrlChan:
            if ctrlCmd == staticCtrlCmdStop {
                _ = staticPush.connClient.conn.Close()
                log.Debugf("Static HandlePacket close: publishUrl=%s", staticPush.RtmpUrl)
                break
            }
        }
    }
}

func (staticPush *StaticPush) WritePacket(packet *protocol.Packet) {
    if !staticPush.isRunning {
        return
    }
    staticPush.packetChan <- packet
}

func (staticPush *StaticPush) sendPacket(packet *protocol.Packet) {
    if !staticPush.isRunning {
        return
    }
    var cs ChunkStream

    cs.Data = packet.Data
    cs.Length = uint32(len(packet.Data))
    cs.StreamID = staticPush.connClient.streamId
    cs.Timestamp = packet.TimeStamp
    //cs.Timestamp += v.BaseTimeStamp()

    //log.Printf("Static sendPacket: rtmpurl=%s, length=%d, streamid=%d",
    //	staticPush.RtmpUrl, len(packet.Data), cs.StreamID)
    if packet.IsVideo {
        cs.TypeID = flv.TagVideo
    } else {
        if packet.IsMetadata {
            cs.TypeID = flv.TagScriptDataAMF0
        } else {
            cs.TypeID = flv.TagAudio
        }
    }

    _ = staticPush.connClient.Write(cs)
}
