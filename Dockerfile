FROM openjdk:11
MAINTAINER saeedshokoohi
COPY target/basic-trading-bot-0.0.1-SNAPSHOT.jar basic-trading-bot.jar
ENTRYPOINT ["java","-jar","/basic-trading-bot.jar"]