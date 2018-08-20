# oauth-server

Spring Security based oauth2 JWT token service.  Creates and validates JWT oauth2 tokens that can then be passed into REST service calls.
## Getting Started

* clone this repository
* build with Maven - "mvn clean install"
* start the app from command line - "java -jar target/oauth-server-0.0.1-SNAPSHOT.jar --spring.profiles.active=dev"

## Using Docker
You can build a docker image and run this service in Docker with the following steps:

* mvn install dockerfile:build
* docker run -e "SPRING_PROFILES_ACTIVE=dev" -d -p 8081:8081 -t fbm/oauth-server

To view the logs of the service running in Docker:
* get the container ID by running "docker ps"
* then run "docker logs -f <container ID># oauth-server
