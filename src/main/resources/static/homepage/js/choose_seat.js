/**
 * choose_seat.js — EventHub
 * Fetch seat map từ API, render sơ đồ ghế động theo dữ liệu thật từ DB.
 * Giữ nguyên toàn bộ logic UX/CSS của bản mẫu (states, badges, pricing, maxed).
 */

// ─── Constants ───────────────────────────────────────────────────────────────

const MAX_SEATS = 6;
const SERVICE_FEE_RATE = 0.05;

// ─── State ───────────────────────────────────────────────────────────────────

let selectedSeats = []; // { seatId, rowLabel, seatNumber, zoneName, price }
let zoneConfig = [];    // TicketTypeSeatsDTO[] từ API — dùng để tra zoneName/price khi cần

// ─── Bootstrap ───────────────────────────────────────────────────────────────

document.addEventListener('DOMContentLoaded', () => {
    const root = document.getElementById('seat-map-root');
    if (!root) {
        console.error('[choose_seat] Không tìm thấy #seat-map-root — thiếu thuộc tính data-event-id.');
        return;
    }

    const eventId = root.dataset.eventId;
    if (!eventId) {
        console.error('[choose_seat] data-event-id chưa được inject vào #seat-map-root.');
        return;
    }

    loadSeatMap(eventId);

    document.getElementById('btn-submit-booking')
        .addEventListener('click', () => handleSubmit(eventId));
});

// ─── API: Fetch seat map ──────────────────────────────────────────────────────

async function loadSeatMap(eventId) {
    showLoadingState();

    try {
        const res = await fetch(`/api/events/${eventId}/seat-map`, {
            headers: { 'Accept': 'application/json' }
        });

        if (!res.ok) {
            throw new Error(`Server trả về ${res.status}`);
        }

        const zones = await res.json(); // List<TicketTypeSeatsDTO>
        zoneConfig = zones;

        clearLoadingState();
        renderAllZones(zones);
        refreshSummary();

    } catch (err) {
        console.error('[choose_seat] Lỗi load seat map:', err);
        showErrorState();
    }
}

// ─── Render ──────────────────────────────────────────────────────────────────

/**
 * Render tất cả zones từ API response.
 * Zone container trong HTML đã có sẵn: #zoneA-grid, #zoneB-grid, #zoneC-grid, ...
 * Nếu API trả nhiều hơn 3 zones thì tạo thêm container động.
 */
function renderAllZones(zones) {
    const zonesContainer = document.querySelector('.zones-container');

    zones.forEach((zone, index) => {
        const zonePrefix = String.fromCharCode(65 + index); // A, B, C, ...
        const gridId = `zone${zonePrefix}-grid`;

        // Kiểm tra grid container đã tồn tại chưa (từ HTML tĩnh)
        let gridEl = document.getElementById(gridId);

        // Nếu chưa có (zone thứ 4 trở đi) → tạo động
        if (!gridEl) {
            const zoneWrapper = buildZoneWrapper(zone, zonePrefix, gridId);
            zonesContainer.appendChild(zoneWrapper);
            gridEl = document.getElementById(gridId);
        } else {
            // Cập nhật zone title trong HTML tĩnh theo data thật
            const zoneEl = gridEl.closest('.seat-zone');
            if (zoneEl) {
                const titleEl = zoneEl.querySelector('.zone-title');
                if (titleEl) titleEl.textContent = `Khu ${zonePrefix} - ${zone.zoneName}`;
            }
        }

        renderZoneGrid(gridEl, zone, zonePrefix);
    });
}

/**
 * Tạo zone wrapper động (dùng khi API trả nhiều hơn số zone trong HTML).
 */
function buildZoneWrapper(zone, prefix, gridId) {
    const wrapper = document.createElement('div');
    wrapper.className = `seat-zone zone-${prefix.toLowerCase()}`;
    wrapper.innerHTML = `
        <div class="zone-title">Khu ${prefix} - ${zone.zoneName}</div>
        <div class="zone-grid-wrapper">
            <div class="seats-grid" id="${gridId}"></div>
        </div>
    `;
    return wrapper;
}

/**
 * Render lưới ghế cho một zone.
 * Nhóm seats theo rowLabel → vẽ từng hàng kèm label trái/phải.
 */
function renderZoneGrid(gridEl, zone, zonePrefix) {
    gridEl.innerHTML = '';

    // Nhóm theo rowLabel, giữ thứ tự A→Z
    const rowMap = new Map();
    zone.seats.forEach(seat => {
        if (!rowMap.has(seat.rowLabel)) rowMap.set(seat.rowLabel, []);
        rowMap.get(seat.rowLabel).push(seat);
    });

    // Tính số cột thực tế của zone này (để set grid-template-columns đúng)
    const maxCols = Math.max(...zone.seats.map(s => s.seatNumber));
    gridEl.style.gridTemplateColumns = `auto repeat(${maxCols}, 1fr) auto`;

    rowMap.forEach((seats, rowLabel) => {
        // Label trái
        gridEl.appendChild(buildRowLabel(rowLabel));

        // Các ghế trong hàng — sắp xếp theo seatNumber
        seats.sort((a, b) => a.seatNumber - b.seatNumber)
             .forEach(seat => {
                 gridEl.appendChild(buildSeatElement(seat, zone, zonePrefix));
             });

        // Label phải
        gridEl.appendChild(buildRowLabel(rowLabel));
    });
}

function buildRowLabel(label) {
    const el = document.createElement('div');
    el.className = 'row-label';
    el.textContent = label;
    return el;
}

/**
 * Tạo một phần tử ghế với đúng CSS class theo status từ API.
 */
function buildSeatElement(seat, zone, zonePrefix) {
    const el = document.createElement('div');
    el.className = 'seat';
    el.textContent = seat.seatNumber;

    // data attributes — seatId là PK thật từ DB
    el.dataset.seatId     = seat.seatId;
    el.dataset.zone       = zonePrefix;
    el.dataset.zoneName   = zone.zoneName;
    el.dataset.rowLabel   = seat.rowLabel;
    el.dataset.seatNumber = seat.seatNumber;
    el.dataset.price      = zone.price;

    // id dùng để tìm lại element từ badge (giữ pattern của bản gốc)
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

        case 'AVAILABLE':
        default:
            el.classList.add('seat--available');
            el.title = `Ghế trống — Giá: ${formatVND(Number(zone.price))}`;
            el.addEventListener('click', () => handleSeatClick(el));
            break;
    }

    return el;
}

// ─── Seat Click Handler ───────────────────────────────────────────────────────

function handleSeatClick(seatEl) {
    // Guard: sold/locked không làm gì
    if (seatEl.classList.contains('seat--sold') ||
        seatEl.classList.contains('seat--locked')) return;

    const seatId     = Number(seatEl.dataset.seatId);
    const zoneName   = seatEl.dataset.zoneName;
    const rowLabel   = seatEl.dataset.rowLabel;
    const seatNumber = Number(seatEl.dataset.seatNumber);
    const price      = Number(seatEl.dataset.price);

    if (seatEl.classList.contains('seat--selected')) {
        // Bỏ chọn
        seatEl.classList.remove('seat--selected');
        seatEl.classList.add('seat--available');
        selectedSeats = selectedSeats.filter(s => s.seatId !== seatId);

        if (selectedSeats.length < MAX_SEATS) {
            clearMaxedState();
        }

    } else if (seatEl.classList.contains('seat--available')) {
        // Kiểm tra giới hạn
        if (selectedSeats.length >= MAX_SEATS) {
            alert(`Bạn chỉ có thể chọn tối đa ${MAX_SEATS} ghế cho sự kiện này.`);
            return;
        }

        // Chọn ghế
        seatEl.classList.remove('seat--available');
        seatEl.classList.add('seat--selected');
        selectedSeats.push({ seatId, zoneName, rowLabel, seatNumber, price });

        if (selectedSeats.length === MAX_SEATS) {
            applyMaxedState();
        }
    }

    refreshSummary();
}

// ─── Maxed State ─────────────────────────────────────────────────────────────

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

// ─── Summary Sidebar ─────────────────────────────────────────────────────────

function refreshSummary() {
    const count    = selectedSeats.length;
    const subtotal = selectedSeats.reduce((sum, s) => sum + s.price, 0);
    const fee      = Math.round(subtotal * SERVICE_FEE_RATE);
    const total    = subtotal + fee;

    document.getElementById('tickets-count').textContent       = count;
    document.getElementById('subtotal-calc').textContent       = formatVND(subtotal);
    document.getElementById('fee-calc').textContent            = formatVND(fee);
    document.getElementById('total-calc').textContent          = formatVND(total);
    document.getElementById('selected-summary-count').textContent = `${count}/${MAX_SEATS}`;

    const badgesContainer = document.getElementById('selected-seats-badges');
    const emptyHint       = document.getElementById('empty-seats-hint');
    const btnSubmit       = document.getElementById('btn-submit-booking');

    // Xóa badges cũ
    badgesContainer.querySelectorAll('.selected-seat-badge').forEach(b => b.remove());

    if (count === 0) {
        emptyHint.style.display = 'block';
        btnSubmit.disabled = true;
    } else {
        emptyHint.style.display = 'none';
        btnSubmit.disabled = false;

        selectedSeats.forEach(s => {
            badgesContainer.appendChild(buildSeatBadge(s));
        });
    }
}

function buildSeatBadge(seat) {
    const badge = document.createElement('div');
    badge.className = 'selected-seat-badge';

    const label = document.createElement('span');
    // Hiển thị: "A3 (VIP)" — dùng rowLabel + seatNumber thay vì string ghép tay
    label.textContent = `${seat.rowLabel}${seat.seatNumber} (${abbrZone(seat.zoneName)})`;

    const closeIcon = document.createElement('i');
    closeIcon.className = 'fa-solid fa-xmark';
    closeIcon.addEventListener('click', () => {
        const seatEl = document.getElementById(`seat-${seat.seatId}`);
        if (seatEl) {
            handleSeatClick(seatEl);
        } else {
            // Fallback nếu DOM không tìm được
            selectedSeats = selectedSeats.filter(s => s.seatId !== seat.seatId);
            if (selectedSeats.length < MAX_SEATS) clearMaxedState();
            refreshSummary();
        }
    });

    badge.appendChild(label);
    badge.appendChild(closeIcon);
    return badge;
}

// ─── Submit: Lock seats → redirect checkout ───────────────────────────────────

async function handleSubmit(eventId) {
    if (selectedSeats.length === 0) return;

    const btnSubmit = document.getElementById('btn-submit-booking');
    btnSubmit.disabled = true;
    btnSubmit.textContent = 'Đang xử lý...';

    try {
        const res = await fetch('/api/seats/lock', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({
                eventId: Number(eventId),
                seatIds: selectedSeats.map(s => s.seatId)
            })
        });

        if (res.status === 409) {
            // Một hoặc nhiều ghế vừa bị người khác chiếm
            const data = await res.json();
            alert(`Một số ghế vừa được đặt bởi người khác:\n${data.conflictedSeats?.join(', ') ?? ''}\n\nSơ đồ sẽ được cập nhật lại.`);
            // Reload lại seat map để reflect trạng thái mới nhất
            selectedSeats = [];
            await loadSeatMap(eventId);
            return;
        }

        if (!res.ok) {
            throw new Error(`Lock thất bại: ${res.status}`);
        }

        // Thành công → redirect sang trang checkout
        window.location.href = `/checkout?eventId=${eventId}`;

    } catch (err) {
        console.error('[choose_seat] Lỗi khi lock ghế:', err);
        alert('Có lỗi xảy ra khi đặt ghế. Vui lòng thử lại.');
        btnSubmit.disabled = false;
        btnSubmit.innerHTML = 'Tiếp tục thanh toán <i class="fa-solid fa-chevron-right ms-1"></i>';
    }
}

// ─── Loading / Error States ───────────────────────────────────────────────────

function showLoadingState() {
    document.querySelectorAll('.seats-grid').forEach(grid => {
        grid.innerHTML = `
            <div style="grid-column: 1 / -1; padding: 40px; text-align: center; color: var(--color-text-muted); font-size: 13px;">
                <i class="fa-solid fa-spinner fa-spin me-2"></i> Đang tải sơ đồ ghế...
            </div>
        `;
    });
}

function clearLoadingState() {
    document.querySelectorAll('.seats-grid').forEach(grid => {
        grid.innerHTML = '';
    });
}

function showErrorState() {
    document.querySelectorAll('.seats-grid').forEach(grid => {
        grid.innerHTML = `
            <div style="grid-column: 1 / -1; padding: 40px; text-align: center; color: var(--color-danger); font-size: 13px;">
                <i class="fa-solid fa-circle-exclamation me-2"></i> Không thể tải sơ đồ ghế. Vui lòng thử lại.
            </div>
        `;
    });
}

// ─── Helpers ─────────────────────────────────────────────────────────────────

function abbrZone(name) {
    if (!name) return '?';
    const upper = name.toUpperCase();
    if (upper.includes('VIP'))      return 'VIP';
    if (upper.includes('STANDARD')) return 'STD';
    if (upper.includes('ECONOMY'))  return 'ECO';
    // Fallback: lấy 3 ký tự đầu
    return name.substring(0, 3).toUpperCase();
}

function formatVND(number) {
    return Number(number).toLocaleString('vi-VN') + ' ₫';
}