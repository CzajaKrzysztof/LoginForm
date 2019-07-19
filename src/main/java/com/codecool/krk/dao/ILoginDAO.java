package com.codecool.krk.dao;

public interface ILoginDAO {
    boolean isLoginPresent(String login);
    String selectSaltByLogin(String login);
    boolean isPasswordCorrect(String login, String hashedPassword);
    int selectUserIdByLogin(String login);
}
