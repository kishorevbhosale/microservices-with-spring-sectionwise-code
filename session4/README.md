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