package com.projects.carturestiwishlist;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;


import static org.junit.jupiter.api.Assertions.*;

class WebScraperTest {

    //test 3 links to get details
    //one out of stock, one with no sale, one on sale
    @Test
    void testGetDetails() {
        WebScraper wbs;
        try {
            wbs = new WebScraper();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        String outOfStockItem = "https://carturesti.ro/carte/tavistock-century-1091540613?p=1",
                inStockItem = "https://carturesti.ro/muzica/christmas-10th-anniversary-edition-1966553313?p=38",
                SaleItem = "https://carturesti.ro/carte/fire-force-volume-30-2053992059?p=4";

        //out of stock : productPage name, -1
        List<String> data = wbs.getDetails(outOfStockItem);
        assertEquals("Tavistock Century",data.get(0));
        assertEquals(-1,data.get(1));

        //inStockItem : productPage name, 69 lei
        data = wbs.getDetails(inStockItem);
        assertEquals("Christmas (10th Anniversary Edition) - Michael Buble",data.get(0));
        assertEquals(69,data.get(1));

        //SaleIte : productPage name, baseprice 60, saleprice 36
        data = wbs.getDetails(SaleItem);
        assertEquals("Fire Force - Volume 30 - Atsushi Ohkubo",data.get(0));
        assertEquals(60,data.get(1));
        assertEquals(36,data.get(2));

    }

    //test getting data from bad links
    //404 error,malformed html,not product page,link is ok
    @Test
    void testCheckLink() {
        WebScraper wbs;
        try {
            wbs = new WebScraper();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        String Link404 = "https://carturesti.ro/carte/qwertyuiop", //404 page on carturesti
                LinkBadHtml = "aslflhhttps://carturesti.ro/carteasgoasf", //bad html format
                LinkNotProductPage="https://carturesti.ro/raft/muzica-110", //valid page but no product
                LinkOk = "https://carturesti.ro/muzica/ride-the-lightning-126015196?t=v1_related&p=4"; //ok link

        assertTrue(wbs.checkLink(LinkOk));
        assertEquals(0,wbs.validLink);

        assertFalse(wbs.checkLink(Link404));
        assertEquals(1,wbs.validLink);

        assertFalse(wbs.checkLink(LinkBadHtml));
        assertEquals(2,wbs.validLink);

        assertFalse(wbs.checkLink(LinkNotProductPage));
        assertEquals(4,wbs.validLink);

    }
}