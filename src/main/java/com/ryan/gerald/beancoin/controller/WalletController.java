package com.ryan.gerald.beancoin.controller;

import com.ryan.gerald.beancoin.Service.BlockchainService;
import com.ryan.gerald.beancoin.Service.TransactionService;
import com.ryan.gerald.beancoin.Service.WalletService;
import com.ryan.gerald.beancoin.entity.*;
import com.ryan.gerald.beancoin.exceptions.TransactionAmountExceedsBalance;
import com.ryan.gerald.beancoin.exceptions.UsernameNotLoaded;
import com.ryan.gerald.beancoin.utils.TransactionRepr;
import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.List;

@Controller
@RequestMapping("wallet")
@SessionAttributes({"wallet", "latesttransaction", "isloggedin", "pool", "username", "user", "blockchain"})

public class WalletController {
    @Autowired private BlockchainService blockchainService;
    @Autowired private TransactionService transactionService;
    @Autowired private WalletService walletService;

    public WalletController() throws InterruptedException {}

    @ModelAttribute("wallet")
    public Wallet initWalletIfNotPresent(Model m) throws UsernameNotLoaded {
        try {
            return walletService.getWalletByUsername(String.valueOf(m.getAttribute("username")));
        } catch (Exception e) {
            throw new UsernameNotLoaded("USER NAME IS NOT LOADED");
        }
    }

    @GetMapping("")
    public String getWallet(Model model) {
        Wallet w;
        try {
            try {
                w = (Wallet) model.getAttribute("wallet");
            } catch (Exception e) {
                w = walletService.getWalletByUsername(String.valueOf(model.getAttribute("username")));
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/";
        }
        List<TransactionRepr> listTransactionsPending = transactionService.getTransactionReprList();

        Blockchain blockchain = blockchainService.getBlockchainByName("beancoin");
        double balance = Wallet.calculateWalletBalanceByTraversingChainIncludePending(blockchain, w.getAddress(),
                listTransactionsPending); // chain is the sourceOfTruth, not DB

        w.setBalance(balance);
        walletService.saveWallet(w);
        model.addAttribute("wallet", w);
//			return "redirect:/";
        return "wallet/wallet";
    }


    @GetMapping("/transact")
    public String getTransact(Model model) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchProviderException, IOException {
        Wallet w;
        try {
            w = (Wallet) model.getAttribute("wallet");
        } catch (Exception e) {
            w = walletService.getWalletByUsername((String) model.getAttribute("username"));
        }
        w.setBalance(Wallet.calculateWalletBalanceByTraversingChainIncludePending(blockchainService.getBlockchainByName(
                "beancoin"), w.getAddress(), transactionService.getTransactionReprList()));
        model.addAttribute("wallet", w);
        walletService.saveWallet(w);
        return "wallet/transact";
    }

    @Hidden
    @RequestMapping(value = "/transaction", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public String makeTransaction(@ModelAttribute("wallet") Wallet w, Model model, @RequestParam("address") String address, @RequestParam("amount") double amount) throws NoSuchAlgorithmException, IOException, NoSuchProviderException, InvalidKeyException {
        try {
            Transaction neu = new Transaction(w, address, amount);
            TransactionPoolMap pool = new TransactionPoolMap(transactionService.getTransactionList()); // TOOD limit
            Transaction existing = pool.findExistingTransactionByWallet(neu.getSenderAddress());
            if (existing == null) {
                model.addAttribute("latesttransaction", neu);
                transactionService.saveTransaction(neu);
//                if (Config.BROADCASTING) {broadcastTransaction(neu);} // TODO KAFKA
                return neu.toJSONtheTransaction();
            } else {
                Transaction merged = transactionService.getTransactionById(existing.getUuid());
                merged.update(neu.getSenderWallet(), neu.getRecipientAddress(), neu.getAmount());
                merged.rebuildOutputInput();
                transactionService.saveTransaction(merged);
                model.addAttribute("latesttransaction", merged);
//                if (Config.BROADCASTING) {broadcastTransaction(merged);} // TODO KAFKA
                return merged.toJSONtheTransaction();
            }
        } catch (TransactionAmountExceedsBalance e) {
            System.err.println("Transaction Amount Exceeds Balance");
            model.addAttribute("exceedsBalance", "Transaction Amount Exceeds Balance. Please enter a lower amount");
            return "Transaction Amount Exceeds Balance. Please enter a lower amount";
        } catch (Exception e) {
            e.printStackTrace();
            return "index";
        }

        //	@PostMapping("")
//	public String postWalletEmailSent(Model model) {
//		Wallet w = (Wallet) model.getAttribute("wallet");
//		if (w == null) {
//			return "redirect:/";
//		}
//		w = ws.updateWalletBalanceService(w);
//		model.addAttribute("wallet", w);
//		emailPrivateKey(model);
//		return "wallet/wallet";
//	}

//	public void emailPrivateKey(Model model) {
//		HashMap<String, String> body = new HashMap();
//		body.put("subject", "Your Private key");
//		body.put("text", "Private Key");
//		Object privateKey = ((Wallet) model.getAttribute("wallet")).getPrivatekey().getEncoded();
//		String userEmail = ((User) model.getAttribute("user")).getEmail();
//		String sender = System.getenv("email");
//		String password = System.getenv("password");
//		EmailSender.sendEmail(sender, password, userEmail, body);
//	}
//
//    /**
//     * WIP to make a better wallet console.
//     */
//    @GetMapping("/betterwallet")
//    public String getBetterWallet(Model model) {
//        return "wallet/betterwallet";
//    }


//    @PostMapping("/transact")
//    @ResponseBody
//    public String postTransact(Model model, @RequestBody Map<String, Object> body)
//            throws InvalidKeyException, NoSuchAlgorithmException, NoSuchProviderException, IOException,
//            InvalidAlgorithmParameterException, InterruptedException {
//        Wallet randomWallet = Wallet.createWallet("anon"); // simulate anon wallet on the wire. This exact name is
//        // important as it skips blockchain traversal for balance calculation, which is
//        // still buggy with non stored wallets
//
//        Transaction neu = new Transaction(randomWallet, (String) body.get("address"),
//                (double) ((Integer) body.get("amount")));
//        pool = TransactionPool.fillTransactionPool(transactionRepository.getListOfTransactions());
//        model.addAttribute("pool", pool);
//        Transaction alt = pool.findExistingTransactionByWallet(neu.getSenderAddress());
//        if (alt == null) {
//            model.addAttribute("latesttransaction", neu);
//            new TransactionService().addTransactionService(neu);
//            if (Config.BROADCASTING) {
//                broadcastTransaction(neu);
//            }
//            return neu.toJSONtheTransaction();
//        } else {
//            Transaction updated = new TransactionService().updateTransactionService(neu, alt);
//            model.addAttribute("latesttransaction", updated);
//            if (Config.BROADCASTING) {
//                broadcastTransaction(updated);
//            }
//            return updated.toJSONtheTransaction();
//        }
//    }


    }

//    /**
//     * Dev method, not necessary. Able to post various combinations to make multiple
//     * random type dev transactions. Because over the network and server, this can
//     * get buggy and consume resources. Easier to run dummy transactions in main
//     * method of initializer. Safest to ignore
//     */
//    @PostMapping("/transactt")
//    @ResponseBody
//    public String postDummyTransactions(Model model, @RequestBody Map<String, Object> body) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchProviderException, IOException, InvalidAlgorithmParameterException {
//
//        String numString = (String) body.get("number");
//        List<Transaction> list = new ArrayList();
//        int n = 1;
//        if (numString != null) {
//            n = new Random().nextInt(999);
//        }
//        String fromaddress = (String) body.get("fromaddress");
//        if (fromaddress == null) {
//            list = new Initializer().postNTransactions(n);
//        } else {
//            list = new Initializer().postNTransactions(n, fromaddress);
//        }
//
//        pool = TransactionPool.fillTransactionPool(transactionRepository.getListOfTransactions());
//        model.addAttribute("pool", pool);
//        return new Gson().toJson(list);
//    }

    public void broadcastTransaction(Transaction t) throws InterruptedException {
        //            new PubNubApp().broadcastTransaction(t);
    }

}
