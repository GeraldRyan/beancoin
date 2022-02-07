package com.ryan.gerald.beancoin.Service;

import com.google.gson.Gson;
import com.ryan.gerald.beancoin.entity.Block;
import com.ryan.gerald.beancoin.entity.Blockchain;
import com.ryan.gerald.beancoin.entity.Transaction;
import com.ryan.gerald.beancoin.entity.TransactionPoolMap;
import com.ryan.gerald.beancoin.evaluation.SerializableChain;
import com.ryan.gerald.beancoin.repository.BlockchainRepository;
import io.swagger.annotations.ApiImplicitParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Service
public class BlockchainService {
    @Autowired BlockchainRepository blockchainRepository;
    @Autowired TransactionService transactionService;
    @Autowired Gson gson;

    public Blockchain saveBlockchain(Blockchain bc) {
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

    public Blockchain CreateNewBlockchain(String name) throws NoSuchAlgorithmException {
        Blockchain bc = Blockchain.createBlockchain(name);
        blockchainRepository.save(bc);
        return bc;
    }

    // probalby need to refactor, don't use transaction pool.. is that useful?
    public SerializableChain.SeralizableBlock mineBlock() throws NoSuchAlgorithmException {
        Blockchain blockchain = this.getBlockchainByName("beancoin"); // later will cache
        List<Transaction> txList = transactionService.getUnminedTransactionList();
        if (txList.isEmpty()) {
            return null;
        } // can throw specific error
        TransactionPoolMap pool = new TransactionPoolMap(txList);
        String txListJson = new Gson().toJson(txList);
        Block new_block = blockchain.add_block(txListJson);
        this.saveBlockchain(blockchain);
        transactionService.deletTransactionsInList(txList);
        System.out.println("NEW BLOCK MINED: " + new_block.toStringConsole());
        //        if (Config.BROADCASTING) { // TODO CHANGE TO KAFKA
////            new PubNubApp().broadcastBlock(new_block);
//        }
        return new SerializableChain(blockchain).new SeralizableBlock(new_block);
    }

    // TODO surely this can't be required?
    public void sortChain(Blockchain bc) {
        ArrayList<Block> new_chain = new ArrayList<Block>(bc.getChain());
        Collections.sort(new_chain, Comparator.comparingLong(Block::getTimestamp));
        bc.setChain(new_chain);
    }

}
