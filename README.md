# Service to manage a voting session

See the technical diagram of the architecture: https://www.lucidchart.com/documents/view/8e47ba52-c712-435b-857f-eb50cc42d930/0_0

## Requirements

1. Java - 1.8.x

2. Maven - 3.x.x


## Steps to Setup

**1. Clone the application**

```bash
git clone https://github.com/douggass/manage-voting-sessions-service.git
```

**2. Build and run the app using maven**

```bash
cd manage-voting-sessions-service
mvn package
java -jar target/manage-voting-sessions-service-1.0.0.jar
```

Alternatively, you can run the app without packaging it using -

```bash
mvn spring-boot:run
```

The server will start at <http://localhost:8089>.

## Exploring the Rest APIs

The application defines following REST APIs

```
1. POST /v1/session - Create a session (which contains a subject), not started

2. POST /v1/session/{sessionId}/vote - Create a vote in the session

3. PUT /v1/start-session - Start a session

4. GET /v1/session - List all sessions documents

5. GET /v1/associate - List all associates documents

```

## Running tests

The project also contains tests for all the rules. For running the tests, go to the root directory of the project and type `mvn test` in your terminal.