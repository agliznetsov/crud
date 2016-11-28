# CRUD

###### A pragmatic framework to create REST APIs in Java.

This project can be seen as a lightweight, magic-less alternative to spring-data-rest.   

#### Features

* Base model classes and interfaces to create Repositories.
* Extendable transformation service to convert from/to entity and DTO model classes. 
* Hibernate repository implementation.
* Spring MVC ResourceController implementation.
* Swagger-UI based GUI and automatic meta-data generation.

#### Run

Run the demo:

    mvn clean install
    cd crud-showcase
    mvn spring-boot:run
    
Then navigate to [http://localhost:8080/swagger-ui](http://localhost:8080/swagger-ui) 