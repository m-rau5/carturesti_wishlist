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

public class WishlistMenuController implements Initializable,hasAccesibleLink,userAccessible {

    User currUser;

    public String link;
    public ObservableList<WishlistItem> whsItems = FXCollections.observableArrayList();
    private int ID;

    private WebScraper wbs;
    {
        try {
            wbs = new WebScraper();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private Button RemoveButton; //search and remove + refresh

    @FXML
    private Button AddButton; //web scrape + refresh page

    @FXML
    private TextField itemID; //FIELD search and remove + refresh

    @FXML
    private TextField itemLink; //FIELD web scrape + refresh page

    @FXML
    private Button BackButton;

    @FXML
    private TableView<WishlistItem> wishlistTable;

    @FXML
    private TableColumn<WishlistItem,Integer> itemId;

    @FXML
    private TableColumn<WishlistItem,String> itemName;

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

        BackButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                DBUtils.changeScene(actionEvent,"Menu.fxml","Wishlist",currUser);
            }
        });


        AddButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                if (checkLink(itemLink.getText())){
                    DBUtils.addItemToDB(actionEvent, currUser, link, wbs, false);
                    getItems();
                }
                else {
                    System.out.println("Not a carturesti or libris link (please input a like of the form: https://carturesti.ro/...).");
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setContentText("Not a carturesti or libris link, please input a like of the form: https://carturesti.ro/...");
                    alert.show();
                }

                DBUtils.changeScene(actionEvent, "WishlistMenu.fxml", "Wishlist Menu", currUser);
            }
        });

        RemoveButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                if(isInteger(itemID.getText())){
                    ID = Integer.parseInt(itemID.getText());
                    try {
                        DBUtils.removeItem(actionEvent,ID,currUser,false);
                        getItems();
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
    }

    @Override
    public void setUsername(User currUser){
        this.currUser = currUser;
        getItems();
    }

    @Override
    public boolean checkLink(String link) {
        if(link.contains("https://carturesti.ro/") || link.contains("https://www.libris.ro/")){
            this.link = link;
            return true;
        }
        else return false;
    }

    public void getItems(){

        whsItems.clear();

        //table items setup
        itemId.setCellValueFactory(new PropertyValueFactory<WishlistItem,Integer>("itemId"));
        itemName.setCellValueFactory(new PropertyValueFactory<WishlistItem,String>("itemName"));
        basePrice.setCellValueFactory(new PropertyValueFactory<WishlistItem,Integer>("basePrice"));
        lowestPrice.setCellValueFactory(new PropertyValueFactory<WishlistItem,Integer>("lowestPrice"));
        currentPrice.setCellValueFactory(new PropertyValueFactory<WishlistItem,Integer>("currentPrice"));
        onSale.setCellValueFactory(new PropertyValueFactory<WishlistItem,Boolean>("onSale"));

        if(currUser.updated) {
            currUser.whsItems.clear();
            currUser.updated = false;
        }
        System.out.println(currUser.whsItems.size());
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
