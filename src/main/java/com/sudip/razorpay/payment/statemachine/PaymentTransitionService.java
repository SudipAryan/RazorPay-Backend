package com.sudip.razorpay.payment.statemachine;

import com.sudip.razorpay.common.enums.PaymentActor;
import com.sudip.razorpay.common.enums.PaymentEvent;
import com.sudip.razorpay.common.enums.PaymentStatus;
import com.sudip.razorpay.payment.entity.Payment;
import com.sudip.razorpay.payment.entity.PaymentTransitionLog;
import com.sudip.razorpay.payment.repository.PaymentTransitionLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PaymentTransitionService {

    private final PaymentTransitionLogRepository paymentTransitionLogRepository;
    private final PaymentStateMachine paymentStateMachine;

    public PaymentStatus apply(Payment payment, PaymentEvent event) {
        PaymentStatus next = paymentStateMachine.transition(payment.getStatus(), event);
        PaymentTransitionLog log = PaymentTransitionLog.builder()
                .payment(payment)
                .fromStatus(payment.getStatus())
                .event(event)
                .toStatus(next)
                .actor(String.valueOf(PaymentActor.SYSTEM))// TODO: fetch merchant context to identify actor
                .occurredAt(LocalDateTime.now())
                .build();
        payment.setStatus(next);
        paymentTransitionLogRepository.save(log);
        return next;
    }
}

