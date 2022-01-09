package com.ryan.gerald.beancoin.controller;

import com.ryan.gerald.beancoin.Service.BlockchainService;
import com.ryan.gerald.beancoin.Service.TransactionService;
import com.ryan.gerald.beancoin.entity.Block;
import com.ryan.gerald.beancoin.entity.Blockchain;
import com.ryan.gerald.beancoin.evaluation.SerializableChain;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.NoSuchAlgorithmException;
import java.util.concurrent.atomic.AtomicReference;

@Controller
@SessionAttributes({})
@RequestMapping("blockchain")
public class BlockchainController {

    @Autowired private BlockchainService blockchainService;
    @Autowired private TransactionService transactionService;

    public BlockchainController() throws InterruptedException {}

    @ModelAttribute("blockchain")
    public Blockchain loadBlockchain(Model model) throws NoSuchAlgorithmException {
        return blockchainService.loadOrCreateBlockchain("beancoin");
    }

    @RequestMapping(value = "", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public String serveBlockchain(Model model) {
        Blockchain bc = blockchainService.getBlockchainByName("beancoin");
        blockchainService.sortChain(bc); // TODO surely this isnt' necessary. Check on that.
        return bc.serialize();
    }

    @RequestMapping(value = "mine", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public String doMineAsGET(Model model)
            throws NoSuchAlgorithmException {
        SerializableChain.SeralizableBlock new_block = blockchainService.mineBlock();
        if (new_block == null) {
            return "No data to mine. Tell your friends to make transactions";
        }
        model.addAttribute("minedblock", new_block);
        return new_block.serialize();
    }

    // TODO Make get Block by Hash instead of number in list
    @RequestMapping(value = "/height/{blockheight}", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public String getNthBlockGET(@PathVariable String blockheight, @ModelAttribute("blockchain") Blockchain blockchain, Model model) {
        Blockchain bc = blockchainService.getBlockchainByName("beancoin");
        Block b = bc.getNthBlock(Integer.valueOf(blockheight));
        SerializableChain.SeralizableBlock sb = new SerializableChain().new SeralizableBlock(b);
        return sb.serialize();
    }

    // TODO Make get Block by Hash instead of number in list
    @RequestMapping(value = "/{blockhash}", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public String blockByHashGET(@PathVariable String blockhash, @ModelAttribute("blockchain") Blockchain blockchain, Model model) {
        AtomicReference<String> ar = new AtomicReference<>("[]");
        Blockchain bc = blockchainService.getBlockchainByName("beancoin");
        SerializableChain sc = new SerializableChain(bc);
        sc.getChain().forEach(b -> {
            if (b.getHash().equals(blockhash)) {
                ar.updateAndGet(x -> b.serialize());
            }
        });
        return ar.get();
    }

    // TODO inefficient- need faster lookup O(1)
    @RequestMapping(value = "/{n}/{thash}", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public String getTransactionMFromBlockN(@PathVariable String n, @PathVariable String thash, @ModelAttribute("blockchain") Blockchain blockchain, Model model) {
        Blockchain bc = blockchainService.getBlockchainByName("beancoin");
        AtomicReference<String> sa = new AtomicReference<>("[]");
        Block b = bc.getNthBlock(Integer.valueOf(n));
        SerializableChain.SeralizableBlock sb = new SerializableChain().new SeralizableBlock(b);
        sb.getTx().forEach(s -> {
                    if (s.getId().equals(thash)) {
                        sa.updateAndGet(x -> s.serialize());
                    }
                }
        );
        return sa.get();
    }
}
