FROM eclipse-temurin:11-jre-jammy
RUN mkdir /opt/app
ADD maven/*.jar /opt/app/japp.jar
CMD java -XX:+PrintFlagsFinal $JAVA_OPTS -jar /opt/app/japp.jar