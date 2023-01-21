package com.knx.inventorydemo;

import javax.sql.DataSource;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import com.knx.inventorydemo.mapper.ProductMetaMapper;

@Configuration
public class MybatisConfig {

	@Bean
	public PlatformTransactionManager platformTransactionManager(DataSource dataSource) {
		return new DataSourceTransactionManager(dataSource);
	}
	
	@Bean
	public SqlSessionFactory sqlSessionFactory(DataSource dataSource) throws Exception{
		SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
		sqlSessionFactoryBean.setDataSource(dataSource);
		SqlSessionFactory factory = sqlSessionFactoryBean.getObject();
		factory.getConfiguration().addMapper(ProductMetaMapper.class);
		return factory;
	}
    
  @Bean
	public ProductMetaMapper productMetaMapper(SqlSessionFactory sqlSessionFactory){
		try (SqlSessionTemplate sqlSessionTemplate = new SqlSessionTemplate(sqlSessionFactory)) {
			return sqlSessionTemplate.getMapper(ProductMetaMapper.class);
		}
	}
}
