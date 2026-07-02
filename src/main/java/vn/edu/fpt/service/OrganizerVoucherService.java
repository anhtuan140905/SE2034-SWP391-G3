package vn.edu.fpt.service;

import vn.edu.fpt.modelview.request.organizer.CreateVoucherRequest;

public interface OrganizerVoucherService {
    void createVoucher(Long evenId, CreateVoucherRequest request);
}
