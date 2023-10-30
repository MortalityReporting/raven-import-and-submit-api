FROM maven:3-jdk-11 AS builder
WORKDIR /usr/src/raven_import_and_submit_api
ADD . .
RUN mvn clean install -DskipTests -f /usr/src/raven_import_and_submit_api/VRDR_javalib/
RUN mvn clean install -DskipTests -f /usr/src/raven_import_and_submit_api/MDI_javalib/
RUN mvn clean install -DskipTests -f /usr/src/raven_import_and_submit_api/

FROM tomcat:9.0.43-jdk11-openjdk-slim
#move the WAR for contesa to the webapps directory
COPY --from=builder /usr/src/raven_import_and_submit_api/target/raven-import-and-submit-api-1.1.2.war /usr/local/tomcat/webapps/raven-import-and-submit-api.war
COPY --from=builder /usr/src/raven_import_and_submit_api/src/main/resources/* /usr/local/tomcat/src/main/resources/
