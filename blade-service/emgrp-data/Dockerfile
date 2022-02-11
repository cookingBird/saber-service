FROM anapsix/alpine-java:8_server-jre_unlimited

MAINTAINER smallchill@163.com

RUN mkdir -p /emgrp/data

RUN mkdir -p /emgrp/ffmpeg

WORKDIR /emgrp/data

EXPOSE 8107

ADD ./target/emgrp-data.jar ./app.jar

ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "app.jar"]

CMD ["--spring.profiles.active=prod"]
