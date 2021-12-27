/**
 *
 */
package com.ryan.gerald.beancoin.entity;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.HashMap;
import java.util.List;

import com.ryan.gerald.beancoin.exceptions.TransactionAmountExceedsBalance;

import com.ryan.gerald.beancoin.utilities.TransactionRepr;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Gerald Ryan Natural constructor is zero arg
 *
 */

@Service
public class TransactionPoolMap {

    @Autowired private TransactionRepository transactionRepository;

    HashMap<String, Object> transactionMap = new HashMap<String, Object>();

    public TransactionPoolMap() {}

    public Transaction putTransaction(Transaction transaction) {
        transactionMap.put(transaction.getUuid(), transaction);
        return transaction;
    }

    public void syncTransactionPool() {}

    public void broadcastTransactionPool() {}

    public void TransactionPool() {}

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
        if (this.transactionMap.size() == 0) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        this.transactionMap.forEach((uuid, t) -> {
            sb.append(((Transaction) t).__repr__());
            sb.append(",");
        });
        sb.deleteCharAt(sb.lastIndexOf(","));
        sb.append("]");
        System.out.println(sb.toString());
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

    /**
     * After successful day in the mines, delete what you have in your pool
     *
     * @param blockchain
     */
    public void clearProcessedTransactions(Blockchain blockchain) {
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
                        transactionRepository.deleteById(t.getId());
                    } catch (Exception e) {e.printStackTrace();}
                }
            }
        }
    }

    public static void main(String[] args) throws InvalidKeyException, NoSuchAlgorithmException,
            NoSuchProviderException, IOException, InvalidAlgorithmParameterException, TransactionAmountExceedsBalance {
        TransactionPoolMap pool = new TransactionPoolMap();
        Wallet w = Wallet.createWallet("sender");
        Wallet unusedWallet = Wallet.createWallet("u");
        pool.putTransaction(new Transaction(w, "foo", 15));
        pool.putTransaction(new Transaction(Wallet.createWallet("1"), "foo", 15));
        pool.putTransaction(new Transaction(Wallet.createWallet("2"), "foo", 15));
        pool.putTransaction(new Transaction(Wallet.createWallet("3"), "foo", 15));
        pool.putTransaction(new Transaction(Wallet.createWallet("4"), "foo", 15));
        Transaction t = pool.findExistingTransactionByWallet(w.getAddress());
        System.out.println(t); // expect a string representation of object (or address in memory). Indeed
        Transaction tnull = pool.findExistingTransactionByWallet(unusedWallet.getAddress());
        System.out.println(tnull); // expect null. Indeed

    }
}
