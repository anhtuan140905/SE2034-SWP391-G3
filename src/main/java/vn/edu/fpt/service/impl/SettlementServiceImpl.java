package vn.edu.fpt.service.impl;


import jakarta.annotation.Nonnull;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import vn.edu.fpt.model.Event;
import vn.edu.fpt.model.Settlement;
import vn.edu.fpt.model.constant.SettlementStatus;
import vn.edu.fpt.modelview.request.finance.SettlementDTO;
import vn.edu.fpt.repository.EventRepository;
import vn.edu.fpt.repository.OrderRepository;
import vn.edu.fpt.repository.SettlementRepository;


import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service("SettlementService")
@AllArgsConstructor
public class SettlementServiceImpl {
    private final EventRepository eventRepository;
    private final SettlementRepository settlementRepository;
    private final OrderRepository orderRepository;

    @Transactional
    public void createSettlement(SettlementDTO request) {


    }
}
