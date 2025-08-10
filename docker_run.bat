docker run --name pdi-jvm-limits -p 9000:8080 ^
-e KEYSTORE_FILENAME=/opt/app/keystore.p12 ^
-e SPRING_DATASOURCE_URL=jdbc:mysql://host.docker.internal:3309/pdi ^
-e GMAIL_PASSWORD=%GMAIL_PASSWORD% ^
-e APPLICATION_PAY_HASHKEY=%APPLICATION_PAY_HASHKEY% ^
-e JAVA_OPTS="-Xms512m -Xmx512m" ^
daxyonis/pdi-fundraising:6.1.0