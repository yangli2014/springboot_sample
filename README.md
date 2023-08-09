# Spring boot REST API application for product management
This is a sample Spring boot REST API application written with Java 17, using Liquibase to initialize the DB schema. The database is using PostgreSQL 14.5 (running in a Docker container)

## How to run and test the application
Clone the project to the local folder and under the project root folder
- Prepare a clean PostgreSQL database, for example, start a docker container.
- Update the database properties in the src/main/resources/application.yaml 
- Build with maven command `mvn clean install`
- Run Java command to start application `java -jar ./target/product-rest-0.0.1-SNAPSHOT.jar`
- Using REST API client to test the REST API endpoint.

## Available endpoints (TBD)
