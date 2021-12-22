package com.ryan.gerald.beancoin.Dao;

import com.ryan.gerald.beancoin.Service.BlockchainService;
import com.ryan.gerald.beancoin.dbConnection.DBConnection;
import com.ryan.gerald.beancoin.entity.Wallet;

public class WalletDao extends DBConnection implements WalletDaoI {

	@Override
	public Wallet addWallet(Wallet w) {
		this.connect();
		em.getTransaction().begin();
		em.persist(w);
		em.getTransaction().commit();
		this.disconnect();
		return w;
	}

	@Override
	public Wallet getWallet(String walletId) {
		this.connect();
		Wallet w = em.find(Wallet.class, walletId);
		this.disconnect();
		return w;
	}

	@Override
	public Wallet removeWallet(String walletId) {

		return null;
	}

	@Override
	public Wallet updateWallet(Wallet wallet) {
		this.connect();
		em.getTransaction().begin();
		double newBalance = Wallet.calculateWalletBalance(new BlockchainService().getBlockchainService("beancoin"),
				wallet.getAddress());
		wallet.setBalance(newBalance);
		em.getTransaction().commit();
		this.disconnect();
		return wallet;
	}

}
