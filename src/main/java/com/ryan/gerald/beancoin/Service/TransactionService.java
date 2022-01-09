package com.ryan.gerald.beancoin.Service;

import com.ryan.gerald.beancoin.entity.*;
import com.ryan.gerald.beancoin.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

// TODO Separate out into two classes or subclasses - UnMinedTransactionService (For transactions to mine into block) and MinedTransactionServices (to scan blockchain/block to verify and query blockchain for transaction deets
@Service
public class TransactionService {

    @Autowired private TransactionRepository transactionRepository;

//    public TransactionPoolMap getUnminedTransactionsAsMap() {
//        List<Transaction> txList = new ArrayList<>(transactionRepository.getListOfTransactions());
//        txList.forEach(t->t.reinflateInputOutputMaps());
//        return TransactionPoolMap.fillTransactionPool(txList);
//    }

    public List<Transaction> getUnminedTransactionList() {
        List<Transaction> lt = transactionRepository.getUnminedTransactionList();
        lt.forEach(t -> t.reinflateInputOutputMaps()); // needed for template!!
        return lt;
    }

    // TODO make use of in future
    public List<Transaction> get_n_oldest_unmined_transactions(Integer n) {
        // note need to get Tx from databse and then rebuild (unpack) input output attributes
        List<Transaction> lt = transactionRepository.getNOldestTransactions();
        lt.forEach(t -> t.reinflateInputOutputMaps());
        return lt;
    }

    public Transaction getTransactionById(String id) {
        return transactionRepository.findById(id).get();

    }

    public void deleteTransactionById(String id) {
        System.out.println("TRANSACTIONREPOSITORY: " + transactionRepository);
        transactionRepository.deleteById(id);
    }


    public Transaction saveTransaction(Transaction t) {
        System.out.println("ADDING TRANASACTION");
        return transactionRepository.save(t);
    }

    /***
     * This takes a list as an input arg vs not requerying because the caller knows what transactions have
     * just been processed- maybe only a subset of all
     *
     */
    public void deletTransactionsInList(List<Transaction> txList) {
        txList.forEach(t-> transactionRepository.deleteById(t.getUuid()));
    }

    // Naive solution- will use Merkle Technology later.
    public long FindTransactionInChain(Blockchain blockchain, String txId) {
        // traverse all blocks in our chain version, skipping genesis blocks (throws Gson error but TODO handle better)
        for (Block b : blockchain.getChain()) {
            List<Transaction> txList = b.deserializeTx();
            if (transactionFoundInBlock(b, txId)) {return b.getTimestamp();}
        }
        return 0;
    }

    public boolean transactionFoundInBlock(Block b, String txId) {
        List<Transaction> txList = b.deserializeTx();
        for (Transaction t : txList) {if (t.getUuid().equals(txId)) {return true;}}
        return false;
    }
}
