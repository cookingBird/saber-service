FROM anapsix/alpine-java:8_server-jre_unlimited

MAINTAINER smallchill@163.com

RUN mkdir -p /blade/task

WORKDIR /blade/task

EXPOSE 8222

ADD ./target/emgrp-task.jar ./app.jar

ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "app.jar"]

CMD ["--spring.profiles.active=prod"]
