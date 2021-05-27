import sys, os
import json
import requests


if __name__ == '__main__':
  config_json = json.load(open('./build/layers/index-config.json', 'r'))

  for file in config_json['ontologies']:
    if file['remote'] == True:
      print('Getting ' + file['name'] + ' from ' + file['path'])
      r = requests.get(file['path'], allow_redirects=True)

      if not os.path.exists('./build/layers/resources/ontologies'):
        os.makedirs('./build/layers/resources/ontologies')
      open('./build/layers/resources/ontologies/' + file['name'], 'wb').write(r.content)

