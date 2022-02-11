package binary


func PutInt24ByBigEndian(b []byte, v int32) {
    b[0] = byte(v >> 16)
    b[1] = byte(v >> 8)
    b[2] = byte(v)
}

func U32BE(b []byte) (i uint32) {
    i = uint32(b[0])
    i <<= 8
    i |= uint32(b[1])
    i <<= 8
    i |= uint32(b[2])
    i <<= 8
    i |= uint32(b[3])
    return
}

func PutU8(b []byte, v uint8) {
    b[0] = v
}

func PutI32BE(b []byte, v int32) {
    b[0] = byte(v >> 24)
    b[1] = byte(v >> 16)
    b[2] = byte(v >> 8)
    b[3] = byte(v)
}

func PutU32BE(b []byte, v uint32) {
    b[0] = byte(v >> 24)
    b[1] = byte(v >> 16)
    b[2] = byte(v >> 8)
    b[3] = byte(v)
}
