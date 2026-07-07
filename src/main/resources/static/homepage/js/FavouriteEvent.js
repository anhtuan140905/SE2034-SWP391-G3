
document.addEventListener('DOMContentLoaded', () => {
    // 1. Dynamic Navbar scroll state detection
    const navbar = document.querySelector('.navbar-custom');
    if (navbar) {
        window.addEventListener('scroll', () => {
            if (window.scrollY > 40) {
                navbar.classList.add('scrolled');
            } else {
                navbar.classList.remove('scrolled');
            }
        });
    }

    // Initialize initial view states (Empty check on startup)
    checkEmptyState();

    // 2. Real-time Search & Category Filtering specifically for Bookmarks
    const searchInput = document.getElementById('fav-search-input');
    const categorySelect = document.getElementById('fav-category-select');
    const getEventCards = () =>
        document.querySelectorAll('.fav-event-col');

    const applyFavoritesFilters = () => {
        const query = searchInput
            ? searchInput.value.toLowerCase().trim()
            : '';

        const categoryVal = categorySelect
            ? categorySelect.value.toLowerCase()
            : 'all';

        let visibleCount = 0;

        getEventCards().forEach(col => {

            if (col.classList.contains('removing') ||
                col.style.opacity === '0') {
                return;
            }

            const cCategory =
                (col.getAttribute('data-category') || '')
                    .toLowerCase();

            const titleEl = col.querySelector('.favorite-title');
            const descEl = col.querySelector('.favorite-description');
            const locationEl = col.querySelector('.favorite-location');

            const titleText = titleEl ? titleEl.textContent.toLowerCase() : '';
            const descText = descEl ? descEl.textContent.toLowerCase() : '';
            const locationText = locationEl ? locationEl.textContent.toLowerCase() : '';

            const matchesSearch = !query ||
                titleText.includes(query) ||
                descText.includes(query) ||
                locationText.includes(query);

            const matchesCategory = categoryVal === 'all' || cCategory === categoryVal;

            if (matchesSearch && matchesCategory) {
                visibleCount++;
                col.style.display = 'flex';
                setTimeout(() => {
                    col.style.opacity = '1';
                    col.style.transform = 'scale(1)';
                }, 10);
            } else {
                col.style.opacity = '0';
                col.style.transform = 'scale(0.95)';
                setTimeout(() => {
                    col.style.display = 'none';
                }, 300);
            }
        });

        // Toggle local empty state if search filters return zero items
        const emptyState = document.getElementById('fav-empty-state');
        const grid = document.getElementById('fav-events-grid');

        if (visibleCount === 0) {
            if (emptyState) {
                emptyState.style.display = 'block';
                const emptyTitle = emptyState.querySelector('h5');
                const emptyDesc = emptyState.querySelector('p');
                if (emptyTitle && emptyDesc) {
                    emptyTitle.textContent = 'Không tìm thấy kết quả';
                    emptyDesc.textContent = 'Không có sự kiện đã ghim nào khớp với từ khóa hoặc danh mục được lọc của bạn.';
                }
            }
        } else {
            if (emptyState && !hasZeroTotalCards()) {
                emptyState.style.display = 'none';
            }
        }
    };

    if (searchInput) searchInput.addEventListener('input', applyFavoritesFilters);
    if (categorySelect) categorySelect.value = 'all'; // Reset default select
    if (categorySelect) categorySelect.addEventListener('change', applyFavoritesFilters);


    // Simple helper to count total un-deleted cards
    function hasZeroTotalCards() {
        const remainingCards =
            document.querySelectorAll(
                '.fav-event-col:not(.removing)'
            );
        return remainingCards.length === 0;
    }

    // Helper to evaluate and shift list view to empty state
    function checkEmptyState() {
        const remainingCards = document.querySelectorAll('.fav-event-col:not(.removing)');
        const emptyState = document.getElementById('fav-empty-state');
        const grid = document.getElementById('fav-events-grid');

        if (remainingCards.length === 0) {
            if (grid) grid.style.display = 'none';
            if (emptyState) {
                emptyState.style.display = 'block';
                const emptyTitle = emptyState.querySelector('h5');
                const emptyDesc = emptyState.querySelector('p');
                if (emptyTitle) emptyTitle.textContent = 'Danh sách yêu thích trống';
                if (emptyDesc) emptyDesc.textContent = 'Bạn chưa đánh dấu trái tim yêu thích cho sự kiện nào. Hãy dạo quanh trang chủ hoặc tìm kiếm để ghim chiếc vé mơ ước nhé!';
            }
        } else {
            if (grid) grid.style.display = 'flex';
            if (emptyState) emptyState.style.display = 'none';
        }
    }

    // Export toggle helper to global scope for th:onclick hooks
    window.toggleFavoriteCard = function(id, buttonEl) {
        const columnEl = buttonEl.closest('.favorite-ticket');
        if (!columnEl) return;

        const titleEl = columnEl.querySelector('.favorite-title a');
        const eventTitle = titleEl ? titleEl.textContent : 'Sự kiện';

        columnEl.classList.add('removing');

            fetch(`/favourites/${id}/remove`, { method: 'POST' })
                .then(response => {
                    if (!response.ok) throw new Error('Xóa thất bại');
                    showEventToast('Đã gỡ sự kiện', `Đã hủy ghim thành công <strong>${eventTitle}</strong> khỏi danh sách yêu thích.`, 'info');
                    setTimeout(() => {
                        columnEl.remove();
                        checkEmptyState();
                    }, 400);
                })
                .catch(() => {
                    columnEl.classList.remove('removing');
                    showEventToast('Lỗi', 'Không thể gỡ sự kiện khỏi danh sách yêu thích. Vui lòng thử lại.', 'danger');
                });
        };

    // 4. Compact Real-time Custom Toast Notification Engine
    window.showEventToast = function(title, message, type = 'info') {
        let toastContainer = document.querySelector('.eventhub-toast-container');
        if (!toastContainer) {
            toastContainer = document.createElement('div');
            toastContainer.className = 'eventhub-toast-container';
            toastContainer.setAttribute('style', 'position: fixed; bottom: 2rem; right: 2rem; z-index: 9999; display: flex; flex-direction: column; gap: 0.5rem;');
            document.body.appendChild(toastContainer);
        }

        const toast = document.createElement('div');
        toast.className = 'eventhub-toast p-3 slide-in';
        toast.setAttribute('style', 'background: rgba(15, 2, 36, 0.95); border: 1px solid rgba(255, 42, 122, 0.2); border-radius: 12px; min-width: 300px; box-shadow: 0 10px 30px rgba(0,0,0,0.5); transition: all 0.3s cubic-bezier(0.175, 0.885, 0.32, 1.25); transform: translateY(50px); opacity: 0;');

        const borderAccentColors = {
            success: '#00f0ff',
            danger: '#ff2a7a',
            info: '#8c2bff'
        };

        toast.style.borderLeft = `4px solid ${borderAccentColors[type] || borderAccentColors.info}`;

        toast.innerHTML = `
            <div class="d-flex justify-content-between align-items-center mb-1">
                <strong class="text-white" style="font-family: 'Space Grotesk', sans-serif;"><i class="fa-solid fa-bell text-pink-glow me-2"></i>${title}</strong>
                <button type="button" class="btn-close btn-close-white" style="font-size: 0.75rem; background: none; border: none; color: white;" onclick="this.parentElement.parentElement.remove()">×</button>
            </div>
            <div class="text-secondary-light" style="font-size: 0.85rem; font-family: 'Outfit', sans-serif; color: #c9bfdc;">${message}</div>
        `;

        toastContainer.appendChild(toast);

        // trigger fade-in animation
        setTimeout(() => {
            toast.style.transform = 'translateY(0)';
            toast.style.opacity = '1';
        }, 30);

        // Auto destroy after 4.5 seconds
        setTimeout(() => {
            toast.style.transform = 'translateY(-20px)';
            toast.style.opacity = '0';
            setTimeout(() => {
                toast.remove();
            }, 300);
        }, 4500);
    };
});
