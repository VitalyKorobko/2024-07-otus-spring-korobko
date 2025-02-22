FROM maven:3.8.4-openjdk-17 as builder
WORKDIR /app
COPY ./project/mail_service/ /app/mail_service/.
RUN mvn -f /app/mail_service/pom.xml clean package  \
    -D maven.test.skip=true

FROM bellsoft/liberica-openjre-alpine-musl:17.0.1
WORKDIR /app
COPY --from=builder app/mail_service/mail_client/target/*.jar /app/mail_service/mail_client/*.jar
COPY --from=builder app/mail_service/mail_processor/target/*.jar /app/mail_service/mail_processor/*.jar
COPY --from=builder app/mail_service/send_mail_service/target/*.jar /app/mail_service/send_mail_service/*.jar
COPY --from=builder app/mail_service/common/target/*.jar /app/mail_service/common/*.jar
EXPOSE 7775
ENTRYPOINT ["java", "-jar", "/app/mail_service/mail_client/*.jar"]
