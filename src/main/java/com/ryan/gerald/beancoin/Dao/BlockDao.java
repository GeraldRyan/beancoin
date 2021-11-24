package com.ryan.gerald.beancoin.Dao;

import java.util.List;

import com.ryan.gerald.beancoin.dbConnection.DBConnection;
import com.ryan.gerald.beancoin.entity.Block;


public class BlockDao extends DBConnection implements BlockDaoI {

	@Override
	public void addBlock(Block block) {
		// Where will the mining happen? It should happen elsewhere. Trust.
		this.connect();
		em.getTransaction().begin();
		em.persist(block);
		em.getTransaction().commit();
		this.disconnect();
	}

	@Override
	public Block getBlock(long id) {
		this.connect();
		Block b = em.find(Block.class, id);
		this.disconnect();
		return b;
	}

	@Override
	public List<Block> getAllBlocks() {
		this.connect();
		List<Block> list_of_blocks = em.createQuery("select b from Block b").getResultList();
		this.disconnect();
		return list_of_blocks;
	}

	@Override
	public Block findBlockByHash(long hashcode) {
		// TODO Auto-generated method stub
		return null;
	}

}
