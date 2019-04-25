#!/bin/bash
service ssh start

export IFS=","
hosts=${HOSTS}

for host in ${hosts}; do
  echo "$host" >> /etc/hosts
done

java -jar kafka-streams-azure-assembly-1.0.0-SNAPSHOT.jar
