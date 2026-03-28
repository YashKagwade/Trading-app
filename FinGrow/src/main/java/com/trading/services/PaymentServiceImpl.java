package com.trading.services;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.paytm.pg.merchant.PaytmChecksum;
import com.trading.Repositories.PaymentOrderRepository;
import com.trading.Repositories.WalletRepository;
import com.trading.Repositories.WalletTransactionRepository;
import com.trading.config.PaytmConfig;
import com.trading.domain.PaymentMethod;
import com.trading.domain.PaymentStatus;
import com.trading.entities.PaymentOrder;
import com.trading.entities.PaymentResponse;
import com.trading.entities.User;
import com.trading.entities.Wallet;
import com.trading.entities.WalletTransaction;
import com.trading.entities.WalletTransactionType;

import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.json.JSONObject;
@Service
public class PaymentServiceImpl implements PaymentService {

    @Autowired
    private PaytmConfig paytmConfig;

    @Autowired
    private RestTemplate restTemplate;
    
    @Autowired
    private WalletService walletService;
    
    @Autowired
    private WalletTransactionRepository walletTransactionrepo;

    @Autowired
    private PaymentOrderRepository paymentorderrepo;

    @Override
    public PaymentOrder createOrder(User user, Long amount, PaymentMethod paymentmethod) {
    	 PaymentOrder paymentOrder = new PaymentOrder();
         paymentOrder.setUser(user);
         paymentOrder.setAmount(amount);
         paymentOrder.setPaymentmethod(paymentmethod);
         paymentOrder.setPaymentstatus(PaymentStatus.PENDING);
         paymentOrder.setOrderId("PAY_" + System.currentTimeMillis());
         return paymentorderrepo.save(paymentOrder);
    }

    @Override
    public PaymentOrder getPaymentOrderById(Long id) throws Exception {
        return paymentorderrepo.findById(id)
                .orElseThrow(() -> new Exception("Payment order not found"));
    }

    @Transactional
    @Override
    public Boolean proceedPaymentOrder(PaymentOrder paymentOrder, String txnId) {
    	 if (!paymentOrder.getPaymentstatus().equals(PaymentStatus.PENDING)) {
             return false;
         }

         paymentOrder.setPaymentstatus(PaymentStatus.SUCCESS);
         paymentOrder.setPaymentId(txnId);
         paymentorderrepo.save(paymentOrder);
         
         User user = paymentOrder.getUser();
         Wallet wallet = walletService.getUserWallet(user);
         walletService.addBalance(wallet, paymentOrder.getAmount());

         WalletTransaction txn = new WalletTransaction();
         txn.setWallet(wallet);
         txn.setAmount(paymentOrder.getAmount());
         txn.setType(WalletTransactionType.ADD_MONEY);
         txn.setDate(LocalDate.now());
         txn.setTransferId(txnId);
         txn.setPurpose("Paytm Deposit");
         walletTransactionrepo.save(txn);

         return true;
    }
    @Override
    public PaymentResponse createPaytmPaymentLink(User user, Long amount) {
        try {
            System.out.println("====== PAYTM DEBUG ======");
            System.out.println("MID: " + paytmConfig.getMerchantId());
            System.out.println("MKEY: " + paytmConfig.getMerchantKey());
            System.out.println("Amount: " + amount);
            
            // Create order
            PaymentOrder order = createOrder(user, amount, PaymentMethod.PAYTM);
            
            // Create JSONObject for request
            JSONObject paytmParams = new JSONObject();
            JSONObject body = new JSONObject();
            
            body.put("requestType", "Payment");
            body.put("mid", paytmConfig.getMerchantId());
            body.put("websiteName", "DEFAULT");
            body.put("orderId", order.getOrderId());
            // IMPORTANT: This callback URL should be your backend endpoint
            body.put("callbackUrl", paytmConfig.getCallbackUrl());
            body.put("channelId", "WEB");
            
            // Transaction amount
            JSONObject txnAmount = new JSONObject();
            txnAmount.put("value", amount.toString());
            txnAmount.put("currency", "INR");
            body.put("txnAmount", txnAmount);
            body.put("industryTypeId", "Retail");
            
            // User info
            JSONObject userInfo = new JSONObject();
            userInfo.put("custId", "CUST_" + user.getId());
            userInfo.put("firstName", user.getFullname());
            userInfo.put("email", user.getEmail());
            userInfo.put("mobile", user.getMobile() != null ? user.getMobile() : "9999999999");
            body.put("userInfo", userInfo);
            
            System.out.println("Request Body: " + body.toString());
            
            // Generate checksum
            String checksum = PaytmChecksum.generateSignature(body.toString(), paytmConfig.getMerchantKey());
            
            // Build head with signature
            JSONObject head = new JSONObject();
            head.put("signature", checksum);
            
            paytmParams.put("body", body);
            paytmParams.put("head", head);
            
            // Prepare HTTP request
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
         // 🔥 FIX FOR 403 ERROR
            headers.set("User-Agent", "Mozilla/5.0");
            headers.set("Accept", "application/json");

            // 🔥 FIX FOR 403 ERROR
            headers.set("User-Agent", "Mozilla/5.0");
            headers.set("Accept", "application/json");
            
            String requestBody = paytmParams.toString();
            HttpEntity<String> httpEntity = new HttpEntity<>(requestBody, headers);
            
            // Call Paytm API
            String url = "https://securestage.paytmpayments.com/theia/api/v1/initiateTransaction?mid=" 
                    + paytmConfig.getMerchantId() 
                    + "&orderId=" + order.getOrderId();
            
            System.out.println("Paytm URL: " + url);
            
            ResponseEntity<String> response = restTemplate.postForEntity(url, httpEntity, String.class);
            
            System.out.println("Response Status: " + response.getStatusCode());
            System.out.println("Response Body: " + response.getBody());
            
            if (response.getBody() == null) {
                throw new RuntimeException("Empty response from Paytm");
            }
            
            // Parse response
            JSONObject responseJson = new JSONObject(response.getBody());
            JSONObject responseBody = responseJson.getJSONObject("body");
            
            // Check for errors
            if (responseBody.has("resultInfo")) {
                JSONObject resultInfo = responseBody.getJSONObject("resultInfo");
                String resultCode = resultInfo.getString("resultCode");
                if (!"0000".equals(resultCode)) {
                    String resultMsg = resultInfo.getString("resultMsg");
                    System.err.println("Paytm Error: " + resultCode + " - " + resultMsg);
                    throw new RuntimeException("Paytm error: " + resultMsg);
                }
            }
            
            String txnToken = responseBody.getString("txnToken");
            
            // Build payment response
            PaymentResponse paymentResponse = new PaymentResponse();
            paymentResponse.setOrderId(order.getOrderId());
            paymentResponse.setTxnToken(txnToken);
            paymentResponse.setMid(paytmConfig.getMerchantId());
            paymentResponse.setAmount(amount.toString());
            paymentResponse.setPaymentUrl(
                    "https://securestage.paytmpayments.com/theia/api/v1/showPaymentPage?mid=" 
                            + paytmConfig.getMerchantId() 
                            + "&orderId=" + order.getOrderId()
            );
            
            System.out.println("Payment URL: " + paymentResponse.getPaymentUrl());
            System.out.println("====== PAYTM SUCCESS ======");
            
            return paymentResponse;
            
        } catch (Exception e) {
            System.err.println("====== PAYTM ERROR ======");
            e.printStackTrace();
            throw new RuntimeException("Paytm API error: " + e.getMessage());
        }
    }

    @Override
    public PaymentOrder getPaymentOrderByOrderId(String orderId) throws Exception {
    	return paymentorderrepo.findByOrderId(orderId)
                .orElseThrow(() -> new Exception("Payment order not found"));
    }

    @Override
    public PaymentOrder save(PaymentOrder order) {
        return paymentorderrepo.save(order);
    }
}