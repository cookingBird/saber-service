package cache

import (
    "fmt"
    log "github.com/sirupsen/logrus"
    "gostream/protocol"
)

var (
    maxGOPCap    = 1024
    ErrGopTooBig = fmt.Errorf("gop to big")
)

type array struct {
    index   int
    packets []*protocol.Packet
}

func newArray() *array {
    ret := &array{
        index:   0,
        packets: make([]*protocol.Packet, 0, maxGOPCap),
    }
    return ret
}

func (array *array) reset() {
    array.index = 0
    array.packets = array.packets[:0]
}

func (array *array) write(packet *protocol.Packet) error {
    if array.index >= maxGOPCap {
        return ErrGopTooBig
    }
    array.packets = append(array.packets, packet)
    array.index++
    return nil
}

func (array *array) send(w protocol.WriteCloser) error {
    var err error
    for i := 0; i < array.index; i++ {
        packet := array.packets[i]
        if err = w.Write(packet); err != nil {
            return err
        }
    }
    return err
}

type GopCache struct {
    start     bool
    num       int
    maxCount  int
    currIndex int
    nextIndex int
    gops      []*array

    //gopList *list.List
}

func NewGopCache(num int) *GopCache {
    return &GopCache{
        maxCount: num,
        gops:     make([]*array, num),
        //gopList:  list.New(),
    }
}

func (gopCache *GopCache) saveToArray(chunk *protocol.Packet, startNew bool) error {

    var garry *array
    if startNew {
        gopCache.currIndex = gopCache.nextIndex
        garry = gopCache.gops[gopCache.currIndex]
        if garry == nil {
            garry = newArray()
            gopCache.num++
            gopCache.gops[gopCache.nextIndex] = garry
        } else {
            garry.reset()
        }
        //if gopCache.nextIndex > 50 {
        //    log.Warning("max cache", gopCache.nextIndex)
        //}
        gopCache.nextIndex = (gopCache.nextIndex + 1) % gopCache.maxCount
    } else {
        //garry = gopCache.gops[(gopCache.nextIndex+1)%gopCache.maxCount]
        garry = gopCache.gops[gopCache.currIndex]
    }
    err := garry.write(chunk)
    if err != nil {
        log.Error("save gop cache err", err)
    }
    return nil
}

func (gopCache *GopCache) Save(p *protocol.Packet) {
    var ok bool
    if p.IsVideo {
       vh := p.Header.(protocol.VideoPacketHeader)
       if vh.IsKeyFrame() && !vh.IsSeq() {
           ok = true
       }
    }
    if ok || gopCache.start {
       gopCache.start = true
       _ = gopCache.saveToArray(p, ok)
    }

    //gopCache.gopList.PushBack(p)
    //if gopCache.gopList.Len() > gopCache.maxCount {
    //    gopCache.gopList.Remove(gopCache.gopList.Front())
    //}
}

func (gopCache *GopCache) Write(w protocol.WriteCloser) error {
    var err error
    //pos := (gopCache.nextIndex + 1) % gopCache.maxCount
    pos := gopCache.currIndex
    //log.Debug(pos, gopCache.num)
    for i := 0; i < gopCache.num; i++ {
       index := (pos - gopCache.num + 1) + i
       if index < 0 {
           index += gopCache.maxCount
       }
       g := gopCache.gops[index]
       err = g.send(w)
       if err != nil {
           return err
       }
    }
    return nil
    //log.Debug(gopCache.gopList.Len(), gopCache.maxCount)
    //count := 0
    //for element := gopCache.gopList.Front(); element != nil; element = element.Next() {
    //    err := w.Write(element.Value.(*protocol.Packet))
    //    if err != nil {
    //        return err
    //    }
    //    count += len(element.Value.(*protocol.Packet).Data)
    //}
    //log.Debug(count)
    //return nil
}
