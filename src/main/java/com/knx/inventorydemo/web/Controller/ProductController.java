package com.knx.inventorydemo.web.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.knx.inventorydemo.Service.ProductService;
import com.knx.inventorydemo.entity.ProductMeta;

@Controller
@RequestMapping("/product")
public class ProductController {
    
    @Autowired
    private ProductService productService;

    @GetMapping(path = "/{id}")
    public String getProduct(@PathVariable String id, Model model){

        if(id == null || id.isEmpty()) throw new IllegalArgumentException("query product detail id should not be null.");

        ProductMeta productMetaById = productService.getProductMetaById(id);

        model.addAttribute("product", productMetaById);

        return "product";
    }

    @GetMapping(path = "/create")
    public String createProduct(){

        return "product";
    }
}
