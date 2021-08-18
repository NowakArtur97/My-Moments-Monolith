# MyMoments

## Table of Contents

- [General info](#general-info)
- [Demo](#demo)
- [Setup](#setup)
- [Built With](#built-with)
- [Features](#features)
- [To Do](#to-do)
- [Endpoints List](#endpoints-list)
- [Status](#status)
- [Screenshots](#screenshots)

## General info

REST API created while writing the thesis entitled: "Functionality problems of monolitical architecture and
microservices in web applications". Version in microservices
architecture: https://github.com/NowakArtur97/My-Moments-Microservices.

## Demo

The application demo is available on the Heroku platform: https://my-moments-monolith.herokuapp.com/swagger-ui.html.<br/>
It may take a while for the application to start.

To access the endpoints you must have an account. You can use the previously prepared account:

```json
# POST /api/v1/authentication
# Content-Type: application/json
{
  "username": "user",
  "password": "user"
}
```

## Setup

To start the application, in the folder, enter the following commands in command line:

- `gradle build -x test -Dspring.profiles.active=dev`
- `docker-compose up -d`
  Go to: `http://YOUR_DOCKER_IP_OR_LOCALHOST:8088/swagger-ui.html`, where YOUR_DOCKER_IP is your docker machine IP
  address (or localhost). To shut down the containers enter:
- `docker-compose down`

For a development use commands:

- `docker-compose -f docker-compose.dev.yml up -d`
- `docker-compose -f docker-compose.dev.yml down`

Use the login details provided above to generate the token:

```json
# POST /api/v1/authentication
# Content-Type: application/json
{
  "username": "user",
  "password": "user"
}
```

or create new account by sending the appropriate request (the profile object and image are optional):

```json
# POST /api/v1/registration
# Content-Type: application/json
{
  "username":"newUser",
  "password":"Password1!",
  "matchingPassword":"Password1!",
  "email":"email@something.com",
  "profile":{
    "about":"profile description",
    "gender":"user's gender (MALE, FEMALE or UNSPECIFIED)",
    "interests":"user interests",
    "languages":"user languages",
    "location":"user's location"
  }
}
```

The password must meet the following requirements:

- Must be between 6 and 30 characters long
- Passwords must match
- Mustn't contain the username
- Mustn't contain spaces
- Mustn't contain a repetitive string of characters longer than 3 characters
- Mustn't be on the list of popular passwords
- Must contain 1 or more uppercase characters
- Must contain 1 or more lowercase characters
- Must contain 1 or more special characters

Then generate JWT. The token can be generated using a username or email address. Password is required.

```json
# POST /api/v1/authentication
# Content-Type: application/json
{
"username": "newUser",
"password": "Password1!"
}
```

Then use the token as a Bearer Token using e.g. Postman or Swagger on /swagger-ui.html endpoint.

## Built With

- Java 11
- Spring (Boot, MVC, Security, Data JPA) - 2.4.2
- Swagger (Core, Bean Validators, UI) - 2.92
- Flyway - 7.11.0
- Lombok - 1.18.16
- jUnit5 - 5.7.2
- Mockito - 3.8.0
- Model Mapper - 2.4.0
- JSON Web Token Support For The JVM (jjwt) - 0.9.1
- Passay - 1.6.0
- Gradle - 6.8
- Docker
- MySQL
- Jenkins
- Heroku

## Features

- User registration
- JWT authorization
- Users endpoint (GET, PUT, DELETE)
- Posts endpoint (GET, POST, PUT, DELETE)
- Comments endpoint (POST, PUT, DELETE)
- Documentation created using Swagger 2
- Custom password validation
- Database Migrations with Flyway
- Deployment on Heroku

## To Do

- More endpoints

## Endpoints List:

### Security

| Method | URI                      | Action                                |
| ------ | ------------------------ | ------------------------------------- |
| `POST` | `/api/v1/registration`   | `Create an account`    |
| `POST` | `/api/v1/authentication` | `Generate a JWT`                        |

### Users

| Method    | URI                          | Action                                                               |
| --------- | ---------------------------- | -------------------------------------------------------------------- |
| `GET`     | `/api/v1/users/me/posts` | `Get user's posts`                     |
| `GET`     | `/api/v1/users/{id}/posts`        | `Get user's posts by user's id`
| `PUT`     | `/api/v1/users/me`        | `Update user information`                                      |
| `DELETE`     | `/api/v1/users/me`        | `Delete user`                                      |

### Posts

| Method    | URI                          | Action                                                               |
| --------- | ---------------------------- | -------------------------------------------------------------------- |
| `GET`     | `/api/v1/posts/{id}` | `Get information about a post`                     |
| `POST`     | `/api/v1/posts`        | `Create a post`
| `PUT`     | `/api/v1/posts/{id}`        | `Update post information`                                      |
| `DELETE`     | `/api/v1/posts/{id}`        | `Delete post with related comments`                                      |

### Comments

| Method    | URI                          | Action                                                               |
| --------- | ---------------------------- | -------------------------------------------------------------------- |
| `POST`     | `/api/v1/posts/{postId}/comments`        | `Add a comment to the post`
| `PUT`     | `/api/v1/posts/{postId}/comments/{commentId}`        | `Update the comment content`                                      |
| `DELETE`     | `/api/v1/posts/{postId}/comments/{commentId}`        | `Delete comment`                                      |

## Status

Project is: in progress

## Screenshots

![Documentation using Swagger 2](./screenshots/documentation.png)

Documentation using Swagger 2
