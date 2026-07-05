const seatMapRoot = document.getElementById('seat-map-root');
const limitReached = seatMapRoot.dataset.limitReached === 'true';

const MAX_SEATS = 3;
let selectedSeats = [];
let zoneConfig = [];

document.addEventListener('DOMContentLoaded', () => {
    const root = document.getElementById('seat-map-root');
    if (!root) return;

    const eventId = root.dataset.eventId;
    if (!eventId) return;

    loadSeatMap(eventId);

    document.getElementById('btn-submit-booking')
        .addEventListener('click', () => handleSubmit(eventId));

});

async function loadSeatMap(eventId) {
    showLoadingState();
    try {
        const res = await fetch(`/api/events/${eventId}/seat-map`, {
            headers: { 'Accept': 'application/json' }
        });
        if (!res.ok) throw new Error(`Server trả về ${res.status}`);

        const zones = await res.json();
        zoneConfig = zones;

        // THÊM MỚI: đồng bộ lại selectedSeats dựa trên dữ liệu thật từ server
        // Quan trọng khi: F5 trang, quay lại choose-seat sau khi đã lock 1 số ghế từ trước
        selectedSeats = [];
        zones.forEach(zone => {
            zone.seats.forEach(seat => {
                if (seat.status === 'SELECTED_BY_ME') {
                    selectedSeats.push({
                        seatId: seat.seatId,
                        zoneName: zone.zoneName,
                        rowLabel: seat.rowLabel,
                        seatNumber: seat.seatNumber,
                        price: Number(zone.price)
                    });
                }
            });
        });

        clearLoadingState();
        renderAllZones(zones);

        // Nếu đã đạt MAX_SEATS từ trước (ví dụ lock 3 ghế rồi F5), phải khóa các ghế available còn lại
        if (selectedSeats.length >= MAX_SEATS) applyMaxedState();

        refreshSummary();
    } catch (err) {
        console.error('[choose_seat] Lỗi load seat map:', err);
        showErrorState();
    }
}

function renderAllZones(zones) {
    const zonesContainer = document.querySelector('.zones-container');
    zones.forEach((zone, index) => {
        const zonePrefix = String.fromCharCode(65 + index);
        const gridId = `zone${zonePrefix}-grid`;
        let gridEl = document.getElementById(gridId);

        if (!gridEl) {
            const zoneWrapper = buildZoneWrapper(zone, zonePrefix, gridId);
            zonesContainer.appendChild(zoneWrapper);
            gridEl = document.getElementById(gridId);
        } else {
            const zoneEl = gridEl.closest('.seat-zone');
            if (zoneEl) {
                const titleEl = zoneEl.querySelector('.zone-title');
                if (titleEl) titleEl.textContent = `Khu ${zonePrefix} - ${zone.zoneName}`;
            }
        }
        renderZoneGrid(gridEl, zone, zonePrefix);
    });
}

function buildZoneWrapper(zone, prefix, gridId) {
    const wrapper = document.createElement('div');
    wrapper.className = `seat-zone zone-${prefix.toLowerCase()}`;
    wrapper.innerHTML = `
        <div class="zone-title">Khu ${prefix} - ${zone.zoneName}</div>
        <div class="zone-grid-wrapper"><div class="seats-grid" id="${gridId}"></div></div>
    `;
    return wrapper;
}

function renderZoneGrid(gridEl, zone, zonePrefix) {
    gridEl.innerHTML = '';
    const rowMap = new Map();
    zone.seats.forEach(seat => {
        if (!rowMap.has(seat.rowLabel)) rowMap.set(seat.rowLabel, []);
        rowMap.get(seat.rowLabel).push(seat);
    });

    const maxCols = Math.max(...zone.seats.map(s => s.seatNumber));
    gridEl.style.gridTemplateColumns = `auto repeat(${maxCols}, 1fr) auto`;

    rowMap.forEach((seats, rowLabel) => {
        gridEl.appendChild(buildRowLabel(rowLabel));
        seats.sort((a, b) => a.seatNumber - b.seatNumber).forEach(seat => {
            gridEl.appendChild(buildSeatElement(seat, zone, zonePrefix));
        });
        gridEl.appendChild(buildRowLabel(rowLabel));
    });
}

function buildRowLabel(label) {
    const el = document.createElement('div');
    el.className = 'row-label';
    el.textContent = label;
    return el;
}

function buildSeatElement(seat, zone, zonePrefix) {
    const el = document.createElement('div');
    el.className = 'seat';
    el.textContent = seat.seatNumber;

    el.dataset.seatId     = seat.seatId;
    el.dataset.zone       = zonePrefix;
    el.dataset.zoneName   = zone.zoneName;
    el.dataset.rowLabel   = seat.rowLabel;
    el.dataset.seatNumber = seat.seatNumber;
    el.dataset.price      = zone.price;
    el.id = `seat-${seat.seatId}`;

    switch (seat.status) {
        case 'SOLD':
            el.classList.add('seat--sold');
            el.title = 'Ghế đã bán';
            break;
        case 'LOCKED':
            el.classList.add('seat--locked');
            el.title = 'Ghế đang được giữ bởi người khác';
            break;
        case 'SELECTED_BY_ME': // THÊM MỚI: ghế mình đang giữ từ trước (reload trang, quay lại choose-seat...)
            el.classList.add('seat--selected');
            el.title = `Ghế bạn đang giữ — Giá: ${formatVND(Number(zone.price))}`;

            el.addEventListener('click', (e) => {
                e.preventDefault();
                e.stopPropagation();
                handleSeatClick(el); // tái sử dụng luôn logic unlock đã có, vì nó check class 'seat--selected'
            });
            break;
        case 'AVAILABLE':
        default:
            el.classList.add('seat--available');
            el.title = `Ghế trống — Giá: ${formatVND(Number(zone.price))}`;

            el.addEventListener('click', (e) => {
                e.preventDefault();
                e.stopPropagation();
                handleSeatClick(el);
            });
            break;
    }
    return el;
}

async function handleSeatClick(seatEl) {

    if (seatEl.classList.contains('seat--sold') || seatEl.classList.contains('seat--locked')) return;

        if (!isAuthenticated) {
            showToast('Bạn cần đăng nhập trước khi chọn vé.', 'warning');
            return;
        }

        if (seatEl.classList.contains('seat--maxed')) {
            showToast(`Bạn chỉ có thể chọn tối đa ${MAX_SEATS} ghế.`);
            return;
        }

    if (seatEl.classList.contains('seat--sold') || seatEl.classList.contains('seat--locked')) return;
    if (seatEl.classList.contains('seat--maxed')) {
            showToast(`Bạn chỉ có thể chọn tối đa ${MAX_SEATS} ghế.`);
            return;
        }
    const seatId = Number(seatEl.dataset.seatId);
    const eventId = document.getElementById('seat-map-root').dataset.eventId;

    if (seatEl.classList.contains('seat--selected')) {
        try {
            const params = new URLSearchParams();
            params.append('seatId', seatId);
            params.append('action', 'UNLOCK');

            const res = await fetch('/checkout/toggle-lock', {
                method: 'POST',
                headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
                body: params.toString()
            });

            if (res.ok) {
                seatEl.classList.remove('seat--selected');
                seatEl.classList.add('seat--available');
                selectedSeats = selectedSeats.filter(s => s.seatId !== seatId);
                if (selectedSeats.length < MAX_SEATS) clearMaxedState();
                refreshSummary();
            }
        } catch (err) {
            console.error("Không thể hủy giữ ghế:", err);
        }
    } else if (seatEl.classList.contains('seat--available')) {
          // Guard limit reached (đã mua đủ 3 vé từ trước)
          if (limitReached) {
              showToast('Bạn đã mua tối đa 3 vé cho sự kiện này.', 'warning');
              return;
          }
          // Guard max seats trong phiên chọn hiện tại
          if (selectedSeats.length >= MAX_SEATS) {
              showToast(`Bạn chỉ có thể chọn tối đa ${MAX_SEATS} ghế.`, 'warning');
              return;
          }

        try {
            const params = new URLSearchParams();
            params.append('seatId', seatId);
            params.append('action', 'LOCK');

            const res = await fetch('/checkout/toggle-lock', {
                method: 'POST',
                headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
                body: params.toString()
            });

            if (res.ok) {
                seatEl.classList.remove('seat--available');
                seatEl.classList.add('seat--selected');
                selectedSeats.push({
                    seatId,
                    zoneName: seatEl.dataset.zoneName,
                    rowLabel: seatEl.dataset.rowLabel,
                    seatNumber: Number(seatEl.dataset.seatNumber),
                    price: Number(seatEl.dataset.price)
                });

                if (selectedSeats.length === MAX_SEATS) applyMaxedState();
                refreshSummary();
            } else {
                const errorMsg = await res.text();
                alert(errorMsg.includes("html") ? "Ghế này vừa bị người khác chọn mất!" : errorMsg);
                await loadSeatMap(eventId);
            }
        } catch (err) {
            console.error("Lỗi kết nối:", err);
        }
    }
}

function applyMaxedState() {
    document.querySelectorAll('.seat--available').forEach(el => {
        el.classList.replace('seat--available', 'seat--maxed');
    });
}

function clearMaxedState() {
    document.querySelectorAll('.seat--maxed').forEach(el => {
        el.classList.replace('seat--maxed', 'seat--available');
    });
}

function refreshSummary() {
    const count    = selectedSeats.length;
    const subtotal = selectedSeats.reduce((sum, s) => sum + s.price, 0);

    document.getElementById('tickets-count').textContent       = count;
    document.getElementById('subtotal-calc').textContent       = formatVND(subtotal);
    document.getElementById('total-calc').textContent          = formatVND(subtotal);
    document.getElementById('selected-summary-count').textContent = `${count}/${MAX_SEATS}`;

    const badgesContainer = document.getElementById('selected-seats-badges');
    const emptyHint       = document.getElementById('empty-seats-hint');
    const btnSubmit       = document.getElementById('btn-submit-booking');

    badgesContainer.querySelectorAll('.selected-seat-badge').forEach(b => b.remove());

    if (count === 0) {
        emptyHint.style.display = 'block';
        btnSubmit.disabled = true;
    } else {
        emptyHint.style.display = 'none';
        btnSubmit.disabled = false;
        selectedSeats.forEach(s => { badgesContainer.appendChild(buildSeatBadge(s)); });
    }
}

function buildSeatBadge(seat) {
    const badge = document.createElement('div');
    badge.className = 'selected-seat-badge';

    const label = document.createElement('span');
    label.textContent = `${seat.rowLabel}${seat.seatNumber} (${abbrZone(seat.zoneName)})`;

    const closeIcon = document.createElement('i');
    closeIcon.className = 'fa-solid fa-xmark';
    closeIcon.addEventListener('click', (e) => {
        e.preventDefault();
        e.stopPropagation();
        const seatEl = document.getElementById(`seat-${seat.seatId}`);
        if (seatEl) handleSeatClick(seatEl);
    });

    badge.appendChild(label);
    badge.appendChild(closeIcon);
    return badge;
}

async function handleSubmit(eventId) {
    if (selectedSeats.length === 0) return;

    const btnSubmit = document.getElementById('btn-submit-booking');
    btnSubmit.disabled = true;
    btnSubmit.textContent = 'Đang xử lý...';

    try {
        const formData = new URLSearchParams();
        selectedSeats.forEach(s => { formData.append('seatIds', s.seatId); });

        const res = await fetch('/checkout/proceed', {
            method: 'POST',
            headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
            body: formData.toString()
        });

        if (res.ok) {
            const htmlContent = await res.text();
            if (htmlContent.includes('Tóm tắt đơn hàng') || res.url.includes('/checkout/')) {
                window.location.href = res.url;
            } else {
                alert('Có lỗi bất ngờ xảy ra, vui lòng chọn lại ghế.');
                selectedSeats = [];
                await loadSeatMap(eventId);
            }
        } else {
            throw new Error(`Mã lỗi: ${res.status}`);
        }
    } catch (err) {
        console.error('[choose_seat] Lỗi luồng:', err);
        alert('Ghế của bạn đã hết hạn giữ hoặc bị trùng lặp. Sơ đồ sẽ được làm mới!');
        selectedSeats = [];
        await loadSeatMap(eventId);
    } finally {
        btnSubmit.disabled = false;
        btnSubmit.innerHTML = 'Tiếp tục thanh toán <i class="fa-solid fa-chevron-right ms-1"></i>';
    }
}

function showLoadingState() {
    document.querySelectorAll('.seats-grid').forEach(grid => {
        grid.innerHTML = `<div style="grid-column: 1 / -1; padding: 40px; text-align: center; color: var(--color-text-muted); font-size: 13px;"><i class="fa-solid fa-spinner fa-spin me-2"></i> Đang tải sơ đồ ghế...</div>`;
    });
}

function clearLoadingState() {
    document.querySelectorAll('.seats-grid').forEach(grid => { grid.innerHTML = ''; });
}

function showErrorState() {
    document.querySelectorAll('.seats-grid').forEach(grid => {
        grid.innerHTML = `<div style="grid-column: 1 / -1; padding: 40px; text-align: center; color: var(--color-danger); font-size: 13px;"><i class="fa-solid fa-circle-exclamation me-2"></i> Không thể tải sơ đồ ghế. Vui lòng thử lại.</div>`;
    });
}

function abbrZone(name) {
    if (!name) return '?';
    const upper = name.toUpperCase();
    if (upper.includes('VIP'))      return 'VIP';
    if (upper.includes('STANDARD')) return 'STD';
    if (upper.includes('ECONOMY'))  return 'ECO';
    return name.substring(0, 3).toUpperCase();
}

function formatVND(number) {
    return Number(number).toLocaleString('vi-VN') + ' ₫';
}

function showToast(message, type = 'warning') {
    // Tạo container nếu chưa có
    let container = document.getElementById('toast-container');
    if (!container) {
        container = document.createElement('div');
        container.id = 'toast-container';
        container.style.cssText = `
            position: fixed;
            bottom: 24px;
            right: 24px;
            z-index: 9999;
            display: flex;
            flex-direction: column;
            gap: 8px;
        `;
        document.body.appendChild(container);
    }

    const toast = document.createElement('div');
    toast.style.cssText = `
        background: #fff3cd;
        color: #856404;
        border: 1px solid #ffc107;
        border-radius: 8px;
        padding: 12px 16px;
        font-size: 14px;
        box-shadow: 0 4px 12px rgba(0,0,0,0.15);
        display: flex;
        align-items: center;
        gap: 8px;
        min-width: 280px;
        max-width: 360px;
        animation: slideIn 0.3s ease;
    `;

    toast.innerHTML = `
        <i class="fa-solid fa-triangle-exclamation"></i>
        <span>${message}</span>
    `;

    container.appendChild(toast);

    // Tự xóa sau 3 giây
    setTimeout(() => {
        toast.style.animation = 'fadeOut 0.3s ease forwards';
        setTimeout(() => toast.remove(), 300);
    }, 3000);
}

