package com.poivredesiles.fundraising.model.file;

import java.time.Instant;
import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;

import com.poivredesiles.fundraising.imports.ImportsUtils;
import org.hibernate.annotations.GenericGenerator;

import lombok.Data;

@Entity
@Table(name = "files")
@Data
public class FileDB {

	@Id
	@GeneratedValue(generator = "uuid")
	@GenericGenerator(name = "uuid", strategy = "uuid2")
	private String id;

	private String name;

	private String type;
	
	private String description;
	
	private LocalDateTime updatedTime;

	@Lob
	private byte[] data;

	public FileDB() {
	}

	public FileDB(String name, String type, byte[] data, String description) {
		this.name = name;
		this.type = type;
		this.data = data;
		this.description = description;
		this.updatedTime = ImportsUtils.convertToLocalDateTime(Instant.now());
	}
}
