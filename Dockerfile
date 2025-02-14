FROM openjdk8-alpine
COPY /start.sh start.sh
COPY /build/libs/*.jar app.jar
LABEL maintainer="pengqd <pqdongo@163.com>"
ENTRYPOINT ["sh", "start.sh"]