FROM openjdk:8-jdk-alpine
VOLUME /tmp
ENV SERVER_NAME activitiflow
ENV OPS " -server -Xms256m -Xmx256m -Xss256k"
COPY target/${SERVICE_NAME}-0.0.1-SNAPSHOT.jar ${SERVICE_NAME}.jar
ENTRYPOINT java -jar ${OPS} ${SERVER_NAME}.jar