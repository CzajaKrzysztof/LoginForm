package com.codecool.krk.dao.sql;

import com.codecool.krk.dao.ICredentialsDAO;

public class CredentialSQL implements ICredentialsDAO {
    IConnectionPool connectionPool;

    public CredentialSQL(IConnectionPool connectionPool) {
        this.connectionPool = connectionPool;
    }
}
