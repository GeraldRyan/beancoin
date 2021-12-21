package com.ryan.gerald.beancoin.controller;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.ryan.gerald.beancoin.entity.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;

import com.ryan.gerald.beancoin.Service.BlockService;
import com.ryan.gerald.beancoin.Service.BlockchainService;
import com.ryan.gerald.beancoin.Service.TransactionService;
import com.ryan.gerald.beancoin.exceptions.BlocksInChainInvalidException;
import com.ryan.gerald.beancoin.exceptions.ChainTooShortException;
import com.ryan.gerald.beancoin.exceptions.GenesisBlockInvalidException;
import com.ryan.gerald.beancoin.initializors.Config;
import com.ryan.gerald.beancoin.initializors.Initializer;
import com.ryan.gerald.beancoin.pubsub.PubNubApp;
import com.ryan.gerald.beancoin.utilities.TransactionRepr;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.pubnub.api.PubNubException;

@Controller
@SessionAttributes({"blockchain", "minedblock", "wallet", "pnapp"})
@RequestMapping("blockchain")
public class BlockchainController {

    @Autowired private BlockchainRepository blockchainRepository;
    @Autowired private TransactionRepository transactionRepository;
    @Autowired private BlockRepository blockRepository;
    @Autowired Initializer initializer;
    @Autowired TransactionPool pool; // REQUIRED for transactionRepo or else can inject dependency self

    //	TransactionService tService = new TransactionService(); // OLD STUFF - NEEDED FOR DAO CONNECTION
//	TransactionPool pool = tService.getAllTransactionsAsTransactionPoolService(); // OLD STUFF


    TransactionPool refreshTransactionPool() {
        List<Transaction> transactionList = transactionRepository.getListOfTransactions();
        System.out.println("SIZE: " + transactionList.size());
//        TransactionPool pool = new TransactionPool();
        for (Transaction t : transactionList) {
            System.out.println(t.toString());
            pool.putTransaction(t);
        }
        return pool;
    }

    /**
     * This method order the chain properly according to timestamp if for some
     * reason it pulled it from the database out of order (JPA error)
     *
     * @param model
     */
    public void refreshChain(Model model) throws NoSuchAlgorithmException, InterruptedException {
//		Blockchain newer_blockchain_from_db = blockchainApp.getBlockchainService("beancoin");
        Blockchain new_or_old_blockchain = makeBlockchainIfNull(model);
        try {
            ArrayList<Block> new_chain = new ArrayList<Block>(new_or_old_blockchain.getChain());
            System.out.println("RE-SORTING ArrayList<Block>");
            Collections.sort(new_chain, Comparator.comparingLong(Block::getTimestamp));
            ((Blockchain) model.getAttribute("blockchain")).setChain(new_chain);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//	List<Transaction> resultsList = em.createQuery("select t from Transaction t").getResultList();
//	for (Transaction t : resultsList) {
//		pool.putTransaction(t);
//	}


    public BlockchainController() throws InterruptedException {
    }
//	BlockService blockApp = new BlockService();
//	BlockchainService blockchainApp = new BlockchainService();

    /**
     * Pulls up beancoin blockchain on startup.
     * If no beancoin exists, create one and populate it with initial values
     */
    @ModelAttribute("blockchain")
    // This pulls from database before any request handler method goes (but only after a request is made)
    public Blockchain makeBlockchainIfNull(Model model) throws NoSuchAlgorithmException, InterruptedException {
//			Blockchain blockchain = blockchainApp.getBlockchainService("beancoin");
        Blockchain blockchain = blockchainRepository.getBlockchainByName("beancoin");
        if (blockchain != null) {
            return blockchain;
        } else {
            blockchain = new Blockchain("beancoin");
//            blockchainRepository.save(blockchain);
//			Blockchain blockchain = blockchainApp.newBlockchainService("beancoin");
            initializer.loadBC(blockchain);
            blockchainRepository.save(blockchain);
            Blockchain populated_blockchain = blockchainRepository.getBlockchainByName("beancoin");
//			Blockchain populated_blockchain = blockchainApp.getBlockchainService("beancoin");
            return populated_blockchain;
        }
    }

    @RequestMapping(value = "", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public String serveBlockchain(Model model) throws NoSuchAlgorithmException, InterruptedException,
            ChainTooShortException, GenesisBlockInvalidException, BlocksInChainInvalidException {
        refreshChain(model);
        return ((Blockchain) model.getAttribute("blockchain")).toJSONtheChain();
    }

    // TODO LATER FACTOR and minify
    @RequestMapping(value = "mine", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public String getMine(Model model)
            throws NoSuchAlgorithmException, PubNubException, InterruptedException {
//		pool = tService.getAllTransactionsAsTransactionPoolService(); // OLD
        pool = refreshTransactionPool();
        String transactionData = pool.getMinableTransactionDataString();
        if (transactionData == null){ return "No data to mine. Tell your friends to make transactions";}
        List<Transaction> tlist = transactionRepository.getListOfTransactions();
        Blockchain blockchain = blockchainRepository.getBlockchainByName("beancoin");

        Block new_block = blockchain.add_block(transactionData);
        System.out.println("NEW BLOCK MINED: " + new_block.toStringConsole());
        blockchainRepository.save(blockchain);
        model.addAttribute("minedblock", new_block);//		Block new_block = blockchainApp.addBlockService("beancoin", transactionData);
        if (Config.BROADCASTING) { // TODO CHANGE TO KAFKA
            new PubNubApp().broadcastBlock(new_block);
        }
        blockchain = blockchainRepository.getBlockchainByName("beancoin");
//		blockchain = blockchainApp.getBlockchainService("beancoin");
        model.addAttribute("blockchain", blockchain);
        pool.refreshBlockchainTransactionPool(blockchain);
//		pool = tService.getAllTransactionsAsTransactionPoolService(); // OLD
//        pool = refreshTransactionPool(); // COPIED FROM OLD BUT WHY DO WE NEED TO KEEP DOING THIS??
        model.addAttribute("pool", pool);
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
