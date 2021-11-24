package com.ryan.gerald.beancoin.Dao;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.Query;

import com.ryan.gerald.beancoin.dbConnection.DBConnection;
import com.ryan.gerald.beancoin.entity.Block;
import com.ryan.gerald.beancoin.entity.Blockchain;
import com.ryan.gerald.beancoin.exceptions.BlocksInChainInvalidException;
import com.ryan.gerald.beancoin.exceptions.ChainTooShortException;
import com.ryan.gerald.beancoin.exceptions.GenesisBlockInvalidException;

public class BlockchainDao extends DBConnection implements BlockchainDaoI {

	@Override
	public Blockchain newBlockchain(String name) {
		this.connect();
		try {
			Blockchain new_blockchain = new Blockchain(name);
			em.getTransaction().begin();
			em.persist(new_blockchain);
			em.getTransaction().commit();
			System.out.println("New Blockchain added");
			this.disconnect();
			return new_blockchain;
		} catch (Exception e) {
			e.printStackTrace();
			this.disconnect();
			return null;
		}
	}

	@Override
	public Blockchain getBlockchainById(int id) {
		try {
			this.connect();
			Blockchain b = em.find(Blockchain.class, id);
			this.disconnect();
			return b;
		} catch (Exception e) {
			e.printStackTrace();
			this.disconnect();
			return null;
		}
	}

	@Override
	public Blockchain getBlockchainByName(String name) throws NoResultException {
		this.connect();
		Query query = em.createQuery("select b from Blockchain b where b.instance_name = :name");
		query.setParameter("name", name);
		Blockchain blockchain;
		try {
			blockchain = (Blockchain) query.getSingleResult();
		} catch (NoResultException e) {
			System.err.println("No result found for blockchain. Returning NULL from query for safety");
			return null;
		} finally {
			this.disconnect();
		}
		return blockchain;
	}

	@Override
	public Block addBlock(String name, String data) {
		this.connect();
		Query query = em.createQuery("select b from Blockchain b where b.instance_name = :name");
		query.setParameter("name", name);
		Blockchain blockchain = (Blockchain) query.getSingleResult();
		try {
			em.getTransaction().begin();
			Block new_block = blockchain.add_block(data);
			em.persist(new_block);
			em.getTransaction().commit();
			return new_block;
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return null;
		} finally {
			this.disconnect();
		}
	}

	@Override
	public Blockchain getTopBlockchain() {
		this.connect();
		Query query = em.createQuery("select b from Blockchain b");
		Blockchain blockchain = (Blockchain) query.setMaxResults(1).getSingleResult();
		this.disconnect();
		return blockchain;
	}

	@Override
	public boolean replaceChain(String name, ArrayList<Block> new_chain) throws NoSuchAlgorithmException,
			ChainTooShortException, GenesisBlockInvalidException, BlocksInChainInvalidException {
		this.connect();
		em.getTransaction().begin();
		Query query = em.createQuery("select b from Blockchain b where b.instance_name = :name");
		query.setParameter("name", name);
		Blockchain blockchain = (Blockchain) query.getSingleResult();
//		blockchain.replace_chain(new_chain);
//		em.merge(blockchain);
		System.out.println("GOING TO REPLACE CHAIN AS SERVICE");

		// THIS LONG BLOCK IS BECAUSE I COULDN'T FIND A MORE NATURAL WAY. I KEEP GETTING
		// ERRORS.
		// I JUST WANT TO OVERWRITE THE CHAIN OR DO A SMART MERGE
		// INSTEAD IT TRIES TO APPEND. I HAVE TO WRITE AN EMPTY SET TO DB AND COMMIT IT
		// AND THEN REPOPULATE IT. ALTERNATELY I COULD MAYBE DO A NATIVE QUERY AND
		// TRUNCATE
		// REGARDLESS IT DOESN'T SEEM TO SMARTLY MERGE THE TWO CHAINS
		// -- IT SHOULD BE EASY WHEN THE NEW CHAIN IS AN EXTENSION, VS A FORK
		// -- HANDLING THE "FORK" POTENTIAL OF BLOCKCHAIN ADDS TO THE COMPLEXITY IN
		// WHICH CASE EASIEST TO TRUNCATE AND START FRESH

		// Try Flush
		if (blockchain.willReplace(new_chain)) {
			blockchain.setChain(null);
			em.getTransaction().commit();
			em.getTransaction().begin();
//			em.flush();
			Query query2 = em.createQuery("select b from Blockchain b where b.instance_name = :name");
			query.setParameter("name", name);
			Blockchain blockchain2 = (Blockchain) query.getSingleResult();
			blockchain2.setChain(new_chain);
			em.getTransaction().commit();
			this.disconnect();
			return true;
		}
		em.getTransaction().commit();
		this.disconnect();
		return true;
	}

	@Override
	public List<Blockchain> getAllBlockchains() {
		try {
			this.connect();
			List<Blockchain> list_of_chains = em.createQuery("select b from Blockchain b").getResultList();
			this.disconnect();
			return list_of_chains;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			this.disconnect();
		}
	}

	@Override
	public Block getBlockById(int id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Block getBlockByHash(String hash) {
		// TODO Auto-generated method stub
		return null;
	}

}
