package com.ryan.gerald.beancoin.controller;

import com.google.gson.Gson;
import com.pubnub.api.PubNubException;
import com.ryan.gerald.beancoin.Service.BlockchainService;
import com.ryan.gerald.beancoin.Service.TransactionService;
import com.ryan.gerald.beancoin.entity.Block;
import com.ryan.gerald.beancoin.entity.Blockchain;
import com.ryan.gerald.beancoin.entity.Transaction;
import com.ryan.gerald.beancoin.entity.TransactionPoolMap;
import com.ryan.gerald.beancoin.initializors.Config;
import com.ryan.gerald.beancoin.initializors.Initializer;
import com.ryan.gerald.beancoin.utils.TransactionRepr;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.NoSuchAlgorithmException;
import java.util.List;

@Controller
@SessionAttributes({"blockchain", "minedblock", "wallet", "pnapp"})
@RequestMapping("blockchain")
public class BlockchainController {

    @Autowired private BlockchainService blockchainService;
    @Autowired private TransactionService transactionService;
    @Autowired Initializer initializer;
    @Autowired TransactionPoolMap UnminedTransactionPoolMap; // Sort of a service/utiltity object

    public BlockchainController() throws InterruptedException {}

    @ModelAttribute("blockchain")
    // This pulls from database before any request handler method goes (but only after a request is made)
    public Blockchain loadBlockchain(Model model) throws NoSuchAlgorithmException, InterruptedException {
        Blockchain bc;
        try {
            bc = (Blockchain) model.getAttribute("blockchain");
        } catch (Exception e) {
            bc = blockchainService.CreateNewBlockchain("beancoin");
        }
        return bc;
    }

    @RequestMapping(value = "", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public String serveBlockchain(Model model) {
        Blockchain bc = (Blockchain) model.getAttribute("blockchain");
        blockchainService.refreshChain(bc); // make sure ordered by timestamp (properly a persistence layer problem)
        return bc.toJSONtheChain();
    }

    @RequestMapping(value = "mine", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public String doMineAsGET(Model model)
            throws NoSuchAlgorithmException, PubNubException, InterruptedException {
        UnminedTransactionPoolMap = transactionService.getUnminedTransactionsPoolMap();
        String transactionData = UnminedTransactionPoolMap.getMinableTransactionDataString();
        if (transactionData == null) {return "No data to mine. Tell you" +
                "r friends to make transactions";}
        List<Transaction> tlist = transactionService.getTransactionList();
        Blockchain blockchain = blockchainService.getBlockchainByName("beancoin");
        Block new_block = blockchain.add_block(transactionData);
        model.addAttribute("blockchain", blockchain);
        model.addAttribute("minedblock", new_block);
        UnminedTransactionPoolMap.clearProcessedTransactions(blockchain);  // deletes from Transaction Table
        model.addAttribute("pool", UnminedTransactionPoolMap);
        blockchainService.saveBlockchain(blockchain);
        System.out.println("NEW BLOCK MINED: " + new_block.toStringConsole());

        if (Config.BROADCASTING) { // TODO CHANGE TO KAFKA
//            new PubNubApp().broadcastBlock(new_block);
        }
        return new_block.webworthyJson(tlist);
    }

    @RequestMapping(value = "/{n}", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public String seeIf(@PathVariable String n, @ModelAttribute("blockchain") Blockchain blockchain, Model model) {
        try {
            Block b = ((Blockchain) model.getAttribute("blockchain")).getNthBlock(Integer.valueOf(n));
            if (Integer.valueOf(n) >= 0 && Integer.valueOf(n) <= 5) {
                return new Gson().toJson(b);
            }
            List<TransactionRepr> tr = b.deserializeTransactionData();
            return b.webworthyJson(tr, "plug");
        } catch (NullPointerException e) {
            e.printStackTrace();
            return "This index doesn't exist yet in our chain. Try a different number";
        }
    }
}
