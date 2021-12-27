package com.ryan.gerald.beancoin.Service;

import com.ryan.gerald.beancoin.Dao.WalletDao;
import com.ryan.gerald.beancoin.entity.Wallet;
import com.ryan.gerald.beancoin.entity.WalletRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class WalletService implements WalletServiceInterface {

    @Autowired WalletRepository walletRepository;

    @Override
    public Wallet getWalletByUsername(String username) {
        return walletRepository.findById(username).get();
    }

    @Override
    public Wallet saveWallet(Wallet wallet) {
        return walletRepository.save(wallet);
    }

    // .. whatever else considered necessary

}
