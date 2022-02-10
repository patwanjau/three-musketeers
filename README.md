# The Game of Three

The Game of three is a coding challenge designed to test the developer's ability to use technologies for microservices.
In this particular case, the tools were:

* Spring Boot
* Spring MVC
* Spring Data JPA
* MySQL Database Engine
* additionally, Spring Boot Test Framework

## Building the Project

This project is Gradle dependent, and therefore Gradle has been used as the underlying Build Tool.

This project requires use of Java 11 as the base JVM.

You must also set up a **MySQL Database** and have the setup the following properties for credentials:

| Property          | Value          |
|-------------------|----------------|
| Database Name     | game_of_three  |
| Database User     | gotcc_user     |
| Database Password | TheG@me0f3Ro11 |

**NB:** _These are dev credentials and will not be used in a production setup_.

To Build this project and run the project, execute the following command from the root of the project directory:

```sh
./gradlew clean build bootRun
```

This command builds the application and runs it on the configured port **8000**.

Open a web browser and navigate to **http://localhost:8000/**. This will open the account registration screen. You can
create an account here, after which you will be directed to the game setup screen.

At the game setup screen, you must choose which player starts the game (either the Computer or the registered player).

When you select the initiator, you will be directed to the play screen to continue playing the game.
