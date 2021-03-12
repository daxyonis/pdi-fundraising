FROM adoptopenjdk:11-jre-openj9
RUN mkdir /opt/app
ADD maven/*.jar /opt/app/japp.jar
CMD ["java", "-jar", "/opt/app/japp.jar"]