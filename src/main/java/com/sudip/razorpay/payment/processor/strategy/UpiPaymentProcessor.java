package com.sudip.razorpay.payment.processor.strategy;

import com.sudip.razorpay.common.util.RandomizerUtil;
import com.sudip.razorpay.payment.processor.PaymentProcessor;
import com.sudip.razorpay.payment.processor.dto.PaymentProcessorRequest;
import com.sudip.razorpay.payment.processor.dto.PaymentProcessorResponse;

public class UpiPaymentProcessor implements PaymentProcessor {

    @Override
    public PaymentProcessorResponse charge(PaymentProcessorRequest request) {
        final String VPA_CODE_FAIL = "fail@okaxis";

        String bankCode = request.methodDetails() != null ?
                request.methodDetails().get("vpa").toString() : null;

//        Simulation
        if (VPA_CODE_FAIL.equals(bankCode)) {
            return new PaymentProcessorResponse.Failure("UPI_REJECTED",
                    "Bank rejected the transaction registration"
                    );
        }

        String processorRef = "UPI_PROCESSOR "+ RandomizerUtil.randomBase64(16);

        return new PaymentProcessorResponse.Pending(processorRef);
    }
}
