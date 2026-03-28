package com.trading.controllers;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import com.paytm.pg.merchant.PaytmChecksum;
import com.trading.config.PaytmConfig;
import com.trading.entities.PaymentOrder;
import com.trading.entities.User;
import com.trading.services.PaymentService;
import com.trading.services.UserService;
import com.trading.services.WalletService;

@CrossOrigin(origins = "*")
@Controller
@RequestMapping("/api")
public class PaymentController {

    @Autowired
    private UserService userService;

    @Autowired
    private PaytmConfig paytmConfig;

    @Autowired
    private PaymentService paymentservice;

    @Autowired
    private WalletService walletService;

    // =========================
    // 🔹 INITIATE PAYMENT
    // =========================
    @PostMapping("/payment/create")
    public ResponseEntity<?> createPayment(
            @RequestHeader("Authorization") String jwt,
            @RequestParam Long amount
    ) throws Exception {
        User user = userService.findUserByJwt(jwt);
        return ResponseEntity.ok(
                paymentservice.createPaytmPaymentLink(user, amount)
        );
    }

    // =========================
    // 🔹 TEST DEPOSIT (No Paytm required)
    // =========================
    @PostMapping("/payment/test-deposit")
    public ResponseEntity<?> testDeposit(
            @RequestHeader("Authorization") String jwt,
            @RequestParam Long amount
    ) throws Exception {
        User user = userService.findUserByJwt(jwt);
        
        PaymentOrder order = paymentservice.createOrder(user, amount, com.trading.domain.PaymentMethod.PAYTM);
        order.setPaymentstatus(com.trading.domain.PaymentStatus.SUCCESS);
        order.setPaymentId("TEST_" + System.currentTimeMillis());
        paymentservice.save(order);
        
        paymentservice.proceedPaymentOrder(order, "TEST_TXN_" + System.currentTimeMillis());
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Test deposit successful");
        response.put("amount", amount);
        response.put("newBalance", walletService.getUserWallet(user).getBalance());
        
        return ResponseEntity.ok(response);
    }

    // =========================
    // 🔹 DEBUG ENDPOINT
    // =========================
    @GetMapping("/payment/debug")
    public ResponseEntity<?> debugConfig() {
        Map<String, Object> debug = new HashMap<>();
        debug.put("mid", paytmConfig.getMerchantId());
        debug.put("midLength", paytmConfig.getMerchantId() != null ? paytmConfig.getMerchantId().length() : 0);
        debug.put("keyLength", paytmConfig.getMerchantKey() != null ? paytmConfig.getMerchantKey().length() : 0);
        debug.put("callbackUrl", paytmConfig.getCallbackUrl());
        debug.put("paytmConfigExists", paytmConfig != null);
        return ResponseEntity.ok(debug);
    }

    // =========================
    // 🔹 PAYTM CALLBACK
    // =========================
    @CrossOrigin("*")
    @PostMapping("/payment/callback")
    public String paymentCallback(@RequestParam Map<String, String> response) throws Exception {

        String paytmChecksum = response.get("CHECKSUMHASH");

        TreeMap<String, String> paytmParams = new TreeMap<>(response);
        paytmParams.remove("CHECKSUMHASH");

        boolean isValidChecksum = PaytmChecksum.verifySignature(
                paytmParams,
                paytmConfig.getMerchantKey(),
                paytmChecksum
        );

        if (!isValidChecksum) {
            return "redirect:http://localhost:5173/wallet?status=failed";
        }

        String orderId = response.get("ORDERID");
        String txnId = response.get("TXNID");
        String txnStatus = response.get("STATUS");

        if ("TXN_SUCCESS".equals(txnStatus)) {

            PaymentOrder order = paymentservice.getPaymentOrderByOrderId(orderId);

            paymentservice.proceedPaymentOrder(order, txnId);

            return "redirect:http://localhost:5173/wallet?status=success&orderId=" + orderId + "&txnId=" + txnId;
        }

        return "redirect:http://localhost:5173/wallet?status=failed";
    }
}