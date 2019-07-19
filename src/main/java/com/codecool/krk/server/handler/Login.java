package com.codecool.krk.server.handler;

import com.codecool.krk.dao.ICredentialsDAO;
import com.codecool.krk.dao.ILoginDAO;
import com.codecool.krk.dao.IUserDao;
import com.codecool.krk.helper.PasswordHasher;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;

public class Login implements HttpHandler {
    ICredentialsDAO credentialsDAO;
    ILoginDAO loginDAO;
    IUserDao userDao;
    PasswordHasher passwordHasher;

    public Login(ICredentialsDAO credentialsDAO, ILoginDAO loginDAO, IUserDao userDao, PasswordHasher passwordHasher) {
        this.credentialsDAO = credentialsDAO;
        this.loginDAO = loginDAO;
        this.userDao = userDao;
        this.passwordHasher = passwordHasher;
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {

    }
}
