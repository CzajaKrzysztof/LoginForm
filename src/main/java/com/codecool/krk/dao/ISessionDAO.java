package com.codecool.krk.dao;

public interface ISessionDAO {
    void insertSessionData(String sessionId, int userId);
    void deleteSessionData(String sessionId);
    int selectUserIdBySessionId(String sessionId);
}
