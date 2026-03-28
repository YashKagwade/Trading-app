package com.trading.services;

import com.trading.entities.Order;
import com.trading.entities.User;
import com.trading.entities.Wallet;

public interface WalletService {

    Wallet getUserWallet(User user);

    Wallet addBalance(Wallet wallet, Long money);

    Wallet findWalletById(Long id) throws Exception;

    Wallet walletToWalletTransfer(User sender, Wallet receiverWallet, Long amount);

    Wallet payOrderPayment(Order order, User user) throws Exception;
   // Wallet findWalletByEmail(String email) throws Exception;
    Wallet findWalletByUserEmail(String email) throws Exception;
}
