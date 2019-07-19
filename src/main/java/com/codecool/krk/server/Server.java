package com.codecool.krk.server;

import com.codecool.krk.dao.ICredentialsDAO;
import com.codecool.krk.dao.ILoginDAO;
import com.codecool.krk.dao.IUserDao;
import com.codecool.krk.dao.sql.*;
import com.codecool.krk.server.handler.Login;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.sql.SQLException;

public class Server {

    private final String URL = "jdbc:postgresql://127.0.0.1:5432/LoginForm";
    private static final String USER = "loginUser";
    private final String PASSWORD = "loginform";
    public Server() {

    }

    public void startServer() throws IOException {
        // create a server on port 8000
        HttpServer server = HttpServer.create(new InetSocketAddress(8000), 0);

        // set routes
        IConnectionPool connectionPool = null;
        try {
            connectionPool = ConnectionPool.create(URL, USER, PASSWORD);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        ICredentialsDAO credentialsDAO = new CredentialSQL(connectionPool);
        ILoginDAO loginDAO = new LoginSQL(connectionPool);
        IUserDao userDao = new UserSQL(connectionPool);

        server.createContext("/", new Login(credentialsDAO, loginDAO, userDao));
        server.setExecutor(null); // creates a default executor

        // start listening
        server.start();
    }
}
