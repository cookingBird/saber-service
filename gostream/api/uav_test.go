package api

import (
    "gostream/config"
    "testing"
)

func TestGetUav(t *testing.T) {
    err := config.LoadConfig("/Volumes/Work/CVS/svn/emergRescue/branches/server/20200508/code/gostream/")
    if err != nil {
        t.Fatal(err)
    }
    uavId, err := GetUavInfo("uav001000002")
    if err != nil {
        t.Fatal(err)
    }
    t.Logf("%+v", uavId)
}
