package com.knx.inventorydemo;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.knx.inventorydemo.Service.ProductService;

@SpringBootTest
class InventorydemoApplicationTests {

	@Autowired
	ProductService productService;

	@Test
	void mybatisSqlConnectTest(){
		assertNotNull(productService.getAllProductMeta()); 
	}

}
