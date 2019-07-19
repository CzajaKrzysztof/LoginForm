package com.codecool.krk.dao.sql;

import com.codecool.krk.dao.ILoginDAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginSQL implements ILoginDAO {
    IConnectionPool connectionPool;

    public LoginSQL(IConnectionPool connectionPool) {
        this.connectionPool = connectionPool;
    }

    @Override
    public boolean isLoginPresent(String login) {
        String query = "SELECT CASE WHEN EXISTS (" +
                "SELECT * FROM credentials WHERE login = ?)" +
                "THEN true " +
                "ELSE false END";
        boolean exists = false;
        try {
            Connection connection = connectionPool.getConnection();
            exists = executeCheckLogin(login, connection, query);
            connectionPool.releaseConnection(connection);
        } catch (SQLException e) {
            System.err.println("SQLException in isLoginPresent: " + e.getMessage());
        }
        return exists;
    }

    private boolean executeCheckLogin(String login, Connection connection, String query) throws SQLException{
        try(PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, login);
            return stmt.execute();
        }
    }

    @Override
    public String selectSaltByLogin(String login) {
        String query = "SELECT salt FROM credentials WHERE login = ?)";
        String salt = "";
        try {
            Connection connection = connectionPool.getConnection();
            prepareSelectSalt(login, connection, query, salt);
            connectionPool.releaseConnection(connection);
        } catch (SQLException e) {
            System.err.println("SQLException in selectSaltByLogin: " + e.getMessage());
        }
        return salt;
    }

    private void prepareSelectSalt(String login, Connection connection, String query, String salt) throws SQLException {
        try(PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, login);
            executeSelectSalt(stmt, salt);
        }
    }

    private void executeSelectSalt(PreparedStatement stmt, String salt) throws SQLException {
        try (ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                salt = rs.getString("salt");
            }
        }
    }

    @Override
    public boolean isPasswordCorrect(String login, String hashedPassword) {
        String query = "SELECT CASE WHEN EXISTS (" +
                "SELECT * FROM credentials WHERE login = ? AND password_hash = ?)" +
                "THEN true " +
                "ELSE false END";
        boolean exists = false;
        try {
            Connection connection = connectionPool.getConnection();
            exists = executeCheckPassword(login, hashedPassword, connection, query);
            connectionPool.releaseConnection(connection);
        } catch (SQLException e) {
            System.err.println("SQLException in isPasswordCorrect: " + e.getMessage());
        }
        return exists;
    }

    private boolean executeCheckPassword(String login, String hashedPassword, Connection connection, String query) throws SQLException{
        try(PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, login);
            stmt.setString(2, hashedPassword);
            return stmt.execute();
        }
    }

    @Override
    public int selectUserIdByLogin(String login) {
        String query = "SELECT user_id FROM credentials WHERE login = ?";
        int userId = 0;
        try {
            Connection connection = connectionPool.getConnection();
            prepareSelectUserIdByLogin(login, connection, query, userId);
            connectionPool.releaseConnection(connection);
        } catch (SQLException e) {
            System.err.println("SQLException in selectUserIdByLogin: " + e.getMessage());
        }
        return userId;
    }

    private void prepareSelectUserIdByLogin(String login, Connection connection, String query,int userId) throws SQLException{
        try(PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, login);
            executeSelectUserIdByLogin(stmt, userId);
        }
    }

    private void executeSelectUserIdByLogin(PreparedStatement stmt, int sauserId) throws SQLException {
        try (ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                sauserId = rs.getInt("user_id");
            }
        }
    }
}
