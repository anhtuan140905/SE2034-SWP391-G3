/**
 * event_management.js
 * Path: src/main/resources/static/moderator/js/event_management.js
 */

document.addEventListener('DOMContentLoaded', () => {

    /* 1. SEARCH + FILTER: Manual submit with Enter key */
    const searchInput  = document.getElementById('searchInput');
    const searchBtn = document.getElementById('searchBtn');
    const statusSelect = document.getElementById('statusSelect');
    const categorySelect = document.getElementById('categorySelect');

    const applyFilters = () => {
        const keyword = searchInput ? searchInput.value.trim() : '';
        const status  = statusSelect ? statusSelect.value : '';
        const categoryId = categorySelect ? categorySelect.value : '';

        const url     = new URL(window.location.href);

        url.searchParams.set('page', '0');
        if (keyword) url.searchParams.set('keyword', keyword);
        else url.searchParams.delete('keyword');

        if (status) url.searchParams.set('status', status);
        else url.searchParams.delete('status');

        if (categoryId) url.searchParams.set('categoryId', categoryId);
        else url.searchParams.delete('categoryId');

        window.location.href = url.toString();
    };

    if (searchInput) {
        searchInput.addEventListener('keydown', (e) => {
            if (e.key === 'Enter') {
                applyFilters();
            }
        });
    }

    if (searchBtn) {
        searchBtn.addEventListener('click', applyFilters);
    }

    if (statusSelect) {
        statusSelect.addEventListener('change', applyFilters);
    }

    if(categorySelect) {
        categorySelect.addEventListener('change', applyFilters);
    }

});