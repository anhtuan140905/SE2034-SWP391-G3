/**
 * checkout.js — Chỉ xử lý UX (loading state khi submit form, xác nhận hủy đơn, hiện toast lỗi).
 * Không tự gọi fetch() để xác nhận thanh toán — để form submit tự nhiên
 * qua CheckoutController (/checkout/{id}/confirm hoặc /checkout/{id}/pay-vnpay).
 */
document.addEventListener('DOMContentLoaded', () => {

    // Disable nút + hiện spinner khi submit form "Tôi đã thanh toán xong"
    const confirmForm = document.getElementById('form-confirm-payment');
    if (confirmForm) {
        confirmForm.addEventListener('submit', () => {
            const btn = confirmForm.querySelector('button[type="submit"]');
            if (btn) {
                btn.disabled = true;
                btn.innerHTML = '<span class="spinner-border spinner-border-sm me-2" role="status" aria-hidden="true"></span> Đang xử lý...';
            }
        });
    }

    // Disable nút + hiện spinner khi submit form thanh toán VNPay
    const vnpayForm = document.getElementById('form-pay-vnpay');
    if (vnpayForm) {
        vnpayForm.addEventListener('submit', () => {
            const btn = vnpayForm.querySelector('button[type="submit"]');
            if (btn) {
                btn.disabled = true;
                btn.innerHTML = '<span class="spinner-border spinner-border-sm me-2" role="status" aria-hidden="true"></span> Đang chuyển sang VNPay...';
            }
        });
    }

    // Xác nhận trước khi hủy đơn hàng (tránh bấm nhầm)
    const cancelForm = document.getElementById('form-cancel-order');
    if (cancelForm) {
        cancelForm.addEventListener('submit', (e) => {
            if (!confirm('Bạn có chắc muốn hủy đơn hàng này? Ghế đã chọn sẽ được giải phóng.')) {
                e.preventDefault();
            }
        });
    }

    // Hiện toast nếu server trả về flash message (toastType/toastMessage) — dùng cho trường hợp lỗi
    const toastEl = document.getElementById('paymentToast');
    if (toastEl && toastEl.dataset.message) {
        const toastBody = document.getElementById('paymentToastBody');
        const type = toastEl.dataset.type === 'success' ? 'bg-success' : 'bg-danger';
        toastEl.classList.add(type);
        toastBody.textContent = toastEl.dataset.message;
        const toast = new bootstrap.Toast(toastEl, { delay: 6000 });
        toast.show();
    }
});