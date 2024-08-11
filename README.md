# File Transfer Application

## Overview

This is a simple file transfer application written in Java 21 that allows users to upload and download files over a network. The application includes both a server and a client. The server can handle multiple client connections concurrently using multithreading, and it is containerized using Docker for easy deployment.

## Features

- **File Upload**: Clients can upload files to the server.
- **File Download**: Clients can download files from the server.
- **Multithreading**: The server can handle multiple clients simultaneously.
- **Dockerized Server**: The server runs inside a Docker container, making it portable and easy to deploy.

## Requirements

- Java 21
- Docker (for running the server in a container)

## Project Structure

- **FileClient.java**: The client application that connects to the server to upload or download files.
- **FileServer.java**: The server application that handles file uploads and downloads.
- **Dockerfile**: Docker configuration file to containerize the server.

## How to Build and Run

### 1. Compile the Server and Client

Ensure you have Java 21 installed. Compile the Java files using the following commands:

```bash
mkdir out
javac -d out src/FileServer.java src/FileClient.java
```

### 2. Run the Server
You can run the server either directly on your machine or inside a Docker container.

Running Directly
```bash
java -cp out FileServer
```
Running with Docker

1. Build the Docker image:
```bash
docker build -t file-server .
```
2. Run the Docker container:
```bash
docker run -p 12345:12345 file-server
```

### 3. Run the Client
You can run the client to either upload or download files.

Upload a File:
```bash
java -cp out FileClient UPLOAD C:/path/to/your/file.txt
```

Download a File:
```bash
java -cp out FileClient DOWNLOAD file name C:/path/to/save/location/
```
** you need to have permission to write and read in this directory

## Logs and Monitoring

Both the server and client have extensive logging to monitor the file transfer process. Logs will be printed to the console, showing details like client connections, file uploads, and downloads.

## Example Usage
### 1. Start the server using Docker:
```bash
docker run -p 12345:12345 file-server
```

### 2. Run the client to upload a file:
```bash
java -cp out FileClient UPLOAD C:/Users/vivianne.miranda/Downloads/sample2.txt
```
### 3. Run the client to download a file:

```bash
java -cp out FileClient DOWNLOAD sample2.txt C:/path/to/save/location/
```

## Troubleshooting
- **Port Conflicts**: If the server port 12345 is already in use, change the port number in both FileServer.java and FileClient.java.
- **File Not Found**: Ensure that the file you are trying to download exists on the server.

## Future Enhancements
- **Security**: Implement secure file transfer using SSL/TLS.
- **Authentication**: Add user authentication to restrict file access.
- **Improved Client UI**: Develop a graphical user interface (GUI) for the client.

## Project developed for the Rede de Computadores I course.

- Vivianne Chaves de Miranda - 510927


<p align="center">
  <img alt="UFC" src="images/ufc-logo-universidade.png" width="400">
</p>

<h4 align="center">Universidade Federal do Ceará - 2024</h4>





