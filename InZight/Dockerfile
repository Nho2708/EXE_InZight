# Stage 1: Build ứng dụng bằng Maven
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app

# Copy file pom.xml và tải các thư viện về trước (để tận dụng cache layer của Docker)
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy toàn bộ source code vào
COPY src ./src

# Build ra file .jar (bỏ qua test để build nhanh hơn trên cloud)
RUN mvn clean package -DskipTests

# Stage 2: Chạy ứng dụng bằng JDK rút gọn (nhẹ hơn)
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Copy file .jar từ bước build sang bước chạy
# Lưu ý: tên file jar phải khớp với artifactId và version trong pom.xml
COPY --from=build /app/target/expense-app-1.0.0.jar app.jar

# Expose port 8080
EXPOSE 8080

# Lệnh chạy ứng dụng
ENTRYPOINT ["java", "-jar", "app.jar"]