package com.knx.inventorydemo.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class fastSearch {
    
    @GetMapping("/search")
    public String search(Model model){
        return "fastSearchPage";
    }
}
