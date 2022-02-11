package flv

import (
    "fmt"
    "gostream/protocol"
)

type Reader struct {
}

func NewReader() *Reader {
    return &Reader{}
}

func (reader *Reader) ReadHeader(p *protocol.Packet) error {
    var tag Tag
    _, err := tag.ParseMediaTagHeader(p.Data, p.IsVideo)
    if err != nil {
        return err
    }
    p.Header = &tag

    return nil
}

func (reader *Reader) Read(p *protocol.Packet) error {
    var tag Tag
    n, err := tag.ParseMediaTagHeader(p.Data, p.IsVideo)
    if err != nil {
        return err
    }
    if tag.CodecID() == CodecIdH264 &&
        p.Data[0] == 0x17 && p.Data[1] == 0x02 {
        return fmt.Errorf("avc end sequence")
    }
    p.Header = &tag
    p.Data = p.Data[n:]

    return nil
}
