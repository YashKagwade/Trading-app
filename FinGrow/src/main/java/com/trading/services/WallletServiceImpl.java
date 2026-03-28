package com.trading.services;

import java.math.BigDecimal;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.trading.Repositories.UserRepo;
import com.trading.Repositories.WalletRepository;
import com.trading.Repositories.WalletTransactionRepository;
import com.trading.entities.Order;
import com.trading.entities.OrderType;
import com.trading.entities.User;
import com.trading.entities.Wallet;
import com.trading.entities.WalletTransaction;
import com.trading.entities.WalletTransactionType;

import jakarta.transaction.Transactional;

@Service
public class WallletServiceImpl implements WalletService{
	
	
	
	@Autowired
	private WalletRepository waletrepo;
	
	@Autowired
	private WalletTransactionRepository wallettransrepo;
	@Autowired
	private UserRepo userrepo;
	@Override
	public Wallet getUserWallet(User user) {

	    Wallet wallet = waletrepo.findByUserId(user.getId());

	    if (wallet == null) {
	        wallet = new Wallet();                 // create new wallet
	        wallet.setUser(user);                  // link to user
	        wallet.setBalance(BigDecimal.ZERO);    // initialize balance
	        wallet = waletrepo.save(wallet);       // save in DB
	    }

	    return wallet;
	}

	@Override
	public Wallet addBalance(Wallet wallet, Long money) {

	    /*
	     * STEP 1:
	     * Get the current balance from wallet.
	     * Balance is stored as BigDecimal because
	     * money calculations must be precise.
	     */
	    BigDecimal balance = wallet.getBalance();

	    /*
	     * STEP 2:
	     * Convert the Long money amount into BigDecimal.
	     *
	     * Why?
	     * Because you cannot directly add Long to BigDecimal.
	     *
	     * BigDecimal.valueOf(money)
	     * converts Long → BigDecimal safely.
	     */
	    BigDecimal amountToAdd = BigDecimal.valueOf(money);

	    /*
	     * STEP 3:
	     * Add existing balance + amountToAdd.
	     *
	     * IMPORTANT:
	     * BigDecimal is immutable.
	     * It does NOT modify original object.
	     * It returns a NEW BigDecimal.
	     */
	    BigDecimal newBalance = balance.add(amountToAdd);

	    /*
	     * STEP 4:
	     * Set updated balance back into wallet object.
	     */
	    wallet.setBalance(newBalance);

	    /*
	     * STEP 5:
	     * Save updated wallet into database.
	     */
	    return waletrepo.save(wallet);
	}

	@Override
	public Wallet findWalletById(Long id) throws Exception {
	    Optional<Wallet> wallet = waletrepo.findById(id);  // Fetch wallet from database using ID (returns Optional)

	    if (wallet.isPresent()) {  // Check if wallet exists in the database
	        return wallet.get();  // Return the wallet object if found
	    }

	    throw new Exception("wallet not found");  // Throw exception if wallet does not exist
	}

	@Transactional
	@Override
	public Wallet walletToWalletTransfer(User sender, Wallet receiverWallet, Long amount) {

	    if (amount <= 0) {
	        throw new RuntimeException("Amount must be greater than 0");
	    }

	    Wallet senderWallet = getUserWallet(sender);

	    if (senderWallet.getId().equals(receiverWallet.getId())) {
	        throw new RuntimeException("Cannot transfer to same wallet");
	    }

	    if (senderWallet.getBalance().compareTo(BigDecimal.valueOf(amount)) < 0) {
	        throw new RuntimeException("Insufficient balance");
	    }

	    // Deduct
	    senderWallet.setBalance(
	        senderWallet.getBalance().subtract(BigDecimal.valueOf(amount))
	    );

	    // Add
	    receiverWallet.setBalance(
	        receiverWallet.getBalance().add(BigDecimal.valueOf(amount))
	    );

	    waletrepo.save(senderWallet);
	    waletrepo.save(receiverWallet);
	 

	    // 🔥 ADD TRANSACTION ENTRY
	    //SENDER transaction
	    WalletTransaction txn = new WalletTransaction();
	    txn.setWallet(senderWallet);
	    txn.setAmount(amount);
	    txn.setType(WalletTransactionType.WALLET_TRANSFER);
	    txn.setDate(java.time.LocalDate.now());
	    txn.setTransferId("TR_" + System.currentTimeMillis());
	    txn.setPurpose("Transfer to another user");

	    //RECEIVER transactoin
	    
	    wallettransrepo.save(txn);
	    WalletTransaction receiverTxn = new WalletTransaction();
	    receiverTxn.setWallet(receiverWallet);
	    receiverTxn.setAmount(amount);
	    receiverTxn.setType(WalletTransactionType.WALLET_TRANSFER);
	    receiverTxn.setDate(java.time.LocalDate.now());
	    receiverTxn.setId(txn.getId());
	    receiverTxn.setPurpose("Received from " + senderWallet.getUser().getEmail());
	    return senderWallet;

	}

	@Override
	public Wallet payOrderPayment(Order order, User user) throws Exception {

	    // Get the wallet associated with the user.
	    // This fetches current balance before processing payment.
	    Wallet wallet = getUserWallet(user);

	    // Check if the order type is BUY.
	    // If BUY → money should be deducted from wallet.
	    if (order.getOrderType().equals(OrderType.BUY)) {

	        // Calculate new balance after subtracting order price.
	        // BigDecimal ensures accurate financial calculation.
	        BigDecimal newbalance = wallet.getBalance().subtract(order.getPrice());

	        // Check if user has enough balance.
	        // compareTo() < 0 means balance is less than required amount.
	        if (wallet.getBalance().compareTo(order.getPrice()) < 0) {
	            throw new Exception("Insufficient Funds for this transaction ");
	        }

	        // Update wallet with deducted balance.
	        // This reflects payment made for BUY order.
	        wallet.setBalance(newbalance);

	    } else {

	        // If order type is SELL.
	        // Add order price to wallet balance.
	        BigDecimal newbalance = wallet.getBalance().add(order.getPrice());

	        // Update wallet with increased balance.
	        // This reflects profit received from SELL order.
	        wallet.setBalance(newbalance);
	    }

	    // Save updated wallet state into database.
	    // Ensures changes are persisted permanently.
	    waletrepo.save(wallet);

	    // Return updated wallet object.
	    // Controller can send updated balance to client.
	    return wallet;
	}

	@Override
	public Wallet findWalletByUserEmail(String email) throws Exception {
		 User user = userrepo.findByEmail(email);

		    if (user == null) {
		        throw new Exception("User not found with email: " + email);
		    }

		    return getUserWallet(user); // auto-create if not exists
	
	}


}
