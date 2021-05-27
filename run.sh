#!/bin/bash
while getopts c: flag
do
    case "${flag}" in
        c) config_path=${OPTARG};;
    esac
done

if [ -z "${config_path}" ]
then
  echo "Config path not given. Exiting..."
  exit 1
fi

echo "Building the project"
./gradlew build

echo "Break down any existing containers..."
docker-compose down

echo "Copying config file"
cp $config_path ./build/layers/index-config.json

echo "Retrieve ontology files"
docker run -v "${PWD}/scripts":/working/scripts -v "${PWD}/build":/working/build -w /working broadinstitute/dsp-toolbox python ./scripts/interpret-config.py

echo "Composing the elasticsearch container..."
docker-compose up --build --remove-orphans

