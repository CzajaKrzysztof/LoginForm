package com.codecool.krk.server.handler;

import com.codecool.krk.dao.ILoginDAO;
import com.codecool.krk.dao.ISessionDAO;
import com.codecool.krk.dao.IUserDao;
import com.codecool.krk.helper.PasswordHasher;
import com.codecool.krk.model.User;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.*;
import java.net.HttpCookie;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Login implements HttpHandler {
    ISessionDAO sessionDAO;
    ILoginDAO loginDAO;
    IUserDao userDao;
    PasswordHasher passwordHasher;

    public Login(ISessionDAO sessionDAO, ILoginDAO loginDAO, IUserDao userDao, PasswordHasher passwordHasher) {
        this.sessionDAO = sessionDAO;
        this.loginDAO = loginDAO;
        this.userDao = userDao;
        this.passwordHasher = passwordHasher;
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        String response = "";
        String method = httpExchange.getRequestMethod();
        String cookieStr = httpExchange.getRequestHeaders().getFirst("Cookie");

        if (cookieStr != null) {
            HttpCookie cookie = HttpCookie.parse(cookieStr).get(0);
            if(sessionDAO.isSessionPresent(cookie.getValue())){
                response = handleExistingSession(httpExchange, method, cookieStr, response);
            } else {
                response = "<html><body>" +
                        "<p>I don't know You" +
                        "</body></html>";
            }
        } else {
            response = handleNewSession(httpExchange, response, method);
        }

        sendResponse(httpExchange, response);
    }

    private String handleNewSession(HttpExchange httpExchange, String response, String method) throws IOException {
        if(method.equals("GET")) {
            response = "<html><body>" +
                    addLoginFormStructure() +
                    "</body></html>";
        }
        if(method.equals("POST")){
            InputStreamReader isr = new InputStreamReader(httpExchange.getRequestBody(), "utf-8");
            BufferedReader br = new BufferedReader(isr);
            String formData = br.readLine();

            System.out.println(formData);
            Map inputs = parseFormData(formData);

            String login = (String) inputs.get("login");
            String password = (String) inputs.get("password");
            response = testUserLogin(httpExchange, login, password);

        }
        return response;
    }

    private String testUserLogin(HttpExchange httpExchange, String login, String password) {
        String response = "";
        if(loginDAO.isLoginPresent(login)) {
            response = testUserPassword(httpExchange, login, password);
        } else {
            System.out.println("Incorrect login: " + login);
            response = "<html><body>" +
                    "<p>Invalid login</p>" +
                    addLoginFormStructure() +
                    "</body></html>";
        }
        return response;
    }

    private String testUserPassword(HttpExchange httpExchange, String login, String password) {
        String response = "";
        String salt = loginDAO.selectSaltByLogin(login);
        String hashedPassword = passwordHasher.hashPassword(salt + password);
        if(loginDAO.isPasswordCorrect(login, hashedPassword)) {
            createSessionForAuthenticatedUser(httpExchange, login);
            User user = userDao.getUserById(loginDAO.selectUserIdByLogin(login));
            response = "<html><body>" +
                    "<p>Hello "+ user.getName() +" </p>" +
                    addLogOutButton() +
                    "</body></html>";
        } else {
            System.out.println("Incorrect password: " + password);
            response = "<html><body>" +
                    "<p>Invalid password</p>" +
                    addLoginFormStructure() +
                    "</body></html>";
        }
        return response;
    }

    private void createSessionForAuthenticatedUser(HttpExchange httpExchange, String login) {
        UUID uuid = UUID.randomUUID();
        HttpCookie cookie = new HttpCookie("sessionId", String.valueOf(uuid.toString()));
        httpExchange.getResponseHeaders().add("Set-Cookie", cookie.toString());
        int userId = loginDAO.selectUserIdByLogin(login);
        sessionDAO.insertSessionData(uuid.toString(), userId);
    }

    private String handleExistingSession(HttpExchange httpExchange, String method, String cookieStr, String response) {
        HttpCookie cookie = HttpCookie.parse(cookieStr).get(0);
        if(method.equals("GET")) {
            User user = userDao.getUserById(sessionDAO.selectUserIdBySessionId(cookie.getValue()));
            response = "<html><body>" +
                    "<p>Hello "+ user.getName() +" </p>" +
                    addLogOutButton() +
                    "\nsession id: " + cookie.getValue() +
                    "</body></html>";
        }
        if(method.equals("POST")){
            long COOKIE_MAX_AGE = 0;
            sessionDAO.deleteSessionData(cookie.getValue());
            cookie.setValue("");
            cookie.setPath("/");
            cookie.setMaxAge(COOKIE_MAX_AGE);
            httpExchange.getResponseHeaders().add("Set-Cookie", cookie.toString());

            response += "Logged out!";
        }
        return response;
    }

    private String addLoginFormStructure() {
        return "<form method=\"POST\">\n" +
                "  Login:<br>\n" +
                "  <input type=\"text\" name=\"login\" placeholder=\"Login\">\n" +
                "  <br>\n" +
                "  Password:<br>\n" +
                "  <input type=\"password\" name=\"password\" placeholder=\"**********\">\n" +
                "  <br><br>\n" +
                "  <input type=\"submit\" value=\"Submit\">\n" +
                "</form> ";
    }

    private String addLogOutButton() {
        return "<form method=\"POST\">\n" +
                "  <input type=\"submit\" value=\"Log out\">\n" +
                "</form> ";
    }

    private void sendResponse(HttpExchange httpExchange, String response) throws IOException {
        httpExchange.sendResponseHeaders(200, response.length());
        OutputStream os = httpExchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }

    private static Map<String, String> parseFormData(String formData) throws UnsupportedEncodingException {
        Map<String, String> map = new HashMap<>();
        String[] pairs = formData.split("&");
        for(String pair : pairs){
            String[] keyValue = pair.split("=");
            String value = new URLDecoder().decode(keyValue[1], "UTF-8");
            map.put(keyValue[0], value);
        }
        return map;
    }
}
