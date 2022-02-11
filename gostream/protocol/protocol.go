package protocol

import (
    "fmt"
)

// 业务常量
const (
    AppNameUav = "uav"
    AppNameAi = "ai"
)

type Alive interface {
    Alive() bool
}

type Closer interface {
    Info() Info
    Close(error)
}

type CalcTime interface {
    CalcBaseTimestamp()
}

type ReadCloser interface {
    Closer
    Alive
    Read(*Packet) error
}

type WriteCloser interface {
    Closer
    Alive
    CalcTime
    Write(*Packet) error
}

type Info struct {
    Key string
    URL string
    UID string
}

func (info Info) String() string {
    return fmt.Sprintf("{key: %s, URL: %s, UID: %s}",
        info.Key, info.URL, info.UID)
}

type PacketHeader interface {
}

type AudioPacketHeader interface {
    PacketHeader
    SoundFormat() uint8
    AACPacketType() uint8
}

type VideoPacketHeader interface {
    PacketHeader
    IsKeyFrame() bool
    IsSeq() bool
    CodecID() uint8
    CompositionTime() int32
}

// Header can be converted to AudioHeaderInfo or VideoHeaderInfo
type Packet struct {
    IsAudio    bool
    IsVideo    bool
    IsMetadata bool
    TimeStamp  uint32
    StreamID   uint32
    Header     PacketHeader
    Data       []byte
}

type Handler interface {
    HandleReader(ReadCloser)
    HandleWriter(WriteCloser)
    StartTransmit(string)
}


