package com.ryan.gerald.beancoin.tutorials.datamodelworkingexample;


import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Hidden
@Controller
@RequestMapping(path="/demo")
public class ExampleController {
    @Autowired private UserExampleRepository userRepository;

    @GetMapping(path="/add") // Map ONLY POST Requests
    public @ResponseBody String addNewUser (@RequestParam String name
            , @RequestParam String email) {
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