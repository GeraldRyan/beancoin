package com.ryan.gerald.beancoin.dao;

import java.util.List;

import com.ryan.gerald.beancoin.entity.Transaction;
import com.ryan.gerald.beancoin.entity.TransactionPoolMap;

@Deprecated
public class TransactionDao extends DBConnection {


	public Transaction getTransaction(String uuid) {
		// TODO Auto-generated method stub
		// might not be needed
		return null;
	}


	public Transaction addTransaction(Transaction t) {
		this.connect();
		em.getTransaction().begin();
		em.persist(t);
		em.getTransaction().commit();
		this.disconnect();
		return t;
	}


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


	public TransactionPoolMap getAllTransactionsAsTransactionPool() {
		this.connect();
		TransactionPoolMap pool = new TransactionPoolMap();
		List<Transaction> resultsList = em.createQuery("select t from Transaction t").getResultList();
		for (Transaction t : resultsList) {
			pool.putTransaction(t);
		}
		this.disconnect();
		return pool;
	}


	public List<Transaction> getAllTransactionsAsTransactionList() {
		this.connect();
		List<Transaction> resultsList = em.createQuery("select t from Transaction t").getResultList();
		for (Transaction t : resultsList) {
			t.reinflateInputOutputMaps();
		}
		this.disconnect();
		return resultsList;
	}
}
