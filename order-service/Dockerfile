FROM openjdk:17-jdk
ADD build/libs/order-service-1.0.jar order-service.jar
ENTRYPOINT ["java", "-jar", "order-service.jar"]
EXPOSE 8080