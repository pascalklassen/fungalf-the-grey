FROM maven AS build
WORKDIR /app
COPY . .
RUN mvn package

FROM openjdk:8
COPY --from=app /app/target/fungalf-the-grey.jar /usr/bot
ENTRYPOINT ["java", "-jar", "fungalf-the-grey.jar"]
