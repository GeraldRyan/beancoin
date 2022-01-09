package com.ryan.gerald.beancoin.Service;

import com.ryan.gerald.beancoin.entity.Blockchain;
import com.ryan.gerald.beancoin.entity.Transaction;
import com.ryan.gerald.beancoin.entity.Wallet;
import com.ryan.gerald.beancoin.evaluation.BalanceCalculator;
import com.ryan.gerald.beancoin.repository.WalletRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class WalletService {

    @Autowired WalletRepository walletRepository;
    @Autowired private BalanceCalculator balanceCalculator;
    @Autowired private TransactionService transactionService;
    @Autowired private BlockchainService blockchainService;

    public Wallet getWalletByUsername(String username) {
        Optional<Wallet> o = walletRepository.findById(username);
        if (o.isPresent()) {
            return this.updateBalance(o.get());
        }
        return null;
    }

    public Wallet saveWallet(Wallet wallet) {
        return walletRepository.save(wallet);
    }

    private Wallet updateBalance(Wallet w) {
        List<Transaction> txListUnmined = transactionService.getUnminedTransactionList();
        Blockchain blockchain = blockchainService.getBlockchainByName("beancoin");
        double balance = balanceCalculator.calculateBalanceFromChainAndLocalTxPool(blockchain, w.getAddress(),
                txListUnmined);
        // wasteful double traversal :(
        w.setBalanceAsMined(balanceCalculator.calculateWalletBalanceByTraversingChain(blockchain, w.getAddress()));
        w.setBalance(balance);
        return this.saveWallet(w);
    }
}
