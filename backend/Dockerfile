FROM eclipse-temurin:17-jdk AS build

WORKDIR /app

COPY . .

# jvm.config 內是 Windows 專用的 trustStore 設定，Linux 容器不適用
RUN rm -f .mvn/jvm.config && ./mvnw clean package -DskipTests


FROM eclipse-temurin:17-jre

WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]