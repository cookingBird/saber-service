package rtmp

import (
    "encoding/binary"
    "fmt"
    log "github.com/sirupsen/logrus"
    "gostream/protocol/container/flv"
    binaryUtil "gostream/utils/binary"
    "gostream/utils/pool"
)

type ChunkStream struct {
    Format    uint32
    CSID      uint32
    Timestamp uint32
    Length    uint32
    TypeID    uint32
    StreamID  uint32
    timeDelta uint32
    extended  bool
    index     uint32
    remain    uint32
    got       bool
    tmpFormat uint32
    Data      []byte
}

func (chunkStream *ChunkStream) new(pool *pool.Pool) {
    chunkStream.got = false
    chunkStream.index = 0
    chunkStream.remain = chunkStream.Length
    chunkStream.Data = pool.Get(int(chunkStream.Length))
}

func initControlMsg(id, size, value uint32) ChunkStream {
    ret := ChunkStream{
        Format:   0,
        CSID:     2,
        TypeID:   id,
        StreamID: 0,
        Length:   size,
        Data:     make([]byte, size),
    }
    //binary.BigEndian.PutUint32(ret.Data[:size], value)
    binaryUtil.PutU32BE(ret.Data[:size], value)
    return ret
}

func (conn *Conn) NewAck(size uint32) ChunkStream {
    return buildControlMsg(idAck, 4, size)
}

func (conn *Conn) NewWindowAckSize(size uint32) ChunkStream {
    return initControlMsg(idWindowAckSize, 4, size)
}

func (conn *Conn) NewSetPeerBandwidth(size uint32) ChunkStream {
    ret := initControlMsg(idSetPeerBandwidth, 5, size)
    ret.Data[4] = 2
    return ret
}

func (conn *Conn) NewSetChunkSize(size uint32) ChunkStream {
    return initControlMsg(idSetChunkSize, 4, size)
}

func buildControlMsg(id, size, value uint32) ChunkStream {
    ret := ChunkStream{
        Format:   0,
        CSID:     2,
        TypeID:   id,
        StreamID: 0,
        Length:   size,
        Data:     make([]byte, size),
    }
    binary.BigEndian.PutUint32(ret.Data[:size], value)
    return ret
}

func (chunkStream *ChunkStream) readChunk(r *ReadWriter, chunkSize uint32, pool *pool.Pool) error {
    if chunkStream.remain != 0 && chunkStream.tmpFormat != 3 {
        return fmt.Errorf("inlaid remin = %d", chunkStream.remain)
    }
    switch chunkStream.CSID {
    case 0:
        id, _ := r.ReadUintLE(1)
        chunkStream.CSID = id + 64
    case 1:
        id, _ := r.ReadUintLE(2)
        chunkStream.CSID = id + 64
    }

    switch chunkStream.tmpFormat {
    case 0:
        chunkStream.Format = chunkStream.tmpFormat
        chunkStream.Timestamp, _ = r.ReadUintBE(3)
        chunkStream.Length, _ = r.ReadUintBE(3)
        chunkStream.TypeID, _ = r.ReadUintBE(1)
        chunkStream.StreamID, _ = r.ReadUintLE(4)
        if chunkStream.Timestamp == 0xffffff {
            chunkStream.Timestamp, _ = r.ReadUintBE(4)
            chunkStream.extended = true
        } else {
            chunkStream.extended = false
        }
        chunkStream.new(pool)
    case 1:
        chunkStream.Format = chunkStream.tmpFormat
        timeStamp, _ := r.ReadUintBE(3)
        chunkStream.Length, _ = r.ReadUintBE(3)
        chunkStream.TypeID, _ = r.ReadUintBE(1)
        if timeStamp == 0xffffff {
            timeStamp, _ = r.ReadUintBE(4)
            chunkStream.extended = true
        } else {
            chunkStream.extended = false
        }
        chunkStream.timeDelta = timeStamp
        chunkStream.Timestamp += timeStamp
        chunkStream.new(pool)
    case 2:
        chunkStream.Format = chunkStream.tmpFormat
        timeStamp, _ := r.ReadUintBE(3)
        if timeStamp == 0xffffff {
            timeStamp, _ = r.ReadUintBE(4)
            chunkStream.extended = true
        } else {
            chunkStream.extended = false
        }
        chunkStream.timeDelta = timeStamp
        chunkStream.Timestamp += timeStamp
        chunkStream.new(pool)
    case 3:
        if chunkStream.remain == 0 {
            switch chunkStream.Format {
            case 0:
                if chunkStream.extended {
                    timestamp, _ := r.ReadUintBE(4)
                    chunkStream.Timestamp = timestamp
                }
            case 1, 2:
                var timeDelta uint32
                if chunkStream.extended {
                    timeDelta, _ = r.ReadUintBE(4)
                } else {
                    timeDelta = chunkStream.timeDelta
                }
                chunkStream.Timestamp += timeDelta
            }
            chunkStream.new(pool)
        } else {
            if chunkStream.extended {
                b, err := r.Peek(4)
                if err != nil {
                    return err
                }
                if binary.BigEndian.Uint32(b) == chunkStream.Timestamp {
                    _, _ = r.Discard(4)
                }
            }
        }
    default:
        return fmt.Errorf("invalid format=%d", chunkStream.Format)
    }
    log.Tracef("fmt:%v, csid:%v, streamId:%v, Timestamp:%v, timeDelta:%v, type:%v, length:%v",
        chunkStream.Format, chunkStream.CSID, chunkStream.StreamID, chunkStream.Timestamp, chunkStream.timeDelta, chunkStream.TypeID, chunkStream.Length)

    size := int(chunkStream.remain)
    if size > int(chunkSize) {
        size = int(chunkSize)
    }

    buf := chunkStream.Data[chunkStream.index : chunkStream.index+uint32(size)]
    if _, err := r.Read(buf); err != nil {
        return err
    }
    chunkStream.index += uint32(size)
    chunkStream.remain -= uint32(size)
    if chunkStream.remain == 0 {
        chunkStream.got = true
    }

    return r.readError
}

func (chunkStream *ChunkStream) writeChunk(w *ReadWriter, chunkSize int) error {
    if chunkStream.TypeID == flv.TagAudio {
        chunkStream.CSID = 4
    } else if chunkStream.TypeID == flv.TagVideo ||
        chunkStream.TypeID == flv.TagScriptDataAMF0 ||
        chunkStream.TypeID == flv.TagScriptDataAMF3 {
        chunkStream.CSID = 6
    }

    totalLen := uint32(0)
    numChunks := chunkStream.Length / uint32(chunkSize)
    for i := uint32(0); i <= numChunks; i++ {
        if totalLen == chunkStream.Length {
            break
        }
        if i == 0 {
            chunkStream.Format = uint32(0)
        } else {
            chunkStream.Format = uint32(3)
        }
        if err := chunkStream.writeHeader(w); err != nil {
            return err
        }
        inc := uint32(chunkSize)
        start := i * uint32(chunkSize)
        if uint32(len(chunkStream.Data))-start <= inc {
            inc = uint32(len(chunkStream.Data)) - start
        }
        totalLen += inc
        end := start + inc
        buf := chunkStream.Data[start:end]
        if _, err := w.Write(buf); err != nil {
            return err
        }
    }
    return nil
}

func (chunkStream *ChunkStream) writeHeader(rw *ReadWriter) error {
    // Chunk Basic Header
    h := chunkStream.Format << 6
    switch {
    case chunkStream.CSID < 64:
        h |= chunkStream.CSID
        _ = rw.WriteUintBE(h, 1)
    case chunkStream.CSID-64 < 256:
        h |= 0
        _ = rw.WriteUintBE(h, 1)
        _ = rw.WriteUintLE(chunkStream.CSID-64, 1)
    case chunkStream.CSID-64 < 65536:
        h |= 1
        _ = rw.WriteUintBE(h, 1)
        _ = rw.WriteUintLE(chunkStream.CSID-64, 2)
    }
    // Chunk Message Header
    ts := chunkStream.Timestamp
    if chunkStream.Format == 3 {
        goto END
    }
    if chunkStream.Timestamp > 0xffffff {
        ts = 0xffffff
    }
    _ = rw.WriteUintBE(ts, 3)
    if chunkStream.Format == 2 {
        goto END
    }
    if chunkStream.Length > 0xffffff {
        return fmt.Errorf("length=%d", chunkStream.Length)
    }
    _ = rw.WriteUintBE(chunkStream.Length, 3)
    _ = rw.WriteUintBE(chunkStream.TypeID, 1)
    if chunkStream.Format == 1 {
        goto END
    }
    _ = rw.WriteUintLE(chunkStream.StreamID, 4)
END:
    // Extended Timestamp
    if ts >= 0xffffff {
        _ = rw.WriteUintBE(chunkStream.Timestamp, 4)
    }
    return rw.WriteError()
}

func (chunkStream *ChunkStream) full() bool {
    return chunkStream.got
}
