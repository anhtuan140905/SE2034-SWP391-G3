package vn.edu.fpt.controller.hompage;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Controller
public class HomepageController {

    @GetMapping("/")
    public String homepage(){
        return "homepage/Home";
    }
}
