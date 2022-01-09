package com.ryan.gerald.beancoin.controller;

import com.google.gson.Gson;
import com.ryan.gerald.beancoin.Service.BlockchainService;
import com.ryan.gerald.beancoin.Service.RegistrationService;
import com.ryan.gerald.beancoin.Service.UserService;
import com.ryan.gerald.beancoin.Service.WalletService;
import com.ryan.gerald.beancoin.entity.*;
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
    @Autowired private RegistrationService registrationService;

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
    public String registerNewUser(Model model, @ModelAttribute("user") @Valid User user) throws NoSuchAlgorithmException, NoSuchProviderException, InvalidAlgorithmParameterException, IOException, InvalidKeyException {

        if ((boolean) model.getAttribute("isloggedin")) {return "redirect:/";}

        Optional<User> existingUser = userService.getUserOptionalByName(user.getUsername());
        if (existingUser.isPresent()) {
            model.addAttribute("existsmsg", "User already exists. Choose another name or log in");
            return "registration/register";
        }
        Wallet wallet = walletService.newWallet(user.getUsername());
        userService.saveOrAddUser(user);
        registrationService.loadWallet(wallet.getAddress());
        model.addAttribute("isloggedin", true);
        model.addAttribute("user", user);
        model.addAttribute("wallet", wallet);
        model.addAttribute("username", user.getUsername());
        return "registration/welcomepage";
    }

    @GetMapping("welcome")
    public String getWelcome(Model model) {
        User u = ((User) model.getAttribute("user"));
        Wallet w = walletService.getWalletByUsername(u.getUsername());
        model.addAttribute("wallet", w);
        return "registration/welcomepage";
    }

}
