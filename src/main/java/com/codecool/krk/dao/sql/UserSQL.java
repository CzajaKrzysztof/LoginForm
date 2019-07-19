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
            prepareSelectUser(userId, connection, query, user);
            connectionPool.releaseConnection(connection);
            return user;
        } catch (SQLException e) {
            System.err.println("SQLException in getUserById: " + e.getMessage());
        }
        throw new RuntimeException("No user by that id");
    }

    private void prepareSelectUser(int userId, Connection connection, String query, User user) throws SQLException {
        try(PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, userId);
            getUserData(stmt, user);
        }
    }

    private void getUserData(PreparedStatement stmt, User user) throws SQLException {
        try (ResultSet resultSet = stmt.executeQuery()) {
            while (resultSet.next()) {
                int userId = resultSet.getInt("user_id");
                String name = resultSet.getString("name");
                String type = resultSet.getString("type");
                user = new User(userId, name, type);
            }
        }
    }
}
