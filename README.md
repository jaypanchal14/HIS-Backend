# HIS-Backend
Sprint-boot API to handle HIS functionalities. <br>

Steps to run :
1) Set up the database : <br>
    (a) Create database with name "his". <br>
    ```create database his;``` 
    <br>
    (b) Create a user first : <br>
    ```create user his@localhost identified by '12345678';``` 
    <br>
    (c) Provide all privileges: <br>
    ```GRANT ALL PRIVILEGES ON *.* to his@localhost WITH GRANT OPTION;```
    <br>
    In case you get issue while creating the user regarding password policy: <br>
    ```SET GLOBAL validate_password.policy=LOW;```
    <br>
   (d) Select database after logging-in with our user (in our case, "his") : <br>
    ```USE his;```
    <br>
2) Check the properties file, if you have provided all the correct configuration. <br><br>
3) Run the application with below command : <br>
    ```
    mvn spring-boot:run
    ```
    <br>

4)Shifts are defined as : <br>
0 : no shift, 1 : 12:00 AM to 08:59 AM, 2 : 09:00 AM to 04:59 PM, 3: 05:00PM to 11:59PM <br>
0 : no shift, 1 : 00:00 to 08:59, 2 : 09:00 to 16:59, 3 : 17:00 to 23:59 <br>

Note: <br>
Please use below headers whenever needed : <br>
```
headers: { "Content-Type": "application/json", "ngrok-skip-browser-warning": "true"}
```
<br>
(Only for Jay)<br>
For exposing your API to the internet, run below with ngrok : <br>
```ngrok http 8090 --domain=present-neat-mako.ngrok-free.app```