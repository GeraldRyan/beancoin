package com.ryan.gerald.beancoin.Dao;

import com.ryan.gerald.beancoin.Service.BlockchainService;
import com.ryan.gerald.beancoin.dbConnection.DBConnection;
import com.ryan.gerald.beancoin.entity.Wallet;
import com.ryan.gerald.beancoin.evaluation.BalanceCalculator;

@Deprecated
public class WalletDao extends DBConnection {
private BalanceCalculator balanceCalculator;
	
	public Wallet addWallet(Wallet w) {
		this.connect();
		em.getTransaction().begin();
		em.persist(w);
		em.getTransaction().commit();
		this.disconnect();
		return w;
	}

	
	public Wallet getWallet(String walletId) {
		this.connect();
		Wallet w = em.find(Wallet.class, walletId);
		this.disconnect();
		return w;
	}

	
	public Wallet removeWallet(String walletId) {

		return null;
	}

	
	public Wallet updateWallet(Wallet wallet) {
		this.connect();
		em.getTransaction().begin();
		double newBalance =
				balanceCalculator.calculateWalletBalanceByTraversingChain(new BlockchainService().getBlockchainByName("beancoin"),
				wallet.getAddress());
		wallet.setBalance(newBalance);
		em.getTransaction().commit();
		this.disconnect();
		return wallet;
	}

}
