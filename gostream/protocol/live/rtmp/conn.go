package rtmp

import (
    "bytes"
    "crypto/hmac"
    "crypto/rand"
    "crypto/sha256"
    "encoding/binary"

    //"encoding/binary"
    "fmt"
    binaryUtil "gostream/utils/binary"
    "gostream/utils/pool"
    "io"
    "net"
    "time"
)

const timeout = 5 * time.Second

var (
    hsClientFullKey = []byte{
        'G', 'e', 'n', 'u', 'i', 'n', 'e', ' ', 'A', 'd', 'o', 'b', 'e', ' ',
        'F', 'l', 'a', 's', 'h', ' ', 'P', 'l', 'a', 'y', 'e', 'r', ' ',
        '0', '0', '1',
        0xF0, 0xEE, 0xC2, 0x4A, 0x80, 0x68, 0xBE, 0xE8, 0x2E, 0x00, 0xD0, 0xD1,
        0x02, 0x9E, 0x7E, 0x57, 0x6E, 0xEC, 0x5D, 0x2D, 0x29, 0x80, 0x6F, 0xAB,
        0x93, 0xB8, 0xE6, 0x36, 0xCF, 0xEB, 0x31, 0xAE,
    }
    hsServerFullKey = []byte{
        'G', 'e', 'n', 'u', 'i', 'n', 'e', ' ', 'A', 'd', 'o', 'b', 'e', ' ',
        'F', 'l', 'a', 's', 'h', ' ', 'M', 'e', 'd', 'i', 'a', ' ',
        'S', 'e', 'r', 'v', 'e', 'r', ' ',
        '0', '0', '1',
        0xF0, 0xEE, 0xC2, 0x4A, 0x80, 0x68, 0xBE, 0xE8, 0x2E, 0x00, 0xD0, 0xD1,
        0x02, 0x9E, 0x7E, 0x57, 0x6E, 0xEC, 0x5D, 0x2D, 0x29, 0x80, 0x6F, 0xAB,
        0x93, 0xB8, 0xE6, 0x36, 0xCF, 0xEB, 0x31, 0xAE,
    }
    hsClientPartialKey = hsClientFullKey[:30]
    hsServerPartialKey = hsServerFullKey[:36]
)

type Conn struct {
    net.Conn
    chunkSize           uint32
    remoteChunkSize     uint32
    windowAckSize       uint32
    remoteWindowAckSize uint32
    received            uint32
    ackReceived         uint32
    rw                  *ReadWriter
    pool                *pool.Pool
    chunks              map[uint32]ChunkStream
}

func NewConn(c net.Conn, bufferSize int) *Conn {
    return &Conn{
        Conn:                c,
        chunkSize:           128,
        remoteChunkSize:     128,
        windowAckSize:       2500000,
        remoteWindowAckSize: 2500000,
        pool:                pool.NewPool(),
        rw:                  NewReadWriter(c, bufferSize),
        chunks:              make(map[uint32]ChunkStream),
    }
}

func (conn *Conn) HandshakeClient() (err error) {
    var random [(1 + 1536*2) * 2]byte

    C0C1C2 := random[:1536*2+1]
    C0 := C0C1C2[:1]
    C0C1 := C0C1C2[:1536+1]
    C2 := C0C1C2[1536+1:]

    S0S1S2 := random[1536*2+1:]

    C0[0] = 3
    // > C0C1
    _ = conn.Conn.SetDeadline(time.Now().Add(timeout))
    if _, err = conn.rw.Write(C0C1); err != nil {
        return
    }
    _ = conn.Conn.SetDeadline(time.Now().Add(timeout))
    if err = conn.rw.Flush(); err != nil {
        return
    }

    // < S0S1S2
    _ = conn.Conn.SetDeadline(time.Now().Add(timeout))
    if _, err = io.ReadFull(conn.rw, S0S1S2); err != nil {
        return
    }

    S1 := S0S1S2[1 : 1536+1]
    //if ver := binary.BigEndian.Uint32(S1[4:8]); ver != 0 {
    if ver := binaryUtil.U32BE(S1[4:8]); ver != 0 {
        C2 = S1
    } else {
        C2 = S1
    }

    // > C2
    _ = conn.Conn.SetDeadline(time.Now().Add(timeout))
    if _, err = conn.rw.Write(C2); err != nil {
        return
    }
    _ = conn.Conn.SetDeadline(time.Time{})
    return
}

func (conn *Conn) HandshakeServer() (err error) {
    var random [(1 + 1536*2) * 2]byte

    C0C1C2 := random[:1536*2+1]
    C0 := C0C1C2[:1]
    C1 := C0C1C2[1 : 1536+1]
    C0C1 := C0C1C2[:1536+1]
    C2 := C0C1C2[1536+1:]

    S0S1S2 := random[1536*2+1:]
    S0 := S0S1S2[:1]
    S1 := S0S1S2[1 : 1536+1]
    S0S1 := S0S1S2[:1536+1]
    S2 := S0S1S2[1536+1:]

    // < C0C1
    _ = conn.Conn.SetDeadline(time.Now().Add(timeout))
    if _, err = io.ReadFull(conn.rw, C0C1); err != nil {
        return
    }
    _ = conn.Conn.SetDeadline(time.Now().Add(timeout))
    if C0[0] != 3 {
        err = fmt.Errorf("rtmp: handshake version=%d invalid", C0[0])
        return
    }

    S0[0] = 3

    //clientTime := binary.BigEndian.Uint32(C1[0:4])
    clientTime := binaryUtil.U32BE(C1[0:4])
    serverTime := clientTime
    serverVersion := uint32(0x0d0e0a0d)
    //clientVersion := binary.BigEndian.Uint32(C1[4:8])
    clientVersion := binaryUtil.U32BE(C1[4:8])

    if clientVersion != 0 {
        var ok bool
        var digest []byte
        if ok, digest = hsParse1(C1, hsClientPartialKey, hsServerFullKey); !ok {
            err = fmt.Errorf("rtmp: handshake server: C1 invalid")
            return
        }
        hsCreate01(S0S1, serverTime, serverVersion, hsServerPartialKey)
        hsCreate2(S2, digest)
    } else {
        copy(S1, C2)
        copy(S2, C1)
    }

    // > S0S1S2
    _ = conn.Conn.SetDeadline(time.Now().Add(timeout))
    if _, err = conn.rw.Write(S0S1S2); err != nil {
        return
    }
    _ = conn.Conn.SetDeadline(time.Now().Add(timeout))
    if err = conn.rw.Flush(); err != nil {
        return
    }

    // < C2
    _ = conn.Conn.SetDeadline(time.Now().Add(timeout))
    if _, err = io.ReadFull(conn.rw, C2); err != nil {
        return
    }
    _ = conn.Conn.SetDeadline(time.Time{})
    return
}

func hsParse1(p []byte, peerKey []byte, key []byte) (ok bool, digest []byte) {
    var pos int
    if pos = hsFindDigest(p, peerKey, 772); pos == -1 {
        if pos = hsFindDigest(p, peerKey, 8); pos == -1 {
            return
        }
    }
    ok = true
    digest = hsMakeDigest(key, p[pos:pos+32], -1)
    return
}

func hsMakeDigest(key []byte, src []byte, gap int) (dst []byte) {
    h := hmac.New(sha256.New, key)
    if gap <= 0 {
        h.Write(src)
    } else {
        h.Write(src[:gap])
        h.Write(src[gap+32:])
    }
    return h.Sum(nil)
}

func hsFindDigest(p []byte, key []byte, base int) int {
    gap := hsCalcDigestPos(p, base)
    digest := hsMakeDigest(key, p, gap)
    if bytes.Compare(p[gap:gap+32], digest) != 0 {
        return -1
    }
    return gap
}

func hsCalcDigestPos(p []byte, base int) (pos int) {
    for i := 0; i < 4; i++ {
        pos += int(p[base+i])
    }
    pos = (pos % 728) + base + 4
    return
}

func hsCreate01(p []byte, time uint32, ver uint32, key []byte) {
    p[0] = 3
    p1 := p[1:]
    _, _ = rand.Read(p1[8:])
    //binary.BigEndian.PutUint32(p1[0:4], time)
    binaryUtil.PutU32BE(p1[0:4], time)
    //binary.BigEndian.PutUint32(p1[4:8], ver)
    binaryUtil.PutU32BE(p1[4:8], ver)
    gap := hsCalcDigestPos(p1, 8)
    digest := hsMakeDigest(key, p1, gap)
    copy(p1[gap:], digest)
}

func hsCreate2(p []byte, key []byte) {
    _, _ = rand.Read(p)
    gap := len(p) - 32
    digest := hsMakeDigest(key, p, gap)
    copy(p[gap:], digest)
}

func (conn *Conn) handleControlMsg(c *ChunkStream) {
    if c.TypeID == idSetChunkSize {
        conn.remoteChunkSize = binary.BigEndian.Uint32(c.Data)
    } else if c.TypeID == idWindowAckSize {
        conn.remoteWindowAckSize = binary.BigEndian.Uint32(c.Data)
    }
}

func (conn *Conn) Read(c *ChunkStream) error {
    for {
        h, _ := conn.rw.ReadUintBE(1)
        format := h >> 6
        csid := h & 0x3f
        chunkStream, ok := conn.chunks[csid]
        if !ok {
            chunkStream = ChunkStream{}
            conn.chunks[csid] = chunkStream
        }
        chunkStream.tmpFormat = format
        chunkStream.CSID = csid
        err := chunkStream.readChunk(conn.rw, conn.remoteChunkSize, conn.pool)
        if err != nil {
            return err
        }
        conn.chunks[csid] = chunkStream
        if chunkStream.full() {
            *c = chunkStream
            break
        }
    }

    conn.handleControlMsg(c)
    conn.ack(c.Length)
    return nil
}

func (conn *Conn) ack(size uint32) {
    conn.received += size
    conn.ackReceived += size
    if conn.received >= 0xf0000000 {
        conn.received = 0
    }
    if conn.ackReceived >= conn.remoteWindowAckSize {
        chunkStream := conn.NewAck(conn.ackReceived)
        _ = chunkStream.writeChunk(conn.rw, int(conn.chunkSize))
        conn.ackReceived = 0
    }
}

func (conn *Conn) Write(chunkStream *ChunkStream) error {
    if chunkStream.TypeID == idSetChunkSize {
        conn.chunkSize = binary.BigEndian.Uint32(chunkStream.Data)
    }
    return chunkStream.writeChunk(conn.rw, int(conn.chunkSize))
}

func (conn *Conn) SetBegin() {
    ret := conn.userControlMsg(streamBegin, 4)
    for i := 0; i < 4; i++ {
        ret.Data[2+i] = byte(1 >> uint32((3-i)*8) & 0xff)
    }
    _ = conn.Write(&ret)
}

func (conn *Conn) SetRecorded() {
    ret := conn.userControlMsg(streamIsRecorded, 4)
    for i := 0; i < 4; i++ {
        ret.Data[2+i] = byte(1 >> uint32((3-i)*8) & 0xff)
    }
    _ = conn.Write(&ret)
}

func (conn *Conn) userControlMsg(eventType, bufLen uint32) ChunkStream {
    var ret ChunkStream
    bufLen += 2
    ret = ChunkStream{
        Format:   0,
        CSID:     2,
        TypeID:   4,
        StreamID: 1,
        Length:   bufLen,
        Data:     make([]byte, bufLen),
    }
    ret.Data[0] = byte(eventType >> 8 & 0xff)
    ret.Data[1] = byte(eventType & 0xff)
    return ret
}

func (conn *Conn) Flush() error {
    return conn.rw.Flush()
}

func (conn *Conn) Close() error {
    return conn.Conn.Close()
}

func (conn *Conn) RemoteAddr() net.Addr {
    return conn.Conn.RemoteAddr()
}

func (conn *Conn) LocalAddr() net.Addr {
    return conn.Conn.LocalAddr()
}

func (conn *Conn) SetDeadline(t time.Time) error {
    return conn.Conn.SetDeadline(t)
}
