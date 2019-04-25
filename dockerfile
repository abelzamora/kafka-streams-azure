FROM openjdk:8
COPY target/scala-2.12/kafka-streams-azure-assembly-1.0.0-SNAPSHOT.jar /usr/src/app/
COPY src/main/resources/init_container.sh /opt
WORKDIR /usr/src/app
RUN chmod 755 /opt/init_container.sh
CMD ["/opt/init_container.sh"]
