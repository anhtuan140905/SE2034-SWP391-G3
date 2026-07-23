package vn.edu.fpt.service.impl;

import jakarta.validation.constraints.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.edu.fpt.model.Event;
import vn.edu.fpt.model.Voucher;
import vn.edu.fpt.model.constant.DiscountType;
import vn.edu.fpt.model.constant.OrderStatus;
import vn.edu.fpt.modelview.request.organizer.CreateVoucherRequest;
import vn.edu.fpt.modelview.response.homepage.VoucherValidationResult;
import vn.edu.fpt.repository.EventRepository;
import vn.edu.fpt.repository.VoucherRepository;
import vn.edu.fpt.service.OrderService;
import vn.edu.fpt.service.VoucherService;
import vn.edu.fpt.service.VoucherUsageService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class VoucherServiceImpl implements VoucherService {

    private final VoucherRepository voucherRepository;
    private final EventRepository eventRepository;
    private final VoucherUsageService voucherUsageService;
    private final OrderService orderService;

    @Override
    @Transactional
    public void createVoucher(Long evenId, Long userId, CreateVoucherRequest request) {

        Event event = eventRepository.findById(evenId).orElseThrow(() -> new IllegalArgumentException("Không tìm thấy sự kiện với id: " + evenId));

        String normalizedCode = normalizeCode(request.getCode());

        validateCreateVoucherBusiness(event, normalizedCode, request);

        Voucher voucher = new Voucher();
        voucher.setEvent(event);
        voucher.setCode(normalizedCode);
        voucher.setTitle(request.getTitle());
        voucher.setDescription(request.getDescription());
        voucher.setDiscountType(request.getDiscountType());
        voucher.setDiscountValue(request.getDiscountValue());
        voucher.setMaxUsage(request.getMaxUsage());
        voucher.setValidFrom(request.getValidFrom());
        voucher.setValidTo(request.getValidTo());
        voucher.setIsActive(true);

        voucherRepository.save(voucher);

    }

    private void validateCreateVoucherBusiness(Event event, String normalizedCode, CreateVoucherRequest request) {
        validateUniqueCode(normalizedCode);
        validateDateRange(request.getValidFrom(), request.getValidTo(), event.getStartTime());
        validateDiscountRule(request.getDiscountType(), request.getDiscountValue());
    }

    // Validate the discount value of the voucher for 2 types of discounts: Fixed - Percent
    private void validateDiscountRule(DiscountType discountType, BigDecimal discountValue) {
        if (discountValue == null || discountValue.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Giá trị giảm phải lớn hơn 0");
        }

        if (discountType == DiscountType.PERCENT && discountValue.compareTo(BigDecimal.valueOf(100)) > 0) {
            throw new IllegalArgumentException("Giá trị giảm giá phần trăm không được lớn hơn 100");
        }
    }

    // Validate the voucher's start and end dates
    private void validateDateRange(LocalDateTime validFrom, LocalDateTime validTo, LocalDateTime eventStartTime) {
        LocalDateTime now = LocalDateTime.now().minusMinutes(3);

        if (!validFrom.isAfter(now)) {
            throw new IllegalArgumentException("Thời gian bắt đầu voucher phải lớn hơn hoặc bằng thời điểm hiện tại");
        }

        if (!validTo.isAfter(validFrom)) {
            throw new IllegalArgumentException("Thời gian kết thúc voucher phải sau thời gian bắt đầu");
        }

        if (!validTo.isBefore(eventStartTime)) {
            throw new IllegalArgumentException("Thời gian kết thúc voucher phải trước thời gian sự kiện bắt đầu.");
        }
    }

    // Validate the voucher code for the event
    private void validateUniqueCode(String normalizedCode) {
        if (voucherRepository.existsByCodeIgnoreCase(normalizedCode)) {
            throw new IllegalArgumentException("Mã voucher đã tồn tại.");
        }
    }

    // Normalizer voucher's code to upper case
    private String normalizeCode(String code) {

        return code == null ? null : code.toUpperCase().trim();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Voucher> getVoucherByEventId(Long evenId) {

        return voucherRepository.findByEvent_EventId(evenId);
    }

    @Override
    @Transactional(readOnly = true)
    public Voucher getVoucherDetail(Long eventId, Long voucherId) {

        return voucherRepository.findByEvent_EventIdAndVoucherId(eventId, voucherId).orElseThrow(() -> new IllegalStateException("Voucher không tồn tại hoặc không thuộc sự kiện này"));
    }

    @Override
    public VoucherValidationResult validate(Long voucherId, Long eventId, Long userId, BigDecimal subtotal) {
        Voucher v = voucherRepository.findByVoucherIdAndEvent_EventId(voucherId, eventId).orElse(null);

        if (v == null) return VoucherValidationResult.invalid("VOUCHER_NOT_FOUND");
        if (v.getValidTo().isBefore(LocalDateTime.now())) return VoucherValidationResult.invalid("VOUCHER_EXPIRED");
        if (this.voucherUsageService.countVoucherUsageByVoucherId(voucherId) >= v.getMaxUsage()) return VoucherValidationResult.invalid("VOUCHER_EXHAUSTED");

        // Đã dùng thành công rồi (Order đã confirm payment)
        if (this.voucherUsageService.existsByVoucher_VoucherIdAndUserId(voucherId, userId)) {
            return VoucherValidationResult.invalid("VOUCHER_ALREADY_USED");
        }

        // MỚI: đang có Order pending khác cũng giữ voucher này (chưa confirm, chưa hết hạn)
        if (this.orderService.existsByVoucher_VoucherIdAndUserIdAndStatus(voucherId, userId, OrderStatus.PENDING_PAYMENT)) {
            return VoucherValidationResult.invalid("VOUCHER_ALREADY_PENDING");
        }

        BigDecimal discount = v.getDiscountType() == DiscountType.PERCENT
                ? subtotal.multiply(v.getDiscountValue()).divide(BigDecimal.valueOf(100)).min(subtotal)
                : v.getDiscountValue().min(subtotal);

        return VoucherValidationResult.valid(v, discount);
    }

    @Override
    public List<Voucher> findAvailableVouchersByEvent(Long eventId) {
        return this.voucherRepository.findAvailableVouchersByEvent(eventId, LocalDateTime.now());
    }

    @Override
    @Transactional
    public void updateVoucherStatus(Long eventId, Long voucherId, Boolean isActive) {
        Voucher voucher = getVoucherDetail(eventId, voucherId);
        voucher.setIsActive(isActive != null ? isActive : false);
        voucherRepository.save(voucher);
    }

}
