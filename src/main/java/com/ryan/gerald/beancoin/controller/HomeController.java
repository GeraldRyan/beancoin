package com.ryan.gerald.beancoin.controller;

import com.ryan.gerald.beancoin.dto.Login;
import com.ryan.gerald.beancoin.Service.BlockchainService;
import com.ryan.gerald.beancoin.Service.TransactionService;
import com.ryan.gerald.beancoin.Service.UserService;
import com.ryan.gerald.beancoin.Service.WalletService;
import com.ryan.gerald.beancoin.entity.*;
import com.ryan.gerald.beancoin.initializors.Initializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.NoHandlerFoundException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.security.NoSuchAlgorithmException;
import java.util.Optional;


@Controller
@SessionAttributes({"blockchain", "wallet", "username", "isloggedin", "user", "msg", "transactionpool", "pubsubapp"})
public class HomeController {

    @Autowired private BlockchainService blockchainService;
    @Autowired private TransactionService transactionService;
    @Autowired private UserService userService;
    @Autowired private WalletService walletService;

    @Autowired Initializer initializer;
    @Autowired private Environment env; // Not using I believe but keep for example

    public HomeController() throws InterruptedException {}

    @ModelAttribute("isloggedin")
    public boolean isLoggedIn() {
        return false;
    }

    @ModelAttribute("blockchain")
    public Blockchain loadOrCreateBlockchain() throws NoSuchAlgorithmException {
        return blockchainService.loadOrCreateBlockchain("beancoin");
    }

    @ModelAttribute("transactionpool")
    public TransactionPoolMap initTransactionPool() {
        return transactionService.getUnminedTransactionsPoolMap();
    }

    @GetMapping("/")
    public String showIndex(Model model) {return "index";}

    @GetMapping("/login")
    public String showLoginPage(Model model, @ModelAttribute("login") Login login) {
        if ((boolean) model.getAttribute(("isloggedin"))) {return "redirect:/";}
        return "login/login";
    }

    @PostMapping("/login")
    public String processLogin(Model model, @ModelAttribute("login") Login login) {
        String result = validateUserAndPassword(login.getUsername(), login.getPassword());
        if (result == "true") {
            model.addAttribute("username", login.getUsername());
            model.addAttribute("isloggedin", true);
            model.addAttribute("user", userService.getUserByUsername(login.getUsername()));
            model.addAttribute("failed", false);
            model.addAttribute("wallet", walletService.getWalletByUsername(login.getUsername()));
        } else if (result == "user not found") {
            model.addAttribute("failed", true);
            model.addAttribute("msg", "User not found. Please try again");
        } else {
            model.addAttribute("failed", true);
            model.addAttribute("msg", "Password incorrect. Please try again");
        }
        return "redirect:/";
    }

    @GetMapping("/logout")
    public String logOut(Model model, HttpServletRequest request) {
        model.addAttribute("isloggedin", false);
        model.addAttribute("wallet", null);
        model.addAttribute("username", null);
        model.addAttribute("user", null);
        HttpSession httpSession = request.getSession();
        return "redirect:/";
    }

    @GetMapping("/transactionpool")
    public String getTransactionPool(Model model) {
        model.addAttribute("transactionpoollist", transactionService.getTransactionList());
        return "home/transactionpool";
    }

    @PostMapping("/transactionpool")
    @ResponseBody
    public String postTransactionPool(Model model) {
        TransactionPoolMap pool = TransactionPoolMap.fillTransactionPool(transactionService.getTransactionList());
        if (pool.getMinableTransactionDataString() == null) {
            return "No transactions in the pool. Tell your friends to make transactions";
        }
        return pool.getMinableTransactionDataString();
    }

    public String validateUserAndPassword(String username, String password) {
        Optional<User> user = userService.getUserOptionalByName(username);
        if (user.isEmpty()) {return "user not found";}
        if (user.get().getPassword().equalsIgnoreCase(password)) {return "true";}
        return "false";
    }

    @ControllerAdvice
    public class ControllerAdvisor {
        @ExceptionHandler(NoHandlerFoundException.class)
        public String handle(Exception ex) {
            return "404";
        }
    }


    //    /**
//     * PubNub pubsub provider. Can be instantiated as needed for broadcast, but as
//     * it is also a listener, should be instantiated right away as session variable
//     * in order to responsond to incoming messages (part of being part of a
//     * community. Hoping this is the right method of doing so
//
//     */
//	@ModelAttribute("pubsubapp")
//	public PubNubApp startupApp() throws InterruptedException {
//		if (Config.LISTENING) {
////			return new PubNubApp();
//		}
//		return null;
//	}
}

