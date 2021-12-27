package com.ryan.gerald.beancoin.Service;

import com.ryan.gerald.beancoin.Dao.WalletDao;
import com.ryan.gerald.beancoin.entity.Wallet;
import com.ryan.gerald.beancoin.entity.WalletRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class WalletService {

	@Autowired
	WalletRepository walletRepository;

	public Wallet getWalletByUsername(String username){
		return walletRepository.findById(username).get();
	}


	public Wallet saveWallet(Wallet wallet){
		return walletRepository.save(wallet);
	}

	@Deprecated
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
