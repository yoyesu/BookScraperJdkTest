package com.example.bookscraperjdktest;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.*;

@Controller
public class AppController {

    @Autowired
    BooksRepository booksRepository;

    @GetMapping("/")
    public String getHomePage(Model model){
        model.addAttribute("downloader", "downloader");
        return "index";
    }

    @RequestMapping("/{url}")
    public void getBook(HttpServletResponse response, @PathVariable("url") String url) {

        Book responseDTO = booksRepository.getBookByUrl(url);
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


    }
}
