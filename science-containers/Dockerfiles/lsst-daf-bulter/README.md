# CADC Butler Server

The CADC operates an LSST IDAC.  The CADC site provides a Butler server for
metadata search backed bu a PostgreSQL database.  Data files are stored
in the CADC Storage Inventory system.  The Butler server is configured to
provide back to the client the URL that points back to the CADC SI and the user
must then authenticate to the CADC SI to retrieve the data file.  Authentication
is managed by the Butler client and based on CADC access tokens.

## Overview

This document provides instructions for setting up the LSST Butler server. The 
Butler Server is a [uvicorn](https://www.uvicorn.org/) ASGI web server listening on port 8080.

## compose.yaml

The Docker Compose file (compose.yaml) defines the services required to run the Butler server.

To start the LSST Butler Server: in root directory run: `docker compose up`

## butler.env

The butler.env file contains environment variables that configure the 
uvicorn web server and the Butler app running on the server.

### UVICORN configuration
The following environment variables configure the uvicorn web server:
#### UVICORN_HOST="0.0.0.0"
The host address that the uvicorn server will bind to on startup.
#### UVICORN_PORT="8080"
The port that the uvicorn server will bind to on startup.
#### UVICORN_LOG_LEVEL="info"
The log level for the uvicorn server.

#### DAF_BUTLER_SERVER_REPOSITORIES

String that provies a list of Butler repositories that the server will serve. This variable provides the path
to find the dc2.yaml and dp1.yaml files.

#### DAF_BUTLER_SERVER_GAFAELFAWR_URL

is set to DISABLED to disable this feature of the Butler server, which is the LSST Gafaelfawr authentication service.

#### DAF_BUTLER_SERVER_AUTHENTICATION 

is set to cadc to ensure that the Butler
server does not pass back presigned URLs to clients.

#### PGPASS File Setup

* For regular user access to the Butler database set the PGPASSFILE
  environment variable to point to lsstuser.pgpass file.

* For administrative access to the Butler database set the PGPASSFILE
  environment variable to point to lsstadmin.pgpass file.

## dc2.yaml

The dc2.yaml file contains the configuration for the Butler database for the DC2 release

## dp1.yaml

The dp1.yaml file contains the configuration for the Butler database for the DP1 release

# Setting up the Butler Client

The Butler Client is configured by setting the Butler to:  
https://your.server.domain/api/butler/repo/dp1/butler.yaml