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

        if(method.equals("GET")){
            String cookieStr = httpExchange.getRequestHeaders().getFirst("Cookie");
            HttpCookie cookie;
            if (cookieStr != null) {  // Cookie already exists
                cookie = HttpCookie.parse(cookieStr).get(0);
                String sessionId = cookie.getValue();
                if(sessionDAO.isSessionPresent(sessionId)){
                    int userId = sessionDAO.selectUserIdBySessionId(sessionId);
                    User user = userDao.getUserById(userId);
                    response += "<h1>Hello" + user.getName() + "</p>\n<button id=\"logout\">logout</button>";
                }
            } else {
                response = "<html><body>" +
                        "<form method=\"POST\">\n" +
                        "  Login:<br>\n" +
                        "  <input type=\"text\" name=\"login\" placeholder=\"Login\">\n" +
                        "  <br>\n" +
                        "  Password:<br>\n" +
                        "  <input type=\"password\" name=\"password\" placeholder=\"**********\">\n" +
                        "  <br><br>\n" +
                        "  <input type=\"submit\" value=\"Submit\">\n" +
                        "</form> " +
                        "</body></html>";
            }
        }

        if(method.equals("POST")){
            String cookieStr = httpExchange.getRequestHeaders().getFirst("Cookie");
            HttpCookie cookie;
            if (cookieStr != null) {  // Cookie already exists
                cookie = HttpCookie.parse(cookieStr).get(0);
                String sessionId = cookie.getValue();
                if(sessionDAO.isSessionPresent(sessionId)){
                    // remove session ---------------------------------------------------------

                }
            }
            else { // Create a new cookie
                InputStreamReader isr = new InputStreamReader(httpExchange.getRequestBody(), "utf-8");
                BufferedReader br = new BufferedReader(isr);
                String formData = br.readLine();
                System.out.println(formData);
                Map inputs = parseFormData(formData);
                String login = (String) inputs.get("login");
                String password = (String) inputs.get("password");
                if(loginDAO.isLoginPresent(login)){
                    String salt = loginDAO.selectSaltByLogin(login);
                    if(loginDAO.isPasswordCorrect(login, passwordHasher.hashPassword(password + salt))){
                        UUID uuid = UUID.randomUUID();
                        cookie = new HttpCookie("sessionId", String.valueOf(uuid.toString()));
                        httpExchange.getResponseHeaders().add("Set-Cookie", cookie.toString());
                        int userId = loginDAO.selectUserIdByLogin(login);
                        sessionDAO.insertSessionData(uuid.toString(), userId);
                        User user = userDao.getUserById(userId);
                        response = "<html><body>" +
                                "<h1>Hello" + user.getName() + "</p>\n" +
                                "<button id=\"logout\">logout</button>\n" +
                                "</body><html>";
                    }
                }
            }


        }

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
