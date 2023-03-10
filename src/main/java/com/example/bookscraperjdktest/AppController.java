package com.example.bookscraperjdktest;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.*;

@Controller
public class AppController {

    @Autowired
    BooksRepository booksRepository;

    @GetMapping("/")
    public String getHomePage(Model model){
        model.addAttribute("config", new UserInput());

        return "index";
    }

    @PostMapping("/download")
    public String getBook(HttpServletResponse response, @ModelAttribute("config") UserInput input) {
        String url = input.getInput();
        Book responseDTO = null;
        if(url.contains("epub.pub")){
            responseDTO = booksRepository.getBookFromEpubPubSite(url);
        } else if (url.contains("freenovel")){
            responseDTO = booksRepository.getBookFromAllFreeNovelSite(url);
        } else {
            throw new RuntimeException("Invalid URL. Input a valid URL.");
        }
        String filename = responseDTO.getName();

        response.setContentType("application/force-download");
        response.setHeader("Content-Transfer-Encoding", "binary");
        response.setHeader("Content-Disposition","attachment; filename=\"" + filename + ".txt\"");
        response.setCharacterEncoding("UTF-8");
        try {
            PrintWriter printer = response.getWriter();

            Object[] listToArray = responseDTO.getSentences().toArray();
            for (int i = 0; i< responseDTO.getSentences().size() ; i++)
            {
                printer.println(listToArray[i]);

            }
            printer.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


        return "index";
    }
}
