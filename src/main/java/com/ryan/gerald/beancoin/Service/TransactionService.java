package com.ryan.gerald.beancoin.Service;

import java.util.List;

import com.ryan.gerald.beancoin.Dao.TransactionDao;
import com.ryan.gerald.beancoin.entity.Transaction;
import com.ryan.gerald.beancoin.entity.TransactionPool;

public class TransactionService {
	TransactionDao transactionD = new TransactionDao();

	/**
	 * Gets a transaction from the local database by transaction ID
	 * 
	 * @param uuid
	 * @return
	 */
	public Transaction getTransactionService(String uuid) {
		return transactionD.getTransaction(uuid);
	}

	/**
	 * Adds a transaction to the database
	 * 
	 * @param t
	 * @return
	 */
	public Transaction addTransactionService(Transaction t) {
		return transactionD.addTransaction(t);
	}

	/**
	 * Updates Transaction of given wallet so that you can append recipients and
	 * increase amounts to existing recipients
	 * 
	 * @param newT
	 * @param original
	 * @return
	 */
	public Transaction updateTransactionService(Transaction nu, Transaction alt) {
		return transactionD.updateTransaction(nu, alt);
	}

	public Transaction removeTransactionService(String UUID) {
		return transactionD.removeTransaction(UUID);
	}

	/**
	 * Gets entire list of transactions as TransactionPool type from database
	 * 
	 * @return
	 */
	public TransactionPool getAllTransactionsAsTransactionPoolService() {
		return transactionD.getAllTransactionsAsTransactionPool();
	}

	/**
	 * Gets all transactions in list format for convenience.
	 * @return
	 */
	public List<Transaction> getAllTransactionsAsTransactionList() {
		return transactionD.getAllTransactionsAsTransactionList();
	}
}
