package vn.edu.fpt.controller;

import org.springframework.web.bind.annotation.RequestParam;
import vn.edu.fpt.model.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import vn.edu.fpt.service.impl.CityServiceImpl;
import vn.edu.fpt.service.impl.CloudinaryService;
import vn.edu.fpt.service.impl.UserServiceImpl;
import vn.edu.fpt.service.impl.WardServiceImpl;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class UserController {
    private final UserServiceImpl userServiceImpl;
    private final CityServiceImpl cityServiceImpl;
    private final WardServiceImpl wardServiceImpl;
    private final CloudinaryService cloudinaryService;



    public UserController(UserServiceImpl userServiceImpl, CityServiceImpl cityServiceImpl, WardServiceImpl wardServiceImpl, CloudinaryService cloudinaryService ) {
        this.userServiceImpl = userServiceImpl;
        this.cityServiceImpl = cityServiceImpl;
        this.wardServiceImpl = wardServiceImpl;
        this.cloudinaryService = cloudinaryService;
    }
    @GetMapping("/listuser")
    public String getListUserPage(Model model,@RequestParam(required = false) String keyword){

        List<User> users ;
        if(keyword==null||keyword.trim().isEmpty()){
            users = userServiceImpl.getAllUser();
        }
        else {
            users = userServiceImpl.searchUser(keyword);
        }

        model.addAttribute("users", users);
        model.addAttribute("keyword",keyword);
        return "admin/user/ListUser";
    }

    @GetMapping("/viewdetailuser")
    public String getViewDetailUserPage(@RequestParam Long id,Model model){
        User users = userServiceImpl.findById(id);
        model.addAttribute("users",users);
        return "admin/user/ViewDetailUser";
    }

    @GetMapping("/edituser")
    public String editUserPage(Model model) {
        List<User> users = userServiceImpl.getAllUser();
        model.addAttribute("users", users);
        return "admin/user/EditUser";
    }
}
