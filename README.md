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
___Application.yaml (Dafault username & password)___
```yaml
username-password:
  list:
    JOHN: "123"
    JANE: "XYZ"
    JACK: "QWE"
```