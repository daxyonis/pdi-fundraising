FROM eclipse-temurin:17-jre-ubi9-minimal
RUN mkdir /opt/app
ADD maven/*.jar /opt/app/japp.jar
ADD  keys/*.p12 /opt/app/
RUN chmod 600 /opt/app/*.p12
CMD java -XX:+PrintFlagsFinal $JAVA_OPTS -jar /opt/app/japp.jar