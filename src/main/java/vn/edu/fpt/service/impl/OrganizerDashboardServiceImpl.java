package vn.edu.fpt.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vn.edu.fpt.model.Event;
import vn.edu.fpt.model.constant.OrderStatus;
import vn.edu.fpt.model.TicketType;
import vn.edu.fpt.modelview.response.organizer.*;
import vn.edu.fpt.repository.EventRepository;
import vn.edu.fpt.repository.OrderRepository;
import vn.edu.fpt.repository.SettlementRepository;
import vn.edu.fpt.repository.TicketTypeRepository;
import vn.edu.fpt.service.OrganizerDashboardService;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
@RequiredArgsConstructor
public class OrganizerDashboardServiceImpl implements OrganizerDashboardService {

    private final TicketTypeRepository ticketTypeRepository;
    private final OrderRepository orderRepository;
    private final SettlementRepository settlementRepository;
    private final EventRepository eventRepository;
    @Override
    public DashboardStatsDTO getDashboardStats(Long eventId) {
        DashboardStatsDTO dto = new DashboardStatsDTO();
        BigDecimal totalRevenue = orderRepository.calculateGrossRevenueByEventId(eventId);
        if (totalRevenue == null) totalRevenue = BigDecimal.ZERO;
        dto.setTotalRevenue(totalRevenue);
        Integer totalTicketsSold = 0 ;
        Event event = eventRepository.findById(eventId).orElseThrow(()->new RuntimeException("Sự kiện Không Tồn Tại"));
        for(TicketType ticketType:event.getTicketTypes()){
            totalTicketsSold = totalTicketsSold + ticketType.getSoldQuantity();
        }
        dto.setTotalTicketsSold(totalTicketsSold);
        BigDecimal settledAmount = BigDecimal.ZERO;
        if(settlementRepository.getPayoutAmountByEventId(eventId)!=null){
            settledAmount = settlementRepository.getPayoutAmountByEventId(eventId).getPayoutAmount();
        }
        dto.setSettledAmount(settledAmount);
        return dto;
    }

        @Override
        public List<DailyRevenueBarDTO> getDailyRevenueChartData(Long eventId) {
            Instant startDate = Instant.now().minus(15, ChronoUnit.DAYS);
            List<Object[]> rawData = orderRepository.getDailyRevenueByEventId(eventId, OrderStatus.PAID, startDate);

            List<DailyRevenueBarDTO> bars = new ArrayList<>();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM").withZone(ZoneId.systemDefault());

            BigDecimal maxRevenue = BigDecimal.ZERO;
            List<Map<String, Object>> tempMapList = new ArrayList<>();

            for (Object[] row : rawData) {
                // CAST AS date trả về java.sql.Date hoặc java.util.Date tùy phiên bản Hibernate
                Object dateObj = row[0];
                Instant dateInstant;
                if (dateObj instanceof java.sql.Date) {
                    dateInstant = ((java.sql.Date) dateObj).toLocalDate().atStartOfDay(ZoneId.systemDefault()).toInstant();
                } else if (dateObj instanceof java.util.Date) {
                    dateInstant = ((java.util.Date) dateObj).toInstant();
                } else {
                    dateInstant = Instant.now();
                }

                BigDecimal revenue = (BigDecimal) row[1];
                if (revenue.compareTo(maxRevenue) > 0) {
                    maxRevenue = revenue;
                }
                Map<String, Object> map = new HashMap<>();
                map.put("date", formatter.format(dateInstant));
                map.put("revenue", revenue);
                tempMapList.add(map);
            }

            if (tempMapList.isEmpty()) {
                List<String> defaultDates = Arrays.asList("05/06", "09/06", "13/06", "17/06", "21/06", "25/06");
                for (String date : defaultDates) {
                    bars.add(new DailyRevenueBarDTO(date, BigDecimal.ZERO, 0));
                }
                return bars;
            }

            for (Map<String, Object> item : tempMapList) {
                String date = (String) item.get("date");
                BigDecimal revenue = (BigDecimal) item.get("revenue");

                int heightPercent = 0;
                if (maxRevenue.compareTo(BigDecimal.ZERO) > 0) {
                    heightPercent = (int) (revenue.doubleValue() / maxRevenue.doubleValue() * 90);
                    if (heightPercent < 5 && revenue.compareTo(BigDecimal.ZERO) > 0) {
                        heightPercent = 5;
                    }
                }

                bars.add(DailyRevenueBarDTO.builder()
                        .date(date)
                        .revenue(revenue)
                        .heightPercent(heightPercent)
                        .build());
            }

            return bars;
        }

    @Override
    public List<TicketTypeStatsDTO> getTicketTypeStats(Long eventId) {
        List<TicketType> ticketTypes = ticketTypeRepository.findByEvent_EventId(eventId);
        List<TicketTypeStatsDTO> statsList = new ArrayList<>();

        for (TicketType tt : ticketTypes) {
            int sold = tt.getSoldQuantity() != null ? tt.getSoldQuantity() : 0;
            int total = tt.getTicketTypeId() != null ? tt.getTotalQuantity() : 0;
            int percent = total > 0 ? (sold * 100) / total : 0;

            statsList.add(TicketTypeStatsDTO.builder()
                    .zoneName(tt.getZoneName())
                    .price(tt.getPrice())
                    .soldQuantity(sold)
                    .totalStock(total)
                    .percentSold(percent)
                    .build());
        }
        return statsList;
    }
}
