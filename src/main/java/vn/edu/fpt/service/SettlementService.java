package vn.edu.fpt.service;


import vn.edu.fpt.modelview.request.finance.SettlementDTO;


public interface SettlementService {
    void createSettlement(SettlementDTO dto);
    long countAllSettlement();
    long countPendingSettlement();
    long countCompletedSettlement();
}
