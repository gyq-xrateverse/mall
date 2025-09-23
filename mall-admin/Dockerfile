# 该镜像需要依赖的基础镜像
FROM openjdk:17-jre-alpine

# 维护者信息
MAINTAINER macro

# 创建应用目录
RUN mkdir -p /app/logs

# 设置工作目录
WORKDIR /app

# 将jar包复制到容器中
COPY target/mall-admin-*.jar app.jar

# 声明服务运行在8080端口
EXPOSE 8080

# 设置时区
RUN apk add --no-cache tzdata
ENV TZ=Asia/Shanghai

# 启动命令
ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-Dspring.profiles.active=${SPRING_PROFILES_ACTIVE:-dev}", "-jar", "app.jar"]