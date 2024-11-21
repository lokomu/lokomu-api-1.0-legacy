# Lokomu - Legacy API 1.0

## Not in development.

💚 The original name for the project was "del". It means "share" in Norwegian. Share your stuff and time with others. 

🎓 This code was developed as part of a [Bachelor's project](https://ntnuopen.ntnu.no/ntnu-xmlui/handle/11250/3078083) at NTNU Trondheim, Norway, for a B.Sc. in Computer Science Engineering.

🚀 The upgraded and polished Kotlin version of this code is now [open sourced!](https://github.com/lokomu/lokomu-api-2.0-legacy)

#### Environment variables

To run our backend, a configuration file is needed.
- Create a ``.env`` file in the projects root directory.
- Copy and paste the following fields into the file.
- If you want to, you can edit the default values with your own configuration. Especially the password should be randomly generated and hard to guess. Remember to make sure the configuration is consistent with the frontend.

```file
POSTGRES_DB=deldb
POSTGRES_IP=localhost
POSTGRES_PORT=5432
POSTGRES_USER=deladmin
POSTGRES_PASS=09!98@87#76$65%54^43&32&21*
SERVER_PORT=3000
SPRING_USER=admin
SPRING_PASS=09!98@87#76$65%54^43&32&21*10+
SECRET_KEY=0sgGagGJJ2880KGLfkkala9G93937gGghaoGAOagk881
```

#### Running with Docker compose

By far the easiest way to run our backend is through Docker Compose. This is because it automatically configures and runs both the PostgreSQL database and the backend for you.

To do this you need to install [Docker](https://docs.docker.com/get-docker/), and if you're on Linux you also need to install [Docker Compose](https://docs.docker.com/compose/install/) independently.

When you have installed Docker and Docker Compose, simply run

```bash
docker-compose up
```

Docker will now start two containers; one running a PostgreSQL database and one running our backend application.


When you want to shut down the containers run

```bash
docker-compose down
```

If you edit the code, you will need to rebuild the application with

*Note: if you want to change any docker/environment variables you also need to shut down the containers before rebuilding.*

```bash
docker-compose up --build
```

If you want to delete the database contents or change anything about the database configuration (for example the environment variables); shut down the containers, then delete the Docker volume containing the database data.

```bash
docker-compose down
docker volume rm del-backend_del-data
```


#### Running natively

First and foremost, to run our backend you need a running [PostgreSQL](https://www.postgresql.org/download/) database.

You also need to install the [JDK](https://www.oracle.com/java/technologies/downloads/) (Java Development Kit). Under development of the API, version 17 LTS (Long Term Support) was used. 

Afterwards, you need to install [Apache Maven](https://maven.apache.org/index.html), our dependency manager of choice.

Then you need to uncomment the PostgreSQL config in the ``application.properties`` file located at ``src/main/resources/``. 

Finally, you can run the API with

```bash
  mvn spring-boot:run
```


## Running Tests

To run tests, run the following command

```bash
  mvn clean test
```
After running the tests a test coverage report will be produced. This report is located in the folder `target/site`

When building through Docker Compose, Docker automatically runs the tests and stops the launching process at failures.

## API Documentation
[API Docs via swagger](http://localhost:3000/api/swagger-ui/index.html#/)

## Author

This project was created by [Titas Virbickas](https://github.com/titusvi).

A heartfelt thank you to Zara Mudassar for her contributions to the development of the app during the bachelor thesis!
