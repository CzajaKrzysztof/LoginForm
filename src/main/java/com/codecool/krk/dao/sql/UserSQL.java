package com.codecool.krk.dao.sql;

import com.codecool.krk.dao.IUserDao;
import com.codecool.krk.model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserSQL implements IUserDao {
    IConnectionPool connectionPool;

    public UserSQL(IConnectionPool connectionPool) {
        this.connectionPool = connectionPool;
    }

    @Override
    public User getUserById(int userId) {
        String query = "SELECT * FROM users WHERE user_id = ?";
        User user = null;
        try {
            Connection connection = connectionPool.getConnection();
            user = prepareSelectUser(userId, connection, query);
            connectionPool.releaseConnection(connection);
            return user;
        } catch (SQLException e) {
            System.err.println("SQLException in getUserById: " + e.getMessage());
        }
        throw new RuntimeException("No user by that id");
    }

    private User prepareSelectUser(int userId, Connection connection, String query) throws SQLException {
        User user = null;
        try(PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, userId);
            return user = getUserData(stmt);
        }
    }

    private User getUserData(PreparedStatement stmt) throws SQLException {
        User user = null;
        try (ResultSet resultSet = stmt.executeQuery()) {
            while (resultSet.next()) {
                int userId = resultSet.getInt("user_id");
                String name = resultSet.getString("name");
                String type = resultSet.getString("type");
                return user = new User(userId, name, type);
            }
        }
        throw new IllegalArgumentException("No user in database");
    }
}
