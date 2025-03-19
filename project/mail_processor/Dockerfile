FROM maven:3.9.1-amazoncorretto-19-debian-bullseye as builder
WORKDIR /app
COPY ./project/mail_processor/ /app/mail_processor/.
RUN mvn -f /app/mail_processor/pom.xml clean package  \
    -D maven.test.skip=true

FROM bellsoft/liberica-openjre-alpine-musl:19.0.1
WORKDIR /app
COPY --from=builder app/mail_processor/target/*.jar /app/mail_processor/*.jar
EXPOSE 7776
ENTRYPOINT ["java", "-jar", "/app/mail_processor/*.jar"]
