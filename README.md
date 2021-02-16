# Ontology Services

### Build and run

```
./gradlew build
./gradlew run
```
Should show you the current version of the service.

### Docker
```
docker-compose down
docker-compose build --no-cache
docker-compose up
```
If docker successfully spins up the containers, you should see `ontology` and `es`. 

`ontology` will output the current version of the service, then exit with code 0. 

`es` will continue to run until you stop the container.
