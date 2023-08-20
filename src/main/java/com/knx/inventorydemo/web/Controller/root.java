package com.knx.inventorydemo.web.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class root {

    // @GetMapping("/")
    // public String dashboard(){
        
    // }
    
    @GetMapping("/greeting")
    public String greeting(@RequestParam(name="name", required = false, defaultValue = "world") String name, Model model){
        model.addAttribute("name", name);
        return "door";
    }
}
