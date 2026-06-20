document.addEventListener('DOMContentLoaded', () => {

    window.setTab = function (el, filter) {

        // active tab
        document.querySelectorAll('.tab-btn')
            .forEach(btn => btn.classList.remove('active'));

        el.classList.add('active');

        // lọc card
        document.querySelectorAll('.ticket-card')
            .forEach(card => {

                const status = card.dataset.status;

                let show = false;

                switch (filter) {

                    case 'all':
                        show = true;
                        break;

                    case 'upcoming':
                        show = status === 'active';
                        break;

                    case 'completed':
                        show = status === 'used';
                        break;

                    case 'cancelled':
                        show = status === 'expired';
                        break;
                }

                card.style.display = show ? '' : 'none';

            });
    };

    window.openDetail = function (orderId) {
        window.location.href = '/my-detailtickets/' + orderId;
    };

});