package com.ryan.gerald.beancoin.Service;

import java.util.ArrayList;
import java.util.List;

import com.ryan.gerald.beancoin.Dao.TransactionDao;
import com.ryan.gerald.beancoin.entity.Transaction;
import com.ryan.gerald.beancoin.entity.TransactionPool;
import com.ryan.gerald.beancoin.entity.TransactionRepository;
import com.ryan.gerald.beancoin.utilities.TransactionRepr;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class TransactionService {

	@Autowired
	TransactionRepository transactionRepository;

	TransactionDao transactionD = new TransactionDao();

	public List<TransactionRepr> getTransactionReprList(){
		List<Transaction> txList = transactionRepository.getListOfTransactions();
		List<TransactionRepr> trList = new ArrayList<>();
		for (Transaction t : txList) {
			t.rebuildOutputInput();
			trList.add(new TransactionRepr(t));
		}
		return trList;
	}

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
	 * @param neu
	 * @param alt
	 * @return
	 */
	public Transaction updateTransactionService(Transaction neu, Transaction alt) {
		return transactionD.updateTransaction(neu, alt);
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
