FROM node:22-alpine AS frontend-build

WORKDIR /frontend

COPY frontend/package.json frontend/package-lock.json ./
RUN npm ci

COPY frontend/ ./
RUN npm run build

FROM eclipse-temurin:17-jdk AS build

WORKDIR /app

COPY backend/.mvn .mvn
COPY backend/mvnw backend/pom.xml ./

RUN chmod +x mvnw
RUN ./mvnw dependency:go-offline

COPY backend/src ./src
COPY --from=frontend-build /frontend/dist ./src/main/resources/static

RUN ./mvnw clean package -DskipTests


FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

# 預設為正式環境設定；本機以 docker compose 的 SPRING_PROFILES_ACTIVE=dev 覆寫
ENV SPRING_PROFILES_ACTIVE=prod

# 非 root 執行，並以 actuator health 供容器平台判斷存活
RUN addgroup -S app && adduser -S app -G app
USER app
HEALTHCHECK --interval=30s --timeout=5s --start-period=60s --retries=3 \
  CMD wget -qO- http://localhost:8080/actuator/health || exit 1

ENTRYPOINT ["java", "-jar", "app.jar"]
