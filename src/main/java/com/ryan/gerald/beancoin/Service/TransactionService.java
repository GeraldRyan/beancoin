package com.ryan.gerald.beancoin.Service;

import java.util.ArrayList;
import java.util.List;

import com.ryan.gerald.beancoin.entity.*;
import com.ryan.gerald.beancoin.utils.TransactionRepr;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class TransactionService {

    @Autowired private TransactionRepository transactionRepository;

    TransactionPoolMap pool;


    public TransactionPoolMap getUnminedTransactionsPoolMap() {
        List<Transaction> tl = new ArrayList<>(transactionRepository.getListOfTransactions());
        return TransactionPoolMap.fillTransactionPool(tl);
    }

    public List<TransactionRepr> getTransactionReprList() {
        List<TransactionRepr> trList = new ArrayList<>();
        this.getTransactionList().forEach(t -> {
            t.rebuildOutputInput();
            trList.add(new TransactionRepr(t));
        });
        return trList;
    }

    public List<Transaction> getTransactionList() {
        // note need to get Tx from databse and then rebuild (unpack) input output attributes
        List<Transaction> lt = transactionRepository.getListOfTransactions();
        lt.forEach(t -> t.rebuildOutputInput());
        return lt;
    }

    /**
     * Implement me
     */
    public List<Transaction> getNOldestTransactions(Integer n) {
        // note need to get Tx from databse and then rebuild (unpack) input output attributes
        List<Transaction> lt = transactionRepository.getNOldestTransactions();
        lt.forEach(t -> t.rebuildOutputInput());
        return lt;
    }

    public Transaction getTransactionById(String id) {
        return transactionRepository.findById(id).get();

    }

    public void deleteTransactionById(String id){
        System.out.println("TRANSACTIONREPOSITORY: " + transactionRepository);
        transactionRepository.deleteById(id);
    }


    public Transaction saveTransaction(Transaction t) {
        System.out.println("ADDING TRANASACTION");
        System.out.println("TRANSACTION REPOSITORY" + transactionRepository);
        return transactionRepository.save(t);
    }

    /***
     * This takes a list as an input arg vs not requerying because the caller knows what transactions have
     * just been processed- maybe only a subset of all
     *
     */
    public void deletTransactionsInList(List<Transaction> list){
        for (Transaction t: list){
            transactionRepository.deleteById(t.getUuid());
        }
    }

    public void clearProcessedTransactions(Blockchain blockchain, List<Transaction> listTransaction) {
        TransactionPoolMap pool = new TransactionPoolMap(listTransaction);
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
                if (pool.getTransactionMap().containsKey(t.getId())) {
                    try {
                        System.out.println("Removing Transaction: " + t.getId());
                        System.out.println("TRANSACTION SERVICE: ");
                        transactionRepository.deleteById(t.getId());
                    } catch (Exception e) {e.printStackTrace();}
                }
            }
        }
    }


}
