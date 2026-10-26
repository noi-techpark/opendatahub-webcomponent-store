<!--
SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>

SPDX-License-Identifier: CC0-1.0
-->

# ODH: Webcomponents

Web component store for OpenDataHub.

- [ODH: Webcomponents](#odh-webcomponents)
  - [Getting started](#getting-started)
    - [Prerequisites](#prerequisites)
    - [Source code](#source-code)
    - [Project Structure](#project-structure)
  - [Building and Testing](#building-and-testing)
    - [Build](#build)
    - [Running tests](#running-tests)
  - [Configuration](#configuration)
  - [Deployment](#deployment)
  - [API & swagger](#api--swagger)
    - [data-service](#data-service)
    - [delivery-service](#delivery-service)
  - [General Usage](#general-usage)
    - [Adding a webcomponent](#adding-a-webcomponent)
    - [wcs-manifest.json](#wcs-manifestjson)
  - [Docker environment](#docker-environment)
    - [Installation](#installation)
    - [Start and stop the containers](#start-and-stop-the-containers)
    - [Keycloak](#keycloak)
  - [Information](#information)
    - [Support](#support)
    - [Contributing](#contributing)
    - [Documentation](#documentation)
    - [License](#license)

## Getting started

These instructions will get you a copy of the project up and running
on your local machine for development and testing purposes.

### Prerequisites

To build the project, the following prerequisites must be met:

- Java JDK 21 or higher (e.g. [OpenJDK](https://openjdk.java.net/))
- [Maven](https://maven.apache.org/) 3.x

To run the project, the following prerequisites must be met:

- Java JDK 21 or higher (e.g. [OpenJDK](https://openjdk.java.net/))
- Tomcat webserver OR local IDE (eg. [IntelliJ](https://www.jetbrains.com/))
- PostgreSQL 9.6+
- SMTP Server

### Source code

Get a copy of the repository:

```bash
git clone https://github.com/noi-techpark/it.bz.opendatahub.webcomponents
```

Change directory:

```bash
cd it.bz.opendatahub.webcomponents/backend/
```

### Project Structure

The backend system is divided in four modules.

The 'common' module holds code that is shared between all parts of the application.

'crawler-service', 'data-service' and 'delivery-service' are distinct spring boot applications.

NB: The crawler is deprecated, and not developed further for now.

## Building and Testing

### Build

Build the project:

```bash
mvn clean install
```

### Running tests

The unit tests can be executed with the following command:

```bash
mvn clean test
```

## Configuration

Each service application comes with one or more profiles that can be configured.

> src/main/resources/application[-profile].properties

It is recommended to make a copy of ***application-dev.properties*** and name it
***application-local.properties*** as this file is already ignored by git. Run
the application with profile name "**local**".

**NOTE:** never change the deployment profile configuration unless you know what
you are doing!

You will have to configure the 'datasource' property for each application.

There are some specific settings in the 'application' section of the
configuration that might need customization: Please refer to each file and its
commands for further information. 

```
application.swaggerBaseUrl
```
set the base url for the Swagger API documentation

```
errorHandling.verboseException
```
true or false -> will enable or disable stacktrackes in the error responses of the API

application.swaggerBaseUrl
```
set the base url for the Swagger API documentation

```

```
errorHandling.verboseException
```
true or false -> will enable or disable stacktrackes in the error responses of the API
## Deployment

The project requires a Tomcat server as well as a PostgreSQL 9.6+ database server.

Build the project

```bash
mvn clean package
```

If you want to indivually build each module, you have to make sure that the common module is built first. eg:
```bash
cd common
mvn clean install
cd data-service
mvn clean package
```

There are three services that need to be deployed.

You will find the .war files to deploy in the 'target' folder of each service.

For an initial setup deploy 'data-service' first to have the database populated with tables.

## API & swagger

### data-service

data-service will expose the main api for webcomponents. it will also expose a
swagger-ui (/swagger-ui.html)

**NOTE**: admin routes require authentication with a bearer token. please
contact your administrator on how to obtain these

### delivery-service

delivery-service will expose api for retrieving the dist files needed to embed
webcomponents. it will also expose a swagger-ui (/swagger-ui.html)

## General Usage

### Adding a webcomponent

Webcomponents can either be managed via the admin API ~~or via the origins method
using the crawler.~~

**NOTE:** The crawler is deprecated. Use the webcompstore-cli python program to interact with the API.

### wcs-manifest.json

To register with the webcomponents hub a wcs-manifest.json file must be in your
repository's root directory. A manifest file should look like this:

```
{
  "title": "Generic Map",
  "description": "Generic Map to access the Open Data Hub Mobility API v2",
  "descriptionAbstract": "Generic Map to access the Open Data Hub Mobility",
  "license": "GPL-3.0",
  "repositoryUrl": "https://github.com/your-organization/your-webcomponent.git",
  "copyrightHolders": [
    {
      "name": "NOI",
      "email": "info@noi.bz.it",
      "organization": "NOI S.p.A",
      "organizationUrl": "https://noi.bz.it"
    }
  ],
  "authors": [
    {
      "name": "NOI",
      "email": "info@noi.bz.it",
      "organization": "NOI S.p.A",
      "organizationUrl": "https://noi.bz.it"
    }
  ],
  "image": "wcs-logo.png",
  "searchTags": ["map"],
  "dist": {
    "basePath": "dist",
    "files": [
      "map_widget.min.js"
    ]
  },
  "configuration": {
    "tagName": "map-widget",
    "options": [
      {
        "key": "domain",
        "type": "select",
        "label": "Domain",
        "options": {
          "values": ["mobility", "tourism"],
          "default": "mobility"
        }
      },
      {
        "key": "station-types",
        "type": "multiselect",
        "label": "Station Types",
        "options": {
          "values": ["CreativeIndustry","EChargingStation","EChargingPlug"],
          "default": []
        }
      }
    ]
  }
}
```

The configuration section of the manifest is needed for the configurator to know what options to include.
```
"configuration": {
    "tagName": "map-widget",
    "options": [
      {
        "key": "singleSelect",
        "type": "select",
        "label": "a single select",
        "required": true,
        "options": {
          "values": ["optionA", "optionB"],
          "default": "optionA"
        }
      },
      {
        "key": "multiselectField",
        "type": "multiselect",
        "label": "a multiselect field",
        "required": true,
        "options": {
          "values": ["optionA","optionB","optionC"],
          "default": []
        }
      },
      {
        "key": "sample-A",
        "type": "text",
        "required": false,
        "options": {
          "default": "Test String"
        }
      },
      {
        "key": "sample-B",
        "type": "textarea",
        "required": false,
        "options": {
          "default": "A multi-\nLine Text"
        }
      },
      {
        "key": "sample-C",
        "type": "number",
        "required": false,
        "options": {
          "default": "342",
          "min": 0,
          "max": 600,
          "step": 1
        }
      },
      {
        "key": "sample-D",
        "type": "number",
        "required": false,
        "options": {
          "default": "34.2",
          "min": 0,
          "max": 60.04,
          "step": 0.01
        }
      },
      {
        "key": "nullField",
        "type": "null",
        "required": false,
        "options": {
          "label": "a field that is either present or absent, it has no value, just a key",
          "default": null
        }
      },
      {
        "key": "booleanField",
        "type": "bool",
        "required": false,
        "options": {
          "label": "a boolean field",
          "default": false
        }
      },
      {
        "key": "sample-F",
        "type": "object",
        "required": false,
        "options": {
          "default": {}
        }
      }
    ]
  }
```

## Docker environment

For local development you can use docker to run a PostgreSQL server and a Mailhog.

### Installation

Install [Docker](https://docs.docker.com/install/) (with Docker Compose) locally
on your machine.

### Start and stop the containers

Before start working you have to configure your environment. Copy/paste
.env.example into .env file, and start the Docker containers:

```
docker-compose up --build --detach
```

After finished working you can stop the Docker containers:

```
docker-compose stop
```

### Keycloak

You can also run your own, local authentication server using docker. While this
is not a complete guide, the following link offers a good starting point on how
to do so: https://www.keycloak.org/getting-started/getting-started-docker

## Information

### Support

For support, please contact [help@opendatahub.com](mailto:help@opendatahub.com).

### Contributing

If you'd like to contribute, please follow the following instructions:

https://docs.opendatahub.com/en/latest/guidelines/contributors.html

### Documentation

More documentation can be found at
[https://docs.opendatahub.com](https://docs.opendatahub.com).

### License

The code in this project is licensed under the GNU AFFERO GENERAL PUBLIC LICENSE
Version 3 license. See the [LICENSE.md](LICENSE.md) file for more information.
