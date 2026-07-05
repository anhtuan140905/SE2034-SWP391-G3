package vn.edu.fpt.service;


import org.springframework.data.repository.query.Param;
import vn.edu.fpt.modelview.request.finance.SettlementDTO;
import vn.edu.fpt.repository.SettlementSummaryProjection;

import java.util.List;


public interface SettlementService {
    void createSettlement(SettlementDTO dto);
    long countAllSettlement();
    long countPendingSettlement();
    long countCompletedSettlement();
    Long sumPayoutAmount();
    List<SettlementSummaryProjection> listSettlement(String tab);
    List<SettlementSummaryProjection> searchSettlement(@Param("keyword") String keyword);
    SettlementSummaryProjection getSettlementDetail(@Param("settlementId") Long settlementId);
    void markAsCompleted(Long settlementId);
}
