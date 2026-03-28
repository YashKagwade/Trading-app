package com.trading.services;

import com.trading.entities.PaymentDetails;
import com.trading.entities.User;

public interface PaymentDetailsService {

	public PaymentDetails addPaymentDetails(String accountNumber,
											String accountHolderName,
											String ifsc,
											String bankName,
											User user);
			
			
public PaymentDetails getUserPaymentDetails(User user);	
	
}
