package vn.edu.fpt.service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public interface TicketTypeService {
    Map<Long, Double> getMinPriceByEventIds(List<Long> eventIds);
}
