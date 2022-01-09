package com.ryan.gerald.beancoin.evaluation;

import com.ryan.gerald.beancoin.entity.Block;
import com.ryan.gerald.beancoin.entity.Blockchain;
import com.ryan.gerald.beancoin.entity.Transaction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SerializableBC {
    List<SeralizableBlock> chain;

    public SerializableBC(Blockchain bc){
        this.chain = new ArrayList<SeralizableBlock>();
        bc.getChain().forEach(b-> chain.add(new SeralizableBlock(b)));
    }

    public List<SeralizableBlock> getChain() {
        return chain;
    }

    class SeralizableBlock {
        Long timestamp;
        private String hash;
        private String lastHash;
        List<SerializableTransaction> tx;
        int difficulty;
        int nonce;


        SeralizableBlock(Block b) {
            this.timestamp = b.getTimestamp();
            this.hash = b.getHash();
            this.lastHash = b.getLastHash();
            this.difficulty = b.getDifficulty();
            this.nonce = b.getNonce();
            List<Transaction> listTx = b.deserializeTx();
            this.tx = new ArrayList<>();
            listTx.forEach(t -> this.tx.add(new SerializableTransaction(t)));
        }
    }

    class SerializableTransaction {
        HashMap<String, Object> input;
        HashMap<String, Object> output;
        String id;

        SerializableTransaction(Transaction t) {
            this.input = t.getInputMap();
            this.output = t.getOutputMap();
            this.id = t.getUuid();
        }
    }


}
