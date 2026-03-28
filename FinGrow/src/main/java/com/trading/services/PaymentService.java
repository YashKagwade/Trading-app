package com.trading.services;

import com.trading.domain.PaymentMethod;
import com.trading.entities.PaymentOrder;
import com.trading.entities.PaymentResponse;
import com.trading.entities.User;

public interface PaymentService {

	PaymentOrder createOrder(User user,Long amount,PaymentMethod paymentmethod);
	
	PaymentOrder getPaymentOrderById(Long id) throws Exception;

	Boolean proceedPaymentOrder(PaymentOrder paymentOrder, String paymentId);

	PaymentResponse createPaytmPaymentLink(User user, Long amount);
	
	PaymentOrder getPaymentOrderByOrderId(String orderId) throws Exception;
	//saves payment order in database
	PaymentOrder save(PaymentOrder order);
}
