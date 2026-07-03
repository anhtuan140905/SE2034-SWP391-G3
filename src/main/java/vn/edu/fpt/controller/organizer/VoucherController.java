package vn.edu.fpt.controller.organizer;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import vn.edu.fpt.service.StaffService;

@Controller
@RequestMapping("organzier/event/{id}/voucher")
@RequiredArgsConstructor
public class VoucherController {

    private final StaffService staffService;

    

}
