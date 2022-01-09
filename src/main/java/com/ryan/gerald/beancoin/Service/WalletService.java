package com.ryan.gerald.beancoin.Service;

import com.ryan.gerald.beancoin.entity.Blockchain;
import com.ryan.gerald.beancoin.entity.Transaction;
import com.ryan.gerald.beancoin.entity.Wallet;
import com.ryan.gerald.beancoin.evaluation.BalanceCalculator;
import com.ryan.gerald.beancoin.repository.WalletRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
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

    public Wallet checkBalance(Wallet w) {
        return this.updateBalance(w);
    }

    public Wallet saveWallet(Wallet wallet) {
        return walletRepository.save(wallet);
    }

    public Wallet newWallet(String username) throws InvalidAlgorithmParameterException, NoSuchAlgorithmException, NoSuchProviderException {
        Wallet w = Wallet.createWallet(username);
        return this.saveWallet(w);
    }

    // Need to use security here. Good practice to check and apply security policy
    public Wallet adminWallet(String userId, double balance) throws InvalidAlgorithmParameterException, NoSuchAlgorithmException, NoSuchProviderException {
        Wallet w = Wallet.createWallet("admin", 1000000); // I know params not passed. keep for now
        w.setAddress("7777777");
        return walletRepository.save(w);
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
