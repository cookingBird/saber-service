FROM anapsix/alpine-java:8_server-jre_unlimited

MAINTAINER smallchill@163.com

RUN mkdir -p /blade/person

WORKDIR /blade/person

EXPOSE 8105

ADD ./target/emgrp-person.jar ./app.jar

ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "app.jar"]

CMD ["--spring.profiles.active=prod"]
