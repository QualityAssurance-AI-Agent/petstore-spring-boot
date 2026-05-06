# petstore-spring-boot

Spring Boot 3 + Java 21 implementation of the Swagger Petstore OpenAPI 3.0 spec
(version **1.0.27**), a direct behavioral analog of the server hosted at
<https://petstore3.swagger.io/>.

The OpenAPI document at
`src/main/resources/openapi.yaml` is the authoritative contract; the
controllers and models in this project implement that contract.

## Prerequisites

- Java 21
- Maven 3.9+

## Build

```bash
mvn clean package
```

## Run

```bash
mvn spring-boot:run
```

or

```bash
java -jar target/petstore-spring-boot-1.0.27.jar
```

The server starts on port **8080** with context path `/api/v3`, matching the
spec's `servers[0].url` (`http://localhost:8080/api/v3`).

## Try it

| What                        | URL                                               |
|-----------------------------|---------------------------------------------------|
| OpenAPI spec (YAML)         | <http://localhost:8080/api/v3/openapi.yaml>       |
| Swagger UI                  | <http://localhost:8080/api/v3/swagger-ui.html>    |
| List pets by status         | <http://localhost:8080/api/v3/pet/findByStatus?status=available> |
| Inventory                   | <http://localhost:8080/api/v3/store/inventory>    |

Sample curl:

```bash
# Create a pet
curl -X POST http://localhost:8080/api/v3/pet \
  -H 'Content-Type: application/json' \
  -d '{"id":10,"name":"doggie","photoUrls":["http://example.com/doggie.jpg"],"status":"available"}'

# Fetch the pet
curl http://localhost:8080/api/v3/pet/10
```

## Implemented operations

All operations defined in the spec are implemented against in-memory stores:

- **Pet** – `PUT /pet`, `POST /pet`, `GET /pet/findByStatus`,
  `GET /pet/findByTags`, `GET /pet/{petId}`, `POST /pet/{petId}`,
  `DELETE /pet/{petId}`, `POST /pet/{petId}/uploadImage`
- **Store** – `GET /store/inventory`, `POST /store/order`,
  `GET /store/order/{orderId}`, `DELETE /store/order/{orderId}`
- **User** – `POST /user`, `POST /user/createWithList`, `GET /user/login`,
  `GET /user/logout`, `GET /user/{username}`, `PUT /user/{username}`,
  `DELETE /user/{username}`

State is held in `ConcurrentHashMap`s and seeded with a couple of pets on
startup; it is discarded when the process exits.

## Tests

```bash
mvn test
```

Tests use `MockMvc` to exercise a handful of end-to-end flows (create pet,
look up pet, find by status, inventory, 404 handling).

## Differences from the reference server

- No OAuth2 enforcement: the `petstore_auth` and `api_key` security schemes are
  declared in the spec but not enforced at runtime, matching the public
  `petstore3.swagger.io` server's behavior.
- Storage is purely in-memory; there is no persistence layer.
- The login endpoint issues an opaque token but does not actually validate
  credentials, matching the reference behavior.
