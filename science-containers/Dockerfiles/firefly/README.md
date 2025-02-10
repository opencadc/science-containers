# CADC SSO Library
## Overview
The CADC SSO library provides classes and interfaces for handling Single Sign-On (SSO) authentication within the CADC (Canadian Astronomy Data Centre) environment. The primary purpose of this library is to facilitate secure authentication and authorization mechanisms for accessing CADC services.

## Key Components
### TokenRelay Class
The TokenRelay class is the core component of this library. It retrieves the authentication token from the SSO cookie and provides methods to interact with the token.

#### Key Methods
- `getAuthToken()`: Retrieves the authentication token from the SSO cookie. It first gets the request agent from the server context and then looks for the SSO cookie using the predefined cookie name. If the cookie is found, it extracts the token value and the domain of the cookie. The method then checks if the domain of the cookie is allowed. If the domain is allowed, it creates a new Token object with the token value and logs the successful retrieval. If the domain is not allowed or the cookie is not found, it logs the appropriate message and returns null.

- `setAuthCredential(HttpServiceInput inputs)`: Sets the authorization credential for the given HTTP service input. This method retrieves an authentication token and, if the token is not null and the request URL requires an authorization credential, sets the "Authorization" header of the HTTP service input to "Bearer " followed by the token ID.

- `getRequestAgent()`: Retrieves the request agent from the server context. This method is package-private to allow for testing with a mock request agent.

## Environment Variables
The library uses the following environment variables to configure the SSO cookie properties and downstream service properties:

- `CADC_SSO_COOKIE_NAME`: The name of the SSO cookie. Default is "CADC_SSO".
- `CADC_SSO_COOKIE_DOMAIN`: The domain of the SSO cookie. Default is ".canfar.net".
- `CADC_ALLOWED_DOMAIN`: The domain of the downstream service. Default is ".canfar.net".

## Build Instructions

To build the project, use the provided Dockerfile and docker-compose.yaml.

```bash
git clone https://github.com/opencadc/science-containers.git
cd science-containers/science-containers/Dockerfiles/firefly/
```

To build the Docker image, run the following command:

```bash
docker build --platform=linux/amd64 -t opencadc/firefly:latest .
```

Alternatively, using Docker Compose, navigate to the project directory containing the `docker-compose.yaml` file and run the following command:

```bash
docker-compose up
```

## Usage

The Docker Compose setup will start the Firefly service on port 8080. You can access it via http://localhost:8080.


