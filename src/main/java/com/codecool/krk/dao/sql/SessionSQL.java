package com.codecool.krk.dao.sql;

import com.codecool.krk.dao.ISessionDAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SessionSQL implements ISessionDAO {
    private IConnectionPool connectionPool;

    public SessionSQL(IConnectionPool connectionPool) {
        this.connectionPool = connectionPool;
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

    @Override
    public int selectUserIdBySessionId(String sessionId) {
        String query = "SELECT user_id FROM sessions WHERE session_id = ?";
        int userId = 0;
        try {
            Connection connection = connectionPool.getConnection();
            executeSelectUserIdBySessionId(connection, query, sessionId, userId);
            connectionPool.releaseConnection(connection);
            return userId;
        } catch (SQLException e) {
            System.err.println("SQLException: " + e.getMessage()
                    + "\nSQLState: " + e.getSQLState()
                    + "\nVendorError: " + e.getErrorCode());
        }
        throw new RuntimeException("No user_Id by that session_id");
    }

    private void executeSelectUserIdBySessionId(Connection connection, String query, String sessionId, int userId) throws SQLException {
        try(PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, sessionId);
            getUserId(stmt, userId);
        }
    }

    private void getUserId(PreparedStatement stmt, int userId) throws SQLException {
        try (ResultSet resultSet = stmt.executeQuery()) {
            while (resultSet.next()) {
                userId = resultSet.getInt("user_id");
            }
        }
    }
}
