FROM maven:3.8.4-openjdk-17 as builder
WORKDIR /app
COPY ./project/order_service/ /app/order_service/.
RUN mvn -f /app/order_service/pom.xml clean package  \
    -D maven.test.skip=true

FROM bellsoft/liberica-openjre-alpine-musl:17.0.1
WORKDIR /app
COPY --from=builder app/order_service/target/*.jar /app/order_service/*.jar
EXPOSE 7772
ENTRYPOINT ["java", "-jar", "/app/order_service/*.jar"]
