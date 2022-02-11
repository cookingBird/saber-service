package rtmp

const (
    publish = "publish"
    play    = "play"
)

const (
    _ = iota
    idSetChunkSize
    idAbortMessage
    idAck
    idUserControlMessages
    idWindowAckSize
    idSetPeerBandwidth
)

const (
    cmdConnect       = "connect"
    cmdFcpublish     = "FCPublish"
    cmdReleaseStream = "releaseStream"
    cmdCreateStream  = "createStream"
    cmdPublish       = "publish"
    cmdFCUnpublish   = "FCUnpublish"
    cmdDeleteStream  = "deleteStream"
    cmdPlay          = "play"
)

const (
    respResult     = "_result"
    respError      = "_error"
    onStatus       = "onStatus"
    publishStart   = "NetStream.Publish.Start"
    playStart      = "NetStream.Play.Start"
    connectSuccess = "NetConnection.Connect.Success"
    onBWDone       = "onBWDone"
)

const (
    publishLive   = "live"
    publishRecord = "record"
    publishAppend = "append"
)

const (
    staticCtrlCmdStop = "stop"
)

const (
    streamBegin      uint32 = 0
    streamEOF        uint32 = 1
    streamDry        uint32 = 2
    setBufferLen     uint32 = 3
    streamIsRecorded uint32 = 4
    pingRequest      uint32 = 6
    pingResponse     uint32 = 7
)
