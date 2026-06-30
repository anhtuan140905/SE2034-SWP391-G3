package vn.edu.fpt.service;

import org.springframework.web.bind.annotation.RequestParam;
import vn.edu.fpt.modelview.request.finance.SettlementDTO;


public interface SettlementService {
    void createSettlement(SettlementDTO request);

}
