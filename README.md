# Ontology Services
This application is used to build ElasticSearch indices in order to test different ontology files and the terms that are created from them. The application is provided a config file and a list of ontology files to run against. A local Elasticsearch container is spun up for queries to be run against and tested.

### Config
The service builds indices based off of a config file that is provided to it.  The config contains the name of the index, the server host names minus http and port (the server name will be elastic if running against the local elastic docker container), and the list of files to index.

If using files from your local machine, place them into the src/main/resources/ontologies/ folder and they will be included with the gradle build. When running, both the local and remote files are copied into the docker container running the app to be processed.

```
{
  "indexName": "index-name",
  "servers": [
    "elastic"
  ],
  "ontologies": [
    {
      "name": "example.owl",
      "path": "./build/layers/resources/temp/example.owl",
      "remote": false,
      "ontologyType": "disease",
      "prefix": "",
      "fileType": "OWL"
    },
    {
      "name": "remote-example.owl",
      "path": "https://some-site.org/remote-example.owl",
      "remote": true,
      "ontologyType": "disease",
      "prefix": "",
      "fileType": "OWL"
    }
  ]
}
```

### Running the app
In order to run the app, there is a shell script that can easily be run into the shell terminal. Just provide a path to the config file for the run with the ``-c`` option. This script will bring down any existing docker containers, copy all of the necessary files, then compose the new containers and run them with the config and files.
```
./run.sh -c path/to/config.json
```

### Testing your index
To test your local connection, run elastic search queries against localhost:
http://localhost:9200/{index name}/_search?q=apnea
