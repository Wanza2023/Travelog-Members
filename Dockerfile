FROM openjdk:11.0-jre
VOLUME /tmp
COPY build/libs/*.jar app.jar
ENTRYPOINT [ "java", "-jar", "/app.jar" ]
