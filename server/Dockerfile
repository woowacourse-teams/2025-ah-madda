FROM amazoncorretto:21

COPY . .

RUN chmod +x ./gradlew

RUN ./gradlew bootJar

RUN cp $(ls -t build/libs/*.jar | head -n 1) app.jar

ENV TZ=Asia/Seoul

EXPOSE 8080

CMD ["sh", "-c", "java -Dspring.profiles.active=${PROFILE} -jar /app.jar"]
