package com.example.bookscraperjdktest;

import com.gargoylesoftware.htmlunit.*;
import com.gargoylesoftware.htmlunit.html.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.springframework.stereotype.Service;
import org.apache.http.conn.ssl.SSLContexts;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

@Service
public class Scraper implements BooksRepository{

        static HtmlPage page;
        WebClient client = new WebClient(BrowserVersion.FIREFOX);
        String bookTitle = "";
        String baseUrl = "";

        private HtmlPage getWebPage(String url) throws IOException {
            client.getOptions().setCssEnabled(false);
            client.getOptions().setJavaScriptEnabled(false);
            return client.getPage(url);
        }

        public Book getBookFromAllFreeNovelSite(String url){
            Book book = new Book();
            try {
                page = getWebPage(url);

                //saving name of book
                book.setName(page.getElementsByTagName("h1").get(0).getVisibleText());

                //gathering all links to the book pages
               List<HtmlAnchor> allLinks = page.getAnchors();
               List<String> bookPageLinks = new ArrayList<>();
               for(HtmlAnchor link : allLinks){
                   if(link.getAttribute("class").equals("card-link")){
                       bookPageLinks.add("https://www.allfreenovel.com" + link.getAttribute("href"));
                   }
               }

               //scraping all pages and saving the lines in the dto
               for (String bookPages : bookPageLinks){
                   page = getWebPage(bookPages);

                   List<DomElement> bookLines = page.getElementsByTagName("p");
                   for (DomElement line : bookLines){
                       if(line.getAttribute("class").equals("storyText")){
                           book.getSentences().add(line.getVisibleText());
                       }
                   }
               }
            } catch (IOException e) {
                throw new RuntimeException("Oops your book couldn't be fetched. Try again later!");
            }

            return book;
        }

        public Book getBookFromEpubPubSite(String url) {

            //getting base url for the links of all chapters
            String finalStructure = getFinalUrlStructure(url);
            Book responseDTO = new Book();

            System.setProperty("webdriver.chrome.driver", "C:\\Users\\maria\\IdeaProjects\\BookScraperJdkTest\\chromedriver.exe");
            ChromeOptions options = new ChromeOptions();
            options.addArguments("--headless","--user-agent=Mozilla/5.0");
            WebDriver driver = new ChromeDriver(options);

            driver.get(baseUrl);
//            WebElement toc = driver.findElement(By.id("toc"));

            //gathering the second part of the links for each chapter
            List<WebElement> links = driver.findElements(By.tagName("li"));
            for(WebElement link : links){
                gatherBookLines(responseDTO, finalStructure + link.getDomAttribute("ref"));

            }

            responseDTO.setName(bookTitle);
            return responseDTO;
        }

    private String getFinalUrlStructure(String url) {
        //(from the main url) gets the url to the actual content
        String secondUrl = generateSecondUrl(url);

        String finalUrlStructure = "";
        try {
            page = getWebPage(secondUrl);

            List<DomElement> listOfInputs = page.getElementsByTagName("input");
            for(DomElement input : listOfInputs){
                if(input.getAttribute("name").equals("assetUrl")){
                    String urlToTrim = input.getAttribute("value");
                    int indexOfSubstring = urlToTrim.indexOf("content.opf");
                    finalUrlStructure = urlToTrim.substring(0,indexOfSubstring);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return finalUrlStructure;
    }

    private String generateSecondUrl(String url) {
        String secondUrl = "";
        try {
            page = getWebPage(url);
            bookTitle = page.getElementById("article-title").getVisibleText();
            List<HtmlAnchor> listOfAnchors = page.getAnchors();
            for(HtmlAnchor anchor : listOfAnchors){
                if(anchor.hasAttribute("data-readid")){
                    secondUrl = anchor.getAttribute("data-domain") + "/epub/" + anchor.getAttribute("data-readid");
                    break;
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        baseUrl = secondUrl;
        return secondUrl;
    }

    private void gatherBookLines(Book responseDTO, String url) {

            try {

                System.setProperty("webdriver.chrome.driver", "chromedriver.exe");
                ChromeOptions options = new ChromeOptions();
                options.addArguments("--headless","--user-agent=Mozilla/5.0");
                WebDriver driver = new ChromeDriver(options);
                driver.get(url);

                List<WebElement> textBoxes = driver.findElements(By.tagName("p"));

                for (WebElement p: textBoxes) {
                    responseDTO.getSentences().add(p.getText());
                }
            driver.close();
            } catch (FailingHttpStatusCodeException ex) {
                throw new RuntimeException("Page not found");
            }
        }

}
