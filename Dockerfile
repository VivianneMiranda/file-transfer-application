# Use an official OpenJDK 21 runtime as a parent image
FROM amazoncorretto:21

# Set the working directory
WORKDIR /src

# Copy the compiled Java program to the container
COPY FileServer.java /src/FileServer.java

# Compile the Java program
RUN javac FileServer.java

# Run the Java program
CMD ["java", "FileServer"]