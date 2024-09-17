FROM openjdk:22-jdk
ADD app.jar /app.jar
ENTRYPOINT ["sh", "-c", "java ${JAVA_OPTS} -jar /app.jar"]