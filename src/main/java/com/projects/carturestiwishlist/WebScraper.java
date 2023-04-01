package com.projects.carturestiwishlist;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomText;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import javafx.scene.control.Alert;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

public class WebScraper implements hasAccesibleLink {
    WebClient webClient;
    HtmlPage productPage;
    int validLink;

    public WebScraper() throws IOException {
        //ignore html error for web scraping
        java.util.logging.Logger.getLogger("com.gargoylesoftware.htmlunit").setLevel(Level.OFF);

        //construction
        //this.url = newUrl;

        webClient = new WebClient();
        webClient.getOptions().setCssEnabled(false);
        webClient.getOptions().setJavaScriptEnabled(false);
    }


    public List<String> getDetails(String link){
        List<String> data = new ArrayList<>();

        if(!checkLink(link)){
            return data;
        }

        //check if avalible
        if(productPage.getByXPath("//div[@class='disponibilitate indisponibil']").size() != 0){
            System.out.println("Item is not avalible");
            data.add(productPage.getTitleText());
            data.add("-1");
            return data;
        }

        //check if on sale
        List<DomText> basePrice = productPage.getByXPath("//span[@class='pret']/text()");
        if(basePrice.size() == 0){
            data.add(productPage.getTitleText());
            //if on sale
            List<DomText> salePrice = productPage.getByXPath("//span[@class='pret reducere']/text()");
            basePrice = productPage.getByXPath("//div[@class='pretVechi pull-right']/span/text()");
            data.add(String.valueOf(basePrice.get(0))); //pos 1
            data.add(String.valueOf(salePrice.get(0))); //pos 2
        }
        else{
            data.add(productPage.getTitleText());
            data.add(String.valueOf(basePrice.get(0))); //pos 1
        }

        return data;
    }

    @Override
    public boolean checkLink(String link) {
        //check if we can set WebParser link (check if valid link in general)
        try {
            productPage = webClient.getPage(link);
            validLink = 0;
        }
        catch (FailingHttpStatusCodeException e) { //Check if link is a Valid carturesti.ro link
            validLink = 1;
            System.out.println("Carturesti page does not exist. (404 error)");
        } catch (MalformedURLException e) {
            validLink = 2;
            System.out.println("Not a valid carturesti link. (malformed link)");
        }catch (IOException e){
            validLink = 3;
            System.out.println("Not a valid carturesti link. (I/O error)");
        }

        //Check if it is a valid "carturesti Product" link
        if(validLink < 1 && productPage.getFirstByXPath("//span[@class='pret']/text()") == null){
            if(productPage.getFirstByXPath("//span[@class='pret reducere']/text()") == null)
            {
                if(productPage.getFirstByXPath("//div[@class='disponibilitate indisponibil']/text()") == null)
                {
                    System.out.println("Not a valid carturesti product page link.");
                    validLink = 4;
                }
            }
        }

        if(validLink > 0) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            switch (validLink){
                case 1:
                    alert.setContentText("Carturesti page does not exist. (404 error)");
                    break;
                case 2:
                    alert.setContentText("Not a valid carturesti link. (malformed link)");
                    break;
                case 3:
                    alert.setContentText("Not a valid carturesti link. (I/O error)");
                    break;
                case 4:
                    alert.setContentText("Not a valid carturesti product page link.");
                    break;
            }
            alert.show();
            return false;
        }

        return true;
    }
}
