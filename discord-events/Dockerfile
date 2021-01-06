# Stage 1, build
ARG VERSION=11
FROM openjdk:${VERSION}-jdk-slim as builder

# Get gradle distribution 
COPY *.gradle gradle.* gradlew /src/
COPY gradle /src/gradle
WORKDIR /src
RUN ./gradlew --version

COPY . .
RUN ./gradlew --no-daemon shadowJar

# Stage 2, distribution container
FROM openjdk:${VERSION}-jre-slim
COPY --from=builder /src/build/libs/shadow.jar /bin/run.jar
COPY --from=builder /src/run.sh /bin/run.sh
RUN chmod +x /bin/run.sh
CMD ["/bin/run.sh"]
