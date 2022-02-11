package httpflv

import (
    "gostream/protocol/live/rtmp"
    log "github.com/sirupsen/logrus"
    "gostream/protocol"
    "net"
    "net/http"
    "strings"
)

type Server struct {
    handler protocol.Handler
}

func NewServer(h protocol.Handler) *Server {
    return &Server{
        handler: h,
    }
}

func (server *Server) Serve(l net.Listener) error {
    mux := http.NewServeMux()
    mux.HandleFunc("/", func(w http.ResponseWriter, r *http.Request) {
        server.handleConn(w, r)
    })
    return http.Serve(l, mux)
}

func (server *Server) handleConn(w http.ResponseWriter, r *http.Request) {
    defer func() {
        if r := recover(); r != nil {
            log.Error("http flv handleConn panic: ", r)
        }
    }()

    url := r.URL.String()
    u := r.URL.Path
    if pos := strings.LastIndex(u, "."); pos < 0 || u[pos:] != ".flv" {
        http.Error(w, "invalid path", http.StatusBadRequest)
        return
    }
    path := strings.TrimSuffix(strings.TrimLeft(u, "/"), ".flv")
    paths := strings.SplitN(path, "/", 2)
    log.Debug("url:", u, " path:", path, " paths:", paths)

    if len(paths) != 2 {
        http.Error(w, "invalid path", http.StatusBadRequest)
        return
    }

    // 判断视屏流是否发布,如果没有发布,直接返回404
    if ret := server.validPath(path); !ret {
        http.Error(w, "invalid path", http.StatusNotFound)
        return
    }

    w.Header().Set("Access-Control-Allow-Origin", "*")
    writer := NewFLVWriter(paths[0], paths[1], url, w)

    server.handler.HandleWriter(writer)
    writer.Wait()
}


// 验证路径是否有发布者
func (server *Server) validPath(path string) bool {
    streamHandler := server.handler.(*rtmp.StreamHandler)
    if streamHandler == nil {
        return false
    }
    for item := range streamHandler.GetStreams().IterBuffered() {
        if stream, ok := item.Val.(*rtmp.Stream); ok {
            if stream.GetReader() != nil && item.Key == path {
                return true
            }
        }
    }
    return false
}
