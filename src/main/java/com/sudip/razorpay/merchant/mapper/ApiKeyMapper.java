package com.sudip.razorpay.merchant.mapper;

import com.sudip.razorpay.merchant.dto.response.ApiKeyCreateResponse;
import com.sudip.razorpay.merchant.dto.response.ApiKeyResponse;
import com.sudip.razorpay.merchant.entity.ApiKey;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ApiKeyMapper {

    ApiKeyCreateResponse toCreateResponse(ApiKey apiKey);

    List<ApiKeyResponse> toResponse(List<ApiKey> apiKeyList);
}
