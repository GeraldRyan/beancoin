package com.ryan.gerald.beancoin.evaluation;

import com.ryan.gerald.beancoin.entity.Block;
import com.ryan.gerald.beancoin.entity.Blockchain;
import com.ryan.gerald.beancoin.utils.TransactionRepr;

import java.util.ArrayList;
import java.util.List;

public class BalanceCalculator {
    final static double STARTING_BALANCE = 0;

    /**
     * Will be best form solution according to this strategy (which may be naive and non-performative).
     * TODO implement and clean up helper methods
     */
    public static double calculateWalletBalance(Blockchain bc, String adds, List<TransactionRepr> pendingTransactions){
        return calculateWalletBalanceByTraversingChain(bc, adds) + calculateNetBalanceInUnminedPool(adds, pendingTransactions);
    }


    /**
     *
     * TODO Naive solution and incorrect actually, and repetitive.
     * Fix this.
     * Dependency Injection is good- but the entire blockchain??? LOL
     * Calculates balance of address based on blockchain history (only counts MINED BLOCKS)
     * Two ways to find balance: calculate all transactions to and fro or trusting
     * output values
     */
    public static double calculateWalletBalanceByTraversingChain(Blockchain bc, String adds) {
        double balance = STARTING_BALANCE; // starting balance. static means not touching real wallet.
        // loop through transactions - yes, every transaction of every block of the
        // entire chain (minus the dummy data chains)
        System.out.println("String address" + adds);
        if (bc == null) {
            System.err.println("BLOCKCHAIN IS NULL");
            System.err.println("String address" + adds);
            return -1; // if -1 in caller function, leave balance same. Should this have been non
            // static perhaps?
        }

        int i = 0;
        for (Block b : bc.getChain()) {
            i++;
            if (i < 7) {continue;} // dummy data blocks
            // would for (i=0; i<7; i++) {continue;} work?
            List<TransactionRepr> trListMinedBlocks = b.deserializeTransactionData();
            for (TransactionRepr t : trListMinedBlocks) {
                if (t.getInput().get("address").equals(adds)) { // wallet is sender -- deduct balance
                    // reset balance after each transaction
                    balance = (double) t.getOutput().get(adds);
                } else if (t.getOutput().containsKey(adds)) { // wallet is receiver. Add balance to.
                    balance += (double) t.getOutput().get(adds);
                }
            }
        }
        List<TransactionRepr> trListUnmined = new ArrayList();
        for (TransactionRepr t : trListUnmined) {
            if (t.getInput().get("address").equals(adds)) { // wallet is sender -- deduct balance
                // reset balance after each transaction
                balance = (double) t.getOutput().get(adds);
            } else if (t.getOutput().containsKey(adds)) { // wallet is receiver. Add balance to.
                balance += (double) t.getOutput().get(adds);
            }
        }
        return balance;
    }

    // TODO implement me!
    public static double calculateNetBalanceInUnminedPool(String adds, List<TransactionRepr> pendingTransactions){
        return 0;
    }

    // TODO wrap above function in this for cleaner code
    // TODO BE ABLE TO TRAVERSE FROM END OF BLOCK BACK UNTIL YOU HIT SOMETHING WTIH USERS ADDRESS. or something
    public static double calculateWalletBalanceByTraversingChainIncludePending(Blockchain bc, String adds, List<TransactionRepr> pendingTransactions) {
        System.out.println("CALCULATING WALLET BALANCE FOR ADDRESS: " + adds);
        double bal = STARTING_BALANCE;
        int i = 0;
        for (Block b : bc.getChain()) {
            i++;
            if (i < 7) {continue;} // dummy data blocks
            // would for (i=0; i<7; i++) {continue;} work?
            System.out.println("BLOCK DESERIALIZED " + b.toStringConsole());
            List<TransactionRepr> trListMinedBlocks = b.deserializeTransactionData();
            double currentPmt = 0;
            for (TransactionRepr t : trListMinedBlocks) {
                if (t.getInput().get("address").equals(adds)) {  // input is moving per transaction and it should not.
                    System.out.println("INPUT BALANCE : " + t.getInput().get("amount"));
                    currentPmt = (double) t.getInput().get("amount") - (double) t.getOutput().get(adds);
                    System.out.println("Input Balance to deduct " + currentPmt);
                    bal -= currentPmt;
                }
                if (t.getOutput().containsKey(adds) && !t.getInput().get("address").equals(adds)) { // wallet is receiver. Add receipts.
                    System.out.println("ADD: " + t.getOutput().get(adds) + "    to " + adds);
                    bal += (double) t.getOutput().get(adds);
                }
            }
        }

        // Process Pending Transactions
        double paying = 0;
        double receiving = 0;
        for (TransactionRepr t : pendingTransactions) {
            if (t.getInput().get("address").equals(adds)) {
                paying = (double) t.getInput().get("amount") - (double) t.getOutput().get(adds);
                System.out.println("T INPUT " + t.getInput().get("amount") + " and output " + t.getOutput().get(adds));
                System.out.println("Paying " + paying + " by " + adds);
                break;
            }
            if (t.getOutput().containsKey(adds)) { // wallet is receiver. Add bal to.
                receiving += (double) t.getOutput().get(adds);
                System.out.println("Receiving " + receiving + " by " + adds);
            }
        }
        return bal - paying + receiving;
    }

}
