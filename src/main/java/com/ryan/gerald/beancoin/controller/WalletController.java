package com.ryan.gerald.beancoin.controller;

import java.io.IOException;
import java.security.*;
import java.util.*;

import javax.servlet.http.HttpServletRequest;

import com.ryan.gerald.beancoin.entity.*;
import com.ryan.gerald.beancoin.exceptions.TransactionAmountExceedsBalance;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.converter.json.GsonBuilderUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;

import com.ryan.gerald.beancoin.Service.TransactionService;
import com.ryan.gerald.beancoin.Service.UserService;
import com.ryan.gerald.beancoin.Service.WalletService;
//import com.ryan.gerald.beancoin.entity.EmailSender;
import com.ryan.gerald.beancoin.initializors.Config;
import com.ryan.gerald.beancoin.initializors.Initializer;
import com.ryan.gerald.beancoin.pubsub.PubNubApp;
import com.google.gson.Gson;
import com.pubnub.api.PubNubException;

@Controller
@RequestMapping("wallet")
@SessionAttributes({"wallet", "latesttransaction", "isloggedin", "pool", "username", "user", "blockchain"})

/**
 * ./transact page is for making transactions, GET or POST Completing the form
 * sends you to /transaction GET page, and completes the transaction through
 * requestparams. You can also post to /transaction but it can be buggy with
 * Spring and synchronization
 */
public class WalletController {

    @Autowired private BlockchainRepository blockchainRepository;
    @Autowired private TransactionRepository transactionRepository;
    @Autowired private BlockRepository blockRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private WalletRepository walletRepository;

//	TransactionPool poolOLD = new TransactionService().getAllTransactionsAsTransactionPoolService();
    @Autowired
    TransactionPool pool;

    @ModelAttribute("wallet")
    public Wallet initWalletIfNotPresent(Model m) {
        return walletRepository.findById(String.valueOf(m.getAttribute("username"))).get();
    }

    public WalletController() throws InterruptedException { }

    /**
     * Display wallet console page. Sometimes wallet doesn't load due to session
     * state. Just click around, log in and out until loads (until fixed)
     *
     * @param model
     * @return
     */
    @GetMapping("")
    public String getWallet(Model model) {
        Wallet w;
        Optional<Wallet> walletOptional = walletRepository.findById(String.valueOf(model.getAttribute("username")));
        if (walletOptional.isPresent()) {
            w = walletOptional.get();
            Wallet walletFromRepo = walletRepository.findById((String) model.getAttribute("username")).get();
            walletRepository.save(w);
            Blockchain blockchain = (Blockchain) model.getAttribute("blockchain");
            double newBalance = Wallet.calculateBalance(blockchain, w.getAddress());
            w.setBalance(newBalance);
            walletRepository.save(w);
            model.addAttribute("wallet", w);
//			return "redirect:/";
        }
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

    /**
     * WIP to make a better wallet console.
     *
     * @param model
     * @return
     */
    @GetMapping("/betterwallet")
    public String getBetterWallet(Model model) {
        return "wallet/betterwallet";
    }

    @GetMapping("/transact")
    public String getTransact(Model model)
            throws InvalidKeyException, NoSuchAlgorithmException, NoSuchProviderException, IOException {
//        Wallet w = (Wallet) model.getAttribute("wallet");
        Wallet w = walletRepository.findById((String) model.getAttribute("username")).get();
//        w = ws.getWalletService((String) model.getAttribute("username"));
        model.addAttribute("wallet", w);
        return "wallet/transact";
    }

    /**
     * This is a simulation of posting transactions from someone who is logged in.
     * <p>
     * { "address":"recipient", "amount": "integer" } in future add { "username":
     * "username", "password": "password" } to make this official
     * <p>
     * In reality login information would have to be provided to access their wallet
     * and post the transaction.
     *
     * @param model
     * @param body
     * @return
     * @throws InvalidKeyException
     * @throws NoSuchAlgorithmException
     * @throws NoSuchProviderException
     * @throws IOException
     * @throws InvalidAlgorithmParameterException
     * @throws InterruptedException
     */
    @PostMapping("/transact")
    @ResponseBody
    public String postTransact(Model model, @RequestBody Map<String, Object> body)
            throws InvalidKeyException, NoSuchAlgorithmException, NoSuchProviderException, IOException,
            InvalidAlgorithmParameterException, InterruptedException {
        Wallet randomWallet = Wallet.createWallet("anon"); // simulate anon wallet on the wire. This exact name is
        // important as it skips blockchain traversal for balance calculation, which is
        // still buggy with non stored wallets

        Transaction neu = new Transaction(randomWallet, (String) body.get("address"),
                (double) ((Integer) body.get("amount")));
        pool = TransactionPool.fillTransactionPool(transactionRepository.getListOfTransactions());
        model.addAttribute("pool", pool);
        Transaction alt = pool.findExistingTransactionByWallet(neu.getSenderAddress());
        if (alt == null) {
            model.addAttribute("latesttransaction", neu);
            new TransactionService().addTransactionService(neu);
            if (Config.BROADCASTING) {
                broadcastTransaction(neu);
            }
            return neu.toJSONtheTransaction();
        } else {
            Transaction updated = new TransactionService().updateTransactionService(neu, alt);
            model.addAttribute("latesttransaction", updated);
            if (Config.BROADCASTING) {
                broadcastTransaction(updated);
            }
            return updated.toJSONtheTransaction();
        }
    }

    /**
     * Very important method, this is how you make a transaction with RequestParams
     * from /transact form posting
     *
     * @param w
     * @param model
     * @param address
     * @param amount
     * @param request
     * @return
     * @throws InvalidKeyException
     * @throws NoSuchAlgorithmException
     * @throws NoSuchProviderException
     * @throws IOException
     * @throws InterruptedException
     */
    @RequestMapping(value = "/transaction", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public String postTransaction(@ModelAttribute("wallet") Wallet w, Model model,
                                  @RequestParam("address") String address, @RequestParam("amount") double amount, HttpServletRequest request)
            throws InvalidKeyException, NoSuchAlgorithmException, NoSuchProviderException, IOException,
            InterruptedException {
        Transaction neu = new Transaction(w, address, amount);
        pool = TransactionPool.fillTransactionPool(transactionRepository.getListOfTransactions());
        Transaction alt = pool.findExistingTransactionByWallet(neu.getSenderAddress());
        if (alt == null) {
            System.err.println("Transaction 2 is null. there is no existing transaction of that sender"
                    + neu.getSenderAddress() + "==" + w.getAddress());
            model.addAttribute("latesttransaction", neu);
            transactionRepository.save(neu);
//            new TransactionService().addTransactionService(neu);
            if (Config.BROADCASTING) {
                broadcastTransaction(neu);
            }
            return neu.toJSONtheTransaction();
        } else {
            System.out.println("Existing transaction found!");
//            Transaction updated = new TransactionService().updateTransactionService(neu, alt);

            Transaction merged = transactionRepository.findById(alt.getUuid()).get(); // guaranteed to exist.
//            Transaction merged = em.find(Transaction.class, alt.getUuid());
            merged.rebuildOutputInput();
            try {
                merged.update(neu.getSenderWallet(), neu.getRecipientAddress(), neu.getAmount());
            } catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchProviderException | SignatureException
                    | TransactionAmountExceedsBalance | IOException e) {
                e.printStackTrace();
            }
            transactionRepository.save(merged);

            model.addAttribute("latesttransaction", merged);
            if (Config.BROADCASTING) {
                broadcastTransaction(merged);
            }
            return merged.toJSONtheTransaction();
        }
    }

    /**
     * Dev method, not necessary. Able to post various combinations to make multiple
     * random type dev transactions. Because over the network and server, this can
     * get buggy and consume resources. Easier to run dummy transactions in main
     * method of initializer. Safest to ignore
     *
     * @param model
     * @param body
     * @return
     * @throws InvalidKeyException
     * @throws NoSuchAlgorithmException
     * @throws NoSuchProviderException
     * @throws IOException
     * @throws InvalidAlgorithmParameterException
     */
    @PostMapping("/transactt")
    @ResponseBody
    public String postDummyTransactions(Model model, @RequestBody Map<String, Object> body) throws InvalidKeyException,
            NoSuchAlgorithmException, NoSuchProviderException, IOException, InvalidAlgorithmParameterException {

        String numString = (String) body.get("number");
        List<Transaction> list = new ArrayList();
        int n = 1;
        if (numString != null) {
            n = new Random().nextInt(999);
        }
        String fromaddress = (String) body.get("fromaddress");
        if (fromaddress == null) {
            list = new Initializer().postNTransactions(n);
        } else {
            list = new Initializer().postNTransactions(n, fromaddress);
        }

        pool = TransactionPool.fillTransactionPool(transactionRepository.getListOfTransactions());
        model.addAttribute("pool", pool);
        return new Gson().toJson(list);
    }

    /**
     * Broadcast to pubnub wrapper method
     *
     * @param t
     * @throws InterruptedException
     */
    public void broadcastTransaction(Transaction t) throws InterruptedException {
        try {
            new PubNubApp().broadcastTransaction(t);
        } catch (PubNubException e) {
            System.err.println("Problem broadcasting the transaction.");
            e.printStackTrace();
        }
    }

}
