package com.codecool.krk;

import com.codecool.krk.server.Server;

import java.io.IOException;

public class App 
{
    public static void main( String[] args )
    {
        Server server = new Server();
        try {
            server.startServer();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
