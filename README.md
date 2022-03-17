# Portfolio API
API service using Spring Framework to show a simple authentication process.

## Tech / Framework used
- [x] Spring Framework
- [x] Spring Security
- [x] WebFilter 
- [x] Exception Handling
- [x] Docker & Redis
- [x] Mockito Unit Test

## Installation
```bash
# prepare redis environment
docker-compose up -d

mvn clean install
mvn clean test

# execute
mvn clean spring-boot:run
```

## API 
___[Postman API Collection Document](https://documenter.getpostman.com/view/12110481/UVsMukcU)___
```batch
# /simple/api/v1/auth/signIn
# /simple/api/v1/auth/signOut
# /simple/api/v1/auth/refreshToken (receive token to generate new token pair)
# /simple/api/v1/officer/inquiry
```

## Usage
___JwtRequestFilter___
```java
// Filter class get login details from redis
var accessTokenRedis = authRedisRepository.getAccessTokenDetail(hashToken);
```
___SignIn___
```java
// SignIn class check username and password from application.yaml
var master = usernamePasswordProperty.getList();
var expectedPassword = master.get(username);

if (password.equals(expectedPassword)) {
    // generate token and store to redis process
}
```
___RestExceptionResolver___
```java
// catch any exception convert to Custom Response Model
if (ex instanceof UnauthorizedException) {
    processResponse(
            "X-UNAUTHORIZED",
            ex.getMessage(),
            HttpStatus.UNAUTHORIZED.value(),
            response);
} else if (ex instanceof CustomException) {
    processResponse(
            "X-CUSTOM",
            ex.getMessage(),
            HttpStatus.UNPROCESSABLE_ENTITY.value(),
            response);
} else {
    processResponse(
            "X-OTHER",
            ex.getMessage(),
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            response);
    logger.warn("Handling of [" + ex.getClass().getName() + "] resulted in Exception", ex);
}
```
___Application.yaml___
```yaml
# Default username & password
username-password:
  list:
    JOHN: "123"
    JANE: "XYZ"
    JACK: "QWE"

# Path that bypass authorization
filter:
  bypass:
    authorization:
      - /internal/**
      - /api/v1/auth/**
```