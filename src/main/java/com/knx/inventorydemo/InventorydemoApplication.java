package com.knx.inventorydemo;

import javax.sql.DataSource;

import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.type.TypeAliasRegistry;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import com.knx.inventorydemo.Service.MeasurementService;
import com.knx.inventorydemo.Service.ProductService;
import com.knx.inventorydemo.Service.StockMovementService;
import com.knx.inventorydemo.Service.StockingService;
import com.knx.inventorydemo.entity.ProductMeta;
import com.knx.inventorydemo.mapper.ProductMeasurementMapper;
import com.knx.inventorydemo.mapper.ProductMetaMapper;
import com.knx.inventorydemo.mapper.ProductMovementMapper;
import com.knx.inventorydemo.mapper.ProductStockingMapper;

@SpringBootApplication
@MapperScan("com.knx.inventorydemo.mapper")
public class InventorydemoApplication {

	// private static ClassPathXmlApplicationContext context;

	public static void main(String[] args) {
		// context = new ClassPathXmlApplicationContext("Beans.xml");
		SpringApplication.run(InventorydemoApplication.class, args);
	}

	@Bean
	public SqlSessionFactory sqlSessionFactory(@Autowired DataSource dataSource) throws Exception{
		SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
		sqlSessionFactoryBean.setDataSource(dataSource);
		SqlSessionFactory factory = sqlSessionFactoryBean.getObject();
		Configuration configuration = factory.getConfiguration();

		TypeAliasRegistry typeAliasRegistry = configuration.getTypeAliasRegistry();
		typeAliasRegistry.registerAlias("ProductMeta", ProductMeta.class);

		configuration.addMapper(ProductMetaMapper.class);
		configuration.addMapper(ProductMeasurementMapper.class);
		configuration.addMapper(ProductMovementMapper.class);
		configuration.addMapper(ProductStockingMapper.class);
		// TODO: un annotation it. and register implement xml file and register spring bean.
		// configuration.addMapper(ProductStockingMapper.class);
		// configuration.addMapper(ProductMovementMapper.class);

		return factory;
	}

	@Bean
	public ProductMetaMapper productMetaMapper(@Autowired SqlSessionFactory sqlSessionFactory){
		SqlSessionTemplate sqlSessionTemplate = new SqlSessionTemplate(sqlSessionFactory);
		return sqlSessionTemplate.getMapper(ProductMetaMapper.class);
		
	}

	@Bean
	public ProductMeasurementMapper productMeasurementMapper(@Autowired SqlSessionFactory sqlSessionFactory){
		SqlSessionTemplate sqlSessionTemplate = new SqlSessionTemplate(sqlSessionFactory);
		return sqlSessionTemplate.getMapper(ProductMeasurementMapper.class);
	}

	@Bean
	public ProductMovementMapper productMovementMapper(@Autowired SqlSessionFactory sqlSessionFactory){
		SqlSessionTemplate sqlSessionTemplate = new SqlSessionTemplate(sqlSessionFactory);
		return sqlSessionTemplate.getMapper(ProductMovementMapper.class);
	}

	@Bean
	public ProductStockingMapper productStockingMapper(@Autowired SqlSessionFactory sqlSessionFactory){
		SqlSessionTemplate sqlSessionTemplate = new SqlSessionTemplate(sqlSessionFactory);
		return sqlSessionTemplate.getMapper(ProductStockingMapper.class);
	}
	
	@Bean
	public ProductService productService(@Autowired ProductMetaMapper productMetaMapper){
		ProductService productService = new ProductService(productMetaMapper);
		productService.init();
		return productService;
	}

	@Bean 
	public MeasurementService measurementService(@Autowired ProductService productService, @Autowired ProductMeasurementMapper productMeasurementMapper){
		MeasurementService measurementService = new MeasurementService(productMeasurementMapper);
		productService.setMeasurementService(measurementService);
		measurementService.init();
		return measurementService;
	}

	@Bean
	public StockingService stockingService(@Autowired ProductStockingMapper productStockingMapper, 
		@Autowired ProductMovementMapper productMovementMapper, @Autowired MeasurementService measurementService,
		@Autowired ProductService productService){
			StockingService stockingService = new StockingService(productStockingMapper, productMovementMapper, measurementService
				,productService);
			stockingService.init();
			productService.setStockingService(stockingService);
			return stockingService;
		}


	// TODO: add a bean of stockMovementService
	@Bean
	public StockMovementService stockMovementService(@Autowired ProductMovementMapper productMovementMapper){
		StockMovementService stockMovementService = new StockMovementService(productMovementMapper);
		return stockMovementService;
	}

	// TODO: base init of ProductUOM's Update Rule
}
