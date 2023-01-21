package com.knx.inventorydemo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.knx.inventorydemo.Service.ProductService;

@SpringBootApplication
public class InventorydemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(InventorydemoApplication.class, args);
	}
	
	@Bean
	public ProductService productService(){
		ProductService productService = new ProductService();
		productService.init();
		return productService;
	}

	// TODO: base init of ProductUOM's Update Rule
}
