package com.sudip.razorpay.payment.config;

import com.sudip.razorpay.common.enums.PaymentMethods;
import com.sudip.razorpay.payment.gateway.PaymentAdapter;
import com.sudip.razorpay.payment.gateway.adapter.CardPaymentAdapter;
import com.sudip.razorpay.payment.gateway.adapter.NetBankingAdapter;
import com.sudip.razorpay.payment.gateway.adapter.UpiPaymentAdapter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class PaymentAdapterConfig {

    private final NetBankingAdapter netBankingAdapter;
    private final CardPaymentAdapter cardPaymentAdapter;
    private final UpiPaymentAdapter upiPaymentAdapter;

    @Bean
    public Map<PaymentMethods, PaymentAdapter> paymentAdapterMap() {
        return Map.of(
                PaymentMethods.CARD, cardPaymentAdapter,
                PaymentMethods.NETBANKING, netBankingAdapter,
                PaymentMethods.UPI, upiPaymentAdapter
        );
    }
}
