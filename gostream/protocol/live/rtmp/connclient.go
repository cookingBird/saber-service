package rtmp

import (
    "bytes"
    "fmt"
    log "github.com/sirupsen/logrus"
    "gostream/protocol/amf"
    "gostream/protocol/container/flv"
    "io"
    "math/rand"
    "net"
    "net/url"
    "strings"
)

var ErrFail = fmt.Errorf("respone err")

type ConnClient struct {
    done     bool
    transID  int
    url      string
    tcUrl    string
    app      string
    title    string
    query    string
    cmd      string
    streamId uint32
    conn     *Conn
    encoder  *amf.Encoder
    decoder  *amf.Decoder
    buffer   *bytes.Buffer
}

func NewClient() *ConnClient {
    return &ConnClient{
        transID: 1,
        buffer:  bytes.NewBuffer(nil),
        encoder: &amf.Encoder{},
        decoder: &amf.Decoder{},
    }
}

func (connClient *ConnClient) Start(urlStr string, method string) error {
    rtmpUrl, err := url.Parse(urlStr)
    if err != nil {
        return err
    }
    connClient.url = urlStr
    path := strings.TrimLeft(rtmpUrl.Path, "/")
    ps := strings.SplitN(path, "/", 2)
    if len(ps) != 2 {
        return fmt.Errorf("u path err: %s", path)
    }
    connClient.app = ps[0]
    connClient.title = ps[1]
    connClient.query = rtmpUrl.RawQuery
    connClient.tcUrl = "rtmp://" + rtmpUrl.Host + "/" + connClient.app
    port := ":1935"
    host := rtmpUrl.Host
    localIP := ":0"
    var remoteIP string
    if strings.Index(host, ":") != -1 {
        host, port, err = net.SplitHostPort(host)
        if err != nil {
            return err
        }
        port = ":" + port
    }
    ips, err := net.LookupIP(host)
    log.Debugf("ips: %v, host: %v", ips, host)
    if err != nil {
        log.Warning(err)
        return err
    }
    remoteIP = ips[rand.Intn(len(ips))].String()
    if strings.Index(remoteIP, ":") == -1 {
        remoteIP += port
    }

    local, err := net.ResolveTCPAddr("tcp", localIP)
    if err != nil {
        log.Warning(err)
        return err
    }
    log.Debug("remoteIP: ", remoteIP)
    remote, err := net.ResolveTCPAddr("tcp", remoteIP)
    if err != nil {
        log.Warning(err)
        return err
    }
    conn, err := net.DialTCP("tcp", local, remote)
    if err != nil {
        log.Warning(err)
        return err
    }

    log.Debug("connection:", "local:", conn.LocalAddr(), "remote:", conn.RemoteAddr())

    connClient.conn = NewConn(conn, 4*1024)

    log.Debug("HandshakeClient....")
    if err := connClient.conn.HandshakeClient(); err != nil {
        return err
    }

    log.Debug("writeConnectMsg....")
    if err := connClient.writeConnectMsg(); err != nil {
        return err
    }
    log.Debug("writeCreateStreamMsg....")
    if err := connClient.writeCreateStreamMsg(); err != nil {
        log.Debug("writeCreateStreamMsg error", err)
        return err
    }

    log.Debug("method control:", method)
    if method == publish {
        if err := connClient.writePublishMsg(); err != nil {
            return err
        }
    } else if method == play {
        if err := connClient.writePlayMsg(); err != nil {
            return err
        }
    }
    return nil
}

func (connClient *ConnClient) readRespMsg() error {
    var err error
    var rc ChunkStream
    for {
        if err = connClient.conn.Read(&rc); err != nil {
            return err
        }
        if err != nil && err != io.EOF {
            return err
        }
        switch rc.TypeID {
        case 20, 17:
            r := bytes.NewReader(rc.Data)
            vs, _ := connClient.decoder.DecodeBatch(r, amf.AMF0)

            log.Debugf("readRespMsg: vs=%v", vs)
            for k, v := range vs {
                switch v.(type) {
                case string:
                    switch connClient.cmd {
                    case cmdConnect, cmdCreateStream:
                        if v.(string) != respResult {
                            return fmt.Errorf(v.(string))
                        }

                    case cmdPublish:
                        if v.(string) != onStatus {
                            return ErrFail
                        }
                    }
                case float64:
                    switch connClient.cmd {
                    case cmdConnect, cmdCreateStream:
                        id := int(v.(float64))

                        if k == 1 {
                            if id != connClient.transID {
                                return ErrFail
                            }
                        } else if k == 3 {
                            connClient.streamId = uint32(id)
                        }
                    case cmdPublish:
                        if int(v.(float64)) != 0 {
                            return ErrFail
                        }
                    }
                case amf.Object:
                    amfObject := v.(amf.Object)
                    switch connClient.cmd {
                    case cmdConnect:
                        code, ok := amfObject["code"]
                        if ok && code.(string) != connectSuccess {
                            return ErrFail
                        }
                    case cmdPublish:
                        code, ok := amfObject["code"]
                        if ok && code.(string) != publishStart {
                            return ErrFail
                        }
                    }
                }
            }

            return nil
        }
    }
}

func (connClient *ConnClient) writeConnectMsg() error {
    event := make(amf.Object)
    event["app"] = connClient.app
    event["type"] = "nonprivate"
    event["flashVer"] = "FMS.3.1"
    event["tcUrl"] = connClient.tcUrl
    connClient.cmd = cmdConnect

    log.Debugf("writeConnectMsg: connClient.transID=%d, event=%v", connClient.transID, event)
    if err := connClient.writeMsg(cmdConnect, connClient.transID, event); err != nil {
        return err
    }
    return connClient.readRespMsg()
}

func (connClient *ConnClient) writeCreateStreamMsg() error {
    connClient.transID++
    connClient.cmd = cmdCreateStream

    log.Debugf("writeCreateStreamMsg: connClient.transID=%d", connClient.transID)
    if err := connClient.writeMsg(cmdCreateStream, connClient.transID, nil); err != nil {
        return err
    }

    for {
        err := connClient.readRespMsg()
        if err == nil {
            return err
        }
        if err == ErrFail {
            log.Debugf("writeCreateStreamMsg readRespMsg err=%v", err)
            return err
        }
    }
}

func (connClient *ConnClient) writePublishMsg() error {
    connClient.transID++
    connClient.cmd = cmdPublish
    if err := connClient.writeMsg(cmdPublish, connClient.transID, nil, connClient.title, publishLive); err != nil {
        return err
    }
    return connClient.readRespMsg()
}

func (connClient *ConnClient) writePlayMsg() error {
    connClient.transID++
    connClient.cmd = cmdPlay
    log.Debugf("writePlayMsg: connClient.transID=%d, cmdPlay=%v, connClient.title=%v",
        connClient.transID, cmdPlay, connClient.title)

    if err := connClient.writeMsg(cmdPlay, 0, nil, connClient.title); err != nil {
        return err
    }
    return connClient.readRespMsg()
}

func (connClient *ConnClient) writeMsg(args ...interface{}) error {
    connClient.buffer.Reset()
    for _, v := range args {
        if _, err := connClient.encoder.Encode(connClient.buffer, v, amf.AMF0); err != nil {
            return err
        }
    }
    msg := connClient.buffer.Bytes()
    c := ChunkStream{
        Format:    0,
        CSID:      3,
        Timestamp: 0,
        TypeID:    20,
        StreamID:  connClient.streamId,
        Length:    uint32(len(msg)),
        Data:      msg,
    }
    _ = connClient.conn.Write(&c)
    return connClient.conn.Flush()
}

func (connClient *ConnClient) Write(c ChunkStream) error {
    if c.TypeID == flv.TagScriptDataAMF0 ||
        c.TypeID == flv.TagScriptDataAMF3 {
        var err error
        if c.Data, err = amf.MetaDataReform(c.Data, amf.ADD); err != nil {
            return err
        }
        c.Length = uint32(len(c.Data))
    }
    return connClient.conn.Write(&c)
}

func (connClient *ConnClient) Flush() error {
    return connClient.conn.Flush()
}

func (connClient *ConnClient) Read(c *ChunkStream) (err error) {
    return connClient.conn.Read(c)
}

func (connClient *ConnClient) GetInfo() (app string, name string, url string) {
    app = connClient.app
    name = connClient.title
    url = connClient.url
    return
}

func (connClient *ConnClient) GetStreamId() uint32 {
    return connClient.streamId
}

func (connClient *ConnClient) Close(err error) {
    _ = connClient.conn.Close()
}
