FROM anapsix/alpine-java:8_server-jre_unlimited

MAINTAINER smallchill@163.com

RUN mkdir -p /emgrp/play

WORKDIR /emgrp/play

EXPOSE 8108

ADD ./target/emgrp-play.jar ./app.jar

ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "app.jar"]

CMD ["--spring.profiles.active=prod"]
