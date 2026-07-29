package com.sudip.razorpay.payment.processor.strategy;

import com.sudip.razorpay.common.util.RandomizerUtil;
import com.sudip.razorpay.payment.processor.PaymentProcessor;
import com.sudip.razorpay.payment.processor.dto.PaymentProcessorRequest;
import com.sudip.razorpay.payment.processor.dto.PaymentProcessorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class CardPaymentProcessor implements PaymentProcessor {

    public static final String PAN_CARD_DECLINE = "400000000000000002";
    public static final String PAN_CARD_EXPIRE = "400000000000000069";

    @Override
    public PaymentProcessorResponse charge(PaymentProcessorRequest request) {

        String pan = request.pan();

        if (PAN_CARD_DECLINE.equals(pan)) {
            log.warn("Card Declined");
            return new PaymentProcessorResponse.Failure("CARD_DECLINED", "Card Declined by Bank");
        }

        if (PAN_CARD_EXPIRE.equals(pan)) {
            log.warn("Pan Card has Expire");
            return new PaymentProcessorResponse.Failure("CARD_EXPIRED", "Card has Expire");
        }

        String processorRef = "CARD_PROCESSOR "+ RandomizerUtil.randomBase64(16);

        return new PaymentProcessorResponse.Pending(processorRef);
    }
}
