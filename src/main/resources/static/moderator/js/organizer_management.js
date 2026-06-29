document.addEventListener('DOMContentLoaded', () => {
    const searchInput = document.getElementById('searchInput');
    const searchBtn = document.getElementById('searchBtn');
    const statusSelect = document.getElementById('statusSelect');

    const applyFilters = () => {
        const keyword = searchInput ? searchInput.value.trim() : '';
        const status = statusSelect ? statusSelect.value : '';

        const url = new URL(window.location.href);
        url.searchParams.set('page', '0');

        if (keyword) url.searchParams.set('keyword', keyword);
        else url.searchParams.delete('keyword');

        if (status) url.searchParams.set('status', status);
        else url.searchParams.delete('status');

        window.location.href = url.toString();
    };

    if (searchInput) {
        searchInput.addEventListener('keydown', (e) => {
            if (e.key === 'Enter') {
                e.preventDefault();
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
});