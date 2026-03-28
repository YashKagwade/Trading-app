package com.trading.services;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.trading.Repositories.WalletTransactionRepository;
import com.trading.Repositories.WithdrawlRepository;
import com.trading.domain.WithdrawalStatus;
import com.trading.entities.User;
import com.trading.entities.Wallet;
import com.trading.entities.WalletTransaction;
import com.trading.entities.WalletTransactionType;
import com.trading.entities.Withdrawl;

@Service
public class WithdrawlServiceImpl implements WithdrawlService {

    // Repository layer to interact with database table
    // Handles CRUD operations for Withdrawl entity
    @Autowired
    private WithdrawlRepository withdrawlrepo;

    
    @Autowired
    private WalletService walletService;
    
    @Autowired
    private WalletTransactionRepository walletTransactionRepo;

    @Override
    public Withdrawl requestWithdrawal(Long amount, User user) {

    	// Create new withdrawal object
        Withdrawl withdrawl = new Withdrawl();
        
        // ✅ Set withdrawal properties
        withdrawl.setAmount(amount);
        withdrawl.setUser(user);
        withdrawl.setStatus(WithdrawalStatus.PENDING);
        withdrawl.setDate(LocalDateTime.now());
        
        // Save withdrawal request
        Withdrawl savedWithdrawal = withdrawlrepo.save(withdrawl);
        
        // ✅ Deduct amount from wallet immediately
        Wallet userWallet = walletService.getUserWallet(user);
        walletService.addBalance(userWallet, -amount);
        
        // ✅ Create Wallet Transaction for withdrawal request
        WalletTransaction txn = new WalletTransaction();
        txn.setWallet(userWallet);
        txn.setAmount(amount);
        txn.setType(WalletTransactionType.WITHDRAWAL);
        txn.setDate(LocalDateTime.now().toLocalDate());
        txn.setTransferId("WD_" + savedWithdrawal.getId());
        txn.setPurpose("Withdrawal Request - Pending Approval");
        walletTransactionRepo.save(txn);

        return savedWithdrawal;

    }

    @Override
    public Withdrawl proceedWithdrawal(Long withdrawalId, boolean accept) throws Exception {

        // Fetch withdrawal record from DB using ID
        Optional<Withdrawl> withdrawlOpt = withdrawlrepo.findById(withdrawalId);

        // If withdrawal not found, throw exception
        if (withdrawlOpt.isEmpty()) {
            throw new Exception("Withdrawl not found");
        }

        // Extract withdrawal entity from Optional
        Withdrawl withdrawal = withdrawlOpt.get();

        // Set processing date (when admin takes action)
        withdrawal.setDate(LocalDateTime.now());

        // Get user's wallet
        Wallet userWallet = walletService.getUserWallet(withdrawal.getUser());

        // If admin approves → mark SUCCESS
        if (accept) {
            withdrawal.setStatus(WithdrawalStatus.SUCCESS);
            
            // ✅ Update transaction to SUCCESS
            Optional<WalletTransaction> txnOpt = walletTransactionRepo.findByTransferId("WD_" + withdrawal.getId());
            if (txnOpt.isPresent()) {
                WalletTransaction txn = txnOpt.get();
                txn.setPurpose("Withdrawal Approved - Amount Sent");
                walletTransactionRepo.save(txn);
            }
        }
        // If admin rejects → mark as DECLINE and refund
        else {
            withdrawal.setStatus(WithdrawalStatus.DECLINE);
            
            // ✅ Update transaction to DECLINE
            Optional<WalletTransaction> txnOpt = walletTransactionRepo.findByTransferId("WD_" + withdrawal.getId());
            if (txnOpt.isPresent()) {
                WalletTransaction txn = txnOpt.get();
                txn.setPurpose("Withdrawal Rejected - Amount Refunded");
                walletTransactionRepo.save(txn);
            }
            
            // ✅ Refund amount back to wallet (since it was deducted on request)
            walletService.addBalance(userWallet, withdrawal.getAmount());
        }

        // Save updated withdrawal status in database
        return withdrawlrepo.save(withdrawal);
    
    }

    @Override
    public List<Withdrawl> getUsersWithdrawalHistory(User user) {

        // Fetch all withdrawal records for specific user
        // Used for showing withdrawal history in dashboard
        return withdrawlrepo.findByUserId(user.getId());
    }

    @Override
    public List<Withdrawl> getAllWithdrawalRequest() {

        // Fetch all withdrawal requests from system
        // Typically used in admin panel
        return withdrawlrepo.findAll();
    }
}