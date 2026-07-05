package com.sudip.razorpay.payment.config;

import com.sudip.razorpay.common.enums.PaymentMethods;
import com.sudip.razorpay.payment.processor.PaymentProcessor;
import com.sudip.razorpay.payment.processor.strategy.CardPaymentProcessor;
import com.sudip.razorpay.payment.processor.strategy.NetBankingPaymentProcessor;
import com.sudip.razorpay.payment.processor.strategy.UpiPaymentProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
public class PaymentProcessorConfig {

    @Bean
    public Map<PaymentMethods, PaymentProcessor> paymentProcessorMap() {
        return Map.of(
                PaymentMethods.CARD, new CardPaymentProcessor(),
                PaymentMethods.NETBANKING, new NetBankingPaymentProcessor(),
                PaymentMethods.UPI, new UpiPaymentProcessor()
        );
    }
}
