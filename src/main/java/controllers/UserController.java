package controllers;

import database.DatabaseService;
import entities.User;

import javax.swing.*;
import java.security.Timestamp;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;

public class UserController {
    private final DatabaseService databaseService;
    private Connection connection;
    private PreparedStatement statement;
    private ResultSet resultSet;

    public UserController() {
        this.databaseService = new DatabaseService();
    }
    public User collectUserData() {
        String name = JOptionPane.showInputDialog(null, "Enter name:");
        String email = JOptionPane.showInputDialog(null, "Enter email:");
        String password = JOptionPane.showInputDialog(null, "Enter password:");
        String balanceStr = JOptionPane.showInputDialog(null, "Enter balance:");
        double balance = Double.parseDouble(balanceStr);

        return new User(name, email, password, balance);
    }

    public void createUser(User user) {
        try {
            String query = "INSERT INTO users(name, email, password, balance) values(?, ?, ?, ?)";

            this.connection = this.databaseService.getConnection();
            this.statement = this.connection.prepareStatement(query);

            this.statement.setString(1, user.getName());
            this.statement.setString(2, user.getEmail());
            this.statement.setString(3, user.getPassword());
            this.statement.setDouble(4, user.getBalance());

            if (this.statement.executeUpdate() == 1) {
                JOptionPane.showMessageDialog(null, "User created successfully");
            } else {
                JOptionPane.showMessageDialog(null, "Failed to create user");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            this.databaseService.closeConnections(this.connection, null, this.statement);
        }
    }

    public String updateUser(User user) {
        try {
            String query = "UPDATE users SET name = ?, email = ?, password = ?, balance = ? WHERE id = ?";

            this.connection = this.databaseService.getConnection();
            this.statement = this.connection.prepareStatement(query);

            this.statement.setString(1, user.getName());
            this.statement.setString(2, user.getEmail());
            this.statement.setString(3, user.getPassword());
            this.statement.setDouble(4, user.getBalance());

            if (this.statement.executeUpdate() == 1) {
                JOptionPane.showMessageDialog(null, "User updated successfully");
                return "User updated successfully";
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            this.databaseService.closeConnections(this.connection, null, this.statement);
        }

        JOptionPane.showMessageDialog(null, "Failed to update user");
        return "Failed to update user";
    }

    public void deleteUser(Integer userId) {
        String query = "DELETE from users WHERE id = ?";

        try (Connection connection = databaseService.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, userId);

            if (statement.executeUpdate() == 1) {
                JOptionPane.showMessageDialog(null, "User deleted successfully");
            } else {
                JOptionPane.showMessageDialog(null, "User not found");
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Failed to delete user");
            e.printStackTrace();
        }
    }

    private User createUserFromResultSet(ResultSet resultSet) throws SQLException {
        return new User(
                resultSet.getInt("id"),
                resultSet.getString("name"),
                resultSet.getString("email"),
                resultSet.getString("password"),
                resultSet.getDouble("balance"),
                resultSet.getTimestamp("createdAt"),
                resultSet.getTimestamp("updatedAt")
        );
    }
    public User getUserByEmail(String email) throws Exception {
        try {
            String query = "SELECT * FROM users WHERE email = ?";
            this.connection = databaseService.getConnection();
            this.statement = this.connection.prepareStatement(query);
            this.statement.setString(1, email);
            this.resultSet = this.statement.executeQuery();
            if (resultSet.next()) {

                return this.createUserFromResultSet(this.resultSet);

            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            this.databaseService.closeConnections(this.connection, this.resultSet, this.statement);
        }

        String errorMessage = "User with email address " + email + " not found!";
        JOptionPane.showMessageDialog(null, errorMessage);
        throw new Exception(errorMessage);
    }


    public Integer getUserIdFromCredentials() {
        String email = JOptionPane.showInputDialog(null, "Enter your email:");
        String password = JOptionPane.showInputDialog(null, "Enter your password:");

        try {
            String getUserQuery = "SELECT id FROM users WHERE email = ? AND password = ?";
            this.connection = this.databaseService.getConnection();
            this.statement = this.connection.prepareStatement(getUserQuery);
            this.statement.setString(1, email);
            this.statement.setString(2, password);
            this.resultSet = this.statement.executeQuery();

            if (this.resultSet.next()) {
                return this.resultSet.getInt("id");
            } else {
                JOptionPane.showMessageDialog(null, "Invalid email or password");
                return null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        } finally {
            this.databaseService.closeConnections(this.connection, this.resultSet, this.statement);
        }
    }

    public void viewOrdersByUser() {
        int userId = getUserIdFromCredentials();

        try {
            String getAllOrdersQuery = "SELECT * FROM orders WHERE userId = ?";
            connection = databaseService.getConnection();
            statement = connection.prepareStatement(getAllOrdersQuery);
            statement.setInt(1, userId);
            resultSet = statement.executeQuery();

            if (resultSet.next()) {
                StringBuilder sb = new StringBuilder("Orders:\n");
                do {
                    int orderId = resultSet.getInt("id");
                    int productId = resultSet.getInt("productId");
                    int quantity = resultSet.getInt("quantity");
                    Date orderDate = resultSet.getTimestamp("purchaseDate");
                    sb.append("Order ID: ").append(orderId).append("\n");
                    sb.append("Product ID: ").append(productId).append("\n");
                    sb.append("Quantity: ").append(quantity).append("\n");
                    sb.append("Order Date: ").append(orderDate).append("\n\n");
                } while (resultSet.next());
                JOptionPane.showMessageDialog(null, sb.toString());
            } else {
                JOptionPane.showMessageDialog(null, "No orders found for this user");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            databaseService.closeConnections(connection, resultSet, statement);
        }
    }
    public void signUp() {
        String name = JOptionPane.showInputDialog(null, "Enter your name:");
        String email = JOptionPane.showInputDialog(null, "Enter your email:");
        String password = JOptionPane.showInputDialog(null, "Enter your password:");
        Double balance = Double.valueOf(JOptionPane.showInputDialog(null, "Enter your balance:"));

        User newUser = new User(name, email, password, balance);
        //newUser.toString();
        createUser(newUser);

    }

    public User login() {
        String email = JOptionPane.showInputDialog(null, "Enter your email:");
        String password = JOptionPane.showInputDialog(null, "Enter your password:");

        try {
            User user = getUserByEmail(email);
            System.out.println(user.getEmail());
            if (user != null && user.getPassword().equals(password)) {
                JOptionPane.showMessageDialog(null, "Login successful!");
                return user;
            } else {
                JOptionPane.showMessageDialog(null, "Invalid email or password");
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
