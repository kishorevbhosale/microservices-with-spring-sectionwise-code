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
      ```yaml
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
   ```yaml
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
    ```yaml
    URL : http://localhost:8072/skbank/cards/cards/properties
    Headers:
    Content-Type:application/json
    Authorization:Bearer <token -generated in point 5>
    ```

**Install Keyclaok into K8s cluster :**
1) Refer link https://bitnami.com/stack/keycloak/helm and follow installation steps
2) After successful installation we get the following msg :
    ```yaml
    C:\WINDOWS\System32>helm install my-release bitnami/keycloak
    W0911 09:39:11.418928   10956 gcp.go:120] WARNING: the gcp auth plugin is deprecated in v1.22+, unavailable in v1.25+; use gcloud instead.
    To learn more, consult https://cloud.google.com/blog/products/containers-kubernetes/kubectl-auth-changes-in-gke
    NAME: my-release
    LAST DEPLOYED: Sun Sep 11 09:39:14 2022
    NAMESPACE: default
    STATUS: deployed
    REVISION: 1
    TEST SUITE: None
    NOTES:
    CHART NAME: keycloak
    CHART VERSION: 9.8.1
    APP VERSION: 18.0.2
    
    ** Please be patient while the chart is being deployed **
    
    Keycloak can be accessed through the following DNS name from within your cluster:
    
        my-release-keycloak.default.svc.cluster.local (port 80)
    
    To access Keycloak from outside the cluster execute the following commands:
    
    1. Get the Keycloak URL by running these commands:
    
      NOTE: It may take a few minutes for the LoadBalancer IP to be available.
            You can watch its status by running 'kubectl get --namespace default svc -w my-release-keycloak'
    
        export HTTP_SERVICE_PORT=$(kubectl get --namespace default -o jsonpath="{.spec.ports[?(@.name=='http')].port}" services my-release-keycloak)
        export SERVICE_IP=$(kubectl get svc --namespace default my-release-keycloak -o jsonpath='{.status.loadBalancer.ingress[0].ip}')
    
        echo "http://${SERVICE_IP}:${HTTP_SERVICE_PORT}/"
    
    2. Access Keycloak using the obtained URL.
    ```
3) To get the admin password connect to kubectl and enter following command:
```yaml
$ echo Password: $(kubectl get secret --namespace default my-release-keycloak -o jsonpath="{.data.admin-password}" | base64 --decode)
```
4) Connect to keycloak server 
    ```
   url :  service and ingress -> Endpoints of "my-release-keycloak"
   username : user
   password : fetched from above command
    ```
5) Create the client -> 
   - Standard flow : disabled
   - access type : confidential
   - service account enabled : on <br />
   save and get credential.

** Updated helm charts :**
1) updated `skbank-common` -> `templates` -> `configmap.yml` and `deployment.yml` file
2) added values in each service -> `values.yml` -> `keycloak_enabled`
3) in `dev-env` and `prod-env` updated `values.yml` -> added property `keyCloakURL`
4) rebuild all helm charts