package com.hoangduong.hoangduongcomputer.controller;

import com.hoangduong.hoangduongcomputer.dto.ApiReponse;
import com.hoangduong.hoangduongcomputer.dto.request.RoleRequest;
import com.hoangduong.hoangduongcomputer.dto.response.RoleResponse;
import com.hoangduong.hoangduongcomputer.service.RoleService;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
public class RoleController {
    RoleService roleService;

    @PostMapping("/role")
    ApiReponse<RoleResponse> create(RoleRequest request) {
        return ApiReponse.<RoleResponse>builder()
                .result(roleService.create(request))
                .build();
    }

    @GetMapping("/role/{roleId}")
    ApiReponse<RoleResponse> getById(@PathVariable  String roleId) {
        return ApiReponse.<RoleResponse>builder()
                .result(roleService.getById(roleId))
                .build();
    }

    @DeleteMapping("/role/{roleId}")
    ApiReponse<String> delete(@PathVariable String roleId) {
        roleService.delete(roleId);
        return ApiReponse.<String>builder()
                .result("Delete role successfully")
                .build();
    }
}
