package rtmp

import (
    "fmt"
    log "github.com/sirupsen/logrus"
    "gostream/config"
    "gostream/protocol"
    "gostream/protocol/common"
    "gostream/protocol/container/flv"
    "gostream/utils/uuid"
    "net/url"
    "reflect"
    "strings"
    "time"
)

const maxQueueNum = 1024

type Writer struct {
    Uid    string
    closed bool
    common.BaseReadWriter
    conn        StreamReadWriteCloser
    packetQueue chan *protocol.Packet
}

func NewWriter(conn StreamReadWriteCloser) *Writer {
    writer := &Writer{
        Uid:            uuid.NewId(),
        conn:           conn,
        BaseReadWriter: common.NewBaseReadWriter(time.Second * time.Duration(config.GetConfig().WriteTimeout)),
        packetQueue:    make(chan *protocol.Packet, maxQueueNum),
    }

    go writer.Check()
    go func() {
        err := writer.SendPacket()
        if err != nil {
            log.Warning(err)
        }
    }()
    return writer
}

func (writer *Writer) Write(packet *protocol.Packet) (err error) {
    err = nil

    if writer.closed {
        err = fmt.Errorf("writer closed")
        return
    }
    defer func() {
        if e := recover(); e != nil {
            err = fmt.Errorf("writer has already been closed:%v", e)
        }
    }()
    if len(writer.packetQueue) >= maxQueueNum-24 {
        writer.DropPacket(writer.packetQueue, writer.Info())
    } else {
        writer.packetQueue <- packet
    }
    return
}

func (writer *Writer) DropPacket(pktQue chan *protocol.Packet, info protocol.Info) {
    log.Warningf("[%v] packet queue max!!!", info)
    for i := 0; i < maxQueueNum-84; i++ {
        tmpPkt, ok := <-pktQue
        // try to don't drop audio
        if ok && tmpPkt.IsAudio {
            if len(pktQue) > maxQueueNum-2 {
                log.Debug("drop audio pkt")
                <-pktQue
            } else {
                pktQue <- tmpPkt
            }

        }

        if ok && tmpPkt.IsVideo {
            videoPkt, ok := tmpPkt.Header.(protocol.VideoPacketHeader)
            // dont't drop sps config and dont't drop key frame
            if ok && (videoPkt.IsSeq() || videoPkt.IsKeyFrame()) {
                pktQue <- tmpPkt
            }
            if len(pktQue) > maxQueueNum-10 {
                log.Debug("drop video pkt")
                <-pktQue
            }
        }

    }
    log.Debug("packet queue len: ", len(pktQue))
}

func (writer *Writer) SendPacket() error {
    Flush := reflect.ValueOf(writer.conn).MethodByName("Flush")
    var cs ChunkStream
    for {
        p, ok := <-writer.packetQueue
        if ok {
            cs.Data = p.Data
            cs.Length = uint32(len(p.Data))
            cs.StreamID = p.StreamID
            cs.Timestamp = p.TimeStamp
            cs.Timestamp += writer.BaseTimeStamp()

            if p.IsVideo {
                cs.TypeID = flv.TagVideo
            } else {
                if p.IsMetadata {
                    cs.TypeID = flv.TagScriptDataAMF0
                } else {
                    cs.TypeID = flv.TagAudio
                }
            }

            writer.SetPreTime()
            writer.RecTimeStamp(cs.Timestamp, cs.TypeID)
            err := writer.conn.Write(cs)
            if err != nil {
                writer.closed = true
                return err
            }
            Flush.Call(nil)
        } else {
            return fmt.Errorf("closed")
        }

    }
}

func (writer *Writer) Check() {
    var c ChunkStream
    for {
        if err := writer.conn.Read(&c); err != nil {
            writer.Close(err)
            return
        }
    }
}

func (writer *Writer) Info() (ret protocol.Info) {
    ret.UID = writer.Uid
    _, _, URL := writer.conn.GetInfo()
    ret.URL = URL
    _url, err := url.Parse(URL)
    if err != nil {
        log.Warning(err)
    }
    ret.Key = strings.TrimLeft(_url.Path, "/")
    return
}

func (writer *Writer) Close(err error) {
    log.Warning("player ", writer.Info(), "closed: "+err.Error())
    if !writer.closed {
        close(writer.packetQueue)
    }
    writer.closed = true
    writer.conn.Close(err)
}
