FROM gradle:6-jdk11 as builder

RUN mkdir -p /home/app
WORKDIR /home/app
COPY build.gradle build.gradle
COPY gradle gradle
COPY gradlew gradlew
COPY gradlew.bat gradlew.bat
COPY gradle.properties gradle.properties
COPY src src
COPY settings.gradle settings.gradle
RUN ./gradlew build

FROM us.gcr.io/broad-dsp-gcr-public/base/jre:11-debian

RUN mkdir -p /home/app
WORKDIR /home/app
COPY configs/config.json index-config.json
COPY --from=builder /home/app/build/layers/libs libs
COPY --from=builder /home/app/build/layers/resources resources
COPY --from=builder /home/app/build/layers/application.jar ontology.jar
COPY --from=builder /home/app/build/layers/resources/ontologies ontologies

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "ontology.jar"]
