<!--
SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>

SPDX-License-Identifier: CC0-1.0
-->

# Open Data Hub: Web Component Store

This repository contains the source code of the Open Data Hub Web Component
Store. It is a store to collect and preview our web components. 

[![REUSE Compliance](https://github.com/noi-techpark/opendatahub-webcomponent-store/actions/workflows/reuse.yml/badge.svg)](https://github.com/noi-techpark/opendatahub-docs/wiki/REUSE)

[![CI/CD data-service (api)](https://github.com/noi-techpark/opendatahub-webcomponent-store/actions/workflows/data-service.yml/badge.svg)](https://github.com/noi-techpark/opendatahub-webcomponent-store/actions/workflows/data-service.yml)
[![CI/CD delivery-service (cdn)](https://github.com/noi-techpark/opendatahub-webcomponent-store/actions/workflows/delivery-service.yml/badge.svg)](https://github.com/noi-techpark/opendatahub-webcomponent-store/actions/workflows/delivery-service.yml)
[![CI/CD frontend](https://github.com/noi-techpark/opendatahub-webcomponent-store/actions/workflows/frontend.yml/badge.svg)](https://github.com/noi-techpark/opendatahub-webcomponent-store/actions/workflows/frontend.yml)

Functionalities are as follows: 
  - Search 
  - Filter
  - Configure and preview
  - Copy a code snippet to be integrated in your web page 

In addition, it connects the source code of each web component to the actual
representation. 

This repository is divided into two sub-projects, namely:
  - [frontend](frontend/README.md)
  - [backend](backend/README.md)

## Local webcomponent development
To test your webcomponent locally you can start an instance of the store on your local machine and deploy your created webcomponent to it.  

To start it follow these steps:
- `cp .env.example .env`
- Adjust .env with your values for WC_PATH (the absolute path of you webcomponent)
- Adjust ports in .env if they have conflicts with services already running on your machine
- `docker-compose up -d`
- Wait until the containers are running. You can check the current state with  
  `docker-compose logs --tail 400 -f`  
  (or simply wait until your computer fan gets silent again ;-) )
- Access the store on `localhost:8999` (or the port you defined) and you'll see your webcomponent

To publish a new version of your webcomponent:
- Increase version number WC_VERSION in your .env file
- `docker-compose up wcstore-cli` 

To stop and delete everything:
- `docker-compose stop`
- in case you want to delete your test do `[sudo] rm -f workspace` and `docker-compose rm -f -v`

## REUSE

This project is [REUSE](https://reuse.software) compliant, more information about the usage of REUSE in NOI Techpark repositories can be found [here](https://github.com/noi-techpark/odh-docs/wiki/Guidelines-for-developers-and-licenses#guidelines-for-contributors-and-new-developers).

Since the CI for this project checks for REUSE compliance you might find it useful to use a pre-commit hook checking for REUSE compliance locally. The [pre-commit-config](.pre-commit-config.yaml) file in the repository root is already configured to check for REUSE compliance with help of the [pre-commit](https://pre-commit.com) tool.

Install the tool by running:
```bash
pip install pre-commit
```
Then install the pre-commit hook via the config file by running:
```bash
pre-commit install
```
