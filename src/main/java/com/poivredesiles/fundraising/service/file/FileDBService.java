package com.poivredesiles.fundraising.service.file;

import java.io.IOException;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.poivredesiles.fundraising.model.file.FileDB;
import com.poivredesiles.fundraising.repository.file.FileDBRepository;

@Service
public class FileDBService {
	
	@Autowired
	private FileDBRepository fileDBRepository;

	public FileDB store(MultipartFile file, String description) throws IOException {
		String fileName = StringUtils.cleanPath(file.getOriginalFilename());
		FileDB FileDB = new FileDB(fileName, file.getContentType(), file.getBytes(), description);

		return fileDBRepository.save(FileDB);
	}

	public FileDB getFile(String id) {
		return fileDBRepository.findById(id).get();
	}

//	public Stream<FileDB> getAllFiles() {
//		return fileDBRepository.findAll().stream();
//	}
	
	public FileDB getMostRecentFileWithDescription(String description) {
		Optional<FileDB> fileDB = fileDBRepository.findTop1ByDescriptionOrderByUpdatedTimeDesc(description);
		return fileDB.orElse(null);		
	}
}
