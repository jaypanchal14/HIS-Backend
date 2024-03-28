# HIS-Backend
Sprint-boot API to handle HIS functionalities. <br>

Steps to run :
1) Set up the database : <br>
    (a) Create a user first : <br>
    ```create user his@localhost identified by '12345678';```
    (b) Provide all privileges: <br>
    ```GRANT ALL PRIVILEGES ON *.* to his@localhost WITH GRANT OPTION;```
    <br>
    In case you get issue while creating the user regarding password policy: <br>
    ```SET GLOBAL validate_password.policy=LOW;```
    <br><br>
2) Check the properties file, if you have provided all the correct configuration. <br><br>
3) Run the application with below command : <br>
    ```mvn spring-boot:run```