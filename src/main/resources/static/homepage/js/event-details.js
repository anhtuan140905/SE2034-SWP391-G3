document.addEventListener('DOMContentLoaded', () => {
    const dataEl = document.getElementById('payment-result-data');
    if(!dataEl) return;

    const status = dataEl.dataset.status;
    const message = dataEl.dataset.message;

    const iconEl = document.getElementById('modal-icon');
    const titleEl = document.getElementById('modal-title');
    const messageEl = document.getElementById('modal-message');
    const ticketsBtn = document.getElementById('modal-btn-tickets');

    if(status === 'success') {
        iconEl.innerHTML = '&#10004;';
        iconEl.classList.add('text-success');
        titleEl.textContent = 'Thanh toán thành công';
        titleEl.classList.add('text-success');
        ticketsBtn.style.display='block';
    } else {
         iconEl.innerHTML = '&#10006;';
         iconEl.classList.add('text-danger');
         titleEl.textContent = 'Thanh toán thất bại';
         titleEl.classList.add('text-danger');
         ticketsBtn.style.display = 'none';
    }
    messageEl.textContent = message;

    const modalEl = document.getElementById('paymentResultModal');
    const modal = new bootstrap.Modal(modalEl);
    modal.show();
})