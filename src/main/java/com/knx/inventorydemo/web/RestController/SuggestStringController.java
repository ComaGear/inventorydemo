package com.knx.inventorydemo.web.restController;

import java.util.HashMap;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/suggest")
public class SuggestStringController {
    
    @GetMapping("/get")
    public Map<String, Map<String, String>> getSuggest(){
        Map<String, Map<String, String>> leveledSuggests = new HashMap<String, Map<String, String>>();
        leveledSuggests.put("0", new HashMap<String, String>());
        leveledSuggests.get("0").put("Apollo Cake Chocolate 24pcs", "9971");
        leveledSuggests.get("0").put("Apollo Cake Original 24pcs", "9972");
        leveledSuggests.get("0").put("Nabati Strawberry", "8891");
        leveledSuggests.get("0").put("No30gg Star 60pcs", "9991");
        
        return leveledSuggests;
    }
}
