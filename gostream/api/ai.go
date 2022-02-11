package api

import (
    "bytes"
    "encoding/json"
    log "github.com/sirupsen/logrus"
    "gostream/config"
    "io/ioutil"
    "net/http"
)

const (
    taskTypePic    = "1"
    taskTypeVideo  = "2"
    TaskTypeStream = "3"

    NotifyStart = 1
    NotifyEnd   = 0
)

type LiveNotifyReq struct {
    UavCode string `json:"uavCode"`
    Url     string `json:"url"`
    IsStart string `json:"isStart"` // 1:开始，0：结束
}

func LiveNotify(reqUrl string, reqObj *LiveNotifyReq) (*BaseResp, error) {
    log.Debugf("LiveNotify reqUrl:%s, req %+v\n", reqUrl, reqObj)
    if currToken == nil {
        err := GetToken()
        if err != nil {
            return nil, err
        }
    }
    jsonReq, _ := json.Marshal(&reqObj)
    req, err := http.NewRequest(http.MethodPost, reqUrl, bytes.NewBuffer(jsonReq))
    if err != nil {
        return nil, err
    }
    req.Header.Set("Content-Type", "application/json;charset=utf-8")
    req.Header.Set("Blade-Auth", currToken.TokenType+" "+currToken.AccessToken)
    resp, err := http.DefaultClient.Do(req)
    if err != nil {
        return nil, err
    }
    if resp.Body != nil {
        defer resp.Body.Close()
    }
    if resp.StatusCode == 401 {
        err := GetToken()
        if err != nil {
            return nil, err
        }
        req.Header.Set("Blade-Auth", currToken.TokenType+" "+currToken.AccessToken)
        resp, err = http.DefaultClient.Do(req)
        if err != nil {
            return nil, err
        }
    }
    respData, err := ioutil.ReadAll(resp.Body)
    var respJson *BaseResp
    err = json.Unmarshal(respData, &respJson)
    return respJson, err
}

func SendLiveNotify(uavCode, rtmpURL, isStart string) {
    req := &LiveNotifyReq{}
    req.UavCode = uavCode
    req.Url = rtmpURL
    req.IsStart = isStart
    resp, err := LiveNotify(config.GetConfig().LiveNotifyUrl, req)
    if err != nil {
        log.Errorf("直播通知接口调用失败, uavCode:%s, err:%v", uavCode, err)
        return
    }
    log.Infof("直播通知，uavCode:%s, 响应:%+v", uavCode, resp)
}

// AI识别请求
type RecognitionReq struct {
    TaskID        string      `json:"taskID"`        // 任务ID
    IfMark        string      `json:"ifMark"`        // 是否返回加标注框及中文，0：否；1：是
    IfPushStream  string      `json:"ifPushStream"`  // 是否推流，0：否；1：是
    IfComputerPos string      `json:"ifComputerPos"` // 是否计算位置，0：否；1：是
    TaskResources []*Resource `json:"taskResources"`
}

type BaseResp struct {
    Code    int    `json:"code"`
    Success bool   `json:"success"`
    Msg     string `json:"msg"`
}

type BaseRespRespResult struct {
    Code string `json:"code"`
    Des  string `json:"des"`
}

type Resource struct {
    ResourceID         string `json:"resourceID"`
    ResourceURL        string `json:"resourceURL"`
    ResourceType       string `json:"resourceType"`       // 1:图片；2：视频文件；3：rtsp流
    CameralFocalLength string `json:"cameralFocalLength"` // 相机焦距
    PixLengthX         string `json:"pixLengthX"`         // x轴单位像素长度mm/pix
    PixLengthY         string `json:"pixLengthY"`         // y轴单位像素长度mm/pix
    CameralPitchAngle  string `json:"cameralPitchAngle"`  // 俯仰角
    CameralYawAngle    string `json:"cameralYawAngle"`    // 偏航角
    CameralRollAngle   string `json:"cameralRollAngle"`   // 滚动角
    UavHeight          string `json:"uavHeight"`          // 无人机距地面高度
    UavLongitude       string `json:"uavLongitude"`       // 经度
    UavLatitude        string `json:"uavLatitude"`        // 纬度
}

// 识别结果
type RecognitionResult struct {
    TaskID         string                     `json:"taskID"`         // 任务ID
    TaskURL        string                     `json:"taskURL"`        // 任务返回地址（图片保存地址等）
    MediaStreamURL string                     `json:"mediaStreamUrl"` // 如http://192.168.0.10:8090
    Object         []*RecognitionResultDetail `json:"object"`         // 对象数组
    PersonCount    string                     `json:"personCount"`    // 人数
    HouseArea      string                     `json:"houseArea"`      // 受损房屋面积
    RoadCount      string                     `json:"roadCount"`      // 受损道路点数
}

type RecognitionResultDetail struct {
    ObjectType      string `json:"objectType"`      // 1:人；2：损毁房屋；3：损毁道路；
    ObjectLongitude string `json:"objectLongitude"` // 目标经度
    ObjectLatitude  string `json:"objectLatitude"`  // 目标纬度
}

func Recognition(reqUrl string, req *RecognitionReq) (*BaseResp, error) {
    log.Debugf("Recognition reqUrl:%s, req %+v\n", reqUrl, req)
    jsonReq, _ := json.Marshal(&req)
    resp, err := http.Post(reqUrl, "application/json;charset=utf-8",
        bytes.NewBuffer(jsonReq))
    if err != nil {
        return nil, err
    }
    if resp.Body != nil {
        defer resp.Body.Close()
    }
    respData, err := ioutil.ReadAll(resp.Body)
    var respJson *BaseResp
    err = json.Unmarshal(respData, &respJson)
    return respJson, err
}
