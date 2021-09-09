ARG BUILD_HOME=/fungalf-the-grey

FROM gradle:7-jdk11 AS build-image

ARG BUILD_HOME
ENV BOT_HOME=$BUILD_HOME
WORKDIR $BOT_HOME

COPY --chown=gradle:gradle build.gradle.kts $BOT_HOME/
COPY --chown=gradle:gradle src $BOT_HOME/src

RUN gradle --no-daemon build installDist

FROM openjdk:11

ARG BUILD_HOME
ENV BOT_HOME=$BUILD_HOME
COPY --from=build-image $BOT_HOME/build/libs ./libs

ENTRYPOINT "libs/install/bin/fungalf-the-grey"
