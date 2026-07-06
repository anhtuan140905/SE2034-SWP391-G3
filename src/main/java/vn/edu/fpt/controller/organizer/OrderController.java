package vn.edu.fpt.controller.organizer;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import vn.edu.fpt.modelview.response.organizer.OrderDto;
import vn.edu.fpt.service.OrderService;
import vn.edu.fpt.service.StaffService;
import vn.edu.fpt.service.impl.security.CustomUserDetails;

import java.util.List;

@Controller
@AllArgsConstructor
@RequestMapping("/organizer/")
public class OrderController {
    private OrderService orderService;
    private StaffService staffService;
    @GetMapping("event/{id}/orders")
    private String getListOrderByEventId(@PathVariable Long id,
                                        @RequestParam(defaultValue = "") String keyword,
                                         @RequestParam(required = false) String status,
                                         @RequestParam(defaultValue = "0") int page,
                                         @AuthenticationPrincipal CustomUserDetails userDetails
                                        , Model model){
//        if(!staffService.checkPermission(userDetails.getUser().getId(),id,"CAN_VIEW_ORDERS")){
//            return "organizer/DashboardOrganizer";
//        }
        Page<OrderDto> orders =  orderService.getOrderbyEventID(id,keyword,status, page);
        model.addAttribute("orders",orders.getContent());
        model.addAttribute("totalPages",  orders.getTotalPages());
        model.addAttribute("currentPage", page);
        model.addAttribute("keyword",keyword);
        model.addAttribute("eventId",id);
        model.addAttribute("status",status);
        return "organizer/order/ListOrders";
    }

}
