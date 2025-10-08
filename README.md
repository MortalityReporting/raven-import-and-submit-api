# Raven-Import-API

## Introduction

The **Raven-Import-API** is a backend API service designed for the Raven Mortality Case Management System. It integrates tightly with a FHIR server and its data source. It is recommended to deploy Raven-Import-API alongside the [raven-fhir-server](https://github.com/MortalityReporting/raven-fhir-server), which serves as the primary data source.

### Key Features
- **Import Service**: Converts XLSX or CSV data into MDI-FHIR resources and submits them to a raven-fhir-server.
- **Export Service**: **Depreciated** Retrieves and packages case data from the raven-fhir-server and submits it to state registrars in the standardized VRDR format.

---

## Requirements And Installation

Requirements for installation are:
* [Java-jdk version 10 or higher](https://www.oracle.com/java/technologies/javase-downloads.html)
* [The build tool maven](http://maven.apache.org/)
* A version of the MDI_java library. The correct version of the library is contained as a git submodule in this repository.

### Installation

1. **Install MDI Java Library**

Navigate to MDI_javalib within the project directory and run:

```bash
mvn install
```

2. **Package the Main Project**
 
Move to the project root directory and build the WAR package:

```bash
mvn package
```

3. **Run Locally**

Run the application with:
    
```bash
mvn spring-boot:run
```

### Dockerfile alternative

You can also use the provided Dockerfile for a containerized setup.

* [Docker installed locally](https://www.docker.com/get-started)

1. **Build the container**

```bash
docker build -t raven-import-api .
```

2. **Run the container**
   If the import api server is communicating with the other Raven modules within the same local docker environment, then this container needs to be in the same network.
   Include the network when running the import-api as shown below. Use env.list file to put your envirnment variables. 

```bash
docker run -d --restart unless-stopped -p 80:8080 --network <your_network_name> --env-file ./env.list --name raven_import raven-import-api```
```

You can verify the installation by using browser. In the address URL section of Internet browser, type the follow and return key. If you see a simple file upload page, then
your installation is good. The CSV template file for the import is available in https://github.com/MortalityReporting/raven-import-and-submit-api/blob/main/MDI-To-EDRS-Template.csv.  


## Configuration

All configuration for the project can be found in ```src/main/resources/application.properties```. All current configurable properties are set to read environment variables and are in the form ```some.key.path=${ENVIRONMENT_VARIABLE_HERE}```. It is recommended to replace these environment variables with hard-coded values to reflect your environment.

* ```fhircms.url``` A full url definition to the fhir base of the current raven-fhir-server
* ```fhircms.basicAuth.username``` If [basic authentication](https://swagger.io/docs/specification/authentication/basic-authentication/) is used to access raven-fhir-server, specify the username to authenticate with.
* ```fhircms.basicAuth.password``` If [basic authentication](https://swagger.io/docs/specification/authentication/basic-authentication/) is used to access raven-fhir-server, specify the password to authenticate with.
* ```fhircms.submit=true```. Specifies whether to use [fhir's batch submission](https://www.hl7.org/fhir/http.html#transaction) when importing into the system. Should not be changed unless you know what you're doing.
* ```canary.url=${CANARY_URL}```. **DEPRECIATED** Specifies a [canary instance](https://github.com/nightingaleproject/canary/) url for validating VRDR records before exporting records from the cms. If left blank, the VRDR records won't be validated from the canary tool, and is unnecessary if a canary instance is unavailable for use.
* ```submission.sources.sourceurl[x]```  **DEPRECIATED** Specifies a url source to export towards in the "export-all" workflow. The service will export VRDR records to each url specified. Can be omitted if the export-targeted mode is used instead (more on that in the exporting section)
* ```submission.sources.mode[x]```  **DEPRECIATED** Specifies a workflow submission mode to export towards in the "export-all" workflow. The currently supported modes are "axiell", "nightingale", and "vitalcheck", which are 3 different state registrar vendors with different workflows for exporting. As more vendors become available, more modes will be added to the system. Can be omitted if the export-targeted mode is used instead (more on that in the exporting section)

## Importing Tool
The web-based importing tool is accessible from the root URL of the deployed application. Use the file selector to upload a CSV or XLSX file of case data. The tool converts the data to FHIR resources, submits them to the raven-fhir-server, and provides a report of imported cases, including name, age, and status.

The reference files for xlsx and csv import are available as files within the top level of the project.
```
MDI-To-EDRS-Template.csv
MDI-To-EDRS-Template.xlsx
Tox-To-MDI-Template.xlsx
```

##Exporting Tool

**DEPRECIATED**

To use the exporting tool, you must make a REST request to the ```/submitEDRS``` endpoint
### /submitEDRS parameters

* `endpointURL`: A url definition of a receiving EDRS system
* `endpointMode`: A workflow mode of submitting, based on receiving system. 3 systems are supported right now: "Nightingale", "Axiell", and "Vitalcheck".
* `systemIdentifier`: A patient identifier system within the fhir system for the patient to be submitted. Works with `codeIdentifier` to define a full patient identifier.
* `codeIdentifier`: A patient identifier code within the fhir system for the patient to be submitted. Works with `systemIdentifier` to define a full patient identifier.

If no parameters are supplied then the configuration endpoints are used instead, as discussed in the configuration section
