package ffmpeg

import (
    "gostream/config"
    "testing"
)

func TestConvertResolution(t *testing.T) {
    file1, file2, file3, err := ConvertResolutionFile("/Volumes/Work/test/upload/video/CH0-041-1m.mp4")
    if err != nil {
        t.Fatal(err)
    }
    t.Log(file1, file2, file3)
}
func TestScreenshot(t *testing.T) {
    err := config.LoadConfig("/Volumes/Work/CVS/svn/emergRescue/branches/server/20200508/code/gostream/")
    if err != nil {
        t.Fatal(err)
    }
    picFile, err := Screenshot("/Volumes/Work/test/12345678_1595574846-1m.flv")
    if err != nil {
        t.Fatal(err)
    }
    t.Log(picFile)
}

func TestGetInfo(t *testing.T) {
    err := config.LoadConfig("/Volumes/Work/CVS/svn/emergRescue/branches/server/20200508/code/gostream/")
    if err != nil {
        t.Fatal(err)
    }
    arr := []string {
        //"/Volumes/Work/server/gostream/gostream1/tmp/uav/u001_1594108431.flv",
        "/Volumes/Work/test/upload/video/CH0-041-1m-1080p.mp4",
        "/Volumes/Work/test/1.4G_1080p.mp4",
        "/Volumes/Work/test/test-5m-640x360.mp4",
        "/Volumes/Work/test/test-5m.flv",
        "/Volumes/Work/test/test.flv",
        "/Volumes/Work/test/The.Blacklist.S07E01.HDTV.x264-SVA.mkv",
    }
    for _, fp := range arr {
        info, err := GetInfo(fp)
        if err != nil {
            t.Fatal(err)
        }
        t.Logf("%+v\n", info)
    }
}
