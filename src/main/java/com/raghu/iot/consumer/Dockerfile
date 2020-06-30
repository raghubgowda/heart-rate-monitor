FROM openjdk:8-jre-alpine
COPY target/iot-0.0.1-SNAPSHOT-jar-with-dependencies.jar /usr/app/
WORKDIR /usr/app
CMD ["java", "-cp","iot-0.0.1-SNAPSHOT-jar-with-dependencies.jar", "com.raghu.iot.consumer.HeartRateDataConsumer"]