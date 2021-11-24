package com.ryan.gerald.beancoin.Dao;

import java.util.List;

import com.ryan.gerald.beancoin.entity.User;
import com.ryan.gerald.beancoin.entity.Wallet;

public interface WalletDaoI {
	public Wallet addWallet(Wallet w);

	public Wallet getWallet(String walletId);

	public Wallet updateWallet(Wallet wallet);

	public Wallet removeWallet(String walletId);
}
