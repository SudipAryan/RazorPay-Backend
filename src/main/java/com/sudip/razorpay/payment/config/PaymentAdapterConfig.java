package com.sudip.razorpay.payment.config;

import com.sudip.razorpay.common.enums.PaymentMethods;
import com.sudip.razorpay.payment.gateway.PaymentAdapter;
import com.sudip.razorpay.payment.gateway.adapter.CardPaymentAdapter;
import com.sudip.razorpay.payment.gateway.adapter.NetBankingAdapter;
import com.sudip.razorpay.payment.gateway.adapter.UpiPaymentAdapter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
public class PaymentAdapterConfig {

    @Bean
    public Map<PaymentMethods, PaymentAdapter> paymentAdapterMap() {
        return Map.of(
                PaymentMethods.CARD, new CardPaymentAdapter(),
                PaymentMethods.NETBANKING, new NetBankingAdapter(),
                PaymentMethods.UPI, new UpiPaymentAdapter()
        );
    }
}
