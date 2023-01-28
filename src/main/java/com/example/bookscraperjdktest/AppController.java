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
    public String getBook(HttpServletResponse response, @ModelAttribute("config") UserInput input, Model model) {
        String url = input.getInput();
        System.out.println("post method = " + url);
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


        return "index";
    }

    @PostMapping("/download2")
    public String getBook2(HttpServletResponse response, @ModelAttribute("config") UserInput input2) {
        String url = input2.getInput();
        System.out.println("post method = " + url);
        Book responseDTO = booksRepository.getAllFreeNovelBook(url);
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
