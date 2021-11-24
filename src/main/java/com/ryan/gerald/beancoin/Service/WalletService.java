package com.ryan.gerald.beancoin.Service;

import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

import com.ryan.gerald.beancoin.Dao.UserDao;
import com.ryan.gerald.beancoin.Dao.WalletDao;
import com.ryan.gerald.beancoin.entity.User;
import com.ryan.gerald.beancoin.entity.Wallet;

public class WalletService {
	WalletDao dao = new WalletDao();

	public Wallet addWalletService(Wallet w) {
		return dao.addWallet(w);
	}

	public Wallet getWalletService(String walletId) {
		return dao.getWallet(walletId);
	}

	/**
	 * 
	 * Updates balance of wallet by blockchain traversal
	 * 
	 * @param wallet
	 * @return
	 */
	public Wallet updateWalletBalanceService(Wallet wallet) {
		return dao.updateWallet(wallet);
	}

}
