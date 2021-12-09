FROM maven:3.8.1-adoptopenjdk-8 AS builder
WORKDIR /build
COPY ./ /build/
ARG SERVER_DIR
ENV MAVEN_OPTS "-Dmaven.repo.local=/build/.m2"
RUN mvn clean package
RUN cp $SERVER_DIR/target/*.jar /server.jar

FROM adoptopenjdk/openjdk8:jre AS runner
WORKDIR /runner
ENV TZ Asia/Shanghai
ENV SERVER_PORT 80
EXPOSE 80

COPY --from=builder /server.jar /runner/server.jar
RUN chmod +x /runner/run_command.sh

ENTRYPOINT ["/runner/run_command.sh"]
