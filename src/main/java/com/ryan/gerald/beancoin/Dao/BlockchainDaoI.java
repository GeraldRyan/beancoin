package com.ryan.gerald.beancoin.Dao;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import com.ryan.gerald.beancoin.entity.Block;
import com.ryan.gerald.beancoin.entity.Blockchain;
import com.ryan.gerald.beancoin.exceptions.BlocksInChainInvalidException;
import com.ryan.gerald.beancoin.exceptions.ChainTooShortException;
import com.ryan.gerald.beancoin.exceptions.GenesisBlockInvalidException;

public interface BlockchainDaoI {
	public Blockchain newBlockchain(String name);

	public Block addBlock(String name, String data);

	public boolean replaceChain(String blockchain, ArrayList<Block> new_chain) throws NoSuchAlgorithmException,
			ChainTooShortException, GenesisBlockInvalidException, BlocksInChainInvalidException;

	public List<Blockchain> getAllBlockchains();

	public Blockchain getBlockchainById(int id);

	public Blockchain getTopBlockchain();

	public Block getBlockById(int id);

	public Block getBlockByHash(String hash); // is it a long? TODO Implement // could overload method if types differ

	public Blockchain getBlockchainByName(String name);

}
