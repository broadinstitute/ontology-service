#FROM gradle:6.8.2-jdk11
#RUN ./gradlew build
# TODO: figure out how to build gradle project here before copying files over
FROM us.gcr.io/broad-dsp-gcr-public/base/jre:15-alpine

WORKDIR /home/app
COPY build/layers/libs /home/app/libs
COPY build/layers/resources /home/app/resources
COPY build/layers/application.jar /home/app/ontology.jar
COPY build/layers/index-config.json /home/app/index-config.json
COPY build/layers/resources/ontologies /home/app/ontologies

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/home/app/ontology.jar"]
