package com.hoangduong.hoangduongcomputer.mapper;

import com.hoangduong.hoangduongcomputer.dto.request.RoleRequest;
import com.hoangduong.hoangduongcomputer.dto.response.RoleResponse;
import com.hoangduong.hoangduongcomputer.entity.Role;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface RoleMapper {
    Role toEntity(RoleRequest request);
    RoleResponse toResponse(Role role);
}
