FROM maven:3.6.3-jdk-11

ADD target/aqa-training-engine.jar /app/
WORKDIR /app

CMD java -jar aqa-training-engine.jar
