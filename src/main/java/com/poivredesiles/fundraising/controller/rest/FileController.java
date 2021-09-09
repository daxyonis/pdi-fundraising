package com.poivredesiles.fundraising.controller.rest;

import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.LocaleResolver;

import com.poivredesiles.fundraising.exception.PdiImportDataException;
import com.poivredesiles.fundraising.imports.CsvImportService;
import com.poivredesiles.fundraising.imports.ImportsUtils.DataTypeEnum;
import com.poivredesiles.fundraising.model.file.FileDB;
import com.poivredesiles.fundraising.resource.ResponseMessage;
import com.poivredesiles.fundraising.service.file.FileDBService;

@RestController
@RequestMapping("/api/file")
public class FileController {
	
	@Autowired
	private CsvImportService csvImportService;
	
	@Autowired
	private MessageSource messageSource;
	
	@Autowired
	private LocaleResolver localeResolver;
	
	@Autowired
	private FileDBService fileService;
	
	@PostMapping("/data/upload")	
	@Secured("ROLE_ADMIN")
	public Map<String, String> handleFileUpload(@RequestParam("file") MultipartFile file, 
												@RequestParam("fileType") String fileType, 
												HttpServletRequest request) 
														throws IllegalStateException, IOException, PdiImportDataException {				
		String lastImportDate = csvImportService.dispatchImport(file, DataTypeEnum.valueOf(fileType));	    
	    String message = messageSource.getMessage("admin.import.success", new Object[] {fileType}, localeResolver.resolveLocale(request));
	    var map = Map.of("lastImportDate",lastImportDate, "message", message);
	    return map;
	}

	
	@PostMapping("/upload")
	@Secured("ROLE_ADMIN")
	public ResponseEntity<ResponseMessage> uploadFile(@RequestParam("file") MultipartFile file, @RequestParam("description") String description) {
		String message = "";
		try {
			fileService.store(file, description);

			message = "Uploaded the file successfully: " + file.getOriginalFilename();
			return ResponseEntity.status(HttpStatus.OK).body(new ResponseMessage(message));
		} catch (Exception e) {
			message = "Could not upload the file: " + file.getOriginalFilename() + "!";
			return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new ResponseMessage(message));
		}
	}
	
	@GetMapping("/")
	public ResponseEntity<byte[]> getMostRecentFile(@RequestParam("description") String description) {
		FileDB fileDB = fileService.getMostRecentFileWithDescription(description);
		if(fileDB != null) {
			return ResponseEntity.ok()
			        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileDB.getName() + "\"")
			        .body(fileDB.getData());	
		} else {
			return ResponseEntity.ok(null);
		}
		
	}
	
	@GetMapping("/{id}")
	  public ResponseEntity<byte[]> getFile(@PathVariable String id) {
	    FileDB fileDB = fileService.getFile(id);

	    return ResponseEntity.ok()
	        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileDB.getName() + "\"")
	        .body(fileDB.getData());
	  }
}
