package com.company;

// MODULES USED:
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

//--------------------------------------------------------------------------------------------------------------------//

public class Server extends Thread
{
    // VARIABLES
    private final int portNumber;
    private ArrayList<Client> client_List = new ArrayList<>();  // Creating an ArrayList to store our clients


    // CONSTRUCTOR
    public Server(int portNumber)
    { this.portNumber = portNumber; }


    // METHODS

    // Method created to enable clients to have access to other clients from the list
    public List<Client> getClientList() { return client_List; }


    // We need to override the run method in every thread
    @Override
    public void run()
    {
        // IO Exception Handling added
        try
        {
            // Creating a new instance of the Server
            ServerSocket server_Socket = new ServerSocket(portNumber);

            // Infinite loops that allows multiple clients to connect by accepting the request to join the server
            // This is done so we can have a collection of clients we can iterate through later in the program
            // Allows us to communicate with different connections
            while (true)
            {
                // Accepts connection from Clients
                // Creates a new client_Socket every time a client is accepted
                // If there are no connections made, the accept method will not be executed
                // Adds the client to the ArrayList every time a connection is made

                System.out.println("Waiting for a connection...");
                Socket client_Socket = server_Socket.accept();
                System.out.println(client_Socket + " is connected...");
                Client client = new Client(this, client_Socket);
                client_List.add(client);
                client.start();
            }
        }
        catch (IOException e){e.printStackTrace();}
    }


    // Method to remove a client from the list of clients
    public void removeClient(Client client) { client_List.remove(client); }
}
