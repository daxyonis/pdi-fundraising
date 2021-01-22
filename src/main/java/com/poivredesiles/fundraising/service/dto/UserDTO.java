package com.poivredesiles.fundraising.service.dto;

import java.util.Set;

import lombok.Data;

/**
 * A DTO representing a user, with his authorities.
 */
@Data
public class UserDTO {

	private Long id;
	
	private String username;
	
	private String password;
	
	private String firstname;
	
	private String lastname;
	
	private String language;
	
	private boolean disabled; 
	
	private String formattedCreatedDate;
	
	private String formattedLastModifiedDate;
	
    private String createdBy;

    private String lastModifiedBy;

    private Set<String> roles;

//    public UserDTO() {
//        // Empty constructor needed for Jackson.
//    }
//
//    public UserDTO(User user) {
//        this.id = user.getId();
//        this.username = user.getUsername(); 
//        this.firstname = user.getFirstname();
//        this.lastname = user.getLastname();
//        this.roles = user.getRoles().stream()
//            .map(Role::getName)
//            .collect(Collectors.toSet());
//    }

    }
