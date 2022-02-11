// 只是用来与AI子系统联调
package api

import (
    "encoding/json"
    log "github.com/sirupsen/logrus"
    "gostream/config"
    "gostream/utils/uuid"
    "io"
    "io/ioutil"
    "net/http"
    "os"
    "path"
    "strconv"
    "strings"
    "sync"
)

var staticHandler http.Handler

var taskMap sync.Map


func StartServer() {
    staticHandler = http.StripPrefix("/v1001/api/xtio-res/pic/download/", http.FileServer(http.Dir(config.GetConfig().FileDir)))
    srvMux := http.NewServeMux()
    //case "/v1001/api/xtio-res/pic/download":
    //    w.Header().Add("Access-Control-Allow-Origin", "*")
    //    staticHandler.ServeHTTP(w, req)
    //    case "/v1001/api/xtio-res/video/download":
    //    w.Header().Add("Access-Control-Allow-Origin", "*")
    //    staticHandler.ServeHTTP(w, req)
    srvMux.HandleFunc("/v1001/api/xtio-res/pic/download/", StaticServer)
    srvMux.HandleFunc("/v1001/api/xtio-res/video/download/", StaticServer)
    srvMux.HandleFunc("/", httpHandle)

    log.Info("启动AI API Server on ", 10003)
    err := http.ListenAndServe(":"+strconv.Itoa(10003), srvMux)
    if err != nil {
        log.Error("API server err:", err)
    }
}

// 静态文件处理
func StaticServer(w http.ResponseWriter, req *http.Request) {
    if req.URL.Path != "/" {
        w.Header().Add("Access-Control-Allow-Origin", "*")
        staticHandler.ServeHTTP(w, req)
        return
    }
}

func httpHandle(w http.ResponseWriter, req *http.Request) {
    log.Debug(req.URL.Path)
    if strings.Compare(req.Method, http.MethodPost) != 0 {
        log.Info("服务器只支持POST方式! RemoteAddr:", req.RemoteAddr)
        return
    } else {
        switch req.URL.Path {
        case "/v1001/api/xiot-res/pic/uav-pic":
            uavPic(w, req)
        case "/v1001/api/xiot-res/video/uav-video":
            uavVideo(w, req)
        //case "/v1001/api/xiot-res/ai/result":
        case "/api/result":
            aiResult(w, req)
        case "/v1001/api/xtio-res/ai/taskList":
            arr := make([]*RecognitionReq, 0)
            taskMap.Range(func(key, value interface{}) bool {
                arr = append(arr, value.(*RecognitionReq))
                return true
            })
            data, _ := json.Marshal(arr)
            w.Write(data)
        default:
            log.Infof("非法请求，不支持的路径! RemoteAddr:%v, Path:%v", req.RemoteAddr, req.URL.Path)
            return
        }
    }
}

func uavPic(w http.ResponseWriter, req *http.Request) {
    log.Debug("uavPic")
    _ = req.ParseMultipartForm(32 << 20)
    uavCode := req.Form.Get("uavCode")
    file, fileHeader, err := req.FormFile("file")
    if err != nil {
        log.Error("form file err", err)
        return
    }
    defer file.Close()
    // 创建文件夹
    err = os.MkdirAll(config.GetConfig().FileDir, os.ModePerm)
    if err != nil {
        log.Error("mkdir error: ", err)
        return
    }
    // TODO 保存到MinIO
    _, fileName := path.Split(fileHeader.Filename)
    f, err := os.OpenFile(path.Join(config.GetConfig().FileDir, uavCode+"-"+fileName), os.O_WRONLY|os.O_CREATE, 0666)
    if err != nil {
        log.Error("open file error: ", err)
        return
    }
    defer f.Close()
    _, _ = io.Copy(f, file)
    recognitionReq := &RecognitionReq{}
    recognitionReq.TaskID = "task-" + uuid.NewId()
    //recognitionReq.TaskType = taskTypePic
    //recognitionReq.TaskURL = "http://192.168.0.108:10003/v1001/api/xtio-res/pic/download/" + uavCode + "-" + fileName
    recognitionReq.IfMark = "true"
    recognitionReq.IfPushStream = "true"
    recognitionReq.IfComputerPos = "true"
    taskMap.Store(recognitionReq.TaskID, recognitionReq)
    log.Debug("保存完毕", uavCode+"-"+fileName)
    //go Recognition(config.GetConfig().AiTaskUrl, recognitionReq)
    log.Debug("返回")
}

func uavVideo(w http.ResponseWriter, req *http.Request) {
    _ = req.ParseMultipartForm(32 << 20)
    uavCode := req.Form.Get("uavCode")
    file, fileHeader, err := req.FormFile("file")
    if err != nil {
        log.Error("form file err", err)
        return
    }
    defer file.Close()
    // 创建文件夹
    err = os.MkdirAll(config.GetConfig().FileDir, os.ModePerm)
    if err != nil {
        log.Error("mkdir error: ", err)
        return
    }
    // TODO 保存到MinIO
    _, fileName := path.Split(fileHeader.Filename)
    f, err := os.OpenFile(path.Join(config.GetConfig().FileDir, uavCode+"-"+fileName), os.O_WRONLY|os.O_CREATE, 0666)
    if err != nil {
        log.Error("open file error: ", err)
        return
    }
    defer f.Close()
    _, _ = io.Copy(f, file)
    recognitionReq := &RecognitionReq{}
    recognitionReq.TaskID = "task-" + uuid.NewId()
    //recognitionReq.TaskType = taskTypeVideo
    //recognitionReq.TaskURL = req.Proto + req.Host + "/v1001/api/xtio-res/pic/download/" + uavCode + "-" + fileName
    recognitionReq.IfMark = "true"
    recognitionReq.IfPushStream = "true"
    recognitionReq.IfComputerPos = "true"
    taskMap.Store(recognitionReq.TaskID, recognitionReq)
    //go Recognition(config.GetConfig().AiTaskUrl, recognitionReq)
}

func aiResult(w http.ResponseWriter, req *http.Request) {
    if req.Body != nil {
        defer req.Body.Close()
    }
    data, err := ioutil.ReadAll(req.Body)
    if err != nil {
        log.Error("read body err", err)
        return
    }
    var result RecognitionResult
    err = json.Unmarshal(data, &result)
    if err != nil {
        log.Error("parse json err", err)
        return
    }
    log.Debugf("%+v\n", result)

    if result.TaskURL != "" {
        downloadFile(result.TaskURL) // TODO 错误的时候重新下发任务
        taskMap.Delete(result.TaskID)
    }
}

func downloadFile(fileUrl string) {

    // Get the data
    resp, err := http.Post(fileUrl, "", nil)
    if err != nil {
        log.Error("downloadFile err", err)
        return
    }
    defer resp.Body.Close()
    // 创建文件夹
    err1 := os.MkdirAll(config.GetConfig().FileDir, os.ModePerm)
    if err1 != nil {
        log.Error("mkdir err: ", err)
        return
    }

    data, err := ioutil.ReadAll(resp.Body)
    if err != nil {
        log.Error("downloadFile err", err)
        return
    }
    _, fileName := path.Split(fileUrl)
    _ = ioutil.WriteFile(path.Join(config.GetConfig().FileDir, fileName), data, os.ModePerm)
}
