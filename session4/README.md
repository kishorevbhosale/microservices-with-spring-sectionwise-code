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

**Client side load balancing :**

In order to set up Client side load balancing using Feign client, 
add @EnableFeignClients annotation on top of AccountsApplication.java class which is present inside accounts microservice.

create two interfaces with the name `LoansFeignClient.java,CardsFeignClient.java` inside accounts microservice project. 
These two interfaces and the methods inside them will help to communicate with loans and cards microservices 
using Feign client from accounts microservice.


Restart the accounts microservice and test the feign client changes done by invoking 
the endpoint http://localhost:8080/customerdetails through Postman 
by passing the below request in JSON format. 
You should get the response from the accounts microservices which has all the details related to account, loans and cards.

`{
"customerId": 1
}`

**Start all microservices using docker compose file :**

- go to `docker-compose`  dir and run the command `docker compose up`
- verify using following links:
- http://localhost:8070/
- http://localhost:8071/loans/dev - (check any service - account/cards/loans and any env - default/dev/prod)
- post call of http://localhost:8080/customerdetails - check finclients working fine

**Check multiple instances of accounts that are created**
- post call to http://localhost:8080/customerdetails
- post call to http://localhost:8081/customerdetails
- http://localhost:8070/eureka/apps/accounts - check the eureka details for instances of accounts

Making Microservices Resilient
---
**CircuitBreaker Pattern**

Key Steps : 
- Added Resilience4j related dependencies to pom.xml of account microservice
- Updated application.properties file of account microservice
- Added `@CircuitBreaker` annotation to related method and handled empty response. (updated `AccountsController`)
- GET call : http://localhost:8080/actuator/circuitbreakers
- GET call : http://localhost:8080/actuator/circuitbreakerevents/?name=detailsForCustomerSupportApp
- POST call to check customer details with circuitbreakers pattern http://localhost:8080/customerdetails


**Retry Pattern**

Key Steps :
- Added Resilience4j related dependencies to pom.xml of account microservice
- Updated application.properties file of account microservice
- Added `@Retry` annotation to related method and handled empty response. (updated `AccountsController`)
- GET call : http://localhost:8080/actuator/retries
- GET call : http://localhost:8080/actuator/retryevents
- POST call to check customer details with retries pattern http://localhost:8080/customerdetails


**RateLimiter Pattern**

Key Steps :
- Added Resilience4j related dependencies to pom.xml of account microservice
- Updated application.properties file of account microservice
- Added `@RateLimiter` annotation to related method and handled empty response. (updated `AccountsController`)
- GET call : http://localhost:8080/actuator/ratelimiters
- GET call to check customer details with retries pattern http://localhost:8080/sayHello

Spring Cloud Gateway
--- 
- A new microservices `gatewayserver` is created in this section based on Spring Cloud Gateway which will help 
  in handling routing & any other cross cutting concerns inside microservices network.
- Update the application.properties in `gatewayserver` service
- Major dependecy added is `spring-cloud-starter-gateway`
- GET call : http://host.docker.internal:8072/actuator
- GET call : http://host.docker.internal:8072/actuator/gateway/routes
- POST call for account detail : http://host.docker.internal:8072/accounts/account/details

**cross cutting concept :**

- Tracing every request (correlationId)
- In order to implement cross cutting concerns inside your microservices created the classes TraceFilter.java, 
ResponseTraceFilter.java, FilterUtility.java.
- updated all the important classes like AccountsController.java, LoansController.java, CardsController.java 
to accept the @RequestHeader("skbank-correlation-id") String correlationid as input inside the method parameters.
- Restart your gatewayserver microservice and invoke the REST API http://localhost:8072/skbank/accounts/myCusomerDetails 
through Postman by passing the below request in JSON format.
- Validate the logger statements of gatewayserver microservice to check if the skbank-correlation-id value is logged properly or not.

Distributed tracing and log aggregation
---

**Using Sleuth and zipkin**

- Added dependency in all pom.xml
- Added logs in respective microservice
- logs from gatewayserver
```aidl
2022-08-25 10:05:09.804  INFO [gatewayserver,d3468216e7433f8e,d3468216e7433f8e] 40952 --- [ctor-http-nio-7] c.b.c.g.filters.RequestTraceFilter       : skbank-correlation-id generated in tracing filter: c8d55cd8-c7f7-44a3-a743-4a59829d73e3.
```

- logs from accoutn service
```aidl
2022-08-25 10:05:10.386  INFO [accounts,d3468216e7433f8e,86e64e87cb365830] 29180 --- [io-8080-exec-10] o.a.c.c.C.[Tomcat].[localhost].[/]       : Initializing Spring DispatcherServlet 'dispatcherServlet'
2022-08-25 10:05:10.387  INFO [accounts,d3468216e7433f8e,86e64e87cb365830] 29180 --- [io-8080-exec-10] o.s.web.servlet.DispatcherServlet        : Initializing Servlet 'dispatcherServlet'
2022-08-25 10:05:10.393  INFO [accounts,d3468216e7433f8e,86e64e87cb365830] 29180 --- [io-8080-exec-10] o.s.web.servlet.DispatcherServlet        : Completed initialization in 6 ms
2022-08-25 10:05:10.622  INFO [accounts,d3468216e7433f8e,86e64e87cb365830] 29180 --- [io-8080-exec-10] c.b.c.a.controller.AccountsController    : myCustomerDetails() method started
Hibernate: select accounts0_.account_number as account_1_0_, accounts0_.account_type as account_2_0_, accounts0_.branch_address as branch_a3_0_, accounts0_.create_dt as create_d4_0_, accounts0_.customer_id as customer5_0_ from accounts accounts0_ where accounts0_.customer_id=?
2022-08-25 10:05:13.073  INFO [accounts,d3468216e7433f8e,86e64e87cb365830] 29180 --- [io-8080-exec-10] c.b.c.a.controller.AccountsController    : myCustomerDetails() method ended
```
- logs from cards service
```aidl
2022-08-25 10:05:12.396  INFO [cards,d3468216e7433f8e,7c831aa86c378198] 39828 --- [nio-9000-exec-1] o.a.c.c.C.[Tomcat].[localhost].[/]       : Initializing Spring DispatcherServlet 'dispatcherServlet'
2022-08-25 10:05:12.396  INFO [cards,d3468216e7433f8e,7c831aa86c378198] 39828 --- [nio-9000-exec-1] o.s.web.servlet.DispatcherServlet        : Initializing Servlet 'dispatcherServlet'
2022-08-25 10:05:12.403  INFO [cards,d3468216e7433f8e,7c831aa86c378198] 39828 --- [nio-9000-exec-1] o.s.web.servlet.DispatcherServlet        : Completed initialization in 7 ms
2022-08-25 10:05:12.617  INFO [cards,d3468216e7433f8e,7c831aa86c378198] 39828 --- [nio-9000-exec-1] c.b.c.cards.controller.CardsController   : getCardDetails() method started
Hibernate: select cards0_.card_id as card_id1_0_, cards0_.amount_used as amount_u2_0_, cards0_.available_amount as availabl3_0_, cards0_.card_number as card_num4_0_, cards0_.card_type as card_typ5_0_, cards0_.create_dt as create_d6_0_, cards0_.customer_id as customer7_0_, cards0_.total_limit as total_li8_0_ from cards cards0_ where cards0_.customer_id=?
2022-08-25 10:05:13.044  INFO [cards,d3468216e7433f8e,7c831aa86c378198] 39828 --- [nio-9000-exec-1] c.b.c.cards.controller.CardsController   : getCardDetails() method ended
```
- logs from loan service
```aidl
2022-08-25 10:05:11.538  INFO [loans,d3468216e7433f8e,4956ae6ad2a25d8b] 37044 --- [nio-8090-exec-1] o.a.c.c.C.[Tomcat].[localhost].[/]       : Initializing Spring DispatcherServlet 'dispatcherServlet'
2022-08-25 10:05:11.538  INFO [loans,d3468216e7433f8e,4956ae6ad2a25d8b] 37044 --- [nio-8090-exec-1] o.s.web.servlet.DispatcherServlet        : Initializing Servlet 'dispatcherServlet'
2022-08-25 10:05:11.541  INFO [loans,d3468216e7433f8e,4956ae6ad2a25d8b] 37044 --- [nio-8090-exec-1] o.s.web.servlet.DispatcherServlet        : Completed initialization in 3 ms
2022-08-25 10:05:11.648  INFO [loans,d3468216e7433f8e,4956ae6ad2a25d8b] 37044 --- [nio-8090-exec-1] c.b.c.loans.controller.LoansController   : getLoansDetails() method started
Hibernate: select loans0_.loan_number as loan_num1_0_, loans0_.amount_paid as amount_p2_0_, loans0_.create_dt as create_d3_0_, loans0_.customer_id as customer4_0_, loans0_.loan_type as loan_typ5_0_, loans0_.outstanding_amount as outstand6_0_, loans0_.start_dt as start_dt7_0_, loans0_.total_loan as total_lo8_0_ from loans loans0_ where loans0_.customer_id=? order by loans0_.start_dt desc
2022-08-25 10:05:11.954  INFO [loans,d3468216e7433f8e,4956ae6ad2a25d8b] 37044 --- [nio-8090-exec-1] c.b.c.loans.controller.LoansController   : getLoansDetails() method ended
```
**Zipkin details :**
- Now in order to use distributed tracing using Zipkin, run the docker command `docker run -d -p 9411:9411 openzipkin/zipkin`. 
- This docker command will start the zipkin docker container using the provided docker image. 
- To validate if the zipkin server started successfully or not, visit the URL http://localhost:9411/zipkin inside your browser. You should be able to see the zipkin home page.
- Open the pom.xml of all the microservices accounts, loans, cards, configserver, eurekaserver, gatewayserver and make sure to add the below required dependency of Zipkin in all of them.
```aidl
<dependency>
  <groupId>org.springframework.cloud</groupId>
  <artifactId>spring-cloud-sleuth-zipkin</artifactId>
 </dependency>
```
- Open the application.properties of all the microservices accounts, loans, cards, configserver, eurekaserver, gatewayserver and make sure to add the below properties/configurations in all of them.
```
spring.sleuth.sampler.percentage=1
spring.zipkin.baseUrl=http://localhost:9411/
```
- Start all the microservices in the order of configserver, eurekaserver, accounts, loans, cards, gatewayserver.
- Once all the microservices are started, access the URL http://localhost:8072/accounts/myCusomerDetails through Postman
  Docker Commands 
- You should be able to see the tracing details inside zipkin console



---
  **Docker commands for reference:**

- $ `docker build . -t <user_name_of_docker_hub/image-name>` (eg. kishorevbhosale/accounts)
- $ `mvn spring-boot:build-image` - (run in respective dir to create docker image)
- $ `docker images` - list all docker images
- $ `docker rmi <image-id prefix>` - remove the docker image
- $ `docker push <image-name>` - push image in docker hub
- $ `docker ps` - check all running container
- $ `docker compose up` - go to docker-compose and soecific env folder and run this
- $ `docker stop <container-id-prefix>` - stop the running containers