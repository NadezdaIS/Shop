package controllers;

import entities.Product;
import entities.User;

import javax.swing.*;
import java.sql.SQLException;

public class MenuController {
    private final ProductController productController = new ProductController();
    private final UserController userController = new UserController();
    private final MenuController menuController = new MenuController();
    private User user = new User();
    private Product product = new Product();

    public MenuController() throws SQLException {
    }
    public void chooseLoginOrSignUp() {
        String[] options = {"Log in", "Sign up"};
        int choice = JOptionPane.showOptionDialog(null, "Choose an option:", "Login/Sign Up", JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, options[0]);

        if (choice == 0) {
            User user = userController.login();
            if (user.getName().equals("admin")) {
                startForAdmin();
                return;
            }
        } else if (choice == 1) {
            userController.signUp();
        } else {
            System.exit(0);
        }

        start();
    }

    private void startForAdmin() {
        String userChoice = JOptionPane.showInputDialog(this.getMenuItems());
        this.handleAdminChoice(userChoice);
        this.startForAdmin();
    }

    private void handleAdminChoice(String userChoice) {
        switch (userChoice){
            case "1":
                userController.createUser(user);
                break;
            case "2":
                userController.deleteUser(user.getId());
                break;
            case "3":
                userController.updateUser(user);
                break;
            case "4":
                userController.viewOrdersByUser();
                break;
            case "5":
                productController.createProduct(product);
                break;
            case "6":
                productController.deleteProduct(product.getId());
                break;
            case "7":
                productController.updateProduct(product);
                break;
            case "8":
                productController.showSellingHistoryForProduct(product);
                break;
            case "9":
                System.exit(0);
                break;
            default:
                JOptionPane.showMessageDialog(null, "Please choose an option from the list.");
                break;
        }

    }

    private String getMenuItems() {
        return """
                Welcome to Todo Application
                1. Add user
                2. Delete user
                3. Update user
                4. See purchase history per user
                5. Create product
                6. Delete product
                7. Update product
                8. Selling history per product
                9. Exit""";
    }
    private void start() {
        String userChoice = JOptionPane.showInputDialog(this.getMenuItemsForUser());
        this.handleUserChoice(userChoice);
        this.start();
    }
    private void handleUserChoice(String userChoice) {
        switch (userChoice){
             case "1":
                userController.viewOrdersByUser();
                break;
            case "2":
                productController.chooseAndBuyProducts();
                break;
            case "3":
                System.exit(0);
                break;
            default:
                JOptionPane.showMessageDialog(null, "Please choose an option from the list.");
                break;
        }

    }

    private String getMenuItemsForUser() {
        return """
                Welcome to Todo Application
                1. Show order history
                2. Buy product
                3. Exit""";
    }
}