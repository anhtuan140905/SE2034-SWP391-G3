package vn.edu.fpt.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.edu.fpt.model.Event;
import vn.edu.fpt.modelview.request.organizer.CreateVoucherRequest;
import vn.edu.fpt.repository.EventRepository;
import vn.edu.fpt.repository.VoucherRepository;
import vn.edu.fpt.service.OrganizerVoucherService;

@Service
@RequiredArgsConstructor
public class OrganizerVoucherServiceImpl implements OrganizerVoucherService
{
    private final VoucherRepository voucherRepository;

    private final EventRepository eventRepository;

    @Override
    @Transactional
    public void createVoucher(Long evenId, CreateVoucherRequest request) {
        Event event = eventRepository.findById(evenId).orElseThrow( () -> new IllegalArgumentException( "Không tìm thấy sự kiện với id: " + evenId));


    }
}
