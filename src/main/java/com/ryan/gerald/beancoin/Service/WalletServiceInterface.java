package com.ryan.gerald.beancoin.Service;

import com.ryan.gerald.beancoin.entity.Wallet;

public interface WalletServiceInterface {

    public Wallet saveWallet(Wallet wallet);

    public Wallet getWalletByUsername(String username);

}
