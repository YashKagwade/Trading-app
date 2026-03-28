package com.trading.controllers;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.trading.Repositories.WalletTransactionRepository;
import com.trading.entities.Order;
import com.trading.entities.User;
import com.trading.entities.Wallet;
import com.trading.entities.WalletResponse;
import com.trading.entities.WalletTransaction;
import com.trading.entities.WalletTransactionType;
import com.trading.entities.WalletTransferRequest;
import com.trading.services.OrderService;
import com.trading.services.UserService;
import com.trading.services.WalletService;
import com.trading.entities.WalletTransacitonResponse;
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:5174"})
@RestController
@RequestMapping("/api/wallet")
public class WalletController {

    @Autowired
    private OrderService orderservice;

    @Autowired
    private WalletService walletService;

    @Autowired
    private UserService userService;
    
    @Autowired
    private WalletTransactionRepository walletTransactionRepo;
    
    
    private WalletResponse mapToResponse(Wallet wallet) {
        WalletResponse res = new WalletResponse();
        res.setWalletId(wallet.getId());
        res.setEmail(wallet.getUser().getEmail());
        res.setBalance(wallet.getBalance());
        return res;
    }
    private WalletTransacitonResponse mapToTxnResponse(WalletTransaction txn) {
    	WalletTransacitonResponse res = new WalletTransacitonResponse();
        res.setAmount(txn.getAmount());
        res.setType(txn.getType().name());
        res.setPurpose(txn.getPurpose());
        res.setDate(txn.getDate());
        return res;
    }
    
    
    // Add this endpoint to WalletController
    //Get all transactions of the wallet
    @GetMapping("/transactions")
    public ResponseEntity<List<WalletTransacitonResponse>> getWalletTransactions(
            @RequestHeader("Authorization") String jwt) throws Exception {

        User user = userService.findUserByJwt(jwt);
        Wallet wallet = walletService.getUserWallet(user);

        List<WalletTransaction> transactions =
                walletTransactionRepo.findByWalletId(wallet.getId());

        List<WalletTransacitonResponse> response =
                transactions.stream()
                        .map(this::mapToTxnResponse)
                        .toList();

        return ResponseEntity.ok(response);
    }
    // =====================================
    // 🔹 GET USER WALLET
    // =====================================
    @GetMapping("/getwallet")
    public ResponseEntity<WalletResponse> getUserWallet(
            @RequestHeader("Authorization") String jwt
    ) throws Exception {

        User user = userService.findUserByJwt(jwt);
        Wallet wallet = walletService.getUserWallet(user);

        return ResponseEntity.ok(mapToResponse(wallet));
    }

  
    @PostMapping("/transfer/email")
    public ResponseEntity<WalletResponse> transferByEmail(
            @RequestBody WalletTransferRequest request,
            @RequestHeader("Authorization") String jwt
    ) throws Exception {

        User sender = userService.findUserByJwt(jwt);

        Wallet receiverWallet = walletService.findWalletByUserEmail(request.getEmail());

        Wallet updatedWallet = walletService.walletToWalletTransfer(
                sender,
                receiverWallet,
                request.getAmount()
        );

        return ResponseEntity.ok(mapToResponse(updatedWallet));
    }
    // =====================================
    // 🔹 WALLET TO WALLET TRANSFER
    // =====================================


    
    // =====================================
    // 🔹 PAY ORDER USING WALLET
    // =====================================
    @PutMapping("/{orderId}/pay")
    public ResponseEntity<WalletResponse> payOrderPayment(
            @RequestHeader("Authorization") String jwt,
            @PathVariable Long orderId
    ) throws Exception {


        User user = userService.findUserByJwt(jwt);
        Order order = orderservice.getOrderById(orderId);
        Wallet wallet = walletService.payOrderPayment(order, user);

        return ResponseEntity.ok(mapToResponse(wallet));
    }
    // Get transactions by type (ADD_MONEY, BUY_ASSET, SELL_ASSET, WITHDRAWAL, WALLET_TRANSFER)
    @GetMapping("/transactions/type/{type}")
    public ResponseEntity<List<WalletTransacitonResponse>> getWalletTransactionsByType(
            @RequestHeader("Authorization") String jwt,
            @PathVariable WalletTransactionType type) throws Exception {

        User user = userService.findUserByJwt(jwt);
        Wallet wallet = walletService.getUserWallet(user);

        List<WalletTransaction> transactions =
                walletTransactionRepo.findByWalletIdAndType(wallet.getId(), type);

        List<WalletTransacitonResponse> response =
                transactions.stream()
                        .map(this::mapToTxnResponse)
                        .toList();

        return ResponseEntity.ok(response);
    }
}