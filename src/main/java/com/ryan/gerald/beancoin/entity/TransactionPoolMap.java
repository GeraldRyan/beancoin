/**
 *
 */
package com.ryan.gerald.beancoin.entity;

import com.google.gson.Gson;
import com.ryan.gerald.beancoin.Service.TransactionService;
import com.ryan.gerald.beancoin.utils.TransactionRepr;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;

/**
 *
 *  Glorified map wrapper utility class for TransactionService
 * @author Gerald Ryan
 *
 */

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
            t.rebuildOutputInput();
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
            System.err.println(t.getInputjson());
            System.err.println("OUTPUT JSON");
            System.err.println(t.getOutputjson());
            tmpinput = new Gson().fromJson(t.getInputjson(), HashMap.class);
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
            sb.append(((Transaction) t).__repr__());
            sb.append(",");
        });
        sb.deleteCharAt(sb.lastIndexOf(","));
        sb.append("]");
        return sb.toString().replace("\\\\", "");
    }

    /**
     * Main data payload of this class- the transaction map contains a HashMap of
     * all the transactions in this pool
     *
     * @return
     */
    public HashMap<String, Object> getTransactionMap() {
        return transactionMap;
    }

//	public void consoleLogAll() {
//		System.err.println("Transactions in Transaction Pool");
//		for (String id : this.getTransactionMap().keySet()) {
//			System.out.println("key: " + id + " value: " + this.getTransactionMap().get(id));
//		}
//	}


    /***
     * Telescoping wrapper class - for some reason TransactionService above was not autowiring
     * @param blockchain
     */
//    public void clearProcessedTransactions(Blockchain blockchain){
////        TransactionService ts = new TransactionService();
//        clearProcessedTransactions(blockchain, this.transactionService);
//    }


    /**
     * After successful day in the mines, delete what you have in your pool
     *
     * @param blockchain
     */
    public void clearProcessedTransactions(Blockchain blockchain, TransactionService transactionService) {
        List<TransactionRepr> trList;
        int i = 0;
        for (Block b : blockchain.getChain()) {
            i++;
            if (i < 7) {
                continue;
            }
            // skip first six blocks as they have dummy data. will cause gson type crash.
            trList = b.deserializeTransactionData();
            for (TransactionRepr t : trList) {
                if (this.getTransactionMap().containsKey(t.getId())) {
                    try {
                        System.out.println("Removing Transaction: " + t.getId());
                        System.out.println("TRANSACTION SERVICE: " + transactionService);
//                        transactionService.deleteById(t.getId());
                    } catch (Exception e) {e.printStackTrace();}
                }
            }
        }
    }
}
