package cache

import (
    "gostream/config"
    "gostream/protocol"
    "gostream/protocol/container/flv"
)

type Cache struct {
    gop      *GopCache
    videoSeq *protocol.Packet
    audioSeq *protocol.Packet
    metadata *protocol.Packet
}

func NewCache() *Cache {
    return &Cache{
        gop: NewGopCache(config.GetConfig().CachePacketNum),
    }
}

func (cache *Cache) Save(p protocol.Packet) {
    if p.IsMetadata {
        cache.metadata = &p
        return
    } else {
        if !p.IsVideo {
            ah, ok := p.Header.(protocol.AudioPacketHeader)
            if ok {
                if ah.SoundFormat() == flv.SoundFormatAAC &&
                    ah.AACPacketType() == flv.AACPocketTypeSeqHeader {
                    cache.audioSeq = &p
                    return
                } else {
                    return
                }
            }

        } else {
            vh, ok := p.Header.(protocol.VideoPacketHeader)
            if ok {
                if vh.IsSeq() {
                    cache.videoSeq = &p
                    return
                }
            } else {
                return
            }

        }
    }
    cache.gop.Save(&p)
}

func (cache *Cache) Send(w protocol.WriteCloser) error {
    if cache.metadata != nil {
        if err := w.Write(cache.metadata); err != nil {
            return err
        }
    }

    if cache.videoSeq != nil {
        if err := w.Write(cache.videoSeq); err != nil {
            return err
        }
    }
    if cache.audioSeq != nil {
        if err := w.Write(cache.audioSeq); err != nil {
            return err
        }
    }

    if err := cache.gop.Write(w); err != nil {
        return err
    }

    return nil
}
