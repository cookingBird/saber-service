package minio

import (
    "fmt"
    "gostream/config"
    "testing"
)

func TestListObject(t *testing.T) {
    err := config.LoadConfig("/Volumes/Work/CVS/svn/emergRescue/branches/server/20200508/code/gostream/")
    if err != nil {
        t.Fatal(err)
    }
    err = InitMinIO()
    if err != nil {
        t.Fatal(err)
    }

    arr, err := ListObject("u001")
    if err != nil {
        t.Fatal(err)
    }
    for i, info := range arr {
        fmt.Printf("i:%v, info:%+v\n", i, info)
    }
}

func TestGetObjectUrl(t *testing.T) {
    err := config.LoadConfig("/Volumes/Work/CVS/svn/emergRescue/branches/server/20200508/code/gostream/")
    if err != nil {
        t.Fatal(err)
    }
    err = InitMinIO()
    if err != nil {
        t.Fatal(err)
    }

    t.Log(GetObjectUrl("5527681011700386026-1080p.mp4"))
}

func TestFPutObject(t *testing.T) {
    err := config.LoadConfig("/Volumes/Work/CVS/svn/emergRescue/branches/server/20200508/code/gostream/")
    if err != nil {
        t.Fatal(err)
    }
    err = InitMinIO()
    if err != nil {
        t.Fatal(err)
    }
    t.Log(FPutObject("u002", "20201020/wrjzb.gif", "/Volumes/Work/Temp/无人机直播.gif"))
}


func TestGetBucketPolicy(t *testing.T) {
    err := config.LoadConfig("/Volumes/Work/CVS/svn/emergRescue/branches/server/20200508/code/gostream/")
    if err != nil {
        t.Fatal(err)
    }
    err = InitMinIO()
    if err != nil {
        t.Fatal(err)
    }
    t.Log(SetBucketPolicy("u001", ""))
    s, err := GetBucketPolicy("u001")
    t.Log(s)
    t.Log(err)
}
