package com.ryan.gerald.beancoin.Dao;

import java.util.List;

import com.ryan.gerald.beancoin.entity.Block;

public interface BlockDaoI {

	public void addBlock(Block block);

	public Block getBlock(long id);

	public Block findBlockByHash(long hashcode);

//	public boolean updateBlock(Block block); // Blocks are immutable. They are not altered. Chains may be altered but not blocks. 

//	public void removeBlock(int id); // Valid Blocks are not removed. They may be removed from a chain but not from existence.

	public List<Block> getAllBlocks(); // could be impractical if have 100k blocks but for now good

}
