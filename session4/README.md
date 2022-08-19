Creating three microservices specific to Accounts, Loans and Cards by taking a Bank application as an example
---

**Description:** This repository has three maven projects with the names **accounts, loans, cards**. These three
projects will acts as a microservices and these are build by taking **EazyBank** as an example Bank application. Below
are the key steps that are followed while creating these projects.

**Key steps:**

- Go to https://start.spring.io/
- Fill all the details required to generate a **accounts** Spring Boot project and add dependencies **Spring Web**,**
  Spring Boot Actuator**,**H2 Database**, **Spring Data JPA**
  , **Lombok**. Click GENERATE which will download the **accounts** maven project in a zip format
- Repeat the above steps for **cards** and **loans** microservices as well.
- Extract the downloaded maven projects of **accounts, cards, loans** and import the same into Eclipse by following the
  steps mentioned in the course


Access the URLs like
- http://localhost:8080/accounts/ 
- http://localhost:8090/loans/
- http://localhost:9000/cards/
  

- A new microservices 'configserver' is created based on Spring Cloud Config which will act as a Config server. 
- These three microservices accounts, loans, cards are updated to read the configurations/properties from the 'configserver' microservices.
- '@EnableConfigServer'. This annotation will make your microservice to act as a Spring Cloud Config Server.
- All the 9 property files related to accounts, loans and cards microservices are present under config folder under 
the path 'configserver\src\main\resources' 
- Go to your Spring Boot main class ConfigserverApplication.java and right click-> Run As -> Java Application. This will start your Spring Boot application successfully at port 8071 which is the port we configured inside application.properties. Your can confirm the same by looking at the console logs.


Access the URLs like 
- http://localhost:8071/accounts/default, (default/dev/prod)
- http://localhost:8071/loans/dev, (default/dev/prod)
- http://localhost:8071/cards/prod  (default/dev/prod)
  
Inside your browser to randomly validate that properties are being read from configured file system by Config Server for all the three microservices accounts, loans and cards.
  

- Actuator url : http://localhost:8080/actuator
- Encrypt data (post call) : http://localhost:8071/encrypt (pass text for encryption in body section)
- Decrypt data (post call) : http://localhost:8071/decrypt (pass text for decryption in body section)

**Eurekaserver :**

A new microservices 'eurekaserver' is created in this section based on Spring Cloud Netflix Eureka which will act as a Service Discovery & Registration server. 
All the existing microservices accounts, loans, cards are updated to register themselves with the eurekaserver during the startup and send heartbeat signals. 
Accounts microservice is also updated to connect with loans and cards microservices using Netflix Feign client.

Open the SpringBoot main class EurekaserverApplication.java . 
We can always identify the main class in a Spring Boot project by looking for the annotation @SpringBootApplication. 
On top of this main class, please add annotation '@EnableEurekaServer'. This annotation will make your microservice to act as a Spring Cloud Netflix Eureka Server.

if you are using a Spring Boot version of >=2.5 then providing ribbon configurations is not required.

Sequence of execution :
- start configserver application
- start eurekaserver
- start any aaplication (accounts/cards/loans)

Access the EUREKA URLs
- http://192.168.0.4:8070/eureka/
- http://192.168.0.4:8070/eureka/apps/accounts
- http://192.168.0.4:8070/eureka/apps/cards
- http://192.168.0.4:8070/eureka/apps/loans

Shutdown the applications:
- http://localhost:8080/actuator/shutdown (postman - post method)
- http://localhost:8090/actuator/shutdown (postman - post method)
- http://localhost:9000/actuator/shutdown (postman - post method)

check eureka dashbord all services are de-registered


**Docker commands for reference:**

- $ `docker build . -t <user_name_of_docker_hub/image-name>` (eg. kishorevbhosale/accounts)
- $ `mvn spring-boot:build-image` - (run in respective dir to create docker image)
- $ `docker images` - list all docker images
- $ `docker rmi <image-id prefix>` - remove the docker image
- $ `docker push <image-name>` - push image in docker hub
- $ `docker ps` - check all running container
- $ `docker compose up` - go to docker-compose and soecific env folder and run this
- $ `docker stop <container-id-prefix>` - stop the running containers