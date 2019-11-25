FROM maven:3.5.0-jdk-8-alpine
LABEL vendor="Skatteverket SPAR"

WORKDIR /spar
COPY src/ /spar/src
COPY pom.xml /spar/pom.xml

RUN mvn install
CMD mvn exec:java -Dexec.mainClass="se.statenspersonadressregister.referensimplementation.PersonsokExempel"
