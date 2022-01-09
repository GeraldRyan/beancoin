package com.ryan.gerald.beancoin.evaluation;

import com.google.gson.Gson;
import com.ryan.gerald.beancoin.entity.Block;
import com.ryan.gerald.beancoin.entity.Blockchain;
import com.ryan.gerald.beancoin.entity.Transaction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Solves the impedence problem of relational to OOP mapping, however inefficiently
 */
public class SerializableChain {
    List<SeralizableBlock> chain;

    public SerializableChain() {}

    public SerializableChain(Blockchain bc) {
        this.chain = new ArrayList<SeralizableBlock>();
        bc.getChain().forEach(b -> chain.add(new SeralizableBlock(b)));
    }

    public List<SeralizableBlock> getChain() {
        return chain;
    }

    public String serialize() {
        return new Gson().toJson(this);
    }

    public class SeralizableBlock {
        private String hash;
        private String lastHash;
        Long timestamp;
        Integer height;
        List<SerializableTransaction> tx;
        int difficulty;
        int nonce;


        public SeralizableBlock(Block b) {
            this.hash = b.getHash();
            this.lastHash = b.getLastHash();
            this.timestamp = b.getTimestamp();
            this.height = b.getHeight();
            this.difficulty = b.getDifficulty();
            this.nonce = b.getNonce();
            List<Transaction> listTx = b.deserializeTx();
            this.tx = new ArrayList<>();
            listTx.forEach(t -> this.tx.add(new SerializableTransaction(t)));
        }

        public String serialize() {
            return new Gson().toJson(this);
        }

        public List<SerializableTransaction> getTx() {return this.tx;}

        public String getHash() {return this.hash;}

        public class SerializableTransaction {
            HashMap<String, Object> input;
            HashMap<String, Object> output;
            String id;

            public SerializableTransaction(Transaction t) {
                this.input = t.getInputMap();
                this.output = t.getOutputMap();
                this.id = t.getUuid();
            }

            public String serialize() {
                return new Gson().toJson(this);
            }

            public String getId() {
                return this.id;
            }
        }

    }


}
