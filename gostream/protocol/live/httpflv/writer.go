package httpflv

import (
    "fmt"
    log "github.com/sirupsen/logrus"
    "gostream/protocol"
    "gostream/protocol/amf"
    "gostream/protocol/common"
    "gostream/protocol/container/flv"
    binaryUtil "gostream/utils/binary"
    "gostream/utils/uuid"
    "net/http"
    "time"
)

const (
    headerLen   = 11
    maxQueueNum = 10240
)

type FLVWriter struct {
    common.BaseReadWriter
    Uid         string
    app         string
    title       string
    url         string
    buf         []byte
    closed      bool
    closedChan  chan struct{}
    ctx         http.ResponseWriter
    packetQueue chan *protocol.Packet
}

func NewFLVWriter(app, title, url string, ctx http.ResponseWriter) *FLVWriter {
    writer := &FLVWriter{
        Uid:            uuid.NewId(),
        app:            app,
        title:          title,
        url:            url,
        ctx:            ctx,
        BaseReadWriter: common.NewBaseReadWriter(time.Second * 10),
        closedChan:     make(chan struct{}),
        buf:            make([]byte, headerLen),
        packetQueue:    make(chan *protocol.Packet, maxQueueNum),
    }

    _, _ = writer.ctx.Write([]byte{0x46, 0x4c, 0x56, 0x01, 0x05, 0x00, 0x00, 0x00, 0x09})
    //binary.BigEndian.PutUint32(writer.buf[:4], 0)
    binaryUtil.PutI32BE(writer.buf[:4], 0)
    _, _ = writer.ctx.Write(writer.buf[:4])
    go func() {
        err := writer.SendPacket()
        if err != nil {
            log.Error("SendPacket error: ", err)
            writer.closed = true
        }
    }()
    return writer
}

func (flvWriter *FLVWriter) SendPacket() error {
    for {
        p, ok := <-flvWriter.packetQueue
        if ok {
            flvWriter.BaseReadWriter.SetPreTime()
            h := flvWriter.buf[:headerLen]
            typeID := flv.TagVideo
            if !p.IsVideo {
                if p.IsMetadata {
                    var err error
                    typeID = flv.TagScriptDataAMF0
                    p.Data, err = amf.MetaDataReform(p.Data, amf.DEL)
                    if err != nil {
                        return err
                    }
                } else {
                    typeID = flv.TagAudio
                }
            }
            dataLen := len(p.Data)
            timestamp := p.TimeStamp
            timestamp += flvWriter.BaseTimeStamp()
            flvWriter.BaseReadWriter.RecTimeStamp(timestamp, uint32(typeID))

            preDataLen := dataLen + headerLen
            timestampBase := timestamp & 0xffffff
            timestampExt := timestamp >> 24 & 0xff

            //h[0] = uint8(typeID)
            binaryUtil.PutU8(h[0:1], uint8(typeID))
            binaryUtil.PutInt24ByBigEndian(h[1:4], int32(dataLen))
            binaryUtil.PutInt24ByBigEndian(h[4:7], int32(timestampBase))
            //h[7] = uint8(timestampExt)
            binaryUtil.PutU8(h[7:8], uint8(timestampExt))

            if _, err := flvWriter.ctx.Write(h); err != nil {
                return err
            }

            if _, err := flvWriter.ctx.Write(p.Data); err != nil {
                return err
            }

            //binary.BigEndian.PutUint32(h[:4], uint32(preDataLen))
            binaryUtil.PutI32BE(h[:4], int32(preDataLen))
            if _, err := flvWriter.ctx.Write(h[:4]); err != nil {
                return err
            }
        } else {
            return fmt.Errorf("closed")
        }

    }
}

func (flvWriter *FLVWriter) Write(packet *protocol.Packet) (err error) {
    err = nil
    if flvWriter.closed {
        err = fmt.Errorf("flvwrite source closed")
        return
    }

    defer func() {
        if e := recover(); e != nil {
            err = fmt.Errorf("FLVWriter has already been closed:%v", e)
        }
    }()

    if len(flvWriter.packetQueue) >= maxQueueNum-24 {
        flvWriter.DropPacket(flvWriter.packetQueue, flvWriter.Info())
    } else {
        flvWriter.packetQueue <- packet
    }

    return
}

func (flvWriter *FLVWriter) DropPacket(packetChan chan *protocol.Packet, info protocol.Info) {
    log.Warningf("[%v] packet chan max!!!", info)
    for i := 0; i < maxQueueNum-84; i++ {
        tmpPkt, ok := <-packetChan
        if ok && tmpPkt.IsVideo {
            videoPkt, ok := tmpPkt.Header.(protocol.VideoPacketHeader)
            // dont't drop sps config and dont't drop key frame
            if ok && (videoPkt.IsSeq() || videoPkt.IsKeyFrame()) {
                log.Debug("insert keyframe to queue")
                packetChan <- tmpPkt
            }

            if len(packetChan) > maxQueueNum-10 {
                <-packetChan
            }
            // drop other packet
            <-packetChan
        }
        // try to don't drop audio
        if ok && tmpPkt.IsAudio {
            log.Debug("insert audio to queue")
            packetChan <- tmpPkt
        }
    }
    log.Debug("packet chan len: ", len(packetChan))
}

func (flvWriter *FLVWriter) Close(error) {
    log.Debug("http flv closed")
    if !flvWriter.closed {
        close(flvWriter.packetQueue)
        close(flvWriter.closedChan)
    }
    flvWriter.closed = true
}

func (flvWriter *FLVWriter) Wait() {
    select {
    case <-flvWriter.closedChan:
        return
    }
}

func (flvWriter *FLVWriter) Info() (info protocol.Info) {
    info.UID = flvWriter.Uid
    info.URL = flvWriter.url
    info.Key = flvWriter.app + "/" + flvWriter.title
    return
}
