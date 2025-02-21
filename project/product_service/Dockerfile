FROM maven:3.8.4-openjdk-17 as builder
WORKDIR /project-app
COPY ../book_service /project-app/.
RUN mvn -f /project-app/pom.xml clean package  \
    -D maven.test.skip=false

FROM bellsoft/liberica-openjre-alpine-musl:17.0.1
WORKDIR /project-app
COPY --from=builder /project-app/target/*.jar /project-app/*.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/project-app/*.jar"]
