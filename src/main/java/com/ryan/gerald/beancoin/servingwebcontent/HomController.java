package com.ryan.gerald.beancoin.servingwebcontent;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomController
{
    @GetMapping("/foo")
    public String getFoo(){
        return "home/foo";
    }
}
