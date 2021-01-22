package com.poivredesiles.fundraising.service.mapper;

import java.util.Set;
import java.util.stream.Collectors;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import com.poivredesiles.fundraising.model.Role;
import com.poivredesiles.fundraising.model.RoleEnum;
import com.poivredesiles.fundraising.model.User;
import com.poivredesiles.fundraising.service.dto.UserDTO;

@Mapper(componentModel = "spring", uses = {MapperUtils.class})
public interface UserMapper extends EntityMapper<UserDTO, User> {

	@Mapping(source="createdDate", target="formattedCreatedDate", qualifiedByName="instantToString")
	@Mapping(source="lastModifiedDate", target="formattedLastModifiedDate", qualifiedByName="instantToString")
	@Mapping(source="roles", target="roles", qualifiedByName="rolesToStrings")
	UserDTO toDto(User user);
	
	@Mapping(source="roles", target="roles", qualifiedByName="stringsToRoles")
	User toEntity(UserDTO userDTO);
	
	@Named("rolesToStrings")
	public static Set<String> mapRoleSetToStrings(Set<Role> roles){
		return roles.stream().map(Role::getName).collect(Collectors.toSet());
	}
	
	@Named("stringsToRoles")
	public static Set<Role> mapStringSetToRoles(Set<String> roles){
		return roles.stream().map(s -> new Role(RoleEnum.valueOf(s))).collect(Collectors.toSet());
	}
	
	default User fromId(Long id) {
        if (id == null) {
            return null;
        }
        User user = new User();
        user.setId(id);
        return user;
    }
}