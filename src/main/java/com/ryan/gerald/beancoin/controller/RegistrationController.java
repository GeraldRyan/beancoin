package com.ryan.gerald.beancoin.controller;

import com.ryan.gerald.beancoin.Service.UserService;
import com.ryan.gerald.beancoin.Service.WalletService;
import com.ryan.gerald.beancoin.entity.User;
import com.ryan.gerald.beancoin.entity.UserRepository;
import com.ryan.gerald.beancoin.entity.Wallet;
import com.ryan.gerald.beancoin.entity.WalletRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.Optional;

@Controller
@RequestMapping("/register")
@SessionAttributes({"isloggedin", "wallet", "username"})
public class RegistrationController {

    @Autowired private UserService userService;
    @Autowired private WalletService walletService;


    @GetMapping("")
    public ModelAndView showRegisterPage(Model model) {
        if ((boolean) model.getAttribute("isloggedin")) {
            ModelAndView modelAndView =  new ModelAndView("redirect:/");
//            modelAndView.addObject("modelAttribute" , new ModelAttribute());
            return modelAndView;
        }
        ModelAndView mv = new ModelAndView("registration/register");
        model.addAttribute("user", new User());
        return mv;
    }

    // THIS WORKS
//	@GetMapping("")
//	public String showRegisterPage() {
////		ModelAndView mv = new ModelAndView("registration/register");
////		model.addAttribute("user", new User());
//		return "registration/register";
//	}

    @PostMapping("")
    public String registerUser(Model model, @ModelAttribute("user") @Valid User user) throws NoSuchAlgorithmException, NoSuchProviderException, InvalidAlgorithmParameterException {

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
        model.addAttribute("isloggedin", true);
        model.addAttribute("user", user);
        model.addAttribute("wallet", wallet);
        model.addAttribute("username", user.getUsername());
        // TODO make URL show welcomepage not registration
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
