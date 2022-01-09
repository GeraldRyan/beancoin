package com.ryan.gerald.beancoin.controller;

import com.ryan.gerald.beancoin.exception.UsernameNotLoaded;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ExceptionHandlingController {

    // @RequestHandler methods

    // Exception handling methods

    @ExceptionHandler(value = UsernameNotLoaded.class)
    public String redirectToIndexIfAccessingWalletAndNotLoggedIn(){
        System.out.println("REDIRECTED TO INDEX");
        return "redirect:/";
    }


//    // Convert a predefined exception to an HTTP Status code
//    @ResponseStatus(value= HttpStatus.CONFLICT,
//            reason="Data integrity violation")  // 409
//    @ExceptionHandler(DataIntegrityViolationException.class)
//    public void conflict() {
//        // Nothing to do
//    }

    // Specify name of a specific view that will be used to display the error:
//    @ExceptionHandler({SQLException.class, DataAccessException.class})
//    public String databaseError() {
//        // Nothing to do.  Returns the logical view name of an error page, passed
//        // to the view-resolver(s) in usual way.
//        // Note that the exception is NOT available to this view (it is not added
//        // to the model) but see "Extending ExceptionHandlerExceptionResolver"
//        // below.
//        return "databaseError";
//    }

    // Total control - setup a model and return the view name yourself. Or
    // consider subclassing ExceptionHandlerExceptionResolver (see below).
//    @ExceptionHandler(Exception.class)
//    public ModelAndView handleError(HttpServletRequest req, Exception ex) {
//        ModelAndView mav = new ModelAndView();
//        mav.addObject("exception", ex);
//        mav.addObject("url", req.getRequestURL());
//        mav.setViewName("error");
//        return mav;
//    }
}
