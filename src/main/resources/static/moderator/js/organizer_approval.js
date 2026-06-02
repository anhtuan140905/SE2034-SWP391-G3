/**
 * organizer_approval.js
 * Path: src/main/resources/static/moderator/js/organizer_approval.js
 *
 * Handles:
 * - Live search with debounce
 * - Status filter auto-submit
 * - Table row entrance animation
 * - Pending badge pulse
 */

document.addEventListener('DOMContentLoaded', () => {

    /* ──────────────────────────────────────────
       1. SEARCH + FILTER
    ────────────────────────────────────────── */
    const searchInput  = document.getElementById('searchInput');
    const statusSelect = document.getElementById('statusSelect');

    const applyFilters = () => {
        const keyword = searchInput ? searchInput.value.trim() : '';
        const status  = statusSelect ? statusSelect.value : '';
        const url     = new URL(window.location.href);

        url.searchParams.set('page', '0');

        if (keyword) url.searchParams.set('keyword', keyword);
        else url.searchParams.delete('keyword');

        if (status) url.searchParams.set('status', status);
        else url.searchParams.delete('status');

        window.location.href = url.toString();
    };

    const debounce = (fn, delay) => {
        let timer;
        return (...args) => { clearTimeout(timer); timer = setTimeout(() => fn(...args), delay); };
    };

    if (searchInput) {
        searchInput.addEventListener('input', debounce(applyFilters, 500));
        searchInput.addEventListener('keydown', (e) => { if (e.key === 'Enter') applyFilters(); });
    }

    if (statusSelect) {
        statusSelect.addEventListener('change', applyFilters);
    }


    /* ──────────────────────────────────────────
       2. TABLE ROW STAGGERED ENTRANCE
    ────────────────────────────────────────── */
    const rows = document.querySelectorAll('.om-table tbody tr');

    rows.forEach((row, i) => {
        row.style.opacity    = '0';
        row.style.transform  = 'translateY(10px)';
        row.style.transition = `opacity 0.35s ease ${i * 0.06}s, transform 0.35s ease ${i * 0.06}s`;

        requestAnimationFrame(() => requestAnimationFrame(() => {
            row.style.opacity   = '1';
            row.style.transform = 'translateY(0)';
        }));
    });

});