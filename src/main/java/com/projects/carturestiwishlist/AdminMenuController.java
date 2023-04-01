package com.projects.carturestiwishlist;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class AdminMenuController implements Initializable,hasAccesibleLink,userAccessible {

    User currUser;

    public String user,link;
    public ObservableList<WishlistItem> whsItems = FXCollections.observableArrayList();
    private int ID;

    boolean updated;

    private WebScraper wbs;
    {
        try {
            wbs = new WebScraper();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private TextField userNameTF;

    @FXML
    private Button RemoveButton; //search and remove + refresh

    @FXML
    private Button AddButton;

    @FXML
    private TextField itemID; //FIELD search and remove + refresh

    @FXML
    private TextField itemLinkTF;

    @FXML
    private TextField userWhsTF;

    @FXML
    private Button LogoutButton;

    @FXML
    private Button UpdateButton;

    @FXML
    private Button RefreshButton;

    @FXML
    private Button ShowWishlist;

    @FXML
    private TableView<WishlistItem> wishlistTable;

    @FXML
    private TableColumn<WishlistItem,Integer> itemId;

    @FXML
    private TableColumn<WishlistItem,String> itemName;

    @FXML
    private TableColumn<WishlistItem,String> username;

    @FXML
    private TableColumn<WishlistItem,String> itemLink;

    @FXML
    private TableColumn<WishlistItem,Integer> basePrice;

    @FXML
    private TableColumn<WishlistItem,Integer> lowestPrice;

    @FXML
    private TableColumn<WishlistItem,Integer> currentPrice;

    @FXML
    private TableColumn<WishlistItem,Boolean> onSale;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        RemoveButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                if(isInteger(itemID.getText())){
                    ID = Integer.parseInt(itemID.getText());
                    try {
                        DBUtils.removeItem(actionEvent,ID,currUser,true);
                        getItems("");
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
                else{
                    System.out.println("ID not integer value");
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setContentText("Please input a valid ID.");
                    alert.show();
                }
            }
        });

        AddButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                if(!itemLinkTF.getText().isEmpty() && !userNameTF.getText().isEmpty()){
                    if (checkLink(itemLinkTF.getText())) {
                        currUser.username = userNameTF.getText();
                        DBUtils.addItemToDB(actionEvent, currUser, link, wbs, true);
                    }
                    else {
                        System.out.println("Not a carturesti link (please input a like of the form: https://carturesti.ro/...).");
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setContentText("Not a carturesti link, please input a like of the form: https://carturesti.ro/...");
                        alert.show();
                    }
                }
                else {
                    System.out.println("One of the fields is empty!");
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setContentText("Please complete both fields.");
                    alert.show();

                }
                //username gets set back in here
                getItems("");
            }
        });

        RefreshButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                getItems("refresh");
            }
        });

        UpdateButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {

                //if we update create wbs (if not already created)
                if(wbs == null) {
                    try {
                        wbs = new WebScraper();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }

                try {
                    updated = DBUtils.updateDB(actionEvent,currUser,wbs,currUser.isAdmin);
                } catch (SQLException e) {
                    e.printStackTrace();
                }

                if(updated) {
                    getItems("");

                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setContentText("");
                    alert.setTitle("Done!");
                    alert.setHeaderText("Done!");
                    alert.show();
                }
            }
        });


        LogoutButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                DBUtils.changeScene(actionEvent, "Login.fxml", "Log In", currUser);
            }
        });

        ShowWishlist.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                getItems(userWhsTF.getText());
            }
        });
    }

    @Override
    public void setUsername(User currUser) {
        //when moving to this page while logged in
        this.currUser = currUser;
        this.user = currUser.getUsername();
        currUser.isAdmin = true;
        //get dbItems
        getItems("");
    }

    @Override
    public boolean checkLink(String link) {
        if(link.contains("https://carturesti.ro/")){
            this.link = link;
            return true;
        }
        else return false;
    }

    public void getItems(String locUser){

        whsItems.clear();

        itemId.setCellValueFactory(new PropertyValueFactory<WishlistItem,Integer>("itemId"));
        itemName.setCellValueFactory(new PropertyValueFactory<WishlistItem,String>("itemName"));
        username.setCellValueFactory(new PropertyValueFactory<WishlistItem,String>("username"));
        itemLink.setCellValueFactory(new PropertyValueFactory<WishlistItem,String>("itemLink"));
        basePrice.setCellValueFactory(new PropertyValueFactory<WishlistItem,Integer>("basePrice"));
        lowestPrice.setCellValueFactory(new PropertyValueFactory<WishlistItem,Integer>("lowestPrice"));
        currentPrice.setCellValueFactory(new PropertyValueFactory<WishlistItem,Integer>("currentPrice"));
        onSale.setCellValueFactory(new PropertyValueFactory<WishlistItem,Boolean>("onSale"));

        if(locUser.equals("refresh")) {
            currUser.updated = true; //to get all items from DB again
            currUser.username = user;
            currUser.isAdmin = true;
        }
        else if(locUser.length()>1) {
            currUser.username = locUser;
            currUser.isAdmin = false;
        }
        else {
            currUser.username = user;
            currUser.isAdmin = true;
        }

        whsItems = DBUtils.getDbItems(currUser);

        wishlistTable.setItems(whsItems);

    }


    public static boolean isInteger(String s) {
        try {
            Integer.parseInt(s);
        } catch(NumberFormatException e) {
            return false;
        } catch(NullPointerException e) {
            return false;
        }
        return true;
    }

}
