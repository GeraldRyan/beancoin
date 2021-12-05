package com.ryan.gerald.beancoin.datamodelworkingexample;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller // This means that this class is a Controller
@RequestMapping(path="/demo") // This means URL's start with /demo (after Application path)
public class MainController {
    @Autowired // This means to get the bean called userRepository
    private UserRepository userRepository;

    @GetMapping(path="/add") // Map ONLY POST Requests
    public @ResponseBody String addNewUser (@RequestParam String name
            , @RequestParam String email) {
        // @ResponseBody means the returned String is the response, not a view name
        // @RequestParam means it is a parameter from the GET or POST request

        UserExample n = new UserExample();
        n.setName(name);
        n.setEmail(email);
        userRepository.save(n);
        return "Saved";
    }

    @PostMapping(path="/add") // Map ONLY POST Requests
    public @ResponseBody String addNewUserPost (@RequestBody UserExample user) {
        userRepository.save(user);
        return "Saved";
    }

    @PostMapping(path="/delete") // Map ONLY POST Requests
    public @ResponseBody String deleteUser (@RequestBody UserExample user) {
//        Only needs the id
        userRepository.delete(user);
        return "Deleted";
    }


    @GetMapping(path="/delete-all") // Map ONLY POST Requests
    public @ResponseBody String deleteAllUsers (@RequestHeader(value="Authorization") String token) {
//        Only needs the id
        System.out.println("token: " + token);
        if (token.equals("12345")){
            userRepository.deleteAll();
            return "Password Accepted, all Deleted";
        }
        else {
            return "Password incorrect or not supplied";
        }
    }

    @GetMapping(path="/all")
    public @ResponseBody Iterable<UserExample> getAllUsers() {
        // This returns a JSON or XML with the users
        return userRepository.findAll();
    }
}