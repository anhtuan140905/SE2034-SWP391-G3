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
import vn.edu.fpt.repository.TicketRepository;
import vn.edu.fpt.service.OrderService;
import vn.edu.fpt.service.SettlementService;


import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

@Service("SettlementService")
@AllArgsConstructor
public class SettlementServiceImpl implements SettlementService {

    private final SettlementRepository settlementRepository;
    private final EventRepository eventRepository;
    private final OrderRepository orderRepository;

    @Transactional
    public void createSettlement(SettlementDTO dto) {
        if(dto.getEventId() == null){
            throw new IllegalArgumentException("Vui lòng chọn sự kiện.");
        }

        if(settlementRepository.existsByEvent_EventId(dto.getEventId())){
            throw new IllegalArgumentException("Sự kiện này đã có hồ sơ quyết toán.");
        }

        Event event = eventRepository.findById(dto.getEventId()).orElseThrow(() -> new IllegalArgumentException("Không tìm thấy sự kiện"));

        BigDecimal grossRevenue = orderRepository.calculateGrossRevenueByEventId(dto.getEventId());
        if(grossRevenue == null) grossRevenue = BigDecimal.ZERO;

        BigDecimal fee = grossRevenue.multiply(BigDecimal.valueOf(0.10)).setScale(2, RoundingMode.HALF_UP);

        BigDecimal payout = grossRevenue.subtract(fee);

        Settlement settlement =  new Settlement();
        settlement.setEvent(event);
        settlement.setGrossRevenue(grossRevenue);
        settlement.setPlatformFee(fee);
        settlement.setPayoutAmount(payout);
        settlement.setStatus(SettlementStatus.PENDING);

        settlementRepository.save(settlement);


    }

    public long countAllSettlement(){
        return settlementRepository.countAllSettlement();
    }

    public long countPendingSettlement(){
        return settlementRepository.countPendingSettlement();
    }

    public long countCompletedSettlement(){
        return settlementRepository.countCompletedSettlement();
    }
}
