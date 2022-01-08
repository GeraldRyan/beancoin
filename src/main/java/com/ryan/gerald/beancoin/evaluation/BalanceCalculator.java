package com.ryan.gerald.beancoin.evaluation;

import com.ryan.gerald.beancoin.entity.Block;
import com.ryan.gerald.beancoin.entity.Blockchain;
import com.ryan.gerald.beancoin.entity.Transaction;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class BalanceCalculator {
    final static double STARTING_BALANCE = 0;

    /**
     * Will be best form solution according to this strategy (which may be naive and non-performative).
     * TODO implement and clean up helper methods
     */
    public static double calculateWalletBalanceIncludeLocalPending(Blockchain bc, String adds, List<Transaction> pendingTransactions) {
        return calculateWalletBalanceByTraversingChain(bc, adds) + calculateNetBalanceInUnminedPool(adds, pendingTransactions);
    }

    /**
     * TODO Naive solution. Later use Merkel Technology.
     * Dependency Injection is good- but pass entire blockchain???
     */
    public static double calculateWalletBalanceByTraversingChain(Blockchain bc, String adds) {
        AtomicReference<Double> balance = new AtomicReference<>(STARTING_BALANCE);
        System.out.println("Looking up balance for " + adds);
        if (bc == null) {
            System.err.println("Error: Blockchain is null");
            return -1;
        }
        int i = 0;
        for (Block b : bc.getChain()) {
            i++;
            if (i < 7) {continue;} // dummy data blocks, breaks deserialization otherwise
            List<Transaction> txListOfBlock = b.deserializeTransactionData();
            for (Transaction t : txListOfBlock) {
                t.reinflateInputOutputMaps();
                if (t.getInputMap().get("address").equals(adds)) { // wallet is sender -- deduct amt sent
                    t.getOutputMap().keySet().forEach(k-> {
                        if (!k.equals(adds)){
                            balance.updateAndGet(v -> (double) (v - (double) t.getOutputMap().get(k)));}
                    });
                } else if (t.getOutputMap().containsKey(adds)) { // wallet is receiver. Add balance to.
                    balance.updateAndGet(v -> (double) (v + (double) t.getOutputMap().get(adds)));
                }
            }
        }
        List<Transaction> txListUnmined = new ArrayList();
        for (Transaction t : txListUnmined) {
            if (t.getInputMap().get("address").equals(adds)) { // wallet is sender -- deduct balance
                // reset balance after each transaction
                balance.set((double) t.getOutputMap().get(adds));
            } else if (t.getOutputMap().containsKey(adds)) { // wallet is receiver. Add balance to.
                balance.updateAndGet(v -> (double) (v + (double) t.getOutputMap().get(adds)));
            }
        }
        return balance.get();
    }

    // TODO implement me!
    public static double calculateNetBalanceInUnminedPool(String adds, List<Transaction> pendingTransactions) {
        return 0;
    }

    // TODO wrap above function in this for cleaner code
    // TODO BE ABLE TO TRAVERSE FROM END OF BLOCK BACK UNTIL YOU HIT SOMETHING WTIH USERS ADDRESS. or something
    public static double calculateWalletBalanceByTraversingChainIncludePending(Blockchain bc, String adds, List<Transaction> pendingTransactions) {
        System.out.println("CALCULATING WALLET BALANCE FOR ADDRESS: " + adds);
        double bal = STARTING_BALANCE;
        int i = 0;
        for (Block b : bc.getChain()) {
            i++;
            if (i < 7) {continue;} // dummy data blocks
            // would for (i=0; i<7; i++) {continue;} work?
            System.out.println("BLOCK DESERIALIZED " + b.toStringConsole());
            List<Transaction> txListMinedBlock = b.deserializeTransactionData();
            double currentPmt = 0;
            for (Transaction t : txListMinedBlock) {
                t.reinflateInputOutputMaps();
                if (t.getInputMap().get("address").equals(adds)) {  // input is moving per transaction and it should not.
                    System.out.println("INPUT BALANCE : " + t.getInputMap().get("amount"));
                    currentPmt = (double) t.getInputMap().get("amount") - (double) t.getOutputMap().get(adds);
                    System.out.println("Input Balance to deduct " + currentPmt);
                    bal -= currentPmt;
                }
                if (t.getOutputMap().containsKey(adds) && !t.getInputMap().get("address").equals(adds)) { // wallet is receiver. Add receipts.
                    System.out.println("ADD: " + t.getOutputMap().get(adds) + "    to " + adds);
                    bal += (double) t.getOutputMap().get(adds);
                }
            }
        }

        // Process Pending Transactions
        double paying = 0;
        double receiving = 0;
        for (Transaction t : pendingTransactions) {
            t.reinflateInputOutputMaps();
            if (t.getInputMap().get("address").equals(adds)) {
                paying = (double) t.getInputMap().get("amount") - (double) t.getOutputMap().get(adds);
                System.out.println("T INPUT " + t.getInputMap().get("amount") + " and output " + t.getOutputMap().get(adds));
                System.out.println("Paying " + paying + " by " + adds);
                break;
            }
            if (t.getOutputMap().containsKey(adds)) { // wallet is receiver. Add bal to.
                receiving += (double) t.getOutputMap().get(adds);
                System.out.println("Receiving " + receiving + " by " + adds);
            }
        }
        return bal - paying + receiving;
    }

}
