package com.codecool.krk.dao.sql;

import com.codecool.krk.dao.IUserDao;

public class UserSQL implements IUserDao {
    IConnectionPool connectionPool;

    public UserSQL(IConnectionPool connectionPool) {
        this.connectionPool = connectionPool;
    }
}
