# Ontology Services
This application is used to build ElasticSearch indices in order to test different ontology files and the terms that are created from them. The application is provided a config file and a list of ontology files to run against. A local Elasticsearch container is spun up for queries to be run against and tested.

### Config
The service builds indices based off of a config file that lives in `src/main/resources/application.yml`.  The config contains the name of the index, the server host names minus http and port (the server name will be localhost if running against the local elastic docker container), and the list of files to index.

Copy/download all files to your local machine. Place them into the `src/main/resources/ontologies/` directory where they will be included with the gradle build. When running, all files are copied into the docker container running the app to be processed.

Example `src/main/resources/application.yml` file:
```
micronaut:
  application:
    name: ontology
    version: 0.1

elasticSearch:
  servers:
    - es
  indexName: ontology-local

ontologyFiles:
  files:
    -
      name: doid.owl
      type: disease
      prefix:
      fileType: OWL
    -
      name: data-use.owl
      type: organization
      prefix:
      fileType: OWL
```

### Running the app
The app can be built/run with docker and the default `docker-compose.yml` file
```
docker compose build --progress=plain --no-cache
docker compose up
```
And then run
```
docker compose down
```
when complete.

### Testing your index
To test your local connection, run elastic search queries against localhost (be sure to modify the index name to match your configuration):
[http://localhost:9200/ontology-local/_search?q=apnea](http://localhost:9200/ontology-local/_search?q=apnea). To maintain the elastic search index data generated betweenr runs, create a local `data` directory that will be re-used between ES runs.

### Running locally with a local ES server

1. Spin up a local ES index:
```
docker run -p 9200:9200 -v data:/usr/share/elasticsearch/data -e discovery.type=single-node -e xpack.security.enabled=false -it docker.elastic.co/elasticsearch/elasticsearch:5.4.0
```
 
2. Update the ES component of the application config (`src/main/resources/application.yml`) to run against local instance:
```yaml
elasticSearch:
  servers:
    - localhost
```

3. Ensure that the ontology files exist in `src/main/resources/ontologies/`
4. Run app: `./gradlew run`

Testing is the same as [above](#testing-your-index).