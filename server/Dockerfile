FROM amazoncorretto:21

COPY build/libs/*.jar /app.jar

ENV TZ=Asia/Seoul

EXPOSE 8080

CMD ["java", "-Dspring.profiles.active=${PROFILE}", "-jar", "/app.jar"]
