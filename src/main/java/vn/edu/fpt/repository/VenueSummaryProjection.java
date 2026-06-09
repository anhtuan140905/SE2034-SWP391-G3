package vn.edu.fpt.repository;

import java.math.BigDecimal;

public interface VenueSummaryProjection {
    Long getEventCount();
    Long getParticipantCount();
    BigDecimal getRevenue();
    Double getUsageRate();
    Integer getMonth();
}
