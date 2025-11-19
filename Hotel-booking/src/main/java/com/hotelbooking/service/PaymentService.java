package com.hotelbooking.service;

import com.hotelbooking.model.Payment;
import com.hotelbooking.model.PaymentMethod;

public interface PaymentService {
    Payment ensurePayment(Long bookingId, String userEmail);
    Payment markSuccess(Long paymentId, String userEmail, PaymentMethod method);
    Payment markFailure(Long paymentId, String userEmail, String reason);
    Payment getPaymentForUser(Long paymentId, String userEmail);
}
