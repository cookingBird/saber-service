#FROM scratch
FROM golang:alpine

MAINTAINER smallchill@163.com

WORKDIR /blade/gostream

EXPOSE 10001
EXPOSE 10002

ADD ./gostream .
#ADD ./test/config.yaml .

ENTRYPOINT ["./gostream"]
