import controllers.MenuController;
import database.DatabaseService;

import java.sql.SQLException;

public class Main {

    public static void main(String[] args) throws SQLException {
        MenuController menuController = new MenuController();
        menuController.chooseLoginOrSignUp();

    }
}
