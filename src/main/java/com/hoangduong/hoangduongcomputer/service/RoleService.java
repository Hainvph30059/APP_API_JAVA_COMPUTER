package com.hoangduong.hoangduongcomputer.service;

import com.hoangduong.hoangduongcomputer.dto.request.RoleRequest;
import com.hoangduong.hoangduongcomputer.dto.response.RoleResponse;
import com.hoangduong.hoangduongcomputer.exception.AppException;
import com.hoangduong.hoangduongcomputer.exception.ErrorCode;
import com.hoangduong.hoangduongcomputer.mapper.RoleMapper;
import com.hoangduong.hoangduongcomputer.reponsitory.RoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
public class RoleService {
    RoleRepository roleRepository;
    RoleMapper roleMapper;

    public RoleResponse create(RoleRequest request) {
        roleRepository.findByName(request.getName()).orElseThrow(() -> new AppException(ErrorCode.ROLE_EXISTED));
        return roleMapper.toResponse(
                roleRepository.save(roleMapper.toEntity(request))
        );
    }

    public RoleResponse getById(String roleId) {
        return roleMapper.toResponse(
                roleRepository.findById(roleId).orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_EXISTED))
        );
    }
    public void delete(String roleId) {
        roleRepository.deleteById(roleId);
    }

}
