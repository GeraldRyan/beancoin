package com.ryan.gerald.beancoin.controller;

import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.Optional;

import javax.validation.Valid;

import com.ryan.gerald.beancoin.entity.UserRepository;
import com.ryan.gerald.beancoin.entity.WalletRepository;
import org.springframework.beans.factory.annotation.Autowired;
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
@SessionAttributes({ "isloggedin", "wallet", "username" })
public class RegistrationController {

	@Autowired
	private UserRepository userRepository;
	@Autowired
	private WalletRepository walletRepository;


	UserService userService = new UserService();

	@GetMapping("")
	public ModelAndView showRegisterPage(Model model) {
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
	public String registerUser(Model model, @ModelAttribute("user") @Valid User user)
			throws NoSuchAlgorithmException, NoSuchProviderException, InvalidAlgorithmParameterException {

//		User existingUser = new UserService().getUserService(user.getUsername()); // old
		Optional<User> existingUser = userRepository.findById(user.getUsername());

		if (existingUser.isPresent()) {
			model.addAttribute("existsmsg", "User already exists. Choose another name or log in");
			return "registration/register";
		}
//		new UserService().addUserService(user); // old code
		userRepository.save(user);
		Wallet wallet = Wallet.createWallet(user.getUsername());  // TODO FIX THIS ERRS OUT
//		new WalletService().addWalletService(wallet); // old code
		walletRepository.save(wallet);
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
