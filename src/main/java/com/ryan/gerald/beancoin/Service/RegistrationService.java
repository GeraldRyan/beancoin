package com.ryan.gerald.beancoin.Service;

import com.google.gson.Gson;
import com.ryan.gerald.beancoin.entity.Block;
import com.ryan.gerald.beancoin.entity.Blockchain;
import com.ryan.gerald.beancoin.entity.Transaction;
import com.ryan.gerald.beancoin.entity.Wallet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.*;
import java.util.ArrayList;
import java.util.List;

@Service
public class RegistrationService {
    @Autowired WalletService walletService;
    @Autowired TransactionService transactionService;

    public void loadWallet(String address) throws InvalidAlgorithmParameterException, NoSuchAlgorithmException, NoSuchProviderException {
        Wallet adminWallet = walletService.getWalletByUsername("admin");
        if (adminWallet == null) {
            System.err.println("ADMIN WALLET IS NULL");
            adminWallet = walletService.adminWallet("admin", 1000000);
        }
        Transaction t;
        try {
            t = adminWallet.createTransaction(address, 1000);
            transactionService.saveTransaction(t);
            walletService.getWalletByUsername("admin");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
