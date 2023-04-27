FROM gradle:8.1-jdk17 AS builder

WORKDIR /app
COPY . /app
RUN gradle wrapper && ./gradlew build

FROM us.gcr.io/broad-dsp-gcr-public/base/jre:17-debian

RUN useradd -m ontology
USER ontology
WORKDIR /home/ontology
RUN mkdir -p src/main/resources/ontologies

COPY --from=builder /app/build/libs libs
COPY --from=builder /app/build/resources resources
COPY --from=builder /app/build/libs/ontology-0.1-all.jar ontology.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "ontology.jar"]
