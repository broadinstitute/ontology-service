#FROM gradle:6.8.2-jdk11
#RUN gradle build
# TODO: figure out how to build gradle project here before copying files over

FROM openjdk:15-alpine
WORKDIR /home/app
COPY build/layers/libs /home/app/libs
COPY build/layers/resources /home/app/resources
COPY build/layers/application.jar /home/app/ontology.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/home/app/ontology.jar"]
