FROM maven:3.8.4-openjdk-17 as builder
WORKDIR /app
COPY ./project/storage_service/ /app/storage_service/.
RUN mvn -f /app/storage_service/pom.xml clean package  \
    -D maven.test.skip=true

FROM bellsoft/liberica-openjre-alpine-musl:17.0.1
WORKDIR /app
COPY --from=builder app/storage_service/target/*.jar /app/storage_service/*.jar
EXPOSE 7774
ENTRYPOINT ["java", "-jar", "/app/storage_service/*.jar"]
