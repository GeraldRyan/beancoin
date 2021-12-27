package com.ryan.gerald.beancoin.controller;

import com.google.gson.Gson;
import com.pubnub.api.PubNubException;
import com.ryan.gerald.beancoin.Service.BlockchainService;
import com.ryan.gerald.beancoin.Service.TransactionService;
import com.ryan.gerald.beancoin.entity.*;
import com.ryan.gerald.beancoin.initializors.Config;
import com.ryan.gerald.beancoin.initializors.Initializer;
import com.ryan.gerald.beancoin.utilities.TransactionRepr;
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
    @Autowired private BlockchainRepository blockchainRepository;
    @Autowired private TransactionRepository transactionRepository;
    @Autowired private TransactionService transactionService;
    @Autowired private BlockRepository blockRepository;
    @Autowired Initializer initializer;
    @Autowired TransactionPoolMap pool; // we need this as state?

    public BlockchainController() throws InterruptedException {}

    /**
     * Pulls up beancoin blockchain on startup.
     * If no beancoin exists, create one and populate it with initial values
     */
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

    TransactionPoolMap refreshTransactionPool() {
        return transactionService.getTransactionPool();
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
    public String getMine(Model model)
            throws NoSuchAlgorithmException, PubNubException, InterruptedException {
        pool = refreshTransactionPool();
        String transactionData = pool.getMinableTransactionDataString();
        if (transactionData == null) {return "No data to mine. Tell your friends to make transactions";}
        List<Transaction> tlist = transactionService.getTransactionList();
        Blockchain blockchain = blockchainService.getBlockchainByName("beancoin");
        Block new_block = blockchain.add_block(transactionData);
        model.addAttribute("blockchain", blockchain);
        model.addAttribute("minedblock", new_block);
        pool.refreshBlockchainTransactionPool(blockchain);
        model.addAttribute("pool", pool);
        blockchainService.saveBlockchain(blockchain);
        System.out.println("NEW BLOCK MINED: " + new_block.toStringConsole());

        if (Config.BROADCASTING) { // TODO CHANGE TO KAFKA
//            new PubNubApp().broadcastBlock(new_block);
        }

        return new_block.webworthyJson(tlist);
    }


    @RequestMapping(value = "/{n}", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public String seeIf(@PathVariable String n, @ModelAttribute("blockchain") Blockchain blockchain, Model model)
            throws NoSuchAlgorithmException, PubNubException, InterruptedException {
        try {
            Block b = ((Blockchain) model.getAttribute("blockchain")).getNthBlock(Integer.valueOf(n));
            if (Integer.valueOf(n) >= 0 && Integer.valueOf(n) <= 5) {
                return new Gson().toJson(b);
            }
            List<TransactionRepr> tr = b.deserializeTransactionData();
            return b.webworthyJson(tr, "plug");
        } catch (Exception e) {
            return "This index doesn't exist yet in our chain. Try a different number";
        }

    }

}
