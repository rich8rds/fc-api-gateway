FROM eclipse-temurin:23-jre

ADD target/ApiGateway.jar /ApiGateway.jar
ADD docker/collector/opentelemetry-javaagent.jar /opentelemetry-javaagent.jar

ENTRYPOINT java -javaagent:/opentelemetry-javaagent.jar -jar /ApiGateway.jar