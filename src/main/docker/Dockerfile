# Note: this Dockerfile is not used by the default docker_build script
FROM eclipse-temurin:21.0.7_6-jre-jammy
RUN mkdir /opt/app
ADD maven/*.jar /opt/app/japp.jar
ADD keys/*.p12 /opt/app/
RUN chmod 600 /opt/app/*.p12
CMD java -XX:+PrintFlagsFinal $JAVA_OPTS -jar /opt/app/japp.jar