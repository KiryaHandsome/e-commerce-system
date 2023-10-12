FROM openjdk:17-jdk
ADD build/libs/inventory-service-1.0.jar inventory-service.jar
ENTRYPOINT ["java", "-jar", "inventory-service.jar"]
EXPOSE 8082