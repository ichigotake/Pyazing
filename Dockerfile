FROM ichigotake/docker-android-dev

MAINTAINER ichigotake <ichigotake.san@gmail.com>

ENV APP_ROOT /opt/Application

# Download repository
RUN apt-get install git
RUN git clone https://github.com/ichigotake/Pyazing $APP_ROOT

# Resolve dependencies
RUN cd $APP_ROOT && ./gradlew

# Add files
ADD gradle.properties $APP_ROOT/gradle.properties
ADD key.jks $APP_ROOT/pyazing_key.jks

# Build and upload!
RUN cd $APP_ROOT  && ./gradlew assembleRelease uploadDeployGateRelease

