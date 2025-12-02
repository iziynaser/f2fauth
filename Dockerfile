FROM eclipse-temurin:17-jdk-alpine

WORKDIR /app

COPY target/f2fauth-0.0.1.jar app.jar

EXPOSE 8090
EXPOSE 5007

ENTRYPOINT ["java", "-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5007", "-jar", "app.jar"]
