package main

import (
    log "github.com/sirupsen/logrus"
    "gopkg.in/natefinch/lumberjack.v2"
    "gostream/config"
    "gostream/data/hbase"
    "gostream/data/minio"
    "gostream/protocol/live/httpflv"
    "gostream/protocol/live/rtmp"
    "io"
    "math/rand"
    "net"
    "os"
    "strconv"
    "sync"
    "time"
)

const Version = "1.0.0"

func main() {
    rand.Seed(time.Now().UnixNano())

    // 初始化配置
    initConfig()

    // 初始化MinIO
    initMinIO()

    // 初始化HBase
    hbase.InitHBase()

    //// 启动api服务
    //go api.StartServer()

    // 构建rtmp处理器
    rtmpStreamHandler := rtmp.NewStreamHandler()

    // 启动httpFlv服务
    startHTTPFlv(rtmpStreamHandler)

    // 启动rtmp服务端
    startRtmp(rtmpStreamHandler)

    log.Info("系统启动完成.  version:", Version)
    // TODO 退出信号，退出操作（停止监听端口，等待保存文件等）
    wg := &sync.WaitGroup{}
    wg.Add(1)
    wg.Wait()
}

func initConfig() {
    // 设置日志
    log.SetOutput(io.MultiWriter(&lumberjack.Logger{
        Filename: "./log/server.log",
        MaxSize:  50,
    }, os.Stdout))
    log.SetReportCaller(true)
    log.SetFormatter(&log.TextFormatter{
        TimestampFormat: "2006-01-02 15:04:05",
    })

    // 加载配置文件
    err := config.LoadConfig("./")
    if err != nil {
        log.Fatal("加载配置文件出错", err)
    }
    log.Info("config", config.GetConfig().String())

    // 设置日志级别
    logLevel, err := log.ParseLevel(config.GetConfig().LogLevel)
    if err != nil {
        log.Fatal("解析日志级别出错", err)
    }
    log.SetLevel(logLevel)
    log.SetReportCaller(logLevel == log.DebugLevel)
}

func initMinIO() {
    // 加载配置文件
    err := minio.InitMinIO()
    if err != nil {
        log.Fatal("初始化MinIO出错", err)
    }
}

func startHTTPFlv(streamHandler *rtmp.StreamHandler) {
    listen, err := net.Listen("tcp", ":"+strconv.Itoa(config.GetConfig().HttpFlvPort))
    if err != nil {
        log.Fatal(err)
    }

    httpflvServer := httpflv.NewServer(streamHandler)
    go func() {
        defer func() {
            if r := recover(); r != nil {
                log.Error("HTTP-FLV server panic: ", r)
            }
        }()
        log.Info("HTTP-FLV listen On ", listen.Addr())
        _ = httpflvServer.Serve(listen)
    }()
}

func startRtmp(streamHandler *rtmp.StreamHandler) {
    rtmpListen, err := net.Listen("tcp", ":"+strconv.Itoa(config.GetConfig().RtmpPort))
    if err != nil {
        log.Fatal(err)
    }

    rtmpServer := rtmp.NewRtmpServer(streamHandler)

    defer func() {
        if r := recover(); r != nil {
            log.Error("RTMP server panic: ", r)
        }
    }()
    log.Info("RTMP Listen On ", rtmpListen.Addr())
    _ = rtmpServer.Serve(rtmpListen)
}
