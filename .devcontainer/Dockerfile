# Use official Ubuntu image as base
FROM ubuntu:22.04

# Avoid interactive prompts
ENV DEBIAN_FRONTEND=noninteractive

# Install Java 21
RUN apt-get update && \
    apt-get install -y wget unzip git curl && \
    wget https://download.oracle.com/java/21/latest/jdk-21_linux-x64_bin.tar.gz -O /tmp/jdk21.tar.gz && \
    mkdir -p /usr/lib/jvm && \
    tar -xzf /tmp/jdk21.tar.gz -C /usr/lib/jvm && \
    rm /tmp/jdk21.tar.gz

ENV JAVA_HOME=/usr/lib/jvm/jdk-21
ENV PATH="$JAVA_HOME/bin:$PATH"

# Install Maven (optional, for future DB dependencies)
RUN apt-get install -y maven

# Set default shell
CMD [ "bash" ]
