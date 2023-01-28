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

        public Book getAllFreeNovelBook(String url){
            Book book = new Book();
            try {
                page = getWebPage(url);
                book.setName(page.getElementsByTagName("h1").get(0).getVisibleText());
               List<HtmlAnchor> allLinks = page.getAnchors();
               List<String> bookPageLinks = new ArrayList<>();
               for(HtmlAnchor link : allLinks){
                   if(link.getAttribute("class").equals("card-link")){
                       bookPageLinks.add("https://www.allfreenovel.com" + link.getAttribute("href"));
                   }
               }

               for (String bookPages : bookPageLinks){
                   page = getWebPage(bookPages);

                   List<DomElement> bookLines = page.getElementsByTagName("p");
                   for (DomElement line : bookLines){
                       book.getSentences().add(line.getVisibleText());
                   }
               }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            return book;
        }

        public Book getBookByUrl(String url) {
            url = "https://www.epub.pub/book/" + url;
            String finalStructure = getFinalUrlStructure(url);
            Book responseDTO = new Book();

            System.setProperty("webdriver.chrome.driver", "C:\\Users\\maria\\IdeaProjects\\BookScraperJdkTest\\chromedriver.exe");
            ChromeOptions options = new ChromeOptions();
            options.addArguments("--headless","--user-agent=Mozilla/5.0");
            WebDriver driver = new ChromeDriver(options);

            driver.get(baseUrl);
            WebElement toc = driver.findElement(By.id("toc"));

            System.out.println(toc.toString());
            List<WebElement> links = driver.findElements(By.tagName("li"));
            System.out.println("number of links" + links.size());

            for(WebElement link : links){

                    if (gatherBookLines(responseDTO, finalStructure + link.getDomAttribute("ref")) == 400) {
                        break;

                }
            }

            responseDTO.setName(bookTitle);
            return responseDTO;
        }

    private String getFinalUrlStructure(String url) {
        String secondUrl = generateSecondUrl(url);
        String finalUrlStructure = "";
        try {
            page = getWebPage(secondUrl);

            List<DomElement> listOfInputs = page.getElementsByTagName("input");
            for(DomElement input : listOfInputs){
                if(input.getAttribute("name").equals("assetUrl")){
                    String urlToTrim = input.getAttribute("value");
                    System.out.println("urlToTrim = " + urlToTrim);
                    int indexOfSubstring = urlToTrim.indexOf("content.opf");
                    finalUrlStructure = urlToTrim.substring(0,indexOfSubstring);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        System.out.println("final structure = " + finalUrlStructure);
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

    private int gatherBookLines(Book responseDTO, String url) {

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
                return 400;
            }
        return 200;
        }

}
