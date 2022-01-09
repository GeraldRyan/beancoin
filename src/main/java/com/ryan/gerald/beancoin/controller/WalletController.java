package com.ryan.gerald.beancoin.controller;

import com.ryan.gerald.beancoin.Service.BlockchainService;
import com.ryan.gerald.beancoin.Service.TransactionService;
import com.ryan.gerald.beancoin.Service.WalletService;
import com.ryan.gerald.beancoin.dto.TransactionDTO;
import com.ryan.gerald.beancoin.entity.Blockchain;
import com.ryan.gerald.beancoin.entity.Transaction;
import com.ryan.gerald.beancoin.entity.Wallet;
import com.ryan.gerald.beancoin.evaluation.BalanceCalculator;
import com.ryan.gerald.beancoin.exception.UsernameNotLoaded;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("wallet")
@SessionAttributes({"wallet", "isloggedin", "username", "user"})
public class WalletController {
    @Autowired private BlockchainService blockchainService;
    @Autowired private TransactionService transactionService;
    @Autowired private WalletService walletService;
    @Autowired private BalanceCalculator balanceCalculator;

    public WalletController() throws InterruptedException {}

    @GetMapping("")
    public String displayWallet(Model model) {
        Wallet w = walletService.getWalletByUsername(String.valueOf(model.getAttribute("username")));
        if (w == null) {return "redirect:/";}
        model.addAttribute("wallet", w);
        return "wallet/wallet";
    }

    @GetMapping("/transact")
    public String transact(Model model) {
        Wallet w = walletService.getWalletByUsername((String) model.getAttribute("username"));
        model.addAttribute("wallet", w);
        return "wallet/transact";
    }

    @RequestMapping(value = "/transaction", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public String doTransactionGET(@ModelAttribute("wallet") Wallet w, Model model, @RequestParam("address") String address, @RequestParam("amount") double amount) {
        try {
            Transaction neu = walletService.createTransaction(w, address, amount);
            if (neu == null){return "Error transacting. Is your balance great enough? Please wait until your mined balance is sufficient. Why don't you go mine a block?";}
            model.addAttribute("latesttransaction", neu);
            return neu.serialize();
        } catch (Exception e) {
            e.printStackTrace();
            return "Error occured with Transaction. Please make sure you have enough of a balance- including as mined";
        }
    }

//
//        @RequestMapping(value = "/transact", method = RequestMethod.POST, produces = "application/json")
//        @ResponseBody
//        public String doTransactionPOST(@RequestBody TransactionDTO transactionDTO){
//            Transaction t = new Transaction(transactionDTO.getToAddress(),transactionDTO.getToAmount())
//            return "";
//        }


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



    public void broadcastTransaction(Transaction t) throws InterruptedException {
        //            new PubNubApp().broadcastTransaction(t);
    }

}
