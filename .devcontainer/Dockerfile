# Base image with Java 21 (compatible with JavaFX)
FROM mcr.microsoft.com/devcontainers/java:21

# Install JavaFX SDK
RUN apt-get update && \
    apt-get install -y wget unzip && \
    wget https://download2.gluonhq.com/openjfx/21/openjfx-21_linux-x64_bin-sdk.zip && \
    unzip openjfx-21_linux-x64_bin-sdk.zip -d /opt/javafx && \
    rm openjfx-21_linux-x64_bin-sdk.zip

# Set environment variables for JavaFX
ENV PATHFX=/opt/javafx/javafx-sdk-21/lib
ENV JAVA_FX_LIBS=$PATHFX
ENV JAVAFX_HOME=/opt/javafx/javafx-sdk-21

# Default working directory
WORKDIR /workspaces/${localWorkspaceFolderBasename}

# Set up Oracle JDBC driver placeholder (I can later mount the .jar)
RUN mkdir -p /opt/jdbc
ENV CLASSPATH=$CLASSPATH:/opt/jdbc/ojdbc8.jar:$JAVA_FX_LIBS
