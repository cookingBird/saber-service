package api

import (
    "bytes"
    "encoding/json"
    "gostream/config"
    "io"
    "io/ioutil"
    "net/http"
    "os"
    "strconv"
    "testing"
    "time"
)

func TestRecognition(t *testing.T) {
    err := config.LoadConfig("/Volumes/Work/CVS/svn/emergRescue/branches/server/20200508/code/gostream/")
    if err != nil {
        t.Fatal(err)
    }

    req := &RecognitionReq{}
    req.IfComputerPos = "true"
    req.IfMark = "true"
    req.IfPushStream = "true"
    req.TaskID = "rtmp" + strconv.FormatInt(time.Now().UnixNano(), 10)
    req.TaskResources = make([]*Resource, 0)
    req.TaskResources = append(req.TaskResources, &Resource{ResourceID: "11132223231232", ResourceType: TaskTypeStream, ResourceURL: "rtmp://localhost:10002/uav/u001"})
    jsonReq, err := json.Marshal(&req)
    if err != nil {
        t.Fatal(err)
    }
    t.Log(string(jsonReq))
}

func TestRecognitionResp(t *testing.T) {
    // { \"result\": {\"code\":\"0\",\"des\":\"任务提交成功\"}\n }
    respStr := "{\"result\": {\"code\":\"0\",\"des\":\"任务提交成功\"}\n }"
    var resp *BaseResp
    err := json.Unmarshal([]byte(respStr), &resp)
    if err != nil {
        t.Fatal(err)
    }
    t.Logf("%+v", resp)
}

func TestRecognitionResultPic(t *testing.T) {
    reqUrl := "http://127.0.0.1:10003/v1001/api/xiot-res/ai/result"
    req := &RecognitionResult{}
    req.TaskID = "task-iImPyUAxQae71ZSL"
    req.TaskURL = "http://127.0.0.1:80/upload/AI-DSC02226-3.JPG"
    jsonReq, _ := json.Marshal(&req)
    resp, err := http.Post(reqUrl, "application/json;charset=utf-8",
        bytes.NewBuffer(jsonReq))
    if err != nil {
        t.Fatal(err)
    }
    if resp.Body != nil {
        defer resp.Body.Close()
    }
    respData, err := ioutil.ReadAll(resp.Body)
    t.Log(string(respData), err)
}

func TestRecognitionResultVideo(t *testing.T) {
    reqUrl := "http://127.0.0.1:10003/v1001/api/xiot-res/ai/result"
    req := &RecognitionResult{}
    req.TaskID = "task-k_gCIqJpQDSmyn4a"
    req.TaskURL = "http://127.0.0.1:80/upload/AI-test-video-5m.mp4"
    jsonReq, _ := json.Marshal(&req)
    resp, err := http.Post(reqUrl, "application/json;charset=utf-8",
        bytes.NewBuffer(jsonReq))
    if err != nil {
        t.Fatal(err)
    }
    if resp.Body != nil {
        defer resp.Body.Close()
    }
    respData, err := ioutil.ReadAll(resp.Body)
    t.Log(string(respData), err)
}

func TestDownloadFile(t *testing.T) {
    fileUrl := "http://127.0.0.1:10003/v1001/api/xtio-res/pic/download/uav-1-DSC02226-3.JPG"
    resp, err := http.Post(fileUrl, "", nil)
    if err != nil {
        t.Fatal(err)
    }
    f, err := os.OpenFile("/Volumes/Work/Test/ai-1-DSC02226-3.JPG", os.O_WRONLY|os.O_CREATE, 0666)
    if err != nil {
        t.Fatal(err)
        return
    }
    defer f.Close()
    defer resp.Body.Close()
    _, err = io.Copy(f, resp.Body)
    if err != nil {
        t.Fatal(err)
    }
}
