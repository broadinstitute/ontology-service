Ontology Match
========
Ontology Match Service

##Running
* To test: ./gradlew test
* To run locally (in development mode): ./gradlew run

##Dependencies:
* JDK 1.8
* Gradle 2.2.1
* Groovy Version: 2.3.6+

##Build Status
<a href='https://travis-ci.org/broadinstitute/orsp-service'><img src='https://travis-ci.org/broadinstitute/ontology-service.svg'></a>

##Testing

###Example curl command
The Match service will take as the first argument a "Research Purpose", and as the second argument, the
"Consented Restrictions". If the requested purpose falls "inside" a consented restriction expression,
then the response is "true". If not, then the response is "false".

####Positive Match
Example research purpose of Paranoid Schizophrenia: http://www.ontobee.org/browser/rdf.php?o=DOID&iri=http://purl.obolibrary.org/obo/DOID_1229

Should match anything consented for for Schizophrenia: http://www.ontobee.org/browser/rdf.php?o=DOID&iri=http://purl.obolibrary.org/obo/DOID_5419

<pre>
curl -v -X post -H "Content-Type: application/json" http://localhost:8181 -d '[{"type":"named","name":"http://purl.obolibrary.org/obo/DOID_1229"},{"type":"named","name":"http://purl.obolibrary.org/obo/DOID_5419"}]'
</pre>

<pre>
* upload completely sent off: 135 out of 135 bytes
&lt; HTTP/1.1 200 OK
&lt; Date: Mon, 15 Dec 2014 21:52:43 GMT
&lt; Content-Type: application/json
&lt; Transfer-Encoding: chunked
&lt;
* Connection #0 to host localhost left intact
true
</pre>

####Negative Match

<pre>
curl -v -X post -H "Content-Type: application/json" http://localhost:8181 -d '[{"type":"named","name":"http://purl.obolibrary.org/obo/DOID_5419"},{"type":"named","name":"http://purl.obolibrary.org/obo/DOID_1229"}]'
</pre>

<pre>
* upload completely sent off: 135 out of 135 bytes
&lt; HTTP/1.1 200 OK
&lt; Date: Mon, 15 Dec 2014 21:53:03 GMT
&lt; Content-Type: application/json
&lt; Transfer-Encoding: chunked
&lt;
* Connection #0 to host localhost left intact
false
</pre>

###Load Testing with Apache ab
<pre>
match.json:
[{"type":"named","name":"http://purl.obolibrary.org/obo/DOID_1229"},{"type":"named","name":"http://purl.obolibrary.org/obo/DOID_5419"}]

ab -n 1000 -c 100 -p match.json -T 'application/json' http://localhost:8181/

Server Software:
Server Hostname:        localhost
Server Port:            8181

Document Path:          /
Document Length:        4 bytes

Concurrency Level:      100
Time taken for tests:   12.510 seconds
Complete requests:      1000
Failed requests:        0
Write errors:           0
Total transferred:      92000 bytes
Total POSTed:           272000
HTML transferred:       4000 bytes
Requests per second:    79.93 [#/sec] (mean)
Time per request:       1251.018 [ms] (mean)
Time per request:       12.510 [ms] (mean, across all concurrent requests)
Transfer rate:          7.18 [Kbytes/sec] received
                        21.23 kb/s sent
                        28.41 kb/s total

Connection Times (ms)
              min  mean[+/-sd] median   max
Connect:        0    0   0.9      0       4
Processing:    20 1182 236.6   1253    1398
Waiting:       20 1182 236.6   1253    1398
Total:         24 1182 235.8   1253    1398

Percentage of the requests served within a certain time (ms)
  50%   1253
  66%   1268
  75%   1286
  80%   1295
  90%   1343
  95%   1358
  98%   1371
  99%   1392
 100%   1398 (longest request)
</pre>

##Developer Notes
* Use @CompileStatic whenever possible
* Need substantial javadocs written
* License issues exist. Pellet is AGPL v3 while everything we do is Apache v2.
