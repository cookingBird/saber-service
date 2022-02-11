package config

import (
    "fmt"
    "github.com/spf13/viper"
    "strings"
)

var config *Config

type Config struct {
    LogLevel       string
    OAuthUrl       string
    ClientId       string
    ClientSecret   string
    TenantId       string
    Username       string
    Password       string
    GetUavUrl      string
    MinIOEndpoint  string
    MinIOAccessKey string
    MinIOSecretKey string
    HBaseUrl       string
    FfmpegPath     string
    LiveNotifyUrl  string
    HttpFlvPort    int
    RtmpPort       int
    ReadTimeout    int
    WriteTimeout   int
    AliveTimeout   int
    CachePacketNum int
    FileDir        string
    Apps           []*App
}

type App struct {
    Name            string
    PushUrls        []string
    LiveTranscoding bool
}

// 加载配置文件
func LoadConfig(path string) error {
    v := viper.New()
    v.SetConfigName("config")
    v.SetConfigType("yaml")
    v.AddConfigPath(path)
    if err := v.ReadInConfig(); err != nil {
        return err
    }
    if err := v.Unmarshal(&config); err != nil {
        return err
    }
    return nil
}

func GetConfig() *Config {
    return config
}

func (config *Config) String() string {
    appDescs := make([]string, 0)
    for _, app := range config.Apps {
        appDescs = append(appDescs, fmt.Sprintf("{Name:%s, PushUrls:%v}", app.Name, app.PushUrls))
    }
    return fmt.Sprintf("LogLevel:%s, HttpFlvPort:%v, RtmpPort:%v, Apps:[%s]",
        config.LogLevel, config.HttpFlvPort, config.RtmpPort, strings.Join(appDescs, ","))
}
