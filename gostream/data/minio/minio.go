package minio

import (
    "context"
    "fmt"
    "github.com/minio/minio-go/v7"
    "github.com/minio/minio-go/v7/pkg/credentials"
    "gostream/config"
    "io"
    "net/url"
    "time"
)

const BucketName = "default"

const POLICY = "{\"Version\":\"2012-10-17\",\"Statement\":[{\"Effect\":\"Allow\",\"Principal\":{\"AWS\":[\"*\"]},\"Action\":[\"s3:GetBucketLocation\",\"s3:ListBucket\",\"s3:ListBucketMultipartUploads\"],\"Resource\":[\"arn:aws:s3:::%s\"]},{\"Effect\":\"Allow\",\"Principal\":{\"AWS\":[\"*\"]},\"Action\":[\"s3:PutObject\",\"s3:AbortMultipartUpload\",\"s3:DeleteObject\",\"s3:GetObject\",\"s3:ListMultipartUploadParts\"],\"Resource\":[\"arn:aws:s3:::%s/*\"]}]}"

var client *minio.Client

func InitMinIO() (err error) {
    // 初使化minio client对象。
    client, err = minio.New(config.GetConfig().MinIOEndpoint,&minio.Options{
        Creds: credentials.NewStaticV4(config.GetConfig().MinIOAccessKey, config.GetConfig().MinIOSecretKey, ""),
        Secure: false,
    })
    if err != nil {
        return err
    }
    return nil
}

func PutObject(objectName string, reader io.Reader, size int64) error {
    ctx := context.Background()
    // 使用FPutObject上传一个zip文件。
    _, err := client.PutObject(ctx, BucketName, objectName, reader, size, minio.PutObjectOptions{})
    //client.PutObject()
    return err
}

func FPutObject(bucketName, objectName, filePath string) error {
    ctx := context.Background()
    // 检查存储桶是否已经存在。
    exists, err := client.BucketExists(ctx, bucketName)
    if err != nil {
        return err
    }
    if !exists {
        err := client.MakeBucket(ctx, bucketName, minio.MakeBucketOptions{})
        if err != nil {
            return err
        }
        err = SetBucketPolicy(bucketName, "")
        if err != nil {
            return err
        }
    }

    // 使用FPutObject上传一个zip文件。
    _, err = client.FPutObject(ctx, bucketName, objectName, filePath, minio.PutObjectOptions{})
    //client.PutObject()
    return err
}

func GetObjectUrl(objectName string) (*url.URL, error) {
    ctx := context.Background()
    expiry := time.Second * 24 * 60 * 60 // 1 day.
    reqParams := make(url.Values)
    reqParams.Set("response-content-disposition", "attachment; filename=\""+objectName+".mp4\"")
    return client.PresignedGetObject(ctx, BucketName, objectName, expiry, reqParams)
}

func ListObject(bucketName string) ([]minio.ObjectInfo, error) {
    ctx := context.Background()
    // Create a done channel to control 'ListObjects' go routine.
    doneCh := make(chan struct{})

    // Indicate to our routine to exit cleanly upon return.
    defer close(doneCh)

    objectCh := client.ListObjects(ctx, bucketName, minio.ListObjectsOptions{})
    objectArr := make([]minio.ObjectInfo, 0)
    for object := range objectCh {
        if object.Err != nil {
            return nil, object.Err
        }
        objectArr = append(objectArr, object)
    }
    return objectArr, nil
}

func GetBucketPolicy(bucketName string) (string, error) {
    ctx := context.Background()
    return client.GetBucketPolicy(ctx, bucketName)
}

func SetBucketPolicy(bucketName, policy string) error {
    if policy == "" {
        policy = fmt.Sprintf(POLICY, bucketName, bucketName)
    }
    ctx := context.Background()
    return client.SetBucketPolicy(ctx, bucketName, policy)
}
