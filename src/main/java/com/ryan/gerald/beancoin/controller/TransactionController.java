package com.ryan.gerald.beancoin.controller;


import com.ryan.gerald.beancoin.Service.BlockchainService;
import com.ryan.gerald.beancoin.Service.TransactionService;
import com.ryan.gerald.beancoin.Service.WalletService;
import com.ryan.gerald.beancoin.entity.TransactionPoolMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("transactions")
@SessionAttributes({"isloggedin","username", "user"})
public class TransactionController {
    @Autowired private BlockchainService blockchainService;
    @Autowired private TransactionService transactionService;
    @Autowired private WalletService walletService;


    @GetMapping("/unmined")
    public String getTransactionPool(Model model) {
        model.addAttribute("transactionpoollist", transactionService.getTransactionList());
        return "home/transactionpool";
    }

    @PostMapping("/unmined")
    @ResponseBody
    public String unminedTransactionsPOST(Model model) {
        TransactionPoolMap pool = TransactionPoolMap.fillTransactionPool(transactionService.getTransactionList());
        if (pool.getMinableTransactionDataString() == null) {
            return "No transactions in the pool. Tell your friends to make transactions";
        }
        return pool.getMinableTransactionDataString();
    }



}
