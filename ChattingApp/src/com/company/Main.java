package com.company;

// Running the server and actual program

public class Main
{
    public static void main(String[] args)
    {
        int portNumber = 2402;
        Server server1 = new Server(portNumber);

        // Starting the server
        //.start() will automatically kick off the main Thread
        server1.start();
        System.out.println("Starting server");
    }
}