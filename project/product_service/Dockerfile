FROM maven:3.8.4-openjdk-17 as builder
WORKDIR /app
COPY ./project/product_service/ /app/product_service/.
RUN mvn -f /app/product_service/pom.xml clean package  \
    -D maven.test.skip=true

FROM bellsoft/liberica-openjre-alpine-musl:17.0.1
WORKDIR /app
COPY --from=builder app/product_service/target/*.jar /app/product_service/*.jar
EXPOSE 7773
ENTRYPOINT ["java", "-jar", "/app/product_service/*.jar"]
