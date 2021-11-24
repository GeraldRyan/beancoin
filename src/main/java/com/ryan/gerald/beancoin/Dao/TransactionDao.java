package com.ryan.gerald.beancoin.Dao;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SignatureException;
import java.util.List;

import com.ryan.gerald.beancoin.dbConnection.DBConnection;
import com.ryan.gerald.beancoin.entity.Transaction;
import com.ryan.gerald.beancoin.entity.TransactionPool;
import com.ryan.gerald.beancoin.entity.Wallet;
import com.ryan.gerald.beancoin.exceptions.TransactionAmountExceedsBalance;

public class TransactionDao extends DBConnection implements TransactionDaoI {

	@Override
	public Transaction getTransaction(String uuid) {
		// TODO Auto-generated method stub
		// might not be needed
		return null;
	}

	@Override
	public Transaction addTransaction(Transaction t) {
		this.connect();
		em.getTransaction().begin();
		em.persist(t);
		em.getTransaction().commit();
		this.disconnect();
		return t;
	}

	@Override
	public Transaction updateTransaction(Transaction nu, Transaction alt) {
		this.connect();
		em.getTransaction().begin();
		Transaction merged = em.find(Transaction.class, alt.getUuid());
		merged.rebuildOutputInput();
		try {
			merged.update(nu.getSenderWallet(), nu.getRecipientAddress(), nu.getAmount());
		} catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchProviderException | SignatureException
				| TransactionAmountExceedsBalance | IOException e) {
			e.printStackTrace();
		} finally {

		}
		em.getTransaction().commit();
		this.disconnect();
		return merged;
	}

	@Override
	public Transaction removeTransaction(String UUID) {
		this.connect();
		em.getTransaction().begin();
		try {
			Transaction t = em.find(Transaction.class, UUID);
			System.out.println("Removing Transaction " + UUID);
			em.remove(t);
			em.getTransaction().commit();
			this.disconnect();
			return t;

		} catch (Exception e) {

		}
		this.disconnect();
		return null;
	}

	@Override
	public TransactionPool getAllTransactionsAsTransactionPool() {
		this.connect();
		TransactionPool pool = new TransactionPool();
		List<Transaction> resultsList = em.createQuery("select t from Transaction t").getResultList();
		for (Transaction t : resultsList) {
			pool.putTransaction(t);
		}
		this.disconnect();
		return pool;
	}

	@Override
	public List<Transaction> getAllTransactionsAsTransactionList() {
		this.connect();
		List<Transaction> resultsList = em.createQuery("select t from Transaction t").getResultList();
		for (Transaction t : resultsList) {
			t.rebuildOutputInput();
		}
		this.disconnect();
		return resultsList;
	}
}