package com.projects.carturestiwishlist;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;

public class MenuController implements Initializable,userAccessible {

    User currUser;

    public String username;
    boolean updated = false;
    public ObservableList<WishlistItem> whsItems = FXCollections.observableArrayList();

    WebScraper wbs = null;

    @FXML
    private Label WishlistLabel;

    @FXML
    private Button LogoutButton;

    @FXML
    private Button EditWishlistButton;

    @FXML
    private Button UpdateButton;

    @FXML
    private TableView<WishlistItem> wishlistTable;

    @FXML
    private TableColumn<WishlistItem, Integer> itemId;

    @FXML
    private TableColumn<WishlistItem, String> itemName;

    @FXML
    private TableColumn<WishlistItem, Integer> basePrice;

    @FXML
    private TableColumn<WishlistItem, Integer> lowestPrice;

    @FXML
    private TableColumn<WishlistItem, Integer> currentPrice;

    @FXML
    private TableColumn<WishlistItem, Boolean> onSale;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

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
                    updated = DBUtils.updateDB(actionEvent, currUser,wbs,false);
                } catch (SQLException e) {
                    e.printStackTrace();
                }

                if(updated) {
                    currUser.updated = true;
                    getItems();

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

        EditWishlistButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                DBUtils.changeScene(actionEvent, "WishlistMenu.fxml", "Wishlist Menu", currUser);
            }
        });
    }

    @Override
    public void setUsername(User currUser) {
        //when moving to this page while logged in

        this.currUser = currUser;
        this.username = currUser.getUsername();

        //update top label
        WishlistLabel.setMaxWidth(Double.MAX_VALUE);
        WishlistLabel.setText(username + "'s Wishlist");
        WishlistLabel.setAlignment(Pos.CENTER);

        //get dbItems
        getItems();
    }

    public void getItems() {
        whsItems.clear();

        //set table columns
        itemId.setCellValueFactory(new PropertyValueFactory<WishlistItem, Integer>("itemId"));
        itemName.setCellValueFactory(new PropertyValueFactory<WishlistItem, String>("itemName"));
        basePrice.setCellValueFactory(new PropertyValueFactory<WishlistItem, Integer>("basePrice"));
        lowestPrice.setCellValueFactory(new PropertyValueFactory<WishlistItem, Integer>("lowestPrice"));
        currentPrice.setCellValueFactory(new PropertyValueFactory<WishlistItem, Integer>("currentPrice"));
        onSale.setCellValueFactory(new PropertyValueFactory<WishlistItem, Boolean>("onSale"));

        whsItems = DBUtils.getDbItems(currUser);
        wishlistTable.setItems(whsItems);
    }
}
