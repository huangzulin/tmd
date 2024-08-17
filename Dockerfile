# Build stage
FROM node:lts-alpine as build-vue
RUN apk add --update git python3 make g++ && rm -rf /var/cache/apk/*
RUN git clone https://github.com/huangzulin/tmd-vue /app 2> /dev/null || git -C /app pull
WORKDIR /app
COPY package*.json ./
RUN npm install
COPY . .
RUN npm run build

# Build stage
FROM maven:3.9.7-eclipse-temurin-21 AS build-jar
COPY --from=build-vue /app/dist /home/app/src/main/resources/static
COPY src /home/app/src
COPY pom.xml /home/app
RUN mvn -f /home/app/pom.xml clean package -DskipTests

# Package stage
FROM eclipse-temurin:21
RUN  apt-get update && apt install openssl zlib1g-dev -y
WORKDIR /home/app
RUN mkdir -p "/home/app/data" && mkdir -p "/home/app/downloads"
COPY --from=build-jar /home/app/target/*.jar app.jar
EXPOSE 3222
ENTRYPOINT ["java","-jar","app.jar"]

