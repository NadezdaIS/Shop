import controllers.MenuController;
import controllers.UserController;
import database.DatabaseService;

import java.sql.SQLException;

public class Main {

    public static void main(String[] args) throws Exception {
       MenuController menuController = new MenuController();
      menuController.chooseLoginOrSignUp();
//UserController controller = new UserController();

    }
}
