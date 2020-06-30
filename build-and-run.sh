docker build -t heart-rate-producer ./src/main/java/com/raghu/iot/producer
docker build -t heart-rate-consumer ./src/main/java/com/raghu/iot/consumer

#docker run -d heart-rate-producer
#docker run -d heart-rate-consumer 7071