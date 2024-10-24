# AMEX API Project


## Prerequisites
- [Docker](https://docs.docker.com/get-docker/) installed and running
- [Docker Compose](https://docs.docker.com/compose/install/) installed
- [Java 17](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html) Installed

## Project Structure
- **docker/**: Contains Docker configuration files.
- **src/**: Contains the source code of the application.
- **gradle/**: Build automation configuration for managing dependencies and tasks.

## Setup Instructions

1. **Clone the Repository** (if not already cloned):
   ```bash
   git clone https://github.com/medicensolla/amex-assignment.git
   cd amex-api
   ```

2. **Build the Project (Optional)**:
   If needed, you can build the project locally using Gradle:
   ```bash
   ./gradlew build
   ```

3. **Run Docker Compose**:
   Ensure Docker is installed and running. To spin up the containers, run:
   ```bash
   docker-compose up --build
   ```

   This command will:
    - Build the Docker images if not already built.
    - Start the containers based on the configuration in the `docker-compose.yml` file.


5. **Stopping the Containers**:
   To stop the running containers, press `Ctrl+C` or run:
   ```bash
   docker-compose down
   ```