package com.ryan.gerald.beancoin.evaluation;

import com.ryan.gerald.beancoin.entity.Block;
import com.ryan.gerald.beancoin.entity.Blockchain;
import com.ryan.gerald.beancoin.entity.Transaction;
import com.ryan.gerald.beancoin.entity.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

@Service
public class BalanceCalculator {
    final double STARTING_BALANCE = 0;
    @Autowired TransactionRepository transactionRepository;


    /**
     * Will be best form solution according to this strategy (which may be naive and non-performative).
     */
    public double calculateBalanceFromChainAndLocalUnminedTxPool(Blockchain bc, String adds, List<Transaction> pendingTransactions) {
        return calculateWalletBalanceByTraversingChain(bc, adds) + this.getBalanceChangeInUnminedTxList(adds, pendingTransactions);
    }

    public double calculateBalanceFromChainAndLocalUnminedTxPool(Blockchain bc, String adds) {
        return calculateWalletBalanceByTraversingChain(bc, adds) + this.getBalanceChangeInUnminedTxList(adds);
    }


    /**
     * TODO Naive solution. Later use Merkel Technology.
     * Dependency Injection is good- but passing entire blockchain???
     */
    public double calculateWalletBalanceByTraversingChain(Blockchain bc, String adds) {
        double balance = STARTING_BALANCE;
        if (bc == null) {throw new NullPointerException("Blockchain is null");}
        System.out.println("Looking up balance for " + adds);
        int i = 0;
        for (Block b : bc.getChain()) {
            i++;
            if (i < 7) {continue;} // dummy data blocks, breaks deserialization otherwise
            balance += this.getBalanceChangeInBlock(b, adds);
        }
        return balance;
    }

    public double getBalanceChangeInBlock(Block b, String adds) {
        AtomicReference<Double> change = new AtomicReference<>((double) 0);
        List<Transaction> txList = b.deserializeTx();
        for (Transaction t : txList) {
            change.updateAndGet(v -> (double) (v + this.getBalanceChangeInTx(t, adds)));
        }
        return change.get();
    }

    public double getBalanceChangeInTx(Transaction t, String adds) {
        AtomicReference<Double> change = new AtomicReference<>((double) 0);
        t.reinflateInputOutputMaps();
        if (t.getInputMap().get("address").equals(adds)) { // wallet is sender -- deduct amt sent
            t.getOutputMap().keySet().forEach(k -> {
                if (!k.equals(adds)) {change.updateAndGet(v -> (double) (v - (double) t.getOutputMap().get(k)));}
            });
        } else if (t.getOutputMap().containsKey(adds)) { // wallet is receiver. Add balance to.
            change.updateAndGet(v -> (double) (v + (double) t.getOutputMap().get(adds)));
        }
        return change.get();
    }

    public double getBalanceChangeInUnminedTxList(String adds) {
        List<Transaction> PendingLocalDBTransactions = transactionRepository.getUnminedTransactionList();
        return getBalanceChangeInUnminedTxList(adds, PendingLocalDBTransactions);
    }

    public double getBalanceChangeInUnminedTxList(String adds, List<Transaction> pendingTransactions) {
        AtomicReference<Double> change = new AtomicReference<>((double) 0);
        pendingTransactions.forEach(t -> {
            change.updateAndGet(v -> (double) (v + (double) this.getBalanceChangeInTx(t, adds)));
        });
        return change.get();
    }
}
