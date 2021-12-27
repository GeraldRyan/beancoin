package com.ryan.gerald.beancoin.Dao;

import java.util.List;

import com.ryan.gerald.beancoin.entity.Transaction;
import com.ryan.gerald.beancoin.entity.TransactionPoolMap;

public interface TransactionDaoI {

	public Transaction getTransaction(String uuid);

	public Transaction addTransaction(Transaction t);

	public Transaction updateTransaction(Transaction t1, Transaction t2);

	public Transaction removeTransaction(String UUID);

	public TransactionPoolMap getAllTransactionsAsTransactionPool();

	public List<Transaction> getAllTransactionsAsTransactionList();

}
