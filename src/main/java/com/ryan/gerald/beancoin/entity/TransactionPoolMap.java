/**
 *
 */
package com.ryan.gerald.beancoin.entity;

import com.google.gson.Gson;

import java.util.HashMap;
import java.util.List;

// TODO is this class necessary? Isn't List<Transaction> enough, that we don't need to make HashMap<String, Object> where String = t.getUuid()?
public class TransactionPoolMap {

    HashMap<String, Object> transactionMap = new HashMap<String, Object>();

    public TransactionPoolMap() {}

    public TransactionPoolMap(List<Transaction> transactionList) {
        for (Transaction t : transactionList) {
            this.putTransaction(t);
        }
    }


    public Transaction putTransaction(Transaction transaction) {
        transactionMap.put(transaction.getUuid(), transaction);
        return transaction;
    }

    public void syncTransactionPool() {}

    public void broadcastTransactionPool() {}

    public static TransactionPoolMap fillTransactionPool(List<Transaction> transactionList) {
        TransactionPoolMap tp = new TransactionPoolMap();
        for (Transaction t : transactionList) {
            t.reinflateInputOutputMaps();
            tp.putTransaction(t);
        }
        return tp;
    }

    /**
     * Finds existing transaction of given Wallet if exists in pool, otherwise
     * returns null
     *
     * @return
     */
    public Transaction findExistingTransactionByWallet(String walletAddress) {
        if (this.getTransactionMap().keySet().size() == 0) {
            System.err.println("KEYSET IS SIZE 00000000");
            return null;
        }
        HashMap<String, Object> tmpinput;
        for (String uuid : this.getTransactionMap().keySet()) {

            System.err.println("UUID OF TRANSACTION IS " + uuid);
            Transaction t = (Transaction) this.getTransactionMap().get(uuid);
            System.err.println("INPUT JSON");
            System.err.println(t.getInputJson());
            System.err.println("OUTPUT JSON");
            System.err.println(t.getOutputJson());
            tmpinput = new Gson().fromJson(t.getInputJson(), HashMap.class);
            if (tmpinput == null) {
                return null; // TODO : empty transaction with only an ID happened. I don't know how.
            }
            if (tmpinput.get("address").equals(walletAddress)) {
                return t;
            }
        }
        return null;
    }

    /**
     * Return the transactions of the transaction pool as a string that is suited to
     * add to block data field, fit for mining, represented in json serialized form
     *
     * @return
     */
    public String getMinableTransactionDataString() {
        if (this.transactionMap.size() == 0) {return null;}
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        this.transactionMap.forEach((uuid, t) -> {
            sb.append(((Transaction) t).serialize());
            sb.append(",");
        });
        sb.deleteCharAt(sb.lastIndexOf(","));
        sb.append("]");
        return sb.toString().replace("\\\\", "");
    }

    public HashMap<String, Object> getTransactionMap() {
        return transactionMap;
    }

}
