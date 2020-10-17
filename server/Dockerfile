FROM openjdk:8-jre-alpine
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar
EXPOSE 8080
RUN chmod 755 app.jar
RUN addgroup -S app && adduser -S app -G app
USER app
ENTRYPOINT ["java","-jar","/app.jar"]
