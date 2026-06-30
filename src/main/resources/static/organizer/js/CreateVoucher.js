/* CreateVoucher.js */

/* Sidebar toggle (mobile) */
function toggleSidebar(open) {
    document.getElementById('sidebar').classList.toggle('open', open);
    document.getElementById('sidebarOverlay').classList.toggle('show', open);
}

/* Cập nhật ký hiệu đơn vị (% hoặc ₫) khi đổi dropdown */
function onDiscountTypeChange() {
    const type = document.getElementById('discountType').value;
    document.getElementById('discountUnit').textContent =
        type === 'PERCENTAGE' ? '%' : type === 'FIXED' ? '₫' : '—';
}

/* Chạy khi trang load xong — init đơn vị theo giá trị đã bind từ server */
document.addEventListener('DOMContentLoaded', function () {
    onDiscountTypeChange();
});