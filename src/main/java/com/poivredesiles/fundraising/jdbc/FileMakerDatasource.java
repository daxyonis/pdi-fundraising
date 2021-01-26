package com.poivredesiles.fundraising.jdbc;

import org.apache.tomcat.jdbc.pool.DataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class FileMakerDatasource {
	
	@Value("${application.filemaker.url}")
	private String url;
	
	@Value("${application.filemaker.username}")
	private String username;
	
	@Value("${application.filemaker.password}")
	private String password;

	public DataSource create() {
		DataSource dataSource = new DataSource();
        dataSource.setDriverClassName("com.filemaker.jdbc.Driver");               
        dataSource.setUrl(url);
        dataSource.setUsername(username);
        dataSource.setPassword(password);
        dataSource.setTestOnConnect(false);
        dataSource.setMaxActive(100);
        dataSource.setMaxIdle(30);

        return dataSource;
	}
}
