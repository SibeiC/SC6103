package com.chencraft.ntu.configuration;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Home redirection to swagger api documentation
 */
@Controller
public class HomeController {
    /**
     * Redirects the root URL to the Swagger UI page.
     *
     * @return a redirection string to /swagger-ui/
     */
    @RequestMapping(value = "/")
    public String index() {
        return "redirect:/swagger-ui/";
    }
}
