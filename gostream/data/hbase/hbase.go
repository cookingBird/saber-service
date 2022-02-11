package hbase

import (
    "context"
    "github.com/tsuna/gohbase"
    "github.com/tsuna/gohbase/hrpc"
    "gostream/config"
)

const VideoTable = "blade_video"
const VideoTaskTable = "blade_task_video"

var client gohbase.Client

func InitHBase() {
    client = gohbase.NewClient(config.GetConfig().HBaseUrl)
}

func Put(table string, rowKey []byte, values map[string]map[string][]byte) (err error) {
    putRequest, err := hrpc.NewPut(context.Background(), []byte(table), rowKey, values)
    if err != nil {
        return err
    }
    _, err = client.Put(putRequest)
    return err
}

func Get(table, rowKey string) (*hrpc.Result, error) {
    getStr, err := hrpc.NewGetStr(context.Background(), table, rowKey)
    if err != nil {
        return  nil, err
    }
    return client.Get(getStr)
}

