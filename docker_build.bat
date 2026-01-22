REM This command requires a buildpack image that doesn't exist for Spring Boot 4.0 yet
REM ./mvnw spring-boot:build-image

REM So we use our old fabric8 plugin instead
./mvnw clean package docker:build