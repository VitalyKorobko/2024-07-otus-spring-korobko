FROM maven:3.8.4-openjdk-17 as builder
WORKDIR /app
COPY ./project/front_service/ /app/front_service/.
RUN mvn -f /app/front_service/pom.xml clean package  \
    -D maven.test.skip=true

FROM bellsoft/liberica-openjre-alpine-musl:17.0.1
WORKDIR /app
COPY --from=builder app/front_service/target/*.jar /app/front_service/*.jar
EXPOSE 7778
ENTRYPOINT ["java", "-jar", "/app/front_service/*.jar"]
