FROM java:openjdk-8-jre-alpine
MAINTAINER martin scharm

# copy the package into the container
COPY jatter-${project.version}-jar-with-dependencies.jar /jatter.jar

# make it execute-able
ENTRYPOINT ["java", "-Dcom.sun.net.ssl.checkRevocation=false", "-jar", "/jatter.jar", "-c", "/jatter/config.properties"]
CMD [""]

