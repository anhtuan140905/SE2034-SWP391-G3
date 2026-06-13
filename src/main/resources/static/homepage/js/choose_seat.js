    const CONFIG = {
        zoneA: {
            name: "Khu A - VIP",
            price: 500000,
            rows: 8,
            cols: 10,
            soldRows: [1, 3, 5, 7],
            soldSeatsPerRow: {
                2: [1, 2, 3],
                4: [1, 2, 3],
                6: [1, 2, 3],
                8: [1, 2, 3]
            }
        },
        zoneB: {
            name: "Khu B - Standard",
            price: 300000,
            rows: 8,
            cols: 10,
            soldRows: [1, 2], // rows 1 and 2 fully sold
            customStates: {
                // Row 3 specials: 1-2 sold, 3 selected, 4 locked
                3: {
                    sold: [1, 2],
                    selected: [3],
                    locked: [4]
                }
            },
            // Intermittent alternating sold/available pattern for rows 4 to 8
            intermittentRowSold: {
                4: [2, 4, 6, 8, 10],
                5: [1, 3, 5, 7, 9],
                6: [2, 5, 7, 8],
                7: [2, 3, 7, 8],
                8: [1, 4, 6, 9]
            }
        },
        zoneC: {
            name: "Khu C - Economy",
            price: 150000,
            rows: 8,
            cols: 10,
            randomSoldRatio: 0.15 // Sparse random sells
        }
    };

    // Track selected state
    let selectedSeats = [];
    const MAX_SEATS = 6;

    // Initialize state mapping
    function initGrids() {
        // Render Zone A (VIP)
        renderZone('zoneA-grid', 'A', CONFIG.zoneA);
        // Render Zone B (Standard)
        renderZone('zoneB-grid', 'B', CONFIG.zoneB);
        // Render Zone C (Economy)
        renderZone('zoneC-grid', 'C', CONFIG.zoneC);

        // Trigger calc to show initial pre-selected seat B3
        refreshSummary();
    }

    function renderZone(gridId, prefix, zoneConf) {
        const gridEl = document.getElementById(gridId);
        if (!gridEl) return;

        gridEl.innerHTML = '';

        for (let r = 1; r <= zoneConf.rows; r++) {
            // Determine row label
            const rowLabel = String.fromCharCode(64 + r); // A, B, C, D...

            // Append Left Row Guideline Label
            const leftLabel = document.createElement('div');
            leftLabel.className = 'row-label';
            leftLabel.innerText = rowLabel;
            gridEl.appendChild(leftLabel);

            for (let c = 1; c <= zoneConf.cols; c++) {
                const seatId = `${prefix}-${rowLabel}${c}`;
                const seatEl = document.createElement('div');
                seatEl.classList.add('seat');
                seatEl.innerText = c;
                seatEl.setAttribute('id', `seat-${seatId}`);
                seatEl.setAttribute('data-id', seatId);
                seatEl.setAttribute('data-zone', prefix);
                seatEl.setAttribute('data-row', rowLabel);
                seatEl.setAttribute('data-num', c);
                seatEl.setAttribute('data-price', zoneConf.price);

                let state = 'available';

                // Apply states based on rules
                if (prefix === 'A') {
                    // VIP RULES
                    if (zoneConf.soldRows.includes(r)) {
                        state = 'sold';
                    } else if (zoneConf.soldSeatsPerRow[r] && zoneConf.soldSeatsPerRow[r].includes(c)) {
                        state = 'sold';
                    }
                } else if (prefix === 'B') {
                    // Standard RULES
                    if (zoneConf.soldRows.includes(r)) {
                        state = 'sold';
                    } else if (zoneConf.customStates[r]) {
                        const spec = zoneConf.customStates[r];
                        if (spec.sold && spec.sold.includes(c)) state = 'sold';
                        else if (spec.selected && spec.selected.includes(c)) state = 'selected';
                        else if (spec.locked && spec.locked.includes(c)) state = 'locked';
                    } else if (zoneConf.intermittentRowSold[r] && zoneConf.intermittentRowSold[r].includes(c)) {
                        state = 'sold';
                    }
                } else if (prefix === 'C') {
                    // Economy RULES: scattered random sold seats
                    // Use deterministic hash so it looks cohesive on reload
                    const hashVal = (r * 13 + c * 7) % 100;
                    if (hashVal < 20) {
                        state = 'sold';
                    }
                }

                // Apply layout state
                if (state === 'sold') {
                    seatEl.classList.add('seat--sold');
                    seatEl.setAttribute('title', 'Ghế đã bán');
                } else if (state === 'locked') {
                    seatEl.classList.add('seat--locked');
                    seatEl.setAttribute('title', 'Ghế đang được giữ');
                } else if (state === 'selected') {
                    seatEl.classList.add('seat--selected');
                    seatEl.setAttribute('title', 'Ghế do bạn chọn');
                    // Track it
                    selectedSeats.push({
                        id: seatId,
                        zoneName: zoneConf.name,
                        price: zoneConf.price,
                        row: rowLabel,
                        col: c
                    });
                } else {
                    seatEl.classList.add('seat--available');
                    seatEl.setAttribute('title', `Ghế trống - Giá: ${formatVND(zoneConf.price)}`);
                    // Set listener
                    seatEl.addEventListener('click', () => handleSeatClick(seatEl));
                }

                gridEl.appendChild(seatEl);
            }

            // Append Right Row Guideline Label
            const rightLabel = document.createElement('div');
            rightLabel.className = 'row-label';
            rightLabel.innerText = rowLabel;
            gridEl.appendChild(rightLabel);
        }
    }

    // Click handler for available/selected seats
    function handleSeatClick(seatEl) {
        const seatId = seatEl.getAttribute('data-id');
        const zonePrefix = seatEl.getAttribute('data-zone');
        const row = seatEl.getAttribute('data-row');
        const col = parseInt(seatEl.getAttribute('data-num'));
        const price = parseInt(seatEl.getAttribute('data-price'));

        // Look up zone name
        const zoneName = zonePrefix === 'A' ? CONFIG.zoneA.name : (zonePrefix === 'B' ? CONFIG.zoneB.name : CONFIG.zoneC.name);

        if (seatEl.classList.contains('seat--sold') || seatEl.classList.contains('seat--locked')) {
            return; // Do nothing
        }

        if (seatEl.classList.contains('seat--selected')) {
            // Deselect
            seatEl.classList.remove('seat--selected');
            seatEl.classList.add('seat--available');
            selectedSeats = selectedSeats.filter(s => s.id !== seatId);

            // Clear state restriction if it was maxed
            if (selectedSeats.length < MAX_SEATS) {
                clearMaxedState();
            }
        } else if (seatEl.classList.contains('seat--available')) {
            // Check limit
            if (selectedSeats.length >= MAX_SEATS) {
                alert(`Bạn chỉ có thể chọn tối đa ${MAX_SEATS} ghế cho sự kiện này.`);
                return;
            }

            // Select
            seatEl.classList.remove('seat--available');
            seatEl.classList.add('seat--selected');
            selectedSeats.push({
                id: seatId,
                zoneName: zoneName,
                price: price,
                row: row,
                col: col
            });

            // Set maxed restriction if limit met
            if (selectedSeats.length === MAX_SEATS) {
                applyMaxedState();
            }
        }

        refreshSummary();
    }

    // Apply maxed state style to all available seats
    function applyMaxedState() {
        const availableSeats = document.querySelectorAll('.seat--available');
        availableSeats.forEach(seat => {
            seat.classList.add('seat--maxed');
            seat.classList.remove('seat--available');
        });
    }

    // Revert maxed state style so users can click again
    function clearMaxedState() {
        const maxedSeats = document.querySelectorAll('.seat--maxed');
        maxedSeats.forEach(seat => {
            seat.classList.remove('seat--maxed');
            seat.classList.add('seat--available');
        });
    }

    // Calculate pricing and refresh invoices
    function refreshSummary() {
        const count = selectedSeats.length;

        // Subtotal
        let subtotal = 0;
        selectedSeats.forEach(s => subtotal += s.price);

        // 5% service fee
        const serviceFee = Math.round(subtotal * 0.05);
        const total = subtotal + serviceFee;

        // Update DOM
        document.getElementById('tickets-count').innerText = count;
        document.getElementById('subtotal-calc').innerText = formatVND(subtotal);
        document.getElementById('fee-calc').innerText = formatVND(serviceFee);
        document.getElementById('total-calc').innerText = formatVND(total);

        // Counter
        document.getElementById('selected-summary-count').innerText = `${count}/${MAX_SEATS}`;

        // Badges mapping
        const badgesContainer = document.getElementById('selected-seats-badges');
        const emptyHint = document.getElementById('empty-seats-hint');

        if (count === 0) {
            emptyHint.style.display = 'block';
            // Clear existing badge items except hint
            const badges = badgesContainer.querySelectorAll('.selected-seat-badge');
            badges.forEach(b => b.remove());

            // Disable CTA button
            document.getElementById('btn-submit-booking').disabled = true;
        } else {
            emptyHint.style.display = 'none';
            // Remove older badge items
            const badges = badgesContainer.querySelectorAll('.selected-seat-badge');
            badges.forEach(b => b.remove());

            // Build new badges
            selectedSeats.forEach(s => {
                const badge = document.createElement('div');
                badge.classList.add('selected-seat-badge');

                const text = document.createElement('span');
                text.innerText = `${s.id} (${abbrZone(s.zoneName)})`;

                const closeIcon = document.createElement('i');
                closeIcon.className = 'fa-solid fa-xmark';
                closeIcon.addEventListener('click', () => {
                    const seatDom = document.getElementById(`seat-${s.id}`);
                    if (seatDom) {
                        handleSeatClick(seatDom);
                    } else {
                        // Fallback if not rendered
                        selectedSeats = selectedSeats.filter(item => item.id !== s.id);
                        if (selectedSeats.length < MAX_SEATS) clearMaxedState();
                        refreshSummary();
                    }
                });

                badge.appendChild(text);
                badge.appendChild(closeIcon);
                badgesContainer.appendChild(badge);
            });

            // Enable CTA button
            document.getElementById('btn-submit-booking').disabled = false;
        }
    }

    // Abbreviate zone representation
    function abbrZone(name) {
        if (name.includes('VIP')) return 'VIP';
        if (name.includes('Standard')) return 'STD';
        if (name.includes('Economy')) return 'ECO';
        return name;
    }

    // Format raw currency with commas and currency abbreviation
    function formatVND(number) {
        return number.toLocaleString('vi-VN') + ' ₫';
    }

    // Submit action trigger
    document.getElementById('btn-submit-booking').addEventListener('click', () => {
        if (selectedSeats.length === 0) return;

        const seatsListStr = selectedSeats.map(s => s.id).join(', ');
        alert(`XÁC NHẬN ĐẶT CHỖ THÀNH CÔNG!\n\nBạn đã chọn ${selectedSeats.length} ghế: [${seatsListStr}].\nHệ thống sẽ chuyển tiếp sang cổng thanh toán với giá trị hóa đơn là ${document.getElementById('total-calc').innerText}.`);
    });

    // Initialize grid layouts on startup
    document.addEventListener('DOMContentLoaded', () => {
        initGrids();
    });