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
            System.err.println("SQLException: " + e.getMessage()
                    + "\nSQLState: " + e.getSQLState()
                    + "\nVendorError: " + e.getErrorCode());
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
            System.err.println("SQLException: " + e.getMessage()
                    + "\nSQLState: " + e.getSQLState()
                    + "\nVendorError: " + e.getErrorCode());
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
            System.err.println("SQLException: " + e.getMessage()
                    + "\nSQLState: " + e.getSQLState()
                    + "\nVendorError: " + e.getErrorCode());
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
            System.err.println("SQLException: " + e.getMessage()
                    + "\nSQLState: " + e.getSQLState()
                    + "\nVendorError: " + e.getErrorCode());
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

    @Override
    public void insertSessionData(String sessionId, int userId) {
        String query = "INSERT INTO sessions VALUES (?, ?)";
        try {
            Connection connection = connectionPool.getConnection();
            executeInsertSessionData(connection, query, sessionId, userId);
            connectionPool.releaseConnection(connection);
        } catch (SQLException e) {
            System.err.println("SQLException: " + e.getMessage()
                    + "\nSQLState: " + e.getSQLState()
                    + "\nVendorError: " + e.getErrorCode());
        }
    }

    private void executeInsertSessionData(Connection connection, String query, String sessionId, int userId) throws SQLException {
        try(PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, sessionId);
            stmt.setInt(2, userId);
            stmt.execute();
        }
    }

    @Override
    public void deleteSessionData(String sessionId) {
        String query = "DELETE FROM sessions WHERE session_id = ?";
        try {
            Connection connection = connectionPool.getConnection();
            executeDeleteSessionData(connection, query, sessionId);
            connectionPool.releaseConnection(connection);
        } catch (SQLException e) {
            System.err.println("SQLException: " + e.getMessage()
                    + "\nSQLState: " + e.getSQLState()
                    + "\nVendorError: " + e.getErrorCode());
        }
    }

    private void executeDeleteSessionData(Connection connection, String query, String sessionId) throws SQLException {
        try(PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, sessionId);
            stmt.execute();
        }
    }
}
