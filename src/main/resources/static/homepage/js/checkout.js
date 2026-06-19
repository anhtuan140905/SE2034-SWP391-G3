const orderId = [[${order.orderId}]];

setInterval(async () => {

    const response =
        await fetch(
            `/api/payment/status/${orderId}`
        );

    const data = await response.json();

    if(data.status === "SUCCESS") {

        location.reload();

    }

}, 3000);

/**
 * checkout.js — Xử lý giả lập hoàn tất đơn hàng tự động
 */
document.addEventListener('DOMContentLoaded', () => {
    const btnConfirm = document.getElementById('btn-confirm-payment');

    if (btnConfirm) {
        btnConfirm.addEventListener('click', async () => {
            // 1. Tự động lấy orderId trực tiếp từ URL của thanh địa chỉ (Pattern: /checkout/{orderId})
            const urlParts = window.location.pathname.split('/');
            const orderId = urlParts[urlParts.length - 1];

            // 2. Thay đổi trạng thái nút để tránh người dùng click spam nhiều lần
            btnConfirm.disabled = true;
            btnConfirm.innerHTML = '<span class="spinner-border spinner-border-sm me-2" role="status" aria-hidden="true"></span> Đang xử lý tạo vé...';

            try {
                // 3. Gọi request fetch() ngầm dạng POST lên Controller thường
                const res = await fetch(`/checkout/success/${orderId}`, {
                    method: 'POST'
                });

                if (res.ok) {
                    const data = await res.json();
                    if (data.success) {
                        alert('Thanh toán thành công! Vé sự kiện của bạn đã được hệ thống khởi tạo.');
                        // 4. Điều hướng người dùng về trang danh sách vé cá nhân (hoặc trang chủ tùy bạn cấu hình)
                        window.location.href = '/profile/tickets';
                    }
                } else {
                    const errorMsg = await res.text();
                    alert('Không thể xử lý đơn hàng: ' + errorMsg);
                    // Reset lại nút bấm nếu lỗi
                    btnConfirm.disabled = false;
                    btnConfirm.innerHTML = '<i class="fa-solid fa-circle-check me-2"></i> Tôi đã thanh toán xong';
                }
            } catch (err) {
                console.error('[checkout] Lỗi luồng gửi dữ liệu:', err);
                alert('Có lỗi kết nối máy chủ xảy ra. Vui lòng thử lại!');
                btnConfirm.disabled = false;
                btnConfirm.innerHTML = '<i class="fa-solid fa-circle-check me-2"></i> Tôi đã thanh toán xong';
            }
        });
    }
});