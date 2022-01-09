package com.ryan.gerald.beancoin.Service;

import com.ryan.gerald.beancoin.entity.Block;
import com.ryan.gerald.beancoin.entity.Blockchain;
import com.ryan.gerald.beancoin.entity.BlockchainRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

@Service
public class BlockchainService {
    @Autowired BlockchainRepository blockchainRepository;

    public Blockchain saveBlockchain(Blockchain bc){
        return blockchainRepository.save(bc);
    }

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
        Blockchain bc = Blockchain.createBlockchain(name);
        blockchainRepository.save(bc);
        return bc;
    }

    // TODO surely this can't be required?
    public void sortChain(Blockchain bc){
            ArrayList<Block> new_chain = new ArrayList<Block>(bc.getChain());
            Collections.sort(new_chain, Comparator.comparingLong(Block::getTimestamp));
            bc.setChain(new_chain);
    }

}
