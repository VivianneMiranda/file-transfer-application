# Use an official Amazon Corretto 21 runtime as a parent image
FROM amazoncorretto:21

# Set the working directory inside the container
WORKDIR /src

# Copy the Java source files to the container
COPY src/FileServer.java /src/

# Compile the Java program
RUN javac FileServer.java

# Run the compiled Java program
CMD ["java", "FileServer"]
