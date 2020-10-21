# AQA Training Engine

[![Build Status](https://travis-ci.org/tpolishchuk/aqa-training-engine.svg?branch=master)](https://travis-ci.org/tpolishchuk/aqa-training-engine)

AQA Test Engine is a simple application, that may help people to practice their UI test automation skills. If you want to try any new approach or a testing tool, but do not want to deal with complicated systems, just install an AQA TE locally and play with it as with any other web application.

### Requirements

The latest version of Docker platform

### Installation

- Clone the repository
- Navigate into the project root folder
- Run

```
docker-compose up
```

- Navigate to [http://localhost:8080/](http://localhost:8080/) after you see the following output

```
Started Application in X.XXX seconds
```

### Test entities

**User**

A user should be registered via [the registration page](http://localhost:8080/) and can log in to the dashboard via [the login page](http://localhost:8080/login). After successful registration or login, the user is redirected to [the home page](http://localhost:8080/home).

User actions have basic validation (e.g. the username should be unique, the wrong credentials do not let the user be signed in, etc.).

*Max user sessions:* 2

*Future plans:* to make a user registration process possible via Google and Facebook.

You can find more details in the [project Wiki](https://github.com/tpolishchuk/aqa-training-engine/wiki).

**Task**
 
An entry with a title, a deadline and an optional description, that is associated with a user. 

Tasks' scope for a certain user is reachable via [the tasks page](http://localhost:8080/tasks). The task entry can be created, edited, deleted or moved to another state by click on the status icon. 

Task actions support basic validation, so you can practice unhappy path scenarios.

*Future plans:* to make a task shareable between users, highlight a deadline if it is overdue.

You can find more details in the [project Wiki](https://github.com/tpolishchuk/aqa-training-engine/wiki).

**Note** 

An entry with a text body, that is associated with a user.

Notes' scope for a certain user is reachable via [the notes page](http://localhost:8080/notes) page. The note entry can be created, edited or deleted on the notes page. 

Notes actions support basic validation, so you can practice unhappy path scenarios.

You can find more details in the [project Wiki](https://github.com/tpolishchuk/aqa-training-engine/wiki).

### Additional functionality

**Random entries generation**

If you need bulk creation of random tasks or notes, just generate them via home page menu.

**Bulk delete**

User tasks or notes can be easily deleted from by a single click on the form button in a corresponding form.

### Technologies used in the project

- Spring Boot
- Spring Security
- Thymeleaf
- Bootstrap
- A bit of jQuery
