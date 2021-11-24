package com.ryan.gerald.beancoin.controller;

import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

import javax.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.ModelAndView;

import com.ryan.gerald.beancoin.Service.UserService;
import com.ryan.gerald.beancoin.Service.WalletService;
import com.ryan.gerald.beancoin.entity.User;
import com.ryan.gerald.beancoin.entity.Wallet;

@Controller
@RequestMapping("/register")
@SessionAttributes({ "user", "isloggedin", "wallet", "username" })
public class RegistrationController {
	UserService userService = new UserService();

	@GetMapping("")
	public ModelAndView showRegisterPage(Model model) {
		ModelAndView mv = new ModelAndView("registration/register");
		model.addAttribute("user", new User());
		return mv;
	}

	@PostMapping("")
	public String registerUser(Model model, @ModelAttribute("user") @Valid User user)
			throws NoSuchAlgorithmException, NoSuchProviderException, InvalidAlgorithmParameterException {

		User existingUser = new UserService().getUserService(user.getUsername());
		if (existingUser != null) {
			model.addAttribute("regmsg", "User already exists. Choose another name");
			return "registration/register";
		}
		new UserService().addUserService(user);
		Wallet wallet = Wallet.createWallet(user.getUsername());
		new WalletService().addWalletService(wallet);
		model.addAttribute("isloggedin", true);
		model.addAttribute("user", user);
		model.addAttribute("wallet", wallet);
		model.addAttribute("username", user.getUsername());
		return "registration/welcomepage";
	}

	@GetMapping("welcome")
	public String getWelcome(Model model) {
		User u = ((User) model.getAttribute("user"));
		Wallet w = (Wallet) new WalletService().getWalletService(u.getUsername()); // I find that extra protection
																					// prevents unexpected crashes
		model.addAttribute("wallet", w);
		return "registration/welcomepage";
	}

}
