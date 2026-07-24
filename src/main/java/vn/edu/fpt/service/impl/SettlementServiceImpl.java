package vn.edu.fpt.service.impl;


import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;
import vn.edu.fpt.exception.ResourceNotFoundException;
import vn.edu.fpt.model.Event;
import vn.edu.fpt.model.Settlement;
import vn.edu.fpt.model.constant.SettlementResult;
import vn.edu.fpt.model.constant.SettlementStatus;
import vn.edu.fpt.modelview.request.finance.SettlementDTO;
import vn.edu.fpt.modelview.response.finance.SettlementSummaryDTO;
import vn.edu.fpt.repository.*;
import vn.edu.fpt.service.SettlementService;


import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;



@Slf4j
@Service("SettlementService")
@AllArgsConstructor
public class SettlementServiceImpl implements SettlementService {

    private final SettlementRepository settlementRepository;
    private final EventRepository eventRepository;
    private final OrderRepository orderRepository;
    private final EmailService emailService;

    @Transactional
    public void createSettlement(SettlementDTO dto) {

        if(dto.getEventId() == null){
            throw new IllegalArgumentException("Vui lòng chọn sự kiện.");
        }

        Event event = eventRepository.findById(dto.getEventId()).orElseThrow(() -> new IllegalArgumentException("Không tìm thấy sự kiện"));

        if(event.getEndTime().isAfter(LocalDateTime.now())){
            throw new IllegalStateException("Chỉ có thể tạo quyết toán cho sự kiện đã kết thúc.");
        }



        BigDecimal grossRevenue = orderRepository.calculateGrossRevenueByEventId(dto.getEventId());

        if(grossRevenue == null || grossRevenue.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalStateException("Không thể tạo quyết toán cho sự kiện chưa có doanh thu.");

        }

        if(settlementRepository.existsByEvent_EventId(dto.getEventId())){
            throw new IllegalStateException("Sự kiện này đã có quyết toán, không thể tạo thêm.");
        }

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

    public Long sumPayoutAmount(){
        return settlementRepository.sumPayoutAmount();
    }

    public List<SettlementSummaryDTO> listSettlement(String tab) {

        if (!List.of("all", "pending", "completed").contains(tab)) {
            throw new IllegalArgumentException("Trạng thái lọc không hợp lệ.");
        }

        List<SettlementSummaryDTO> list = settlementRepository.listSettlement()
                .stream()
                .map(SettlementSummaryDTO::new)
                .toList();

        list.forEach(SettlementSummaryDTO::calculateTimeDisplay);

        return switch (tab == null ? "all" : tab) {

            case "pending" ->
                    list.stream()
                            .filter(se -> "PENDING".equals(se.getStatus()))
                            .toList();

            case "completed" ->
                    list.stream()
                            .filter(se -> "COMPLETED".equals(se.getStatus()))
                            .toList();

            default -> list;
        };
    }

    public List<SettlementSummaryDTO> searchSettlement(String keyword) {

        if(keyword == null){
            throw new IllegalArgumentException("Từ khóa tìm kiếm không hợp lệ.");
        }
        return settlementRepository.searchSettlement(keyword)
                .stream()
                .map(projection -> {
                    SettlementSummaryDTO dto = new SettlementSummaryDTO(projection);
                    dto.calculateTimeDisplay();
                    return dto;
                })
                .toList();
    }

    public SettlementSummaryProjection getSettlementDetail(@Param("settlementId") Long settlementId){
        SettlementSummaryProjection settlementDetail = settlementRepository.getSettlementDetail(settlementId);
        if (settlementDetail == null) {
            throw new ResourceNotFoundException("Quyết toaán với ID " + settlementId + " không tồn tại.");
        }
        return settlementDetail;

    }

    @Transactional
    public void markAsCompleted(Long settlementId) {
        Settlement settlement = settlementRepository.findById(settlementId)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy quyết toán"));

        if (settlement.getStatus() != SettlementStatus.PENDING) {
            throw new IllegalStateException("Quyết toán này đã được thanh toán.");
        }

        settlement.setStatus(SettlementStatus.COMPLETED);
        settlement.setPaidAt(Instant.now());
        settlementRepository.save(settlement);


        SettlementSummaryProjection settlementDetail = settlementRepository.getSettlementDetail(settlementId);
        EventSummaryProjection eventDetail = eventRepository.getEventDetail(settlementId);

        String organizerFullName = eventDetail.getLastNameOrganizer() + " "
                + eventDetail.getMiddleNameOrganizer() + " "
                + eventDetail.getFirstNameOrganizer();

        emailService.sendSettlementPaidEmail(
                eventDetail.getEmail(),
                organizerFullName,
                eventDetail.getTitle(),
                eventDetail.getTotalTickets(),
                settlementDetail.getPayoutAmount(),
                settlementDetail.getPaidAt(),
                "Chuyển Khoản Ngân Hàng",
                settlementDetail.getBankName(),
                settlementDetail.getBankAccountNumber(),
                settlementDetail.getBankAccountName()
        );
    }
    public SettlementSummaryProjection findEventDetailById(@Param("eventId") Long eventId){
        SettlementSummaryProjection eventDetail = settlementRepository.findEventDetailById(eventId);
        if (eventDetail == null) {
            throw new ResourceNotFoundException("Sự kiện với ID " + eventId + " không tồn tại.");
        }
        return eventDetail;
    }

    @Override
    @Transactional
    public SettlementResult autoCreateSettlement(Long eventId){
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy sự kiện " + eventId));

        if (settlementRepository.existsByEvent_EventId(eventId)){
            return SettlementResult.ALREADY_EXISTS;
        }

        BigDecimal grossRevenue = orderRepository.calculateGrossRevenueByEventId(eventId);
        if(grossRevenue == null || grossRevenue.compareTo(BigDecimal.ZERO) <= 0){
            return SettlementResult.NO_REVENUE;
        }

        BigDecimal fee = grossRevenue.multiply(BigDecimal.valueOf(0.10)).setScale(2,RoundingMode.HALF_UP);
        BigDecimal payout = grossRevenue.subtract(fee);

        Settlement settlement = new Settlement();
        settlement.setEvent(event);
        settlement.setGrossRevenue(grossRevenue);
        settlement.setPlatformFee(fee);
        settlement.setPayoutAmount(payout);
        settlement.setStatus(SettlementStatus.PENDING);

        try{
            settlementRepository.save(settlement);
            return SettlementResult.CREATED;
        }
        catch (DataIntegrityViolationException e){
            return SettlementResult.ALREADY_EXISTS;
        }

    }

    public Long sumPendingPayoutAmount(){
        return settlementRepository.sumPendingPayoutAmount();
    }

    public long countNearDuePendingSettlements(){
        return settlementRepository.countNearDuePendingSettlements();
    }

    public long countUnsettledEvents(){
        return settlementRepository.countUnsettledEvents();
    }

    public List<SettlementSummaryProjection> platformFeeByMonth(){
        return settlementRepository.platformFeeByMonth();
    }

    public SettlementAgingProjection getSettlementAging(){
        return settlementRepository.getSettlementAging();
    }
}
