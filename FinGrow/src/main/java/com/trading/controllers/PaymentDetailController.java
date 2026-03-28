package com.trading.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.trading.entities.PaymentDetails;
import com.trading.entities.PaymentDetailsRequest;
import com.trading.entities.PaymentOrder;
import com.trading.entities.User;
import com.trading.services.PaymentDetailsService;
import com.trading.services.PaymentService;
import com.trading.services.UserService;

@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:5174"})
@RestController
@RequestMapping("/api")
public class PaymentDetailController {

    @Autowired
    private UserService userService;

    @Autowired
    private PaymentDetailsService paymentDetailsService;

    @Autowired
   private  PaymentService paymentservice;
    
    @PostMapping("/payment-details")
    public ResponseEntity<PaymentDetails> addPaymentDetails(
            @RequestBody PaymentDetailsRequest paymentdetrequest,
            @RequestHeader("Authorization") String jwt
    ) throws Exception {

        User user = userService.findUserByJwt(jwt);

        PaymentDetails paymentDetails =
                paymentDetailsService.addPaymentDetails(
                        paymentdetrequest.getAccountNumber(),
                        paymentdetrequest.getAccountHolderName(),
                        paymentdetrequest.getIfsc(),
                        paymentdetrequest.getBankName(),
                        user
                );

        return new ResponseEntity<>(paymentDetails, HttpStatus.CREATED);
    }

    @GetMapping("/payment-details")
    public ResponseEntity<PaymentDetails> getUsersPaymentDetails(
            @RequestHeader("Authorization") String jwt
    ) throws Exception {

        User user = userService.findUserByJwt(jwt);

        PaymentDetails paymentDetails =
                paymentDetailsService.getUserPaymentDetails(user);

        return new ResponseEntity<>(paymentDetails, HttpStatus.OK);
    }
    
}