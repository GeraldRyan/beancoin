package com.ryan.gerald.beancoin.initializors;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

import com.ryan.gerald.beancoin.Service.BlockchainService;
import com.ryan.gerald.beancoin.Service.TransactionService;
import com.ryan.gerald.beancoin.controller.Development;
import com.ryan.gerald.beancoin.entity.Blockchain;
import com.ryan.gerald.beancoin.entity.Transaction;
import com.ryan.gerald.beancoin.entity.TransactionPool;
import com.ryan.gerald.beancoin.entity.Wallet;
import com.ryan.gerald.beancoin.utilities.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Initializer {

	/**
	 * Used to load a new blockchain up with 5 valid blocks
	 * 
	 * @param nameOfBlockchain
	 */
	public static void loadBC(String nameOfBlockchain) {
		BlockchainService blockchainApp = new BlockchainService();
		blockchainApp.addBlockService(nameOfBlockchain, "Dance The Quickstep");
		blockchainApp.addBlockService(nameOfBlockchain, "Dance The Waltz");
		blockchainApp.addBlockService(nameOfBlockchain, "Dance The Tango");
		blockchainApp.addBlockService(nameOfBlockchain, "Dance The Samba");
		blockchainApp.addBlockService(nameOfBlockchain, "Dance With Us America");
	}

	/**
	 * Initialize random blockchain instances with random string names. Why would
	 * one use this?
	 * 
	 * @return
	 * @throws NoSuchAlgorithmException
	 */
	public static Blockchain initRandomBlockchain() throws NoSuchAlgorithmException {
		Blockchain blockchain = Blockchain.createBlockchainInstance(StringUtils.RandomStringLenN(5));
		for (int i = 0; i < 2; i++) {
			blockchain.add_block(String.valueOf(i));
		}
		return blockchain;
	}

	/**
	 * Submits N number transactions to various addresses (from fixed wallet adds if
	 * provided) to random wallet addresses
	 * 
	 * @throws InvalidAlgorithmParameterException
	 * @throws NoSuchProviderException
	 * @throws NoSuchAlgorithmException
	 * @throws IOException
	 * @throws InvalidKeyException
	 */
	public static List<Transaction> postNTransactions(int n) throws NoSuchAlgorithmException, NoSuchProviderException,
			InvalidAlgorithmParameterException, InvalidKeyException, IOException {
		List<Transaction> l = new ArrayList();
		for (int i = 0; i < n; i++) {
			String senderAddress = StringUtils.getUUID8();
			Wallet w = new Wallet().createWallet(senderAddress);
			String recipientAddress = StringUtils.getUUID8();
			double amt = new Random().nextInt(900);
			Transaction t = new Transaction(w, recipientAddress, amt);
			new TransactionService().addTransactionService(t);
			l.add(t);
		}
		return l;
	}

	@Development
	public static List<Transaction> postNTransactions(int n, String senderAddress) throws NoSuchAlgorithmException,
			NoSuchProviderException, InvalidAlgorithmParameterException, InvalidKeyException, IOException {
		List<Transaction> l = new ArrayList();
		Wallet w = new Wallet().createWallet(senderAddress);
		for (int i = 0; i < n; i++) {
			String recipientAddress = StringUtils.getUUID8();
			double amt = new Random().nextInt(900);
			Transaction t = new Transaction(w, recipientAddress, amt);
			// Check if should post or update existing (will be lot of updating existing in
			// this method. maybe a better method, but this is only dev anyway).
			TransactionPool pool = new TransactionService().getAllTransactionsAsTransactionPoolService();
			Transaction existing = pool.findExistingTransactionByWallet(t.getSenderAddress());
			if (existing == null) {
				new TransactionService().addTransactionService(t);
				l.add(t);
			} else {
				Transaction updated = new TransactionService().updateTransactionService(t, existing);
				l.add(updated);
			}
		}
		return l;

	}

	public static void main(String[] args) throws InvalidKeyException, NoSuchAlgorithmException,
			NoSuchProviderException, InvalidAlgorithmParameterException, IOException {
//		postNTransactions(100);
		postNTransactions(40, "OU812");
		postNTransactions(4);
	}
}
