package api

import (
    "bytes"
    "io"
    "io/ioutil"
    "mime/multipart"
    "net/http"
    "os"
    "testing"
)

func TestPostPic(t *testing.T) {
    //filename := "/Volumes/Work/Test/upload/DSC02226-3.JPG"
    filename := "/Volumes/Work/Test/upload/wallhaven-268993.jpg"
    targetUrl := "http://127.0.0.1:10003/v1001/api/xiot-res/pic/uav-pic"
    testPostFile(t, filename, targetUrl)
}

func TestPostVideo(t *testing.T) {
    filename := "/Volumes/Work/Test/upload/test-video-5m.mp4"
    targetUrl := "http://127.0.0.1:10003/v1001/api/xiot-res/video/uav-video"
    testPostFile(t, filename, targetUrl)
}


func testPostFile(t *testing.T, filename, targetUrl string) {
    bodyBuf := &bytes.Buffer{}
    bodyWriter := multipart.NewWriter(bodyBuf)
    _ = bodyWriter.WriteField("uavCode", "uav-1")
    //关键的一步操作
    fileWriter, err := bodyWriter.CreateFormFile("file", filename)
    if err != nil {
        t.Fatal("error writing to buffer")
    }

    //打开文件句柄操作
    fh, err := os.Open(filename)
    if err != nil {
        t.Fatal("error opening file")
    }
    defer fh.Close()

    //iocopy
    _, err = io.Copy(fileWriter, fh)
    if err != nil {
        return
    }

    contentType := bodyWriter.FormDataContentType()
    bodyWriter.Close()

    resp, err := http.Post(targetUrl, contentType, bodyBuf)
    if err != nil {
        return
    }
    defer resp.Body.Close()
    respBody, err := ioutil.ReadAll(resp.Body)
    if err != nil {
        t.Fatal(err)
    }
    t.Log(resp.Status, string(respBody))
}

