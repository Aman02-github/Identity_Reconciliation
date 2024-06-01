Identity Reconciliation: A service that identifies and manages contacts based on email and phone numbers, ensuring correct handling of primary and secondary contact details.<br>

### To intsall all the required dependecies
```
mvn install
```
## Start the project
```
mvn spring-boot:run
```
Runs the application.<br>
[http://localhost:8080](http://localhost:8080).

Endpoint /identify will accept HTTP POST requests with a JSON body of the following format:<br>
```
{
  "email"?: string,
  "phoneNumber"?: number
}
```

A JSON payload containing the consolidated contact will be returned along with an HTTP 200 response.<br>

### Build application
```
mvn package
```
Package the compiled code in its distributable JAR.<br>

