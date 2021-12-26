package com.ryan.gerald.beancoin.controller;

import java.io.IOException;
import java.security.*;
import java.util.*;

import com.ryan.gerald.beancoin.entity.*;
import com.ryan.gerald.beancoin.exceptions.EntityNotLoadedException;
import com.ryan.gerald.beancoin.exceptions.TransactionAmountExceedsBalance;
import com.ryan.gerald.beancoin.exceptions.UsernameNotLoaded;
import com.ryan.gerald.beancoin.utilities.TransactionRepr;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.ryan.gerald.beancoin.Service.TransactionService;
//import com.ryan.gerald.beancoin.entity.EmailSender;
import com.ryan.gerald.beancoin.initializors.Config;
import com.ryan.gerald.beancoin.pubsub.PubNubApp;
import com.pubnub.api.PubNubException;

@Controller
@RequestMapping("wallet")
@SessionAttributes({"wallet", "latesttransaction", "isloggedin", "pool", "username", "user", "blockchain"})

public class WalletController {

    @Autowired private BlockchainRepository blockchainRepository;
    @Autowired private TransactionRepository transactionRepository;
    @Autowired private BlockRepository blockRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private WalletRepository walletRepository;
    @Autowired private TransactionService transactionService;

    @Autowired TransactionPool pool;

    public WalletController() throws InterruptedException {}

    @ModelAttribute("wallet")
    public Wallet initWalletIfNotPresent(Model m) throws UsernameNotLoaded {
        try {
            return walletRepository.findById(String.valueOf(m.getAttribute("username"))).get();
        }
        catch (Exception e){
            throw new UsernameNotLoaded("USER NAME IS NOT LOADED");
        }
    }

    @GetMapping("")
    public String getWallet(Model model) {
        Wallet w;
        try {
            try{
                w = (Wallet) model.getAttribute("wallet");
            }
            catch (Exception e){
                w = walletRepository.findById(String.valueOf(model.getAttribute("username"))).get();
            }
        }
        catch (Exception e){
            e.printStackTrace();
            return "redirect:/";
        }
        List<TransactionRepr> listTransactionsPending = transactionService.getTransactionReprList();
        Blockchain blockchain = (Blockchain) model.getAttribute("blockchain");
        w.setBalance(Wallet.calculateWalletBalanceByTraversingChain(blockchain, w.getAddress(),
                listTransactionsPending)); // chain is the sourceOfTruth
        walletRepository.save(w);
        model.addAttribute("wallet", w);
//			return "redirect:/";
        return "wallet/wallet";
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

    @GetMapping("/transact")
    public String getTransact(Model model) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchProviderException, IOException {
        Wallet w;
        try {
            w = (Wallet) model.getAttribute("wallet");
        } catch (Exception e) {
            w = walletRepository.findById((String) model.getAttribute("username")).get();
        }
        w.setBalance(Wallet.calculateWalletBalanceByTraversingChain(blockchainRepository.getBlockchainByName(
                "beancoin"), w.getAddress(), transactionService.getTransactionReprList()));
        model.addAttribute("wallet", w);
        walletRepository.save(w);
        return "wallet/transact";
    }

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

    @RequestMapping(value = "/transaction", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public String makeTransaction(@ModelAttribute("wallet") Wallet w, Model model, @RequestParam("address") String address, @RequestParam("amount") double amount) throws NoSuchAlgorithmException, IOException, NoSuchProviderException, InvalidKeyException {
        try {
            Transaction neu = new Transaction(w, address, amount);
            pool = TransactionPool.fillTransactionPool(transactionRepository.getListOfTransactions());
            Transaction old = pool.findExistingTransactionByWallet(neu.getSenderAddress());
            if (old == null) {
                model.addAttribute("latesttransaction", neu);
                transactionRepository.save(neu);
                if (Config.BROADCASTING) {broadcastTransaction(neu);}
                return neu.toJSONtheTransaction();
            } else {
                System.out.println("Existing transaction found!");
                Transaction merged = transactionRepository.findById(old.getUuid()).get();
                merged.update(neu.getSenderWallet(), neu.getRecipientAddress(), neu.getAmount());
                merged.rebuildOutputInput();
                transactionRepository.save(merged);
                model.addAttribute("latesttransaction", merged);
                if (Config.BROADCASTING) {broadcastTransaction(merged);}
                return merged.toJSONtheTransaction();
            }
        } catch (TransactionAmountExceedsBalance e) {
            System.out.println("Transaction Amount Exceeds Balance");
            model.addAttribute("exceedsBalance", "Transaction Amount Exceeds Balance. Please enter a lower amount");
            return "Transaction Amount Exceeds Balance. Please enter a lower amount";
        } catch (Exception e) {
            e.printStackTrace();
//            alert(Unkonwn Error occurred)
            return "index";
        }
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
        try {
            new PubNubApp().broadcastTransaction(t);
        } catch (PubNubException e) {
            System.err.println("Problem broadcasting the transaction.");
            e.printStackTrace();
        }
    }

}
