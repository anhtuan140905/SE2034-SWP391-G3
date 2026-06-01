/**
 * moderator_profile.js
 * Path: src/main/resources/static/moderator/js/moderator_profile.js
 *
 * Handles:
 * - Entrance animations cho card + dossier
 * - Avatar hover effect
 */

document.addEventListener('DOMContentLoaded', () => {

    /* ──────────────────────────────────────────
       1. ENTRANCE ANIMATIONS
    ────────────────────────────────────────── */
    const animateIn = (selector, delay = 0) => {
        const el = document.querySelector(selector);
        if (!el) return;
        el.style.opacity    = '0';
        el.style.transform  = 'translateY(18px)';
        el.style.transition = `opacity 0.45s ease ${delay}s, transform 0.45s ease ${delay}s`;
        requestAnimationFrame(() => requestAnimationFrame(() => {
            el.style.opacity   = '1';
            el.style.transform = 'translateY(0)';
        }));
    };

    animateIn('.mp-id-card',  0.0);
    animateIn('.mp-dossier',  0.1);


    /* ──────────────────────────────────────────
       2. DOSSIER ITEMS STAGGER
    ────────────────────────────────────────── */
    const items = document.querySelectorAll('.mp-dossier-item');
    items.forEach((item, i) => {
        item.style.opacity    = '0';
        item.style.transform  = 'translateY(10px)';
        item.style.transition = `opacity 0.35s ease ${0.15 + i * 0.07}s, transform 0.35s ease ${0.15 + i * 0.07}s`;
        requestAnimationFrame(() => requestAnimationFrame(() => {
            item.style.opacity   = '1';
            item.style.transform = 'translateY(0)';
        }));
    });


    /* ──────────────────────────────────────────
       3. META ROWS STAGGER
    ────────────────────────────────────────── */
    // const metaRows = document.querySelectorAll('.mp-meta-row');
    // metaRows.forEach((row, i) => {
    //     row.style.opacity    = '0';
    //     row.style.transition = `opacity 0.3s ease ${0.2 + i * 0.08}s`;
    //     requestAnimationFrame(() => requestAnimationFrame(() => {
    //         row.style.opacity = '1';
    //     }));
    // });

});