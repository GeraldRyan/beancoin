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
@SessionAttributes({"blockchain", "minedblock", "wallet"})
@RequestMapping("blockchain")
public class BlockchainController {

    @Autowired private BlockchainService blockchainService;
    @Autowired private TransactionService transactionService;
    @Autowired Initializer initializer;

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
        if (bc == null) {
            /*
            TODO this was returning NULL on OpenAPI call. OPENAPI UI NEEDS TO WORK.
            Need to instantiate Blockchain. Need to set up pattern of model, session-scope cache, general cache, no
            cache patterns.
            Start by removing all the sessions and then put them back as desirable (startover strategy)
            Second, need to put the check session, if null then run dao/db query. Put this functionality in service
            class if possible, or at least a function. Ideally abstract it away, make your methods take a string
            argument or two- at least one to check if exists in model. Clean up controller class. 
             */
        }
        blockchainService.refreshChain(bc); // make sure ordered by timestamp (properly a persistence layer problem)
        return bc.toJSONtheChain();
    }

    @RequestMapping(value = "mine", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public String doMineAsGET(Model model)
            throws NoSuchAlgorithmException {
        Blockchain blockchain = blockchainService.getBlockchainByName("beancoin"); // later will cache
        List<Transaction> transList = transactionService.getTransactionList();
        if (transList.isEmpty()) {return "No data to mine. Tell your friends to make transactions";}
        TransactionPoolMap pool = new TransactionPoolMap(transList); // ok to construct. Ephemeral helper class
        String transactionData = pool.getMinableTransactionDataString();  // payload of block
        Block new_block = blockchain.add_block(transactionData);

        model.addAttribute("blockchain", blockchain);  // why? DELETE, REFACTOR, RESTORE
        model.addAttribute("minedblock", new_block);

        // delete from transaction table.
        transactionService.deletTransactionsInList(transList);
        blockchainService.saveBlockchain(blockchain); // refresh blockchain by adding chain
        System.out.println("NEW BLOCK MINED: " + new_block.toStringConsole());

        if (Config.BROADCASTING) { // TODO CHANGE TO KAFKA
//            new PubNubApp().broadcastBlock(new_block);
        }
        return new_block.webworthyJson(transList); // later manage max size of block
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
