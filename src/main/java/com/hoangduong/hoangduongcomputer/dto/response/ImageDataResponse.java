package com.hoangduong.hoangduongcomputer.dto.response;

import org.springframework.core.io.Resource;

public record ImageDataResponse(String contentType, Resource resource) {}
