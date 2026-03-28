package com.trading.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.trading.Repositories.PaymentDetailsRepository;
import com.trading.entities.PaymentDetails;
import com.trading.entities.User;

@Service
public class PaymentDetailsServiceImpl implements PaymentDetailsService {

	@Autowired
	PaymentDetailsRepository paymentdetrepo;
	
	
	@Autowired
	private RestTemplate restTemplate;
	@Override
	public PaymentDetails addPaymentDetails(String accountNumber, String accountHolderName, String ifsc,
			String bankName, User user) {
		PaymentDetails paydetails=new PaymentDetails();
		paydetails.setAccountNumber(accountNumber);
		paydetails.setAccountHolderName(accountHolderName);
		paydetails.setIfsc(ifsc);
		paydetails.setBankName(bankName);
		paydetails.setUser(user);
		
		return paymentdetrepo.save(paydetails);
	}

	@Override
	public PaymentDetails getUserPaymentDetails(User user) {
		PaymentDetails userpayment=paymentdetrepo.findByUserId(user.getId());
		
		
		return userpayment;
	}

}
