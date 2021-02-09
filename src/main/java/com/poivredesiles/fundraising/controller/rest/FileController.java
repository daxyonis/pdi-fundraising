package com.poivredesiles.fundraising.controller.rest;

import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.LocaleResolver;

import com.poivredesiles.fundraising.exception.PdiImportDataException;
import com.poivredesiles.fundraising.imports.CsvImportService;
import com.poivredesiles.fundraising.imports.ImportsUtils.DataTypeEnum;

@RestController
@RequestMapping("/api/file")
public class FileController {
	
	@Autowired
	private CsvImportService csvImportService;
	
	@Autowired
	private MessageSource messageSource;
	
	@Autowired
	private LocaleResolver localeResolver;
	
	@PostMapping("/upload")	
	@Secured("ROLE_ADMIN")
	public Map<String, String> handleFileUpload(@RequestParam("file") MultipartFile file, 
												@RequestParam("fileType") String fileType, 
												HttpServletRequest request) 
														throws IllegalStateException, IOException, PdiImportDataException {
//		String fileName = file.getOriginalFilename();	    
//	    file.transferTo( new File("C:\\upload\\" + fileName));
				
		String lastImportDate = csvImportService.dispatchImport(file, DataTypeEnum.valueOf(fileType));	    
	    String message = messageSource.getMessage("admin.import.success", new Object[] {fileType}, localeResolver.resolveLocale(request));
	    Map<String, String> map = Map.of("lastImportDate",lastImportDate, "message", message);
	    return map;
	}

}
