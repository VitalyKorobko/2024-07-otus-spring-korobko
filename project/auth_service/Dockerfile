FROM maven:3.8.4-openjdk-17 as builder
WORKDIR /app
COPY ./project/auth_service/ /app/auth_service/.
RUN mvn -f /app/auth_service/pom.xml clean package  \
    -D maven.test.skip=true

FROM bellsoft/liberica-openjre-alpine-musl:17.0.1
WORKDIR /app
COPY --from=builder app/auth_service/target/*.jar /app/auth_service/*.jar
EXPOSE 7778
ENTRYPOINT ["java", "-jar", "/app/auth_service/*.jar"]
