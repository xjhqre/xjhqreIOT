# 该镜像需要依赖的基础镜像
FROM openjdk:8-jdk-alpine
RUN mkdir -p /home/xjhqreIOT
WORKDIR /home/xjhqreIOT
#captcher 字体包
RUN set -xe \
&& apk --no-cache add ttf-dejavu fontconfig
# 将targer目录下的jar包复制到docker容器/home/springboot目录下面目录下面
ADD ./target/xjhqreIOT-admin-0.0.1-SNAPSHOT.jar /home/xjhqreIOT/xjhqreIOT-admin.jar
EXPOSE 8080

ENV JAVA_OPTS="-Xmx150m -Djasypt.encryptor.password=xjhqre"
# 执行命令
CMD ["java","-jar","/home/xjhqreIOT/xjhqreIOT-admin.jar"]
ENTRYPOINT java ${JAVA_OPTS} -jar /home/xjhqreIOT/xjhqreIOT-admin.jar
