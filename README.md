# TransactionSolution
Financial transaction solution REST API to store and retrieve transactions. It's connected with [**Treasury Reporting Rates of Exchange**](https://fiscaldata.treasury.gov/datasets/treasury-reporting-rates-exchange/treasury-reporting-rates-of-exchange)
using a Spring Feign Client. This Client allows the project to provide functionalities like retrieve a saved Transaction with the amount converted based on a specific currency.
It can also be used to list valid currencies and to get exchange information for specified currency from the date informed within the last six months.

## The project was built using the following tools:

* [**Java JDK 17**](https://docs.oracle.com/en/java/javase/17/)
* [**Spring Boot 3.1.4**](https://docs.spring.io/spring-boot/docs/3.1.4/reference/html/)
* [**Spring Web 3.1.4**](https://docs.spring.io/spring-boot/docs/3.1.4/reference/html/web.html#web)
* [**Spring Reactive Web 3.1.4**](https://docs.spring.io/spring-boot/docs/3.1.4/reference/html/web.html#web.reactive)
* [**Spring OpenFeign 4.0.4**](https://spring.io/projects/spring-cloud-openfeign)
* [**Spring Validation (Hibernate Validation) 8.0.1.Final**](https://docs.jboss.org/hibernate/stable/validator/reference/en-US/html_single/)
* [**Spring Data JPA 3.1.4**](https://spring.io/projects/spring-data-jpa)
* [**JUnit 5.9.3**](https://junit.org/junit5/docs/current/user-guide/)
* [**Mockito 5.3.1**](https://javadoc.io/doc/org.mockito/mockito-core/latest/org/mockito/Mockito.html)
* [**Flyway 9.16.3**](https://documentation.red-gate.com/fd/quickstart-how-flyway-works-184127223.html)
* [**H2 Database 2.1.214**](https://www.h2database.com/html/main.html)
* [**Lombok 1.18.30**](https://projectlombok.org/features/)
* [**OpenAPI 3.0 with Swagger**](https://swagger.io/specification/)
* [**Hibernate Jpamodelgen 6.1.7**](https://docs.jboss.org/hibernate/stable/jpamodelgen/reference/en-US/html_single/)
* [**Jacoco**](https://www.jacoco.org/jacoco/trunk/doc/)

# Run/Debug
* Run the MAVEN build `spring-boot:run` or start Java application `TransactionSolutionApplication` depending on the IDE that you are using.

If you face problems to run the APP it's recommended to [install maven](https://maven.apache.org/install.html)
  and use the following maven commands in the root project folder on your terminal:

```bash
mvn clean install
mvn spring-boot:run
```

* You can access Swagger at this URL: [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html#/).
 

* And the ApiDocs at this URL: [http://localhost:8080/api-docs](http://localhost:8080/api-docs) in JSON or [http://localhost:8080/api-docs.yaml](http://localhost:8080/api-docs.yaml) if you prefer yaml.

 
* `transaction-controller` contains three endpoints which can be used to create a transaction, list transactions from database using pagination, sort and filters, and finally an endpoint to find a transaction by its id and convert its amount to a informed currency.
 

* `exchange-rate-controller` contains two endpoints, one can list all valid country currency for exchange rate from [**Treasury Reporting Rates of Exchange**](https://fiscaldata.treasury.gov/datasets/treasury-reporting-rates-exchange/treasury-reporting-rates-of-exchange). 
The currency code can be used in the /transactions/{id}?currency={currency} to find a transaction by its id and convert its amount to the informed currency.
The Second endpoint can get exchange information for specified currency from the date informed within the last six months.

# Test
* To run the unit and coverage tests you can use the following maven commands.

**To unit tests:**

```bash
mvn clean test
```
**To coverage:**
```bash
mvn verify
```

and you can check the coverage report in the **target/site/jacoco/index.html** file

# Implementation

There are 7 main parts in this project **Transaction Control**, **ExchangeRate Control**, **Client**, **Configuration**, **Exception**, **Search Criteria** and **Migrations**.

## Transaction Control

Transaction control is the part responsible to do all the operations involving transactions, such as saving, retrieving, listing, sorting, filtering, paginating and converting amount based on exchange rate.

Create a transaction with a date, a description and an amount (will be rounded the nearest cent) like in the following payload example. `POST` it to `/transactions`.

``` json
{
 	"date": "2020-10-12",
  	"description": "Transaction example",
  	"amount": 80.25
}
```
Please check Swagger and API-Docs for more samples.


## ExchangeRate Control

This part use the **Client** part to retrieve data from the **Treasury Reporting Rates of Exchange** and provide information about valid country currencies, and their exchange rates history comparing with US Dollar.

## Client
This model connect the system to the **Treasury Reporting Rates of Exchange** using Spring OpenFeign to build a declarative WebClient that will consume the following endpoints using the following parameters:

**To retrieve all valid currencies:**
``` bash
https://api.fiscaldata.treasury.gov/services/api/fiscal_service/v1/accounting/od/rates_of_exchange?fields={fields}&filter=record_date:gte:{data}&page[size]=350

Real example:

https://api.fiscaldata.treasury.gov/services/api/fiscal_service/v1/accounting/od/rates_of_exchange?fields=country_currency_desc&filter=record_date:gte:2022-10-25&page[size]=5
```

<details>
    <summary>Response example</summary>

``` json
{
    "data": [
        {
            "country_currency_desc": "Afghanistan-Afghani"
        },
        {
            "country_currency_desc": "Albania-Lek"
        },
        {
            "country_currency_desc": "Algeria-Dinar"
        },
        {
            "country_currency_desc": "Angola-Kwanza"
        },
        {
            "country_currency_desc": "Antigua & Barbuda-E. Caribbean Dollar"
        }
    ],
    "meta": {
        "count": 5,
        "labels": {
            "country_currency_desc": "Country - Currency Description"
        },
        "dataTypes": {
            "country_currency_desc": "STRING"
        },
        "dataFormats": {
            "country_currency_desc": "String"
        },
        "total-count": 173,
        "total-pages": 35
    },
    "links": {
        "self": "&page%5Bnumber%5D=1&page%5Bsize%5D=5",
        "first": "&page%5Bnumber%5D=1&page%5Bsize%5D=5",
        "prev": null,
        "next": "&page%5Bnumber%5D=2&page%5Bsize%5D=5",
        "last": "&page%5Bnumber%5D=35&page%5Bsize%5D=5"
    }
}

```
</details>

**To get exchange information for specified currency from the date informed within the last six months:**
``` bash
https://api.fiscaldata.treasury.gov/services/api/fiscal_service/v1/accounting/od/rates_of_exchange?fields={fields}&filter=country_currency_desc:eq:{currency},record_date:gte:{startDate},record_date:lte:{endDate}&sort=-record_date

Real example:

https://api.fiscaldata.treasury.gov/services/api/fiscal_service/v1/accounting/od/rates_of_exchange?fields=country_currency_desc,exchange_rate,record_date&filter=country_currency_desc:eq:Brazil-Real,record_date:gte:2022-07-01,record_date:lte:2023-01-01&sort=-record_date
```

<details>
    <summary>Response example</summary>

``` json
{
    "data": [
        {
            "country_currency_desc": "Brazil-Real",
            "exchange_rate": "5.286",
            "record_date": "2022-12-31"
        },
        {
            "country_currency_desc": "Brazil-Real",
            "exchange_rate": "5.397",
            "record_date": "2022-09-30"
        }
    ],
    "meta": {
        "count": 2,
        "labels": {
            "country_currency_desc": "Country - Currency Description",
            "exchange_rate": "Exchange Rate",
            "record_date": "Record Date"
        },
        "dataTypes": {
            "country_currency_desc": "STRING",
            "exchange_rate": "NUMBER",
            "record_date": "DATE"
        },
        "dataFormats": {
            "country_currency_desc": "String",
            "exchange_rate": "10.2",
            "record_date": "YYYY-MM-DD"
        },
        "total-count": 2,
        "total-pages": 1
    },
    "links": {
        "self": "&page%5Bnumber%5D=1&page%5Bsize%5D=100",
        "first": "&page%5Bnumber%5D=1&page%5Bsize%5D=100",
        "prev": null,
        "next": null,
        "last": "&page%5Bnumber%5D=1&page%5Bsize%5D=100"
    }
}

```
</details>

These two functions can be used in a simpler way in the ExchangeRate endpoints.

## Configuration
It's a simple part with just two classes, to have a specific place to put general configuration if needed. 
One class is to configure a bean to "see" a message source in the message.properties file. And the other is to configure the log of the requests received by the API.

## Exception
This part is used to deal with the exceptions that can happen during the program execution. It has a GlobalException handles that allows the program to get, treat and return an appropriate response 
to some common exception such as validation errors and resources not found. Also, it easy extendable to catch and treat any other kind of exception that may appear.

## Search Criteria
This part provide an easy way to create Specifications and Criteria to filter data from the database for a specific entity model.
In this project only Transaction is using this functionality, it allowed the Transaction list to filter by fields like *id* and *data* creation range.

## Migrations
This part store all database scripts in a historical order using Flyway to manage this. It allows the project to create the same database doesn't 
matter the environment what mitigate the risk of database schema change without the program adaptation. 
