package com.codecool.krk.dao.sql;

import com.codecool.krk.dao.ILoginDAO;

public class LoginSQL implements ILoginDAO {
    IConnectionPool connectionPool;

    public LoginSQL(IConnectionPool connectionPool) {
        this.connectionPool = connectionPool;
    }
}
