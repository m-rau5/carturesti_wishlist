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

        if(link.contains("https://www.libris.ro/"))
        {
            if(!checkLinkLibris(link)){
                return data;
            }
            return getDetailsLibris(link);
        }
        else if(!checkLink(link)){
            return data;
        }

        String page = productPage.getTitleText();
        String header = "(C) ";

        String title = header + page;

        //check if avalible
        if(productPage.getByXPath("//div[@class='disponibilitate indisponibil']").size() != 0){
            System.out.println("Item is not avalible");
            data.add(title);
            data.add("-1");
            return data;
        }

        //check if on sale
        List<DomText> basePrice = productPage.getByXPath("//span[@class='pret']/text()");
        if(basePrice.size() == 0){
            data.add(title);
            //if on sale
            List<DomText> salePrice = productPage.getByXPath("//span[@class='pret reducere']/text()");
            basePrice = productPage.getByXPath("//div[@class='pretVechi pull-right']/span/text()");
            data.add(String.valueOf(basePrice.get(0))); //pos 1
            data.add(String.valueOf(salePrice.get(0))); //pos 2
        }
        else{
            data.add(title);
            data.add(String.valueOf(basePrice.get(0))); //pos 1
        }

        System.out.println(data);
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

    public List<String> getDetailsLibris(String link){
        List<String> data = new ArrayList<>();

        if(!checkLinkLibris(link)){
            return data;
        }

        String page = productPage.getTitleText();
        String header = "(L) ";

        String title = header + page;

        //check if avalible
        if(productPage.getByXPath("//div[@class='main-produs-ct']//a[@class='pr-adauga-in-cos-btn' and text()='Indisponibil']").size() != 0){
            System.out.println("Item is not avalible");
            data.add(title);
            data.add("-1");
            return data;
        }

        data.add(title);

        List<DomText> salePrice = productPage.getByXPath("//div[@class='main-produs-ct']//p[@class='pr-pret-redus']/text()");
        List<DomText> basePrice = null;
        if(productPage.getByXPath("//div[@class='main-produs-ct']//span[@class='pr-pret-intreg']").size()>0)
        {
            basePrice = productPage.getByXPath("//div[@class='main-produs-ct']//span[@class='pr-pret-intreg']/text()");
            System.out.println("base: "+ basePrice);
            data.add(String.valueOf(basePrice.get(0)).substring(0,2)); //pos 1
            data.add(String.valueOf(salePrice.get(0)).substring(0,2)); //pos 2
        }
        else{
            data.add(String.valueOf(salePrice.get(0)).substring(0,2)); //pos 1
        }

        System.out.println(data);

        return data;
    }
    public boolean checkLinkLibris(String link) {
        //check if we can set WebParser link (check if valid link in general)
        try {
            productPage = webClient.getPage(link);
            validLink = 0;
        }
        catch (FailingHttpStatusCodeException e) { //Check if link is a Valid Libris.ro link
            validLink = 1;
            System.out.println("Libris page does not exist. (404 error)");
        } catch (MalformedURLException e) {
            validLink = 2;
            System.out.println("Not a valid Libris link. (malformed link)");
        }catch (IOException e){
            validLink = 3;
            System.out.println("Not a valid Libris link. (I/O error)");
        }

        //Check if it is a valid "Libris Product" link
        if(validLink < 1 && productPage.getFirstByXPath("//p[@class='pr-pret-redus']/text()") == null){
            if(productPage.getFirstByXPath("//a[@class='pr-adauga-in-cos-btn' and text()='Indisponibil']") == null)
            {
                System.out.println("Not a valid Libris product page link.");
                validLink = 4;
            }
        }

        if(validLink > 0) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            switch (validLink){
                case 1:
                    alert.setContentText("Libris page does not exist. (404 error)");
                    break;
                case 2:
                    alert.setContentText("Not a valid Libris link. (malformed link)");
                    break;
                case 3:
                    alert.setContentText("Not a valid Libris link. (I/O error)");
                    break;
                case 4:
                    alert.setContentText("Not a valid Libris product page link.");
                    break;
            }
            alert.show();
            return false;
        }

        return true;
    }
}

