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