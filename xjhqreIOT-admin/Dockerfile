# 该镜像需要依赖的基础镜像
#FROM openjdk:8-jdk-alpine
FROM frolvlad/alpine-java:jdk8-slim
RUN mkdir -p /home/xjhqreIOT
WORKDIR /home/xjhqreIOT
#captcher 字体包
#RUN set -xe \
# RUN echo -e 'https://mirrors.aliyun.com/alpine/v3.6/main/\nhttps://mirrors.aliyun.com/alpine/v3.6/community/' > /etc/apk/repositories \
#    && apk update \
#    && apk upgrade \
#    && apk --no-cache add ttf-dejavu fontconfig

# 将targer目录下的jar包复制到docker容器/home/springboot目录下面目录下面
ADD ./target/xjhqreIOT-admin-0.0.1-SNAPSHOT.jar /home/xjhqreIOT/xjhqreIOT-admin.jar
EXPOSE 8082

ENV JAVA_OPTS="-Xmx150m -Djasypt.encryptor.password=xjhqre"
# 执行命令
CMD ["java","-jar","/home/xjhqreIOT/xjhqreIOT-admin.jar"]
ENTRYPOINT java ${JAVA_OPTS} -jar /home/xjhqreIOT/xjhqreIOT-admin.jar
