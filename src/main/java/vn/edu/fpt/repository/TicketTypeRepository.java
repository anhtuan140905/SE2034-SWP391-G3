package vn.edu.fpt.repository;

import org.springframework.data.repository.CrudRepository;
import vn.edu.fpt.model.TicketType;

public interface TicketTypeRepository extends CrudRepository<TicketType, Integer> {
}
