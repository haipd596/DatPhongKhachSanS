FROM maven:3.9.9-eclipse-temurin-17 AS build

WORKDIR /app

COPY pom.xml .
RUN mvn -q -DskipTests dependency:go-offline

COPY src src

RUN mvn -q package -DskipTests

FROM eclipse-temurin:17-jre-alpine AS extract

WORKDIR /app

COPY --from=build /app/target/rental-management-0.0.1-SNAPSHOT.jar app.jar

RUN java -Djarmode=layertools -jar app.jar extract --destination extracted

FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

ENV TZ=Asia/Ho_Chi_Minh
ENV JAVA_OPTS=""
ENV SPRING_PROFILES_ACTIVE=prod

COPY --from=extract /app/extracted/dependencies/ ./
COPY --from=extract /app/extracted/spring-boot-loader/ ./
COPY --from=extract /app/extracted/snapshot-dependencies/ ./
COPY --from=extract /app/extracted/application/ ./

EXPOSE 8080

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS org.springframework.boot.loader.launch.JarLauncher"]
