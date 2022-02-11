package ffmpeg

import (
    "fmt"
    "gostream/config"
    "os"
    "os/exec"
    "path"
    "regexp"
    "strconv"
    "strings"
)

type Info struct {
    Duration   int
    Size       int64
    VideoCodec string
    Fps        string
    AudioCodec string
    Samplerate string
    Bitrate    string
    CreateTime string
}

// ffmpeg -i CH0-041-5m.mp4 -vf scale=-1920:1080 ./CH0-041-5m-1080p.mp4
// 根据执行路径获取进程PID
func ConvertResolutionFile(filePath string) (string, string, string, error) {
    dir, fileName := path.Split(filePath)
    ext := path.Ext(fileName)
    fileNameBase := fileName[:strings.LastIndex(fileName, ext)]
    file1080p := dir + fileNameBase + "-1080p.mp4"
    file720p := dir + fileNameBase + "-720p.mp4"
    file480p := dir + fileNameBase + "-480p.mp4"
    command := config.GetConfig().FfmpegPath + " -i " + filePath + " -s 1920*1080 -aspect 16:9 " + file1080p + " -s 1280*720 -aspect 16:9 " + file720p + " -s 854*480 -aspect 16:9 " + file480p + " -y"
    _, err := exec.Command("/bin/sh", "-c", command).Output()
    return file1080p, file720p, file480p, err
}

// ffmpeg -i rtmp://localhost:10002/uav/u001 -max_muxing_queue_size 1024 -vf scale=1280:720,setdar='r=16/9' -c:v libx264 -f flv rtmp://localhost:10002/uav720p/u001 -max_muxing_queue_size 1024 -vf scale=854:480,setdar='r=16/9' -c:v libx264 -f flv rtmp://localhost:10002/uav480p/u001
// 根据执行路径获取进程PID
func ConvertResolutionRtmp(rtmpUrl string) ([]byte, error) {
    rtmp720p := strings.ReplaceAll(rtmpUrl, "/uav/", "/uav720p/")
    rtmp480p := strings.ReplaceAll(rtmpUrl, "/uav/", "/uav480p/")

    command := config.GetConfig().FfmpegPath + " -i " + rtmpUrl + " -max_muxing_queue_size 1024 -s 1280*720 -aspect 16:9 -c:v libx264 -f flv " +
        rtmp720p + " -max_muxing_queue_size 1024 -s 854*480 -aspect 16:9 -c:v libx264 -f flv " + rtmp480p
    return exec.Command("/bin/sh", "-c", command).CombinedOutput()
}

var durationReg = regexp.MustCompile("Duration: (\\d{2}):(\\d{2}):(\\d{2})\\.(\\d{2}), start: .*?, bitrate: (\\d*) kb\\/s")
var videoReg = regexp.MustCompile("Video: (.*?),.*, (.*? fps),.*[\n\r]")
var audioReg = regexp.MustCompile("Audio: (.*?),.* (\\d*) Hz,.*")
var timeReg = regexp.MustCompile("creation_time *?: (.*?)\\.\\d{6}Z")

func GetInfo(filePath string) (*Info, error) {
    fileInfo, err := os.Stat(filePath)
    if err != nil {
        return nil, err
    }
    info := &Info{}
    info.Size = fileInfo.Size()
    command := config.GetConfig().FfmpegPath + " -i " + filePath
    result, err := exec.Command("/bin/sh", "-c", command).CombinedOutput()
    resultStr := string(result)
    if resultStr == "" {
        return info, fmt.Errorf("ffmpeg -i fail:%v", err)
    }
    match := durationReg.FindStringSubmatch(resultStr)
    if len(match) > 1 {
        hour, _ := strconv.Atoi(match[1])
        min, _ := strconv.Atoi(match[2])
        sec, _ := strconv.Atoi(match[3])
        t, _ := strconv.Atoi(match[4])
        duration := hour*60*60 + min*60 + sec
        if t >= 50 {
            duration++
        }
        info.Duration = duration
        info.Bitrate = match[5]
    }
    videoMatch := videoReg.FindStringSubmatch(resultStr)
    if len(videoMatch) > 1 {
        info.VideoCodec = strings.TrimSpace(videoMatch[1])
        info.Fps = strings.TrimSpace(videoMatch[2])
    }

    audioMatch := audioReg.FindStringSubmatch(resultStr)
    if len(audioMatch) > 1 {
        info.AudioCodec = strings.TrimSpace(audioMatch[1])
        info.Samplerate = strings.TrimSpace(videoMatch[2])
    }
    timeMatch := timeReg.FindStringSubmatch(resultStr)
    if len(timeMatch) > 1 {
        info.CreateTime = strings.TrimSpace(timeMatch[1])
    }
    return info, nil
}

// ffmpeg -ss 0.1 -t 0.001 -i 1.mp4 -y -f image2 -frames:v 1 0.jpg
func Screenshot(filePath string) (string, error) {
    dir, fileName := path.Split(filePath)
    ext := path.Ext(fileName)
    fileNameBase := fileName[:strings.LastIndex(fileName, ext)]
    picFile := dir + fileNameBase + ".jpg"
    command := config.GetConfig().FfmpegPath + " -ss 0.1 -t 0.001 -i " + filePath + " -y -f image2 -frames:v 1 " + picFile
    _, err := exec.Command("/bin/sh", "-c", command).Output()
    return picFile, err
}
