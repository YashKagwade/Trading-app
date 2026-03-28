package com.trading.services;

import java.util.List;


import com.trading.entities.User;
import com.trading.entities.Withdrawl;

public interface WithdrawlService {

	// Create a new withdrawal request from user
    // Deduct amount from wallet and mark status as PENDING
    Withdrawl requestWithdrawal(Long amount, User user);

    // Admin approves or rejects a withdrawal request
    // Updates withdrawal status based on accept flag
    Withdrawl proceedWithdrawal(Long withdrawalId, boolean accept) throws Exception;

    // Fetch complete withdrawal history of a specific user
    // Used for user dashboard transaction records
    List<Withdrawl> getUsersWithdrawalHistory(User user);

    // Fetch all withdrawal requests in system
    // Mainly used by admin panel to manage requests
    List<Withdrawl> getAllWithdrawalRequest();	
}
