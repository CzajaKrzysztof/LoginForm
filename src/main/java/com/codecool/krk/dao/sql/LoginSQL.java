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
                "ELSE false END AS result";
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
            return getBooleanResult(stmt);
        }
    }

    private boolean getBooleanResult(PreparedStatement stmt) throws SQLException {
        boolean result = false;
        try (ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                result = rs.getBoolean("result");
            }
        }
        return result;
    }

    @Override
    public String selectSaltByLogin(String login) {
        String query = "SELECT salt FROM credentials WHERE login = ?";
        String salt = "";
        try {
            Connection connection = connectionPool.getConnection();
            salt = prepareSelectSalt(login, connection, query, salt);
            connectionPool.releaseConnection(connection);
        } catch (SQLException e) {
            System.err.println("SQLException in selectSaltByLogin: " + e.getMessage());
        }
        return salt;
    }

    private String prepareSelectSalt(String login, Connection connection, String query, String salt) throws SQLException {
        try(PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, login);
            salt = executeSelectSalt(stmt, salt);
        }
        return salt;
    }

    private String executeSelectSalt(PreparedStatement stmt, String salt) throws SQLException {
        try (ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                salt = rs.getString("salt");
            }
        }
        return salt;
    }

    @Override
    public boolean isPasswordCorrect(String login, String hashedPassword) {
        String query = "SELECT CASE WHEN EXISTS (" +
                "SELECT * FROM credentials WHERE login = ? AND password_hash = ?)" +
                "THEN true " +
                "ELSE false END AS result";
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
            return getBooleanResult(stmt);
        }
    }

    @Override
    public int selectUserIdByLogin(String login) {
        String query = "SELECT user_id FROM credentials WHERE login = ?";
        int userId = 0;
        try {
            Connection connection = connectionPool.getConnection();
            userId = prepareSelectUserIdByLogin(login, connection, query, userId);
            connectionPool.releaseConnection(connection);
        } catch (SQLException e) {
            System.err.println("SQLException in selectUserIdByLogin: " + e.getMessage());
        }
        return userId;
    }

    private int prepareSelectUserIdByLogin(String login, Connection connection, String query,int userId) throws SQLException{
        try(PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, login);
            userId = executeSelectUserIdByLogin(stmt, userId);
        }
        return userId;
    }

    private int executeSelectUserIdByLogin(PreparedStatement stmt, int userId) throws SQLException {
        try (ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                userId = rs.getInt("user_id");
            }
        }
        return userId;
    }
}
