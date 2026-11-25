FROM maven:3.9.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
# Use Google's Maven Central mirror to avoid transient 429 rate limits from repo.maven.apache.org.
RUN mkdir -p /root/.m2 && printf '%s\n' '<settings xmlns="http://maven.apache.org/SETTINGS/1.2.0">' '  <mirrors>' '    <mirror>' '      <id>google-maven-central</id>' '      <name>Google Maven Central mirror</name>' '      <url>https://maven-central.storage-download.googleapis.com/maven2/</url>' '      <mirrorOf>central</mirrorOf>' '    </mirror>' '  </mirrors>' '</settings>' > /root/.m2/settings.xml
RUN mvn -B -ntp -DskipTests package
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/target/online-examination-system-1.0.0.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","app.jar"]