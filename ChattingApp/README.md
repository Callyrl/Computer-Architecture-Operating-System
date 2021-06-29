# Computer-Architecture-Operating-System
Projects for Computer Architecture &amp; Operating System Module

## Installation and Preparations: ##
* common-lang3.jar
* any ASCII protocol application to provide a virtual terminal connection (e.g. Telnet, NetCat)

## Instructions for use: ##
1. Run Main.java
2. Open terminal and connect to the server
    * port number: 2402
3. Available commands:
    * **login**: log into an existing or guest account ("login _username_ _password_")
                 type "login guest guest" into the terminal to login as a guest
    * **message**: send a message to an existing user ("message _username_ _your-message_")
    * **join**: enter a group chat ("join _group-name_")
                type "message" + _your-message_ to broadcast to the group chat
    * **leave**: quit the group ("leave _group-name_")
    * **exit**: log out of the current account and exit the server, connection will be closed

### Notes: ###
* syntax for connecting to localhost or testing in the same device: {command of the ASCII protocol} localhost 2402
* users will be notified when a new user logs in or out
* succesful/failed connections to the server will be notified in the console
