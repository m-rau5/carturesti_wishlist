package com.projects.carturestiwishlist;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.*;
import java.util.List;

public class DBUtils {
    public static void changeScene(ActionEvent event, String fxmlFile, String title, User currUser) {
        Parent root = null;
        try {
            if (currUser != null) {
                FXMLLoader loader = new FXMLLoader(DBUtils.class.getResource(fxmlFile));
                root = loader.load();
                switch (fxmlFile) {
                    case "Menu.fxml":
                        MenuController menuController = loader.getController();
                        menuController.setUsername(currUser);
                        break;
                    case "AdminMenu.fxml":
                        AdminMenuController adminMenuController = loader.getController();
                        adminMenuController.setUsername(currUser);
                        break;
                    case "WishlistMenu.fxml":
                        WishlistMenuController whsMenuController = loader.getController();
                        whsMenuController.setUsername(currUser);
                        break;
                }
            } else {
                FXMLLoader loader = new FXMLLoader(DBUtils.class.getResource(fxmlFile));
                root = loader.load();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setTitle(title);
        switch (fxmlFile) {
            case ("Login.fxml"):
                stage.setScene(new Scene(root, 400, 300));
                break;
            case ("Signup.fxml"):
                stage.setScene(new Scene(root, 450, 350));
                break;
            case ("Menu.fxml"):
            case ("WishlistMenu.fxml"):
                stage.setScene(new Scene(root, 800, 480));
                break;
            case ("AdminMenu.fxml"):
                stage.setScene(new Scene(root, 1000, 700));
                break;
        }
        stage.show();
    }

    public static void signUpUser(ActionEvent event, String username, String password,User currUser) {
        Connection connection = null;
        PreparedStatement psInsert = null;
        PreparedStatement psUserExists = null;
        ResultSet resultSet = null;

        try {
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/carturestiwishlist", "root", "admin");
            psUserExists = connection.prepareStatement("SELECT * FROM userdata WHERE username = ?");
            psUserExists.setString(1, username);
            resultSet = psUserExists.executeQuery();

            if (resultSet.isBeforeFirst()) { //false if empty or true if else => not empty: user exists
//              --- USER ALREADY EXISTS IN THE DB ---
                System.out.println("User already exists");
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText("Username already exists");
                alert.show();
            } else {
//              --- INSERT USER IN THE DB ---
                psInsert = connection.prepareStatement("INSERT INTO userdata (username,password) VALUES (?,?)");
                psInsert.setString(1, username);
                psInsert.setString(2, password);
                psInsert.executeUpdate(); // update for things that don't return sth , like insert

                currUser = new User(username,false);
                changeScene(event, "Menu.fxml", "Wishlist", currUser);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (resultSet != null) {
                try {
                    resultSet.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (psUserExists != null) {
                try {
                    psUserExists.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (psInsert != null) {
                try {
                    psInsert.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    public static void logInUser(ActionEvent event, String username, String password,User currUser) {
        Connection connection = null; //setup connection to db
        PreparedStatement ps = null; //prepare a statement to send to the db
        ResultSet resultSet = null; //get the result of the statement

        try {
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/carturestiwishlist", "root", "admin");
            ps = connection.prepareStatement("SELECT * FROM userdata WHERE username like ?");
            ps.setString(1, username);
            resultSet = ps.executeQuery();
            if (!resultSet.isBeforeFirst()) { //if the result set we got from the DB is empty
//              --- USER DOES NOT EXIST ---
                System.out.println("User non-existent.");
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText("User does not exist.");
                alert.show();

            } else { //if result set not empty
                resultSet.next(); ///get the fields from the result set

                //compare inputted username to username field from the Database
                if(!username.equals(resultSet.getString("username"))){
                    System.out.println("User non-existent.");
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setContentText("User does not exist.");
                    alert.show();
                }
                else { //if user in the database
//              --- LOGIN USER ---
                    String dbPass = resultSet.getString("password");
                    if (dbPass.equals(password)) {
                        if (resultSet.getInt("isAdmin") == 1) { //if admin
                            currUser = new User(username, true);
                            changeScene(event, "AdminMenu.fxml", "Admin Menu", currUser);
                        } else {
                            currUser = new User(username, false);
                            changeScene(event, "Menu.fxml", "Wishlist", currUser);
                        }
                    } else {
                        System.out.println("Incorrect password.");
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setContentText("Incorrect password.");
                        alert.show();
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (resultSet != null) {
                try {
                    resultSet.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (ps != null) {
                try {
                    ps.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void addItemToDB(ActionEvent event, User user, String link, WebScraper wbs, Boolean admin) {
        Connection connection = null;
        PreparedStatement psCheckExistingItem = null;
        ResultSet resultSet = null;
        PreparedStatement psUserExists = null;
        PreparedStatement psItemGetInfo = null;
        //create web scraper
        try {
            //gets an integer list:
            // 2 values when item is on sale, 1 for non sale, number -1 when not in stock
            List<String> itemDetails = wbs.getDetails(link);

            //Check if item in/out of stock and if on sale
            if (itemDetails.size() > 0) { //if exists

                boolean onSale = false;
                boolean inStock = true;

                if (itemDetails.size() == 3)
                    onSale = true;

                if (itemDetails.get(1).equals("-1"))
                    inStock = false;

                Integer basePrice, salePrice, lowestPrice, currPrice;

                //If the web scraper works, connect to db
                connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/carturestiwishlist", "root", "admin");

                psUserExists = connection.prepareStatement("Select * from userdata where username = ?");
                psUserExists.setString(1, user.getUsername());
                resultSet = psUserExists.executeQuery();

                if (!resultSet.isBeforeFirst()) {
                    System.out.println("User does not exist.");
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setContentText("User does not exist.");
                    alert.show();
                    return;
                }

                //CHECKED BECAUSE THE QUERY STILL RETURNS STH EVEN IF username = "abc" AND BD HAS username = "AbC"
                resultSet.next();
                if (!user.username.equals(resultSet.getString("username"))) {
                    System.out.println("User does not exist.");
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setContentText("User does not exist.");
                    alert.show();
                    return;
                }
                //Look if the item is already in the DB
                psItemGetInfo = connection.prepareStatement("SELECT * FROM wishlistitems WHERE productname=? AND username=?");
                psItemGetInfo.setString(1, itemDetails.get(0));
                psItemGetInfo.setString(2, user.getUsername());
                resultSet = psItemGetInfo.executeQuery();

                if (resultSet.isBeforeFirst()) {

//                  --- if NOT empty => item ALREADY IN THE DB + If product name in DB also ---

                    System.out.println("Item ALREADY in the DB");
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setContentText("Item already in wishlist.");
                    alert.show();

                } else { //IF EMPTY (name not in db) => NOT IN DB
                    System.out.println("Item NOT in the DB");

//                  ---  INSERT ITEM INTO DATABASE  ---

                    if (!inStock) {
                        System.out.println("Item not in stock");
                        basePrice = 0;
                        salePrice = 0;
                    } else {
                        {
                            basePrice = Integer.parseInt(itemDetails.get(1));
                            salePrice = basePrice;
                            if (onSale) {
                                salePrice = Integer.parseInt(itemDetails.get(2));
                            }
                        }
                    }

                    psCheckExistingItem = connection.prepareStatement("INSERT INTO wishlistitems " +
                            " (username,productname,productlink,baseprice,lowestprice,currentprice) VALUES (?,?,?,?,?,?);");
                    psCheckExistingItem.setString(1, user.getUsername());
                    psCheckExistingItem.setString(2, itemDetails.get(0)); //item name
                    psCheckExistingItem.setString(3, link); //item link
                    psCheckExistingItem.setInt(4, basePrice);
                    psCheckExistingItem.setInt(5, salePrice);
                    psCheckExistingItem.setInt(6, salePrice);
                    psCheckExistingItem.executeUpdate();

                    resultSet = psItemGetInfo.executeQuery();
                    resultSet.next();

                    WishlistItem tempWhs = new WishlistItem(resultSet.getInt("itemID"),
                            resultSet.getString("productname"),
                            resultSet.getInt("baseprice"),
                            resultSet.getInt("lowestprice"),
                            resultSet.getInt("currentprice"));
                    tempWhs.setAdminValues(resultSet.getString("username"),
                            resultSet.getString("productlink"));
                    user.addWhsItem(tempWhs);

                    //user.updated = true;

                    //changeScene(event, "WishlistMenu.fxml", "Wishlist Menu", user);
                }
            } else
                System.out.println("Error getting prices.");
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (resultSet != null) {
                try {
                    resultSet.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (psCheckExistingItem != null) {
                try {
                    psCheckExistingItem.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static boolean updateDB(ActionEvent event, User user, WebScraper wbs, boolean admin) throws SQLException {
        boolean updated = false;
        Connection connection = null;
        PreparedStatement psCheckExistingItem = null;
        ResultSet resultSet = null;


//      --- Get all item links from db ---
        PreparedStatement psGetAllLinks = null;
        String link;
        connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/carturestiwishlist", "root", "admin");
        if (admin) {
            psGetAllLinks = connection.prepareStatement("select * from wishlistitems");
        } else {
            psGetAllLinks = connection.prepareStatement("select * from wishlistitems where username = ?");
            psGetAllLinks.setString(1, user.getUsername());
        }
        resultSet = psGetAllLinks.executeQuery();

        if (!resultSet.isBeforeFirst()) { //if no links in user's DB
            System.out.println("No items in wishlist");
            Alert ALERT = new Alert(Alert.AlertType.ERROR);
            ALERT.setContentText("No items to update.");
            ALERT.show();
            return false;
        }

        while (resultSet.next()) {

            System.out.println("Checking: " + resultSet.getString("productlink"));

            link = resultSet.getString("productlink");
            List<String> itemDetails = wbs.getDetails(link);

            if (itemDetails.size() > 0) { //if item is valid

                updated = true; //then we check at least 1 item

                boolean onSale = false;
                boolean inStock = true;

                if (itemDetails.size() == 3)
                    onSale = true;

                if (itemDetails.get(1).equals("-1"))
                    inStock = false;

//                  ---  UPDATE DATABASE  --

                int userID = resultSet.getInt("itemID");
                psCheckExistingItem = connection.prepareStatement("UPDATE wishlistitems SET currentprice = ? WHERE itemID = ? ");

                if (!inStock) {
//              --- If out of stock -> set currPrice to 0 ---
                    psCheckExistingItem.setInt(1, 0);
                    psCheckExistingItem.setInt(2, userID);
                    psCheckExistingItem.executeUpdate();
                }
                else { //if item is now in stock

                    Integer lowestPrice = resultSet.getInt("lowestprice");
                    Integer currPrice = Integer.parseInt(itemDetails.get(1));

                    //If item on sale get sale price
                    Integer salePrice = onSale ? Integer.parseInt(itemDetails.get(2)) : currPrice;

                    //Check if the item is out of stock in the DB
                    boolean inStockPreviously = resultSet.getInt("lowestprice") != 0;

                    if (!inStockPreviously) {
                        psCheckExistingItem = connection.prepareStatement("UPDATE wishlistitems SET currentprice = ?,lowestprice = ? WHERE itemID = ?");
                        psCheckExistingItem.setInt(1, currPrice);
                        psCheckExistingItem.setInt(2, currPrice);
                        psCheckExistingItem.setInt(3, userID);
                        psCheckExistingItem.executeUpdate();
                    } else
                    {
                        if (onSale) { //if item is on sale

                            if (salePrice < lowestPrice) {

//                      --- change query since lowest price is changed ---
                                psCheckExistingItem = connection.prepareStatement("UPDATE wishlistitems SET currentprice = ?,lowestprice = ? WHERE itemID = ?");
                                psCheckExistingItem.setInt(1, salePrice);
                                psCheckExistingItem.setInt(2, salePrice);
                                psCheckExistingItem.setInt(3, userID);
                            } else {
                                psCheckExistingItem.setInt(1, salePrice);
                                psCheckExistingItem.setInt(2, userID);
                            }
                        } else { //if NOT on sale
                                psCheckExistingItem.setInt(1, currPrice);
                                psCheckExistingItem.setInt(2, userID);
                            }
                        psCheckExistingItem.executeUpdate();
                        user.updated = true;
                    }
                }
            }
        }
        if (resultSet != null) {
            try {
                resultSet.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if (psCheckExistingItem != null) {
            try {
                psCheckExistingItem.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return updated;
    }

    public static void removeItem(ActionEvent event, int ID, User user, Boolean admin) throws SQLException {
        Connection connection = null;
        PreparedStatement psCheckExistingItem = null;
        PreparedStatement psRemove = null;
        ResultSet resultSet = null;

        connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/carturestiwishlist", "root", "admin");
        if (admin) {
            psCheckExistingItem = connection.prepareStatement("select * from wishlistitems where itemID = ?");
        } else {
            psCheckExistingItem = connection.prepareStatement("select * from wishlistitems where itemID = ? and username = ?");
            psCheckExistingItem.setString(2, user.getUsername());
        }

        psCheckExistingItem.setInt(1, ID);
        resultSet = psCheckExistingItem.executeQuery();

        if (resultSet.isBeforeFirst()) {

            //Item in db
            psRemove = connection.prepareStatement("DELETE FROM wishlistitems where itemID = ?");
            psRemove.setInt(1, ID);
            psRemove.executeUpdate();

            user.removeWhsItem(ID);

        } else {
            System.out.println("Item ID not in user wishlist");
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Item ID not in user wishlist");
            alert.show();
        }

        if (resultSet != null) {
            try {
                resultSet.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if (psCheckExistingItem != null) {
            try {
                psCheckExistingItem.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if (psRemove != null) {
            try {
                psRemove.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static ObservableList<WishlistItem> getDbItems(User user) {
        ObservableList<WishlistItem> ObsWhsItems = FXCollections.observableArrayList();
        Connection connection = null;
        PreparedStatement psGetItems = null;
        ResultSet resultSet = null;

        try {
            if(user.whsItems.isEmpty() || user.updated) {
                connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/carturestiwishlist", "root", "admin");
                if(user.updated) {
                    user.whsItems.clear();
                    user.updated = false;
                }
                if (user.isAdmin) {
                    psGetItems = connection.prepareStatement("select * from wishlistitems");
                } else {
                    psGetItems = connection.prepareStatement("select * from wishlistitems where username = ?");
                    psGetItems.setString(1, user.username);
                }
                resultSet = psGetItems.executeQuery();
                    while (resultSet.next()) {
                        //System.out.println("Got connection");
                        WishlistItem tempWhs = new WishlistItem(resultSet.getInt("itemID"),
                                resultSet.getString("productname"),
                                resultSet.getInt("baseprice"),
                                resultSet.getInt("lowestprice"),
                                resultSet.getInt("currentprice"));
                        tempWhs.setAdminValues(resultSet.getString("username"),
                                resultSet.getString("productlink"));
                        user.addWhsItem(tempWhs);
                    }
            }
            //store items
            for(WishlistItem Item : user.whsItems) {
                //System.out.println(Item.getUsername() + ' ' + user.getUsername());
                if((Item.username.equals(user.username) && !user.isAdmin) || user.isAdmin)
                    ObsWhsItems.add(Item);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (resultSet != null) {
            try {
                resultSet.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if (psGetItems != null) {
            try {
                psGetItems.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return ObsWhsItems;
    }
}