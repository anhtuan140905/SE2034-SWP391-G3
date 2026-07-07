package vn.edu.fpt.service;


import org.springframework.data.repository.query.Param;
import vn.edu.fpt.model.constant.SettlementResult;
import vn.edu.fpt.modelview.request.finance.SettlementDTO;
import vn.edu.fpt.modelview.response.finance.SettlementSummaryDTO;
import vn.edu.fpt.repository.SettlementAgingProjection;
import vn.edu.fpt.repository.SettlementSummaryProjection;

import java.util.List;


public interface SettlementService {
    void createSettlement(SettlementDTO dto);
    long countAllSettlement();
    long countPendingSettlement();
    long countCompletedSettlement();
    Long sumPayoutAmount();
    List<SettlementSummaryDTO> listSettlement(String tab);
    List<SettlementSummaryDTO> searchSettlement(@Param("keyword") String keyword);
    SettlementSummaryProjection getSettlementDetail(@Param("settlementId") Long settlementId);
    void markAsCompleted(Long settlementId);
    SettlementSummaryProjection findEventDetailById(@Param("eventId") Long eventId);
    SettlementResult autoCreateSettlement(Long eventId);
    Long sumPendingPayoutAmount();
    long countNearDuePendingSettlements();
    long countUnsettledEvents();
    List<SettlementSummaryProjection> platformFeeByMonth();
    SettlementAgingProjection getSettlementAging();
}
