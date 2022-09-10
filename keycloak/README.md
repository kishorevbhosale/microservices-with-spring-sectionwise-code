Securing Microservices using OAuth2 client credentials grant flow
---

**Key Steps :**
1) We are using opensource `OAuth2` tool i.e. `keycloak`  
2) Download, install & setup the Keycloak using docker command in your local system.<br />
   Link : https://www.keycloak.org/getting-started/getting-started-docker <br />
   Command : `> docker run -p 7080:8080 -e KEYCLOAK_ADMIN=admin -e KEYCLOAK_ADMIN_PASSWORD=admin 
quay.io/keycloak/keycloak:19.0.1 start-dev`

**Access Keycloak Server :**
1) Once docker image is running open `http://localhost:7080/` in browser
2) Register a client inside Keycloak that supports Client Credentials grant flow.<br />
   - Create a Client inside auth server (`Authentication flow` -> only select `Service account role`) rest default value and save <br />
   - Check `credentials` in `Credentials` tab.
3) List of url supported by keycloak : http://localhost:7080/realms/master/.well-known/openid-configuration
4) We need token_endpoint: http://localhost:7080/realms/master/protocol/openid-connect/token url 
5) POST call details :
      ```aidl
      URL : http://localhost:7080/realms/master/protocol/openid-connect/token
      Body: 
      content-type : x-www-form-urlencoded
      client_id:skbank-callcenter
      client_secret:iGdUWz2DdowBLlgoFkkrePKB497QxCpp
      scope:openid
      grant_type:client_credentials
      ```
6) go to https://jwt.io/ and check the access token details 
7) Copy all services from session 4 to keycloak folder and update gatewayserver with above details
8) Open the pom.xml of the microservices gatewayserver and make sure to add the below required dependencies of Spring Security,OAuth2.
   ```aidl
   <dependency>
     <groupId>org.springframework.boot</groupId>
     <artifactId>spring-boot-starter-security</artifactId>
   </dependency>
   <dependency>
     <groupId>org.springframework.security</groupId>
     <artifactId>spring-security-oauth2-resource-server</artifactId>
   </dependency>
   <dependency>
     <groupId>org.springframework.security</groupId>
     <artifactId>spring-security-oauth2-jose</artifactId>
   </dependency>
   ```
9) Create the classes SecurityConfig.java, KeycloakRoleConverter.java, so that our Spring Cloud Gateway to act as a Resource server & handle both Authentication & Authorization.
10) Use URL listed in point no 3, and use "jwks_uri": http://localhost:7080/realms/master/protocol/openid-connect/certs,
11) Run all the service on local, but before that run zipkin container : `> docker run -d -p 9411:9411 openzipkin/zipkin`
12) After running all the service 
    - check in browser :  http://localhost:8072/skbank/accounts/sayHello -> `HTTP ERROR 401` that means your url is secure now
    - Simi. check for cards - http://localhost:8072/skbank/cards/cards/properties -> `HTTP ERROR 401`
    - We have not secured loan service: http://localhost:8072/skbank/loans/loans/properties -> return proper response
13) POST call for accessing accounts service
    ```aidl
    URL : http://localhost:8072/skbank/cards/cards/properties
    Headers:
    Content-Type:application/json
    Authorization:Bearer <token -generated in point 5>
```
