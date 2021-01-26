package com.poivredesiles.fundraising;

import java.sql.Connection;
import java.sql.SQLException;

import org.apache.tomcat.jdbc.pool.DataSource;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class FundraisingApplicationTests {
	
	Logger log = LoggerFactory.getLogger(FundraisingApplicationTests.class);
		
	@Test
	void testFileMakerConnection() throws SQLException, InterruptedException {		
		DataSource datasource = create();
		log.info("Got datasource !");
		 Connection con = datasource.getConnection();
		 log.info("Got connection !");
         Thread.sleep(5000);
         con.close();
         log.info("Closed connection !");
	}

	
	private DataSource create() {
		DataSource dataSource = new DataSource();
        dataSource.setDriverClassName("com.filemaker.jdbc.Driver");               
        dataSource.setUrl("jdbc:filemaker://127.0.0.1/test");
        dataSource.setUsername("UsagerJdbc");
        dataSource.setPassword("admin");
        dataSource.setTestOnConnect(false);
        dataSource.setMaxActive(100);
        dataSource.setMaxIdle(30);

        return dataSource;
	}

}
