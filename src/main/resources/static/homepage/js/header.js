/**
 * EventHub — Header Scripts
 * Phạm vi: navbar scroll state, mobile collapse behaviour
 */

document.addEventListener('DOMContentLoaded', () => {

    // 1. Navbar scroll state — thêm class "scrolled" khi cuộn quá 50px
    const navbar = document.querySelector('.navbar-custom');
    if (navbar) {
        window.addEventListener('scroll', () => {
            navbar.classList.toggle('scrolled', window.scrollY > 50);
        });
    }

});

document.addEventListener('DOMContentLoaded', function () {
    const flashData = document.getElementById('flashToastData');
    if (!flashData) return;

    const toastEl = document.getElementById('paymentToast');
    const toastBody = document.getElementById('paymentToastBody');
    const type = flashData.dataset.type;

    toastEl.classList.remove('bg-success', 'bg-danger');
    toastEl.classList.add(type === 'success' ? 'bg-success' : 'bg-danger');
    toastBody.textContent = flashData.dataset.message;

    new bootstrap.Toast(toastEl, { delay: 10000 }).show();
});