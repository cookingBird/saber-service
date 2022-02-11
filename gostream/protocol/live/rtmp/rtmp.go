package rtmp

type StreamReadWriteCloser interface {
    GetInFo
    Close(error)
    Write(ChunkStream) error
    Read(c *ChunkStream) error
}

type GetInFo interface {
    GetInfo() (string, string, string)
}
