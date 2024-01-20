# expression-evaluator

Implemented with Java 21 Version

Java Version : 21
Maven Verison : 3.11.0

Expression Evaluator microservice runs on port 8080

Technical Document is in the project
Postman Collection is in the project

Under resource/dynamicModel there is a Customer.json file.This file is being read when loading project at the first time and Customer Object is being created dynamically. 
With such configuration, In further development more pojo.json files can be added to extend the expression evaluator functionality.

Docker File has been created. To Run the container follow below commmands
- To build docker image, go to project directory in terminal and run below command :
  
  docker build -t iremga/expression-evaluator:0.1 .

- To Run Docker image, go to docker desktop and run the image with filling out following option :
  ![image](https://github.com/IremGA/expression-evaluator/assets/21036082/6d973c8d-ab9a-46a5-aca2-553ee9f2a050)

- Then you can send requests defined in postman collection as in the sample screenshot:
  ![image](https://github.com/IremGA/expression-evaluator/assets/21036082/c4a10b8a-9567-49c0-a036-4b9a3bae3c43)


