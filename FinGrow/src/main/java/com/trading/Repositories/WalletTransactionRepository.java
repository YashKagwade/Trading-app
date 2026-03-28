package com.trading.Repositories;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.trading.entities.WalletTransaction;
import com.trading.entities.WalletTransactionType;

public interface WalletTransactionRepository extends JpaRepository<WalletTransaction, Long> {
    
    // Find all transactions by wallet ID
    List<WalletTransaction> findByWalletId(Long walletId);
    
    // Find transactions by wallet ID sorted by date descending (newest first)
    List<WalletTransaction> findByWalletIdOrderByDateDesc(Long walletId);
    
    // ✅ Find by transfer ID - returns Optional (may or may not exist)
    Optional<WalletTransaction> findByTransferId(String transferId);
    
    // Find by transaction type
    List<WalletTransaction> findByType(WalletTransactionType type);
    
    // Find by wallet ID and type
    List<WalletTransaction> findByWalletIdAndType(Long walletId, WalletTransactionType type);
}