package com.sudip.razorpay.payment.mapper;

import com.sudip.razorpay.payment.dto.response.OrderResponse;
import com.sudip.razorpay.payment.entity.OrderRecord;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface OrderMapper {

    OrderResponse toResponse(OrderRecord orderRecord);
}
