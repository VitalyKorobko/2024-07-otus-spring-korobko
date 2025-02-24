FROM maven:3.9.1-amazoncorretto-19-debian-bullseye as builder
WORKDIR /app
COPY ./project/send_mail_service/ /app/send_mail_service/.
RUN mvn -f /app/send_mail_service/pom.xml clean package  \
    -D maven.test.skip=true

FROM bellsoft/liberica-openjre-alpine-musl:19.0.1
WORKDIR /app
COPY --from=builder app/send_mail_service/target/*.jar /app/send_mail_service/*.jar
EXPOSE 7777
ENTRYPOINT ["java", "-jar", "/app/send_mail_service/*.jar"]

