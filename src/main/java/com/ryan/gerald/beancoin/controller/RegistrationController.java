package com.ryan.gerald.beancoin.controller;

import com.ryan.gerald.beancoin.Service.BlockchainService;
import com.ryan.gerald.beancoin.Service.UserService;
import com.ryan.gerald.beancoin.Service.WalletService;
import com.ryan.gerald.beancoin.entity.*;
import com.ryan.gerald.beancoin.exceptions.TransactionAmountExceedsBalance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.io.IOException;
import java.security.*;
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
//            modelAndView.addObject("modelAttribute" , new ModelAttribute());
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
        if (adminWallet == null) {
            adminWallet = Wallet.createWallet("admin");
        }
        Transaction loadNewUserBalance = null;
        try {
            loadNewUserBalance = adminWallet.createTransaction(wallet.getAddress(), 1000);
        } catch (SignatureException e) {
            e.printStackTrace();
        }
        bc.add_block(Transaction.transactionStringSingleton(loadNewUserBalance));
        blockchainService.saveBlockchain(bc);
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
