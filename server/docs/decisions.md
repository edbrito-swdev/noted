# Summary

The exercise was for a fullstack developer. I am mostly a backend developer so I decided to focus on backend features and only included features in the frontend that would highlight backend decisions and how they could interact with each other.

For the backend, I developed main requirements plus some extras (shared notes and notes with images).

# Tech stack

## Frontend

	HTML5 + Bootstrap + Vue:
	
		- Just having a minimal website running to interact with the backend;
		- Vue is small and easy enough integrate easily into a simple site.
		 
## Server/Backend

**REST** server

Programming language: **Java**

	- JAX-RS (Jersey) + Grizzly
		- Application is just a common Java executable;
		- Minimal configuration;
		- Easy to run;
		- Easy to develop for.
	- MySQL Database
	- Redis (running on docker; used for tokens)
	- jUnit

Implemented:

	- User registration / login / logout
	- CRUD for notes
	- Sharing notes
	- Images on notes
		- Images can only be accessed by authenticated people related to the image
	- Basic logging (log4j)
		- Logs come only for "Main" class;
		- Not directing to any logging management system.
	- Authentication
		- Using JAX-RS filters;
		- Using tokens stored in Redis database;
			- No OAuht, no token revokation.
	
Testing:

	- No unit tests were implemented;
	- Manual testing using Postman.

	For the exercise I wanted to show how to develop the features for the backend.
	I decided not to implement automated testing features since I needed some time 
	to make a proper testing environment with the databases (MySQL and Redis) as to 
	have accurate results.


Not using:

	- ORM - No real benefit in using ORM for such a small project;
	- Data migration (flyway) - We could/should make database migrations
	for changes to the DB during development but for a small project like
	this it doesn't really make much sense;
	- HTTPS.
	
Not implemented:
	
	- Integration testing
		- For integration testing with our DB, if we had flyway set up, we
		could have an in-memory DB for running the tests. 
	- Security measures:
		- Not doing CSRF prevention;
		- Could/should use provider to sanitize the user input being 
		received and prevent it from going directly to the services;
		- Passwords are not using salt.

# Database

	MySQL Database using a simple schema.
	No DB migrations were used.
	
# Documentation

	Postman collection with the endpoints and authorization.
	

	