# Ontology Services
This application is used to build ElasticSearch indices in order to test different ontology files and the terms that are created from them. The application is provided a config file and a list of ontology files to run against. A local Elasticsearch container is spun up for queries to be run against and tested.

### Config
The service builds indices based off of a config file that lives in `configs/config.json`.  The config contains the name of the index, the server host names minus http and port (the server name will be elastic if running against the local elastic docker container), and the list of files to index.

Copy/download all files to your local machine. Place them into the src/main/resources/ontologies/ folder and they will be included with the gradle build. When running, all files are copied into the docker container running the app to be processed.

Example `configs/config.json` file:
```
{
  "indexName": "doid-2023-04-20",
  "servers": [
    "elastic"
  ],
  "ontologies": [
    {
      "name": "doid-2023-04-20.owl",
      "path": "./src/main/resources/ontologies/doid-2023-04-20.owl",
      "remote": false,
      "ontologyType": "disease",
      "prefix": "",
      "fileType": "OWL"
    }
  ]
}
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
[http://localhost:9200/doid-2023-04-20/_search?q=apnea](http://localhost:9200/doid-2023-04-20/_search?q=apnea)
