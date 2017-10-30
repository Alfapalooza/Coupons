# Base Image as CentOS
FROM centos 

RUN yum -y update

# Install Java 1.7
RUN yum -y install wget git unzip pwgen ca-certificates java-1.8.0-openjdk java-1.8.0-openjdk-devel
ENV PROJECT_WORKPLACE /usr/src/
RUN mkdir -p $PROJECT_WORKPLACE/activator $PROJECT_WORKPLACE/build $PROJECT_WORKPLACE/app
WORKDIR $PROJECT_WORKPLACE/activator

# Fetch Activator for Play
RUN wget http://downloads.typesafe.com/typesafe-activator/1.3.2/typesafe-activator-1.3.2.zip && \
    unzip typesafe-activator-1.3.2.zip
ENV PATH $PROJECT_WORKPLACE/activator/activator-1.3.2:$PATH

# Copy Files to Docker Container
WORKDIR $PROJECT_WORKPLACE/build
COPY . $PROJECT_WORKPLACE/build
RUN activator clean stage
RUN cp -R $PROJECT_WORKPLACE/build/target/universal/stage $PROJECT_WORKPLACE/app
ENV PORT 9000
EXPOSE $PORT

# This Runs with docker run
CMD $PROJECT_WORKPLACE/app/stage/bin/coupon-service -Dhttp.port=$PORT -Dplay.crypto.secret=DOCKER