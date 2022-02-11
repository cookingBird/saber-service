package rtmp

import (
    "fmt"
    log "github.com/sirupsen/logrus"
    "gostream/api"
    "gostream/config"
    "gostream/protocol"
    "gostream/protocol/container/flv"
    "gostream/utils/ffmpeg"
    "net"
    "strings"
)

type Server struct {
    handler protocol.Handler
}

func NewRtmpServer(h protocol.Handler) *Server {
    return &Server{
        handler: h,
    }
}

func (server *Server) Serve(listener net.Listener) (err error) {
    defer func() {
        if r := recover(); r != nil {
            log.Error("rtmp serve panic: ", r)
        }
    }()

    for {
        var netConn net.Conn
        netConn, err = listener.Accept()
        if err != nil {
            return
        }
        conn := NewConn(netConn, 4*1024)
        log.Debug("new client, connect remote: ", conn.RemoteAddr().String(),
            "local:", conn.LocalAddr().String())
        go server.handleConn(conn)
    }
}

func (server *Server) handleConn(conn *Conn) error {
    if err := conn.HandshakeServer(); err != nil {
        _ = conn.Close()
        log.Error("handleConn HandshakeServer err: ", err)
        return err
    }
    log.Debugf("handshake finish...\n")
    connServer := NewConnServer(conn)

    if err := connServer.ReadMsg(); err != nil {
        _ = conn.Close()
        log.Error("handleConn read msg err: ", err)
        return err
    }

    log.Debugf("ReadMsg finish...\n")

    appName, name, _ := connServer.GetInfo()

    configApp := CheckApp(appName)
    if configApp == nil {
        err := fmt.Errorf("application name=%s is not configured", appName)
        _ = conn.Close()
        log.Error("CheckAppName err: ", err)
        return err
    }
    // 验证无人机编码
    uavInfo, err := api.GetUavInfo(name)
    if err != nil {
        _ = conn.Close()
        log.Errorf("查询不到无人机, uavDevCode:%s, err:%v", name, err)
        return err
    }

    log.Debugf("handleConn: IsPublisher=%v, appName:%s, name:%s", connServer.IsPublisher(), appName, name)
    if connServer.IsPublisher() {
        //channel, err := configure.RoomKeys.GetChannel(name)
        //if err != nil {
        //	err := fmt.Errorf("invalid key")
        //	conn.Close()
        //	log.Error("CheckKey err: ", err)
        //	return err
        //}
        connServer.PublishInfo.Name = name
        if pushList, err := GetStaticPushUrls(appName); err == nil {
            log.Debugf("GetStaticPushUrls: %v", pushList)
        }
        reader := NewReader(connServer)
        log.Debugf("new publisher: %+v", reader.Info())
        server.handler.HandleReader(reader)
        appNameLower := strings.ToLower(appName)
        flvWriter := flv.NewWriter(reader.Info(), name, appNameLower, uavInfo)
        server.handler.HandleWriter(flvWriter)
        if appNameLower == protocol.AppNameUav {
            go api.SendLiveNotify(name, reader.Info().URL, "1")
        }
        server.handler.StartTransmit(reader.Info().Key)
        if configApp.LiveTranscoding && appNameLower == protocol.AppNameUav {
            go ConvertResolutionRtmp(reader.Info().URL)
        }
    } else {
        writer := NewWriter(connServer)
        log.Debugf("new player: %+v", writer.Info())
        server.handler.HandleWriter(writer)
    }
    return nil
}

func CheckApp(appName string) *config.App {
    for _, app := range config.GetConfig().Apps {
        if app.Name == appName {
            return app
        }
    }
    return nil
}

//func sendAiTask(taskID, rtmpURL string) {
//    req := &api.RecognitionReq{}
//    req.IfComputerPos = "true"
//    req.IfMark = "true"
//    req.IfPushStream = "true"
//    req.TaskID = taskID
//    req.TaskResources = make([]*api.Resource, 0)
//    req.TaskResources = append(req.TaskResources, &api.Resource{ResourceID: taskID, ResourceType: api.TaskTypeStream, ResourceURL: rtmpURL})
//    resp, err := api.Recognition(config.GetConfig().AiTaskUrl, req)
//    if err != nil {
//        log.Errorf("发送AI任务失败, aiTaskId:%s, err:%v", taskID, err)
//        return
//    }
//    log.Infof("发送AI任务，aiTaskId:%s, 响应:%+v", taskID, resp)
//}



func ConvertResolutionRtmp(rtmpUrl string) {
    log.Infof("开始直播转码 %s", rtmpUrl)
    data, err := ffmpeg.ConvertResolutionRtmp(rtmpUrl)
    log.Infof("直播转码结束 %s", rtmpUrl)
    log.Debugf("直播转码结果 %s, out:%s, err:%v", rtmpUrl, string(data), err)
}
