package com.ryan.gerald.beancoin.Service;

import com.ryan.gerald.beancoin.entity.Blockchain;
import com.ryan.gerald.beancoin.entity.Transaction;
import com.ryan.gerald.beancoin.entity.Wallet;
import com.ryan.gerald.beancoin.evaluation.BalanceCalculator;
import com.ryan.gerald.beancoin.repository.WalletRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.*;
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

    public Wallet saveWallet(Wallet w) {
        return walletRepository.save(w);
    }

    public Wallet newWallet(String username) throws InvalidAlgorithmParameterException, NoSuchAlgorithmException, NoSuchProviderException {
        Wallet w = Wallet.createWallet(username);
        return this.saveWallet(w);
    }

    public Transaction createTransaction(Wallet w, String address, double amount) throws NoSuchAlgorithmException, SignatureException, IOException, NoSuchProviderException, InvalidKeyException {
        Transaction neu = w.createTransaction(address, amount);
        if (neu == null){
            System.out.println("Something went wrong; Please check your balance- or wait until enough is mined");
            return neu;
        }
        transactionService.saveTransaction(neu);
        return neu;
    };

    // Need to use security here. Good practice to check and apply security policy
    public Wallet createAdminWallet(String userId, double balance) throws InvalidAlgorithmParameterException, NoSuchAlgorithmException, NoSuchProviderException {
        Wallet w = Wallet.createAdminWallet(); // See if Java SecurityManager can protect this and other things
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
