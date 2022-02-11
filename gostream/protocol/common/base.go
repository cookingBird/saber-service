package common

import (
    "sync"
    "time"
)

const typeVideo = 9
const typeAudio = 8

type BaseReadWriter struct {
    lock               sync.Mutex
    timeout            time.Duration
    PreTime            time.Time
    BaseTimestamp      uint32
    LastVideoTimestamp uint32
    LastAudioTimestamp uint32
}

func NewBaseReadWriter(duration time.Duration) BaseReadWriter {
    return BaseReadWriter{
        timeout: duration,
        PreTime: time.Now(),
    }
}

func (baseReadWriter *BaseReadWriter) BaseTimeStamp() uint32 {
    return baseReadWriter.BaseTimestamp
}

func (baseReadWriter *BaseReadWriter) CalcBaseTimestamp() {
    if baseReadWriter.LastAudioTimestamp > baseReadWriter.LastVideoTimestamp {
        baseReadWriter.BaseTimestamp = baseReadWriter.LastAudioTimestamp
    } else {
        baseReadWriter.BaseTimestamp = baseReadWriter.LastVideoTimestamp
    }
}

func (baseReadWriter *BaseReadWriter) RecTimeStamp(timestamp, typeID uint32) {
    if typeID == typeVideo {
        baseReadWriter.LastVideoTimestamp = timestamp
    } else if typeID == typeAudio {
        baseReadWriter.LastAudioTimestamp = timestamp
    }
}

func (baseReadWriter *BaseReadWriter) SetPreTime() {
    baseReadWriter.lock.Lock()
    baseReadWriter.PreTime = time.Now()
    baseReadWriter.lock.Unlock()
}

func (baseReadWriter *BaseReadWriter) Alive() bool {
    baseReadWriter.lock.Lock()
    b := !(time.Now().Sub(baseReadWriter.PreTime) >= baseReadWriter.timeout)
    baseReadWriter.lock.Unlock()
    return b
}
