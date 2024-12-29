FROM maven:3.8.4-openjdk-17 as builder
WORKDIR /books-app
COPY . /books-app/.
RUN mvn -f /books-app/pom.xml clean package  \
    -D maven.test.skip=false

FROM bellsoft/liberica-openjre-alpine-musl:17.0.1
WORKDIR /books-app
COPY --from=builder /books-app/target/*.jar /books-app/*.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/books-app/*.jar"]
