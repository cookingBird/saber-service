FROM bladex/alpine-java:8_server-jre_cn_unlimited

MAINTAINER smallchill@163.com

RUN mkdir -p /blade/flow

WORKDIR /blade/flow

EXPOSE 8008

ADD ./target/blade-flow.jar ./app.jar

ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "app.jar"]

CMD ["--spring.profiles.active=prod"]
