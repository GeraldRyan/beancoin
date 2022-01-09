package com.ryan.gerald.beancoin.initializors;

import java.security.NoSuchAlgorithmException;

import com.ryan.gerald.beancoin.entity.*;
import org.springframework.stereotype.Service;

@Service
public class Initializer {

    public void loadBC(Blockchain blockchain) throws NoSuchAlgorithmException {
//        blockchain.add_block("Dance The Waltz");
//        blockchain.add_block("Dance The Tango");
//        blockchain.add_block("Dance The Quickstep");
//        blockchain.add_block("Dance The Samba");
    }

//    /**
//     * Initialize random blockchain instances with random string names. Why would
//     * one use this?
//     *
//     * @return
//     * @throws NoSuchAlgorithmException
//     */
//    public static Blockchain initRandomBlockchain() throws NoSuchAlgorithmException {
//        Blockchain blockchain = Blockchain.createBlockchainInstance(StringUtils.RandomStringLenN(5));
//        for (int i = 0; i < 2; i++) {
//            blockchain.add_block(String.valueOf(i));
//        }
//        return blockchain;
//    }
//
//    /**
//     * Submits N number transactions to various addresses (from fixed wallet adds if
//     * provided) to random wallet addresses
//     *
//     * @throws InvalidAlgorithmParameterException
//     * @throws NoSuchProviderException
//     * @throws NoSuchAlgorithmException
//     * @throws IOException
//     * @throws InvalidKeyException
//     */
//    public static List<Transaction> postNTransactions(int n) throws NoSuchAlgorithmException, NoSuchProviderException,
//            InvalidAlgorithmParameterException, InvalidKeyException, IOException {
//        List<Transaction> l = new ArrayList();
//        for (int i = 0; i < n; i++) {
//            String senderAddress = StringUtils.getUUID8();
//            Wallet w = new Wallet().createWallet(senderAddress);
//            String recipientAddress = StringUtils.getUUID8();
//            double amt = new Random().nextInt(900);
//            Transaction t = null;
//            try {
//                t = new Transaction(w, recipientAddress, amt);
//            } catch (TransactionAmountExceedsBalance e) {
//                e.printStackTrace();
//            }
//            new TransactionService().addTransactionService(t);
//            l.add(t);
//        }
//        return l;
//    }

//    public static List<Transaction> postNTransactions(int n, String senderAddress) throws NoSuchAlgorithmException,
//            NoSuchProviderException, InvalidAlgorithmParameterException, InvalidKeyException, IOException {
//        List<Transaction> l = new ArrayList();
//        Wallet w = new Wallet().createWallet(senderAddress);
//        for (int i = 0; i < n; i++) {
//            String recipientAddress = StringUtils.getUUID8();
//            double amt = new Random().nextInt(900);
//            Transaction t = null;
//            try {
//                t = new Transaction(w, recipientAddress, amt);
//            } catch (TransactionAmountExceedsBalance e) {
//                e.printStackTrace();
//            }
//            // Check if should post or update existing (will be lot of updating existing in
//            // this method. maybe a better method, but this is only dev anyway).
//            TransactionPool pool = new TransactionService().getAllTransactionsAsTransactionPoolService();
//            Transaction existing = pool.findExistingTransactionByWallet(t.getSenderAddress());
//            if (existing == null) {
//                new TransactionService().addTransactionService(t);
//                l.add(t);
//            } else {
//                Transaction updated = new TransactionService().updateTransactionService(t, existing);
//                l.add(updated);
//            }
//        }
//        return l;
//    }

//    public static void main(String[] args) throws InvalidKeyException, NoSuchAlgorithmException,
//            NoSuchProviderException, InvalidAlgorithmParameterException, IOException {
////		postNTransactions(100);
//        postNTransactions(40, "OU812");
//        postNTransactions(4);
//    }
}
