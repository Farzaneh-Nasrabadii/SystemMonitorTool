# Step 1: Use Amazon Corretto 21 based on a full OS (Linux)
FROM amazoncorretto:21

# Step 2: Install system monitoring tools needed by Java code (vmstat, procps, etc.)
RUN yum update -y && yum install -y procps-ng shadow-utils

# Step 3: Set working directory
WORKDIR /app

# Step 4: Copy the compiled fat JAR
COPY target/SystemMonitorTool-1.0-SNAPSHOT-jar-with-dependencies.jar app.jar

# Step 5: Expose WebSocket port
EXPOSE 8080

# Step 6: Run Java application
CMD ["java", "-jar", "app.jar"]