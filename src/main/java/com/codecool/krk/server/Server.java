package com.codecool.krk.server;

import com.codecool.krk.dao.ILoginDAO;
import com.codecool.krk.dao.ISessionDAO;
import com.codecool.krk.dao.IUserDao;
import com.codecool.krk.dao.sql.*;
import com.codecool.krk.helper.PasswordHasher;
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
        HttpServer server = HttpServer.create(new InetSocketAddress(8000), 0);

        IConnectionPool connectionPool = null;
        try {
            connectionPool = ConnectionPool.create(URL, USER, PASSWORD);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        ISessionDAO sessionDAO = new SessionSQL(connectionPool);
        ILoginDAO loginDAO = new LoginSQL(connectionPool);
        IUserDao userDao = new UserSQL(connectionPool);
        PasswordHasher passwordHasher = new PasswordHasher();

        server.createContext("/", new Login(sessionDAO, loginDAO, userDao, passwordHasher));
        server.setExecutor(null);

        server.start();
    }
}
