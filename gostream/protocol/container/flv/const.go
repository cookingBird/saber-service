package flv

// tag header常量
const (
    TagAudio          = 8
    TagVideo          = 9
    TagScriptDataAMF0 = 18
    TagScriptDataAMF3 = 0xf
)

const (
    MetadataAMF0 = 0x12
    MetadataAMF3 = 0xf
)

// 音频Tag常量
const (
    // 音频格式
    SoundFormatMP3 = 2
    SoundFormatAAC = 10

    // 采样率，对AAC来说，永远等于3
    SoundRate5d5kHz = 0
    SoundRate11KHz  = 1
    SoundRate22KHz  = 2
    SoundRate44KHz  = 3

    // 采样精度，对于压缩过的音频，永远是16位
    SoundSize8Bit  = 0
    SoundSize16Bit = 1

    // 声道类型，对Nellymoser来说，永远是单声道；对AAC来说，永远是双声道；
    SoundTypeMono   = 0
    SoundTypeStereo = 1

    // 包类型
    AACPocketTypeSeqHeader = 0
    AACPocketTypeRaw       = 1
)

// 视频tag常量
const (
    // 包类型
    AVCPacketTypeSeqHeader = 0
    AVCPacketTypeNALU      = 1
    AVCPacketTypeEndSeq    = 2

    // 帧类型
    FrameTypeKEY   = 1
    FrameTypeInter = 2

    // 编解码器
    CodecIdH264 = 7
)


