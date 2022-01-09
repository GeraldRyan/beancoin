package com.ryan.gerald.beancoin.Service;

import com.ryan.gerald.beancoin.entity.Transaction;
import com.ryan.gerald.beancoin.entity.Wallet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.*;

@Service
public class RegistrationService {
    @Autowired WalletService walletService;
    @Autowired TransactionService transactionService;

    public void loadWallet(String address) throws InvalidAlgorithmParameterException, NoSuchAlgorithmException, NoSuchProviderException {
        Wallet adminWallet = walletService.getWalletByUsername("admin");
        if (adminWallet == null) {
            System.err.println("ADMIN WALLET IS NULL");
            adminWallet = walletService.createAdminWallet("admin", 1000000);
        }
        walletService.checkBalance(adminWallet);
        Transaction t;
        try {
            t = adminWallet.createTransaction(address, 1000);
            transactionService.saveTransaction(t);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
