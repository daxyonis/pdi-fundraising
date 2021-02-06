package com.poivredesiles.fundraising.controller.rest;

import java.io.File;
import java.io.IOException;

import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/file")
public class FileController {
	
	@PostMapping("/upload")	
	@Secured("ROLE_ADMIN")
	public String handleFileUpload(@RequestParam("file") MultipartFile file, @RequestParam("fileType") String fileType) throws IllegalStateException, IOException {
		String fileName = file.getOriginalFilename();
	    
	    file.transferTo( new File("C:\\upload\\" + fileName));
	    
	    return "File of type " + fileType + ": " + fileName;
	}

}
