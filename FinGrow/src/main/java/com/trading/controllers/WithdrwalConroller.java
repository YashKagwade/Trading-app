package com.trading.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.trading.domain.USER_ROLE;
import com.trading.entities.User;
import com.trading.entities.Wallet;
import com.trading.entities.Withdrawl;
import com.trading.services.UserService;
import com.trading.services.WalletService;
import com.trading.services.WithdrawlService;

@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:5174"})
@RestController
@RequestMapping("/api/withdrawal")
public class WithdrwalConroller {

    // Handles withdrawal business logic (create, approve, reject)
    @Autowired
    private WithdrawlService withdrawlservie;

    // Used for wallet balance updates (deduct/refund)
    @Autowired
    private WalletService waletservice;

    // Extract authenticated user from JWT
    @Autowired
    private UserService userService;

    // ✅ Admin check helper method
    private void checkAdmin(User user) throws Exception {
        if (user.getRole() != USER_ROLE.ROLE_ADMIN) {
            throw new Exception("Access denied. Admin privileges required.");
        }
    }

    // ================= USER WITHDRAWAL REQUEST =================
    // Flow: JWT → User → Create withdrawal → Deduct balance
    @PostMapping("/{amount}")
    public ResponseEntity<?> withdrawalRequest(
            @PathVariable Long amount,
            @RequestHeader("Authorization") String jwt
    ) throws Exception {

        // Extract logged-in user
        User user = userService.findUserByJwt(jwt);

        // Get user's wallet
        Wallet userWallet = waletservice.getUserWallet(user);

        // Create withdrawal request (status = PENDING)
        Withdrawl withdrawal = withdrawlservie.requestWithdrawal(amount, user);

        // Immediately deduct amount from wallet (freeze funds)
        waletservice.addBalance(userWallet, -withdrawal.getAmount());

        // Return withdrawal details
        return new ResponseEntity<>(withdrawal, HttpStatus.OK);
    }

 // Add this method to WithdrwalConroller.java
    @GetMapping("/user")
    public ResponseEntity<List<Withdrawl>> getUserWithdrawalHistory(
            @RequestHeader("Authorization") String jwt) throws Exception {
        User user = userService.findUserByJwt(jwt);
        List<Withdrawl> withdrawals = withdrawlservie.getUsersWithdrawalHistory(user);
        return ResponseEntity.ok(withdrawals);
    }
    // ================= ADMIN APPROVAL / REJECTION =================
    // Flow: Admin → Approve/Reject → Update status → Refund if rejected
    @PatchMapping("/admin/{id}/proceed/{accept}")
    public ResponseEntity<?> proceedWithdrawal(
            @PathVariable Long id,
            @PathVariable boolean accept,
            @RequestHeader("Authorization") String jwt
    ) throws Exception {

        // Authenticate admin (role validation should be added)
        User user = userService.findUserByJwt(jwt);
        checkAdmin(user); // ✅ Only admin can approve/reject
        // Update withdrawal status (SUCCESS or PENDING)
        Withdrawl withdrawal = withdrawlservie.proceedWithdrawal(id, accept);

        // If rejected → refund amount back to user's wallet
        Wallet userWallet = waletservice.getUserWallet(user);
        if (!accept) {
            waletservice.addBalance(userWallet, withdrawal.getAmount());
        }

        // Return updated withdrawal object
        return new ResponseEntity<>(withdrawal, HttpStatus.OK);
    }


    // ================= ADMIN VIEW ALL WITHDRAWALS =================
    // Used for admin dashboard
    @GetMapping("/admin")
    public ResponseEntity<List<Withdrawl>> getAllWithdrawalRequest(
            @RequestHeader("Authorization") String jwt
    ) throws Exception {

        // Authenticate admin (role check recommended)
        User user = userService.findUserByJwt(jwt);
        checkAdmin(user); // ✅ Only admin can access
        // Fetch all withdrawal requests
        List<Withdrawl> withdrawal =
                withdrawlservie.getAllWithdrawalRequest();

        // Return list of withdrawals
        return new ResponseEntity<>(withdrawal, HttpStatus.OK);
    }
}