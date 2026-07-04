package com.sudip.razorpay.merchant.service.impl;

import com.sudip.razorpay.common.enums.MerchantStatus;
import com.sudip.razorpay.common.enums.UserRole;
import com.sudip.razorpay.common.exceptions.DuplicateResourceException;
import com.sudip.razorpay.merchant.dto.request.MerchantSignupRequest;
import com.sudip.razorpay.merchant.dto.response.MerchantResponse;
import com.sudip.razorpay.merchant.entity.AppUser;
import com.sudip.razorpay.merchant.entity.Merchant;
import com.sudip.razorpay.merchant.mapper.MerchantMapper;
import com.sudip.razorpay.merchant.repository.AppUserRepository;
import com.sudip.razorpay.merchant.repository.MerchantRepository;
import com.sudip.razorpay.merchant.service.AuthService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final AppUserRepository appUserRepository;
    private final MerchantRepository merchantRepository;
    private final MerchantMapper merchantMapper;

    @Override
    @Transactional
    public MerchantResponse signup(MerchantSignupRequest request) {
        if(merchantRepository.existsByEmail(request.email())) {
            throw new DuplicateResourceException("DUPLICATE_MERCHANT_EMAIL",
                    "Merchant with email already exists! "+request.email());
        }

        Merchant merchant = merchantMapper.toEntityFromSignUpRequest(request);
        merchant.setStatus(MerchantStatus.PENDING_KYC);
        merchant = merchantRepository.save(merchant);

        AppUser appUser = AppUser.builder()
                .email(request.email())
                .merchant(merchant)
                .passwordHash(request.password()) // TODO: encrypt using Bcrypt
                .role(UserRole.OWNER)
                .build();
        appUserRepository.save(appUser);

        return merchantMapper.toResponse(merchant);
    }
}
