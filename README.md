# Coupon Service
Service that can decode North American GS1 Omnidirectional Coupon Databar.

### How to Run
To run the application
```
  activator run
```

To run in prod:
```
  activator stage
```

To test application:
```
  activator test
```

### Docker (Install [Docker](https://www.docker.com/get-docker))
To build the docker image, from the directory:
```
  docker build -t coupon_service .
```

Run the image using:
```
  docker run -p <external_port>:<internal_port> coupon_service
```

* Note that ```<external_port>``` should be the port exposed by the server that maps to the internal port, the ```<internal_port>``` is the port that the application will run in the docker container. The default port is '9000', but you can override it by specify an extra run parameter like: ``` -e PORT = <internal_port> ```.
