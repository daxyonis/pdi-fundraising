package com.poivredesiles.fundraising.model.file;

import com.poivredesiles.fundraising.imports.ImportsUtils;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import java.time.Instant;
import java.time.LocalDateTime;

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
