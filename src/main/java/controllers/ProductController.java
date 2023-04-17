package controllers;

import database.DatabaseService;
import entities.Order;
import entities.Product;
import entities.User;

import javax.swing.*;


import java.sql.*;
import java.util.ArrayList;
import java.util.Date;

public class ProductController {
    private final DatabaseService databaseService;
    private Connection connection;
    private PreparedStatement statement;
    private ResultSet resultSet;
    private UserController userController = new UserController();

    private ArrayList<Product> productList;

    public ProductController() throws SQLException {
        this.databaseService = new DatabaseService();
        this.productList = getAllProducts();
    }


    public void createProduct(Product product) {
        try {
            String query = "INSERT INTO products(name, quantity, price, sellingPrice) values(?, ?, ?, ?)";

            this.connection = this.databaseService.getConnection();
            this.statement = this.connection.prepareStatement(query);

            this.statement.setString(1, product.getName());
            this.statement.setInt(2, product.getQuantity());
            this.statement.setDouble(3, product.getPrice());
            this.statement.setDouble(4, product.getSellingPrice());

            if (this.statement.executeUpdate() == 1) {
                JOptionPane.showMessageDialog(null, "Product created successfully");
            } else {
                JOptionPane.showMessageDialog(null, "Failed to create product");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Failed to create product");
        } finally {
            this.databaseService.closeConnections(this.connection, null, this.statement);
        }
    }

    public void updateProduct(Product product) {
        try {
            String query = "UPDATE products SET name = ?, quantity = ?, price = ?, sellingPrice = ? WHERE id = ?";

            this.connection = this.databaseService.getConnection();
            this.statement = this.connection.prepareStatement(query);

            this.statement.setString(1, product.getName());
            this.statement.setInt(2, product.getQuantity());
            this.statement.setDouble(3, product.getPrice());
            this.statement.setDouble(4, product.getSellingPrice());
            this.statement.setInt(5, product.getId());

            int rowsUpdated = this.statement.executeUpdate();

            if (rowsUpdated == 1) {
                JOptionPane.showMessageDialog(null, "Product updated successfully");
            } else {
                JOptionPane.showMessageDialog(null, "Failed to update product");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            this.databaseService.closeConnections(this.connection, null, this.statement);
        }
    }

    public String deleteProduct(Integer id) {
        try {
            String query = "DELETE from products WHERE id = ?";

            this.connection = this.databaseService.getConnection();
            this.statement = this.connection.prepareStatement(query);

            this.statement.setInt(1, id);

            if (this.statement.executeUpdate() == 1) {
                JOptionPane.showMessageDialog(null, "Product deleted successfully");
                return "Product deleted successfully";
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            this.databaseService.closeConnections(this.connection, null, this.statement);
        }

        JOptionPane.showMessageDialog(null, "Failed to delete product");
        return "Failed to delete product";
    }

    private Product createProductFromResultSet(ResultSet resultSet) throws SQLException {
        return new Product(
                resultSet.getInt("id"),
                resultSet.getString("name"),
                resultSet.getInt("quantity"),
                resultSet.getDouble("price"),
                resultSet.getDouble("sellingPrice"),
                resultSet.getTimestamp("createdAt"),
                resultSet.getTimestamp("updatedAt")
        );

    }



    private Product chooseProductFromList(ArrayList<Product> productList) {
        try {
            String[] productNames = new String[productList.size()];
            for (int i = 0; i < productList.size(); i++) {
                productNames[i] = productList.get(i).getName();
            }

            String selectedProductName = (String) JOptionPane.showInputDialog(null, "Choose a product:", "Product List", JOptionPane.QUESTION_MESSAGE, null, productNames, productNames[0]);

            for (Product product : productList) {
                if (product.getName().equals(selectedProductName)) {
                    String result = buyProduct(product.getId(), 1, 1);
                    System.out.println(result);
                    return product;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private String buyProduct(Integer productId, Integer quantity, int userId) {
        try {
            String getProductQuery = "SELECT * FROM products WHERE id = ?";
            this.connection = this.databaseService.getConnection();
            this.statement = this.connection.prepareStatement(getProductQuery);
            this.statement.setInt(1, productId);
            this.resultSet = this.statement.executeQuery();

            if (this.resultSet.next()) {
                Integer availableQuantity = this.resultSet.getInt("quantity");
                if (availableQuantity < quantity) {
                    JOptionPane.showMessageDialog(null, "Item is sold out");
                } else {
                    Integer newQuantity = availableQuantity - quantity;
                    String updateProductQuery = "UPDATE products SET quantity = ? WHERE id = ?";
                    this.statement = this.connection.prepareStatement(updateProductQuery);
                    this.statement.setInt(1, newQuantity);
                    this.statement.setInt(2, productId);
                    if (this.statement.executeUpdate() == 1) {
                        String insertPurchaseQuery = "INSERT INTO orders (userId, productId, quantity) VALUES (?, ?, ?)";
                        this.statement = this.connection.prepareStatement(insertPurchaseQuery);
                        this.statement.setInt(1, userId);
                        this.statement.setInt(2, productId);
                        this.statement.setInt(3, quantity);
                        this.statement.executeUpdate();
                        JOptionPane.showMessageDialog(null, "Product purchased successfully");
                    }
                }
            } else {

            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            this.databaseService.closeConnections(this.connection, this.resultSet, this.statement);
        }
        return null;
    }

    private ArrayList<Product> getAllProducts() throws SQLException {
        ArrayList<Product> products = new ArrayList<>();
        String query = "SELECT * FROM products";
        this.connection = this.databaseService.getConnection();
        PreparedStatement statement = connection.prepareStatement(query);
        ResultSet resultSet = statement.executeQuery();

        while (resultSet.next()){
            products.add(this.createProductFromResultSet(resultSet));
        }

        this.databaseService.closeConnections(connection, resultSet, statement);
        return products;
    }
    public void chooseAndBuyProducts() {
        ArrayList<Product> productList;
        try {
            productList = getAllProducts();
        } catch (SQLException e) {
            e.printStackTrace();
            return;
        }

        Product selectedProduct = chooseProductFromList(productList);
        if (selectedProduct == null) {
            return;
        }

        int quantity;
        try {
            quantity = Integer.parseInt(JOptionPane.showInputDialog("Enter quantity:"));
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Invalid quantity entered");
            return;
        }

        Integer currentUserId = userController.getUserIdFromCredentials();
        if (currentUserId == null) {
            return;
        }

        String result = buyProduct(selectedProduct.getId(), quantity, currentUserId);
        System.out.println(result);
    }

    public void showSellingHistoryForProduct(Product product) {
        ArrayList<Order> orderList = getAllOrdersForProduct(product.getId());
        StringBuilder sb = new StringBuilder();

        if (orderList.size() == 0) {
            sb.append("No orders found for this product.");
        } else {
            sb.append("Selling history for " + product.getName() + ":\n");
            for (Order order : orderList) {
                sb.append(order.getPurchaseDate() + " - Quantity: " + order.getQuantity() + "\n");
            }
            sb.append("\nTotal quantity sold: " + getTotalQuantitySoldForProduct(product.getId()));
            sb.append("\nTotal revenue: $" + getTotalPrice(orderList, product.getPrice()));
        }

        JOptionPane.showMessageDialog(null, sb.toString());
    }

    public int getTotalQuantitySoldForProduct(int productId) {
        int totalQuantitySold = 0;
        ArrayList<Order> orderList = getAllOrdersForProduct(productId);

        for (Order order : orderList) {
            totalQuantitySold += order.getQuantity();
        }

        return totalQuantitySold;
    }
    public double getTotalPrice(ArrayList<Order> orderList, double productPrice) {
        double totalPrice = 0;

        for (Order order : orderList) {
            totalPrice += order.getQuantity() * productPrice;
        }

        return totalPrice;
    }

    public ArrayList<Order> getAllOrdersForProduct(int productId) {
        ArrayList<Order> orderList = new ArrayList<>();
        String getOrderQuery = "SELECT * FROM orders WHERE productId = ?";

        try {
            this.connection = this.databaseService.getConnection();
            this.statement = this.connection.prepareStatement(getOrderQuery);
            this.statement.setInt(1, productId);
            this.resultSet = this.statement.executeQuery();

            while (this.resultSet.next()) {
                Order order = new Order(
                        this.resultSet.getInt("id"),
                        this.resultSet.getInt("userId"),
                        this.resultSet.getInt("productId"),
                        this.resultSet.getInt("quantity"),
                        this.resultSet.getTimestamp("purchaseDate")
                );
                orderList.add(order);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            this.databaseService.closeConnections(this.connection, this.resultSet, this.statement);
        }

        return orderList;
    }
}

