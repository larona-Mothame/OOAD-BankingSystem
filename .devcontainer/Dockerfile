# Use official Java 21 JDK image
FROM eclipse-temurin:21-jdk

# Install Maven, git, curl (for future use)
RUN apt-get update && \
    apt-get install -y maven git curl && \
    rm -rf /var/lib/apt/lists/*

# Set default working directory
WORKDIR /workspace

# Default shell
CMD [ "bash" ]
