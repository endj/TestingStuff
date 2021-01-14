FROM openjdk:11
COPY target/client-0.0.1-SNAPSHOT.jar app.jar
RUN mkdir reports
CMD ["java", "-jar", "app.jar"]