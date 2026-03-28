FROM maven:3.9.9-eclipse-temurin-17 AS build

WORKDIR /app

COPY pom.xml .
COPY .mvn .mvn
COPY mvnw .
COPY mvnw.cmd .

RUN chmod +x mvnw
RUN ./mvnw -q -DskipTests dependency:go-offline

COPY src src

RUN ./mvnw -q clean package -DskipTests

FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

ENV TZ=Asia/Ho_Chi_Minh
ENV JAVA_OPTS=""

COPY --from=build /app/target/rental-management-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
