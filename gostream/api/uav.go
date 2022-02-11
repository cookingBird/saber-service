package api

import (
    "crypto/md5"
    "encoding/base64"
    "encoding/hex"
    "encoding/json"
    "fmt"
    "gostream/config"
    "io/ioutil"
    "net/http"
    "net/url"
    "strings"
)

const (
    grantType = "password"
    scope     = "all"
)

var currToken *tokenResp

type tokenResp struct {
    AccessToken string `json:"access_token"`
    TokenType   string `json:"token_type"`
    Error       string `json:"error"`
    ErrorDesc   string `json:"error_description"`
}

type uavDevInfoResp struct {
    Code    int         `json:"code"`
    Success bool        `json:"success"`
    Msg     string      `json:"msg"`
    Data    *UavDevInfo `json:"data"`
}

type UavDevInfo struct {
    UavId   int64 `json:"uavId"`
    EventId int64 `json:"eventId"`
    TaskId  int64 `json:"worktaskid"`
    UserId  int64 `json:"createUser"`
}

func GetToken() error {
    reqData := url.Values{}
    reqData["grant_type"] = []string{grantType}
    reqData["scope"] = []string{scope}
    reqData["tenant_id"] = []string{config.GetConfig().TenantId}
    reqData["client_id"] = []string{config.GetConfig().ClientId}
    reqData["client_secret"] = []string{config.GetConfig().ClientSecret}
    reqData["username"] = []string{config.GetConfig().Username}
    reqData["password"] = []string{Md5Encode(config.GetConfig().Password)}
    ////t.Log(reqData.Encode())
    //http.PostForm()
    req, err := http.NewRequest(http.MethodPost, config.GetConfig().OAuthUrl, strings.NewReader(reqData.Encode()))
    if err != nil {
        return err
    }
    req.Header.Set("Content-Type", "application/x-www-form-urlencoded")
    req.Header.Set("Authorization", "Basic "+base64.StdEncoding.EncodeToString(
        []byte(config.GetConfig().ClientId+":"+config.GetConfig().ClientSecret)))
    resp, err := http.DefaultClient.Do(req)
    if err != nil {
        return err
    }
    if resp.Body != nil {
        defer resp.Body.Close()
    }
    respData, err := ioutil.ReadAll(resp.Body)
    if err != nil {
        return err
    }
    var tk tokenResp
    err = json.Unmarshal(respData, &tk)
    if err != nil {
        return err
    }
    if tk.Error != "" {
        return fmt.Errorf(tk.ErrorDesc)
    }
    currToken = &tk
    return nil
}

func GetUavInfo(devCode string) (*UavDevInfo, error) {
    if currToken == nil {
        err := GetToken()
        if err != nil {
            return nil, err
        }
    }

    getUavUrl := config.GetConfig().GetUavUrl + "?uavCode=" + devCode
    req, err := http.NewRequest(http.MethodGet, getUavUrl, nil)
    if err != nil {
        return nil, err
    }
    req.Header["Blade-Auth"] = []string{currToken.TokenType + " " + currToken.AccessToken}
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
        req.Header["Blade-Auth"] = []string{currToken.TokenType + " " + currToken.AccessToken}
        resp, err = http.DefaultClient.Do(req)
        if err != nil {
            return nil, err
        }
    }
    respData, err := ioutil.ReadAll(resp.Body)
    if err != nil {
        return nil, err
    }
    var uavResp uavDevInfoResp
    err = json.Unmarshal(respData, &uavResp)
    if err != nil {
        return nil, err
    }
    if !uavResp.Success {
        return nil, fmt.Errorf(uavResp.Msg)
    }
    return uavResp.Data, nil
}

func Md5Encode(str string) string {
    h := md5.New()
    h.Write([]byte(str)) // 需要加密的字符串为 123456
    cipherStr := h.Sum(nil)
    return hex.EncodeToString(cipherStr)
}
