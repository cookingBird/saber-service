FROM anapsix/alpine-java:8_server-jre_unlimited

MAINTAINER smallchill@163.com

RUN mkdir -p /blade/uav

WORKDIR /blade/uav

EXPOSE 8333

ADD ./target/emgrp-uav.jar ./app.jar

ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "app.jar"]

CMD ["--spring.profiles.active=prod"]
