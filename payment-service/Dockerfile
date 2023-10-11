FROM openjdk:17-jdk
ADD build/libs/payment-service-1.0.jar payment-service.jar
ENTRYPOINT ["java", "-jar", "payment-service.jar"]
EXPOSE 8081