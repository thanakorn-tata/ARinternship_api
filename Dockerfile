# ===== Stage 1: Build =====
# ใช้ image ที่มี Maven + JDK 17 ครบ เพื่อ compile โปรเจกต์
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app

# copy แค่ pom.xml ก่อน เพื่อให้ Docker cache dependency ไว้
# (ถ้าโค้ดเปลี่ยนแต่ pom.xml ไม่เปลี่ยน ครั้งถัดไปจะ build เร็วขึ้นมาก)
COPY pom.xml .
RUN mvn dependency:go-offline -B

# copy โค้ดที่เหลือ แล้ว build เป็น .jar
COPY src ./src
RUN mvn clean package -DskipTests

# ===== Stage 2: Run =====
# ใช้ image เล็ก ๆ ที่มีแค่ JRE (ไม่ต้องมี Maven ตอนรันจริง) ทำให้ image ขนาดเล็กลงมาก
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# copy เอาเฉพาะไฟล์ .jar ที่ build เสร็จจาก stage แรกมา
COPY --from=build /app/target/arinternship-0.0.1-SNAPSHOT.jar app.jar

# Render จะส่ง PORT มาทาง environment variable ให้เอง ต้องให้ Spring Boot ฟังพอร์ตนั้น
ENV PORT=8080
EXPOSE 8080

ENTRYPOINT ["sh", "-c", "java -jar app.jar --server.port=${PORT}"]
