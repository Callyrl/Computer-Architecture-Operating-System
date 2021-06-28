package com.company;

// MODULES USED:
import java.io.*;
import java.net.Socket;
import java.util.HashSet;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

//--------------------------------------------------------------------------------------------------------------------//

// Creating a new thread every time a client is accepted
// Used to handle communication with existing clients simultaneously
// This allows the main thread to continuously accept clients

public class Client extends Thread
{
    // VARIABLES
    private final Socket client_Socket;
    private final Server server1;
    private String login = null;
    private OutputStream output;
    private HashSet<String> groupSet = new HashSet<>();


    // CONSTRUCTOR
    // server1 is passed as parameter to allow each client to access the server
    public Client(Server server1, Socket client_Socket)
    {
        this.server1 = server1;
        this.client_Socket = client_Socket;
    }

    // METHODS

    @Override
    public void run()
    {
        // Exception Handling
        try { clientSocketHandling(); }
        catch(IOException | InterruptedException e) { e.printStackTrace(); }
    }

    // Method to handle communication
    // Exception Handling added
    private void clientSocketHandling() throws IOException, InterruptedException
    {
        if(Thread.interrupted()){throw new InterruptedException();}

        else
        {
            String line;

            // Reading and obtaining data or input from clients
            InputStream input = client_Socket.getInputStream();
            this.output = client_Socket.getOutputStream();

            // Bufferreader is used to read the data line by line
            BufferedReader reader = new BufferedReader(new InputStreamReader(input));

            // While loop to keep reading the input message per line until client types "exit"
            while((line = reader.readLine())!= null)
            {
                // if(Thread.interrupted()) { throw new InterruptedException(); }
                //else {}
                // Splitting our line into individual tokens (based on white spaces)
                String[] tokens = StringUtils.split(line);

                if (tokens != null && tokens.length >0)
                {
                    // First token will be the one to initiate commands from clients
                    String command = tokens[0];

                    // COMMANDS:

                    // If client types "exit", program will stop reading the line
                    // Connection will be closed automatically by server
                    if("logout".equalsIgnoreCase(command) || "exit".equalsIgnoreCase(command))
                    {
                        logoutHandling();
                        break;
                    }

                    // If first index of what client types starts with login, they will be added to list of online clients
                    else if("login".equalsIgnoreCase(command)) { loginHandling(output, tokens); }

                    // If first index of what client types starts with message, they will be able to chat with other online clients
                    // Direct Messaging
                    else if("message".equalsIgnoreCase(command))
                    {
                        // To ensure that the whole message will be outputted
                        // So everything after the first two tokens will be considered as text
                        String[] tokenText = StringUtils.split(line, null, 3);
                        messageHandling(tokenText);
                    }

                    // If first index of what client types starts with join, they will be able to join a group chatroom
                    else if("join".equalsIgnoreCase(command)) { joinGroup(tokens); }

                    // If first index of what client types starts with leave, they will be able to join a group chatroom
                    else if("leave".equalsIgnoreCase(command)) { leaveGroup(tokens); }

                    // If an unknown command is prompted by client, this error message will show
                    else
                    {
                        String message = "Unrecognized command " + line + "\n";
                        output.write(message.getBytes());
                    }
                }
            }
            //client_Socket.close();
        }
    }


    private String getLogin() { return login; }


    // Method to handle the login process of clients
    private void loginHandling(OutputStream output, String[] tokens) throws IOException {
        if(tokens.length == 3)
        {
            String username = tokens[1];
            String password = tokens[2];

            // Allows guests to join the server
            if(username.equals("guest") && password.equals("guest") || uusername.equals("cally") && password.equals("iloverachel") || username.equals("rachel") && password.equals("aimeeforlife") || username.equals("aimee") && password.equals("cally4ver"))
            {
                String message = "logging in\n";
                output.write(message.getBytes());

                // Setting the username of the client
                this.login = username;

                // Notifying that client has connected
                System.out.println(username + " successfully logged in \n");

                // Iterating through the list of clients
                List<Client> client_List = server1.getClientList();

                // Used to notify new client, which clients are already online
                for (Client client : client_List)
                {
                    // Filtering out current client as to avoid notifying themselves when they go online
                    if(client.getLogin() != null)
                    {
                        if(!username.equals(client.getLogin()))
                        {
                            String onlineStatus =  client.getLogin() + ": online\n";
                            send(onlineStatus);
                        }
                    }
                }

                // Used to send a broadcast message to all clients notifying that someone went online
                String status = username + " is online\n";
                for (Client client : client_List)
                {  if(!username.equals(client.getLogin())) { client.send(status); } }
            }

            else
            {
                String message = "unsuccessful login\n";
                output.write(message.getBytes());
                System.err.println(username + " failed to log in");
            }
        }
    }


    // A method to access the output stream and send messages to the clients
    private void send(String message) throws IOException
    { if(login != null) { output.write(message.getBytes()); } }


    // Method to verify whether the group is part of the groupSet or exists in the server connection
    public boolean isMember(String groupName){ return groupSet.contains(groupName); }


    // Method to handle clients when they join a chatroom
    private void joinGroup(String[] tokens)
    {
        if (tokens.length > 1)
        {
            String groupName = tokens[1];

            // Set is used for storing client's memberships in each chatroom
            // Adding the group or chatroom to the set
            groupSet.add(groupName);
        }
    }


    private void leaveGroup(String[] tokens)
    {
        if (tokens.length > 1)
        {
            String groupName = tokens[1];

            // Removing the group or chatroom from the set
            groupSet.remove(groupName);
        }
    }


    // Method to handle communications between clients
    private void messageHandling(String[] tokens) throws IOException
    {
        // The person who will receive the message
        String recipient = tokens[1];
        String text = tokens[2];

        // Checking to see whether client is direct messaging or chatting in a chatroom
        // Chatroom chat indicated by the '*' symbol
        boolean isGroupChat = recipient.charAt(0) == '*';

        // Iterating through the list of clients to find the recipient
        List<Client> client_List = server1.getClientList();
        for (Client client : client_List)
        {
            // Check whether conversation is really a group chat or not
            // Handles group messaging (sending messages to the group - broadcast messages to clients)
            if(isGroupChat)
            {
                // Checks to see if client is part of the chatroom
                if(client.isMember(recipient))
                {
                    // Formats how the message will look like as well as specify the sender of the message
                    String outputText = login + "[" + recipient + "]"+ ": " + text + "\n";
                    client.send(outputText);
                }
            }

            // Handles direct messaging (sending messages from one client to another)
            else
            {
                // If the recipient is the same as that in the client list, message will be sent
                if (recipient.equalsIgnoreCase(client.getLogin()))
                {
                    // Formats how the message will look like as well as specify the sender of the message
                    String outputText = login + ": " + text + "\n";
                    client.send(outputText);
                }
            }
        }
    }


    // Method that handles clients when they logout or quit the program
    private void logoutHandling() throws IOException
    {
        // Removes the client from the list
        server1.removeClient(this);

        // Iterating through the list of clients
        List<Client> client_List = server1.getClientList();

        // Notify other clients that someone logged out
        String status = login + " is offline\n";
        for (Client client : client_List)
        {  if(!login.equals(client.getLogin())) { client.send(status); } }

        client_Socket.close();
        System.out.println("Client logged out");
    }

}
