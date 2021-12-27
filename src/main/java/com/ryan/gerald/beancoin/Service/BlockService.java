//package com.ryan.gerald.beancoin.Service;
//
//import java.util.List;
//
//import com.ryan.gerald.beancoin.Dao.BlockDao;
//import com.ryan.gerald.beancoin.entity.Block;
//
//
//
//public class BlockService {
//	private BlockDao blockD = new BlockDao();
//
//	/**
//	 * Adds block to database. Note that does not add to chain. Must be called with block returned aformentioned method
//	 * @param block
//	 */
//	public void addBlockService(Block block) {
//		blockD.addBlock(block);
//	}
//
//	/**
//	 * Gets given block from blockchain
//	 * @param id
//	 * @return
//	 */
//	public Block getBlockService(long id) {
//		return blockD.getBlock(id);
//	}
//
//	/**
//	 * Get all blocks listed in the block table
//	 */
//	public List<Block> getAllBlocksService() {
//		return blockD.getAllBlocks();
//	}
//}
