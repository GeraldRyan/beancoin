package com.ryan.gerald.beancoin.Service;

import com.ryan.gerald.beancoin.entity.Block;
import com.ryan.gerald.beancoin.entity.Blockchain;
import com.ryan.gerald.beancoin.entity.BlockchainRepository;
import com.ryan.gerald.beancoin.initializors.Initializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

@Service
public class BlockchainService {
    @Autowired BlockchainRepository blockchainRepository;
    @Autowired Initializer initializer;

    public Blockchain getBlockchainByName(String name) {
        return blockchainRepository.getBlockchainByName(name);
    }

    public Blockchain loadOrCreateBlockchain(String name) throws NoSuchAlgorithmException {
        Blockchain bc = this.getBlockchainByName(name);
        if (bc == null) {
            bc = this.CreateNewBlockchain(name);  // don't hardcode name
            blockchainRepository.save(bc);
        }
        return bc;
    }

    public Blockchain CreateNewBlockchain (String name) throws NoSuchAlgorithmException {
        Blockchain bc = new Blockchain(name);
        initializer.loadBC(bc);
        blockchainRepository.save(bc);
        return bc;
    }

    public void refreshChain(Blockchain bc){
        try {
            ArrayList<Block> new_chain = new ArrayList<Block>(bc.getChain());
            Collections.sort(new_chain, Comparator.comparingLong(Block::getTimestamp));
            bc.setChain(new_chain);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Blockchain saveBlockchain(Blockchain bc){
        return blockchainRepository.save(bc);
    }


}


//	public Blockchain newBlockchainService(String name) {
//		return blockchainD.newBlockchain(name);
//	}
//
//	public Blockchain getBlockchainService(String name) {
//		return blockchainD.getBlockchainByName(name);
//	}
//
//	/**
//	 * Gets all blockchain instances as list
//	 */
//	public List<Blockchain> getAllBlockchainsService() {
//		return blockchainD.getAllBlockchains();
//	}
//
//	/**
//	 * Adds block to blockchain as a service that persists to the database. Calls
//	 * add_block method of blockchain, which calls mine_block method of Block class
//	 *
//	 * @param name
//	 * @param data
//	 * @return
//	 */
//	public Block addBlockService(String nameOfBlockchain, String data) {
//		return blockchainD.addBlock(nameOfBlockchain, data);
//	}
//
//	/**
//	 * Gets first blockchain result in database query
//	 *
//	 * @return
//	 */
//	public Blockchain getTopBlockchain() {
//		return blockchainD.getTopBlockchain();
//	}
//
//	/**
//	 * Replaces blockchain chain by calling DAO and updates local database entries.
//	 * TODO - Find better way to replace blockchain OneToMany table in database, as
//	 * opposed to current method of setting it to null (truncating) and then setting
//	 * the incoming chain in order to avoid collisions
//	 *
//	 * @param name
//	 * @param new_chain
//	 * @return
//	 * @throws NoSuchAlgorithmException
//	 * @throws ChainTooShortException
//	 * @throws GenesisBlockInvalidException
//	 * @throws BlocksInChainInvalidException
//	 */
//	public boolean replaceChainService(String name, ArrayList<Block> new_chain) throws NoSuchAlgorithmException,
//			ChainTooShortException, GenesisBlockInvalidException, BlocksInChainInvalidException {
//		return blockchainD.replaceChain(name, new_chain);
//	}
//
//}
