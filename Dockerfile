FROM gradle:6-jdk11 AS builder

WORKDIR /app
COPY . /app
RUN gradle wrapper && ./gradlew build

FROM us.gcr.io/broad-dsp-gcr-public/base/jre:11-debian

RUN useradd -m ontology
USER ontology
WORKDIR /home/ontology

COPY configs/config.json index-config.json
COPY --from=builder /app/build/layers/libs libs
COPY --from=builder /app/build/layers/resources resources
COPY --from=builder /app/build/layers/application.jar ontology.jar
COPY --from=builder /app/build/layers/resources/ontologies ontologies

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "ontology.jar"]
