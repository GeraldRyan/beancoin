package com.ryan.gerald.beancoin.controller;

import com.google.gson.Gson;
import com.ryan.gerald.beancoin.Service.BlockchainService;
import com.ryan.gerald.beancoin.Service.UserService;
import com.ryan.gerald.beancoin.Service.WalletService;
import com.ryan.gerald.beancoin.entity.*;
import com.ryan.gerald.beancoin.exception.TransactionAmountExceedsBalance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.io.IOException;
import java.security.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/register")
@SessionAttributes({"isloggedin", "wallet", "username"})
public class RegistrationController {

    @Autowired private UserService userService;
    @Autowired private WalletService walletService;
    @Autowired private BlockchainService blockchainService;


    @GetMapping("")
    public ModelAndView showRegisterPage(Model model) {
        if ((boolean) model.getAttribute("isloggedin")) {
            ModelAndView modelAndView = new ModelAndView("redirect:/");
            return modelAndView;
        }
        ModelAndView mv = new ModelAndView("registration/register");
        model.addAttribute("user", new User());
        return mv;
    }

    @PostMapping("")
    public String registerUser(Model model, @ModelAttribute("user") @Valid User user) throws NoSuchAlgorithmException, NoSuchProviderException, InvalidAlgorithmParameterException, IOException, InvalidKeyException, TransactionAmountExceedsBalance {

        if ((boolean) model.getAttribute("isloggedin")) {
            return "redirect:/";
        }

        // TODO BELONGS IN NEW USER SERVICE!! TOO MUCH LOGIC HERE!!!
        Optional<User> existingUser = userService.getUserOptionalByName(user.getUsername());
        if (existingUser.isPresent()) {
            model.addAttribute("existsmsg", "User already exists. Choose another name or log in");
            return "registration/register";
        }
        Wallet wallet = Wallet.createWallet(user.getUsername());
        userService.saveUser(user);
        walletService.saveWallet(wallet);
        Blockchain bc = blockchainService.getBlockchainByName("beancoin");
        Wallet adminWallet;
        adminWallet = walletService.getWalletByUsername("admin");
        if (adminWallet == null) {adminWallet = Wallet.createWallet("admin");}
        Transaction newUserTransfer = null;
        try {newUserTransfer = adminWallet.createTransaction(wallet.getAddress(), 1000);} catch (SignatureException e) {
            e.printStackTrace();
        }
        List<Transaction> singleList = new ArrayList<Transaction>();
        singleList.add(newUserTransfer);
        String singleListJson = new Gson().toJson(singleList);
        bc.add_block(singleListJson); // does need to serialize as list not as individual?
        blockchainService.saveBlockchain(bc);  // go straight to blockchain, no need to mine this type
        model.addAttribute("isloggedin", true);
        model.addAttribute("user", user);
        model.addAttribute("wallet", wallet);
        model.addAttribute("username", user.getUsername());
        return "registration/welcomepage";
    }

    @GetMapping("welcome")
    public String getWelcome(Model model) {
        User u = ((User) model.getAttribute("user"));
        Wallet w = walletService.getWalletByUsername(u.getUsername()); // I find that extra protection
        // prevents unexpected crashes
        model.addAttribute("wallet", w);
        return "registration/welcomepage";
    }

}
