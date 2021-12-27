package com.ryan.gerald.beancoin.Service;

import java.util.ArrayList;
import java.util.List;

import com.ryan.gerald.beancoin.Dao.TransactionDao;
import com.ryan.gerald.beancoin.entity.Transaction;
import com.ryan.gerald.beancoin.entity.TransactionPoolMap;
import com.ryan.gerald.beancoin.entity.TransactionRepository;
import com.ryan.gerald.beancoin.utils.TransactionRepr;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class TransactionService {

    @Autowired TransactionRepository transactionRepository;

    @Deprecated
    TransactionDao transactionD = new TransactionDao();

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

    public Transaction getTransactionById(String id) {
        return transactionRepository.findById(id).get();

    }

    public Transaction saveTransaction(Transaction t) {
        return transactionRepository.save(t);
    }
}
