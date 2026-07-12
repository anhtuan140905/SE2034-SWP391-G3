document.addEventListener('DOMContentLoaded', () => {
    const triggerRecBtn = document.getElementById('trigger-rec-btn');
    const loadingState = document.getElementById('loading-state');

    // --- ĐÃ SỬA: Lấy đúng các phần tử theo ID từ HTML ---
    const resultsWrapper = document.getElementById('results-state'); // Thẻ cha bao bọc toàn bộ kết quả
    const aiReasoningText = document.getElementById('ai-reasoning-text'); // Thẻ chữ nhận định của AI
    const aiBox = document.querySelector('.ai-analysis-box');
    const recommendationsContainer = document.getElementById('recommendations-container');

    const formatDateStr = (dateTimeStr) => {
        try {
            return new Date(dateTimeStr).toLocaleDateString('vi-VN', {
                weekday: 'long',
                year: 'numeric',
                month: 'long',
                day: 'numeric'
            });
        } catch (e) {
            return dateTimeStr;
        }
    };

    const formatTimeStr = (dateTimeStr) => {
        try {
            return new Date(dateTimeStr).toLocaleTimeString('vi-VN', {
                hour: '2-digit',
                minute: '2-digit'
            });
        } catch (e) {
            return '';
        }
    };

    const getCalendarParts = (dateTimeStr) => {
        try {
            const d = new Date(dateTimeStr);
            const months = ['Tháng 1','Tháng 2','Tháng 3','Tháng 4','Tháng 5','Tháng 6','Tháng 7','Tháng 8','Tháng 9','Tháng 10','Tháng 11','Tháng 12'];
            return {
                month: months[d.getMonth()],
                day:   String(d.getDate()).padStart(2, '0')
            };
        } catch (e) {
            return { month: 'Tháng 1', day: '25' };
        }
    };

    const formatPrice = (minPrice) => {
        if (!minPrice || minPrice <= 0) return 'Miễn phí';
        return Math.floor(minPrice).toLocaleString('vi-VN') + ' ₫';
    };

    const buildCard = (item, index) => {
        const cal         = getCalendarParts(item.startTime);
        const displayDate = formatDateStr(item.startTime);
        const displayTime = formatTimeStr(item.startTime);
        const price       = formatPrice(item.minPrice);

        const buyButton = `
            <a href="/events/${item.eventId}/choose_seat"
               class="btn mt-3"
               style="background: linear-gradient(to right, #ff2a7a, #8c2bff); color: white; font-weight: 600; border-radius: 12px; padding: 8px 20px; font-size: 0.85rem; align-self: flex-start;">
                Mua vé ngay <i class="fa-solid fa-chevron-right ms-1"></i>
            </a>`;

        const timeBlock = displayTime
            ? `<div class="col-sm-6"><span><i class="fa-solid fa-clock text-brand-pink me-2"></i> Bắt đầu lúc ${displayTime}</span></div>`
            : '';

        const reasonBlock = item.reason
            ? `<div class="p-3 rounded-3 border-start border-3 mt-3"
                    style="border-color: #7c3aed !important; background: rgba(124, 58, 237, 0.1);">
                   <p class="mb-0 text-white" style="font-size: 0.8rem; line-height: 1.6;">
                       <span class="text-brand-pink fw-bold d-block mb-1">Tại sao phù hợp với bạn:</span>
                       <span class="fst-italic" style="color: rgba(255,255,255,0.85);">${item.reason}</span>
                   </p>
               </div>`
            : '';

        return `
            <div class="col-12 mb-4">
                <div class="card rec-card border-0 text-white p-0 d-flex flex-column flex-md-row position-relative"
                     style="background: rgba(30,41,59,0.45); backdrop-filter: blur(20px);
                            border: 1px solid rgba(255,255,255,0.08) !important;
                            min-height: 250px; border-radius: 24px;">

                    <div class="rec-card-image-wrapper" style="position: relative; width: 35%; min-height: 250px; overflow: hidden; border-radius: 24px 0 0 24px; flex-shrink: 0;">
                        <img src="${item.thumbnailUrl || 'https://images.unsplash.com/photo-1492684223066-81342ee5ff30?w=600'}"
                             alt="${item.title}"
                             style="position: absolute; inset: 0; width: 100%; height: 100%; object-fit: cover; transition: transform 0.5s ease;"
                             onerror="this.src='https://images.unsplash.com/photo-1492684223066-81342ee5ff30?w=600'">
                        <div style="position: absolute; inset: 0; background: linear-gradient(to right, rgba(0,0,0,0.3), rgba(30,41,59,0.5));"></div>

                        <div style="position: absolute; top: 15px; left: 15px; z-index: 10;">
                            <span class="badge text-white"
                                  style="background: linear-gradient(to right, #ff2a7a, #8c2bff);
                                         font-family: monospace; font-size: 11px;
                                         padding: 6px 12px; border-radius: 20px;">
                                <i class="fa-solid fa-star"></i> TOP ${index + 1}
                            </span>
                        </div>

                        <div style="position: absolute; bottom: 15px; left: 15px; z-index: 10;">
                            <span class="badge text-white"
                                  style="background: #ec4899; font-size: 9px;
                                         padding: 4px 10px; border-radius: 20px;">
                                ${item.categoryName || 'Sự kiện'}
                            </span>
                        </div>
                    </div>

                    <div class="d-none d-md-block position-absolute"
                         style="left: 35%; top: 0; bottom: 0; width: 0; pointer-events: none; z-index: 20;">
                        <div style="position: absolute; top: 0; bottom: 0; left: 0;
                                    border-left: 3px dashed rgba(255,255,255,0.2);
                                    transform: translateX(-50%); height: 100%;"></div>
                        <div style="position: absolute; top: 0; left: 0;
                                    transform: translate(-50%, -50%);
                                    width: 24px; height: 24px; border-radius: 50%;
                                    background-color: #0a0118; z-index: 25;"></div>
                        <div style="position: absolute; bottom: 0; left: 0;
                                    transform: translate(-50%, 50%);
                                    width: 24px; height: 24px; border-radius: 50%;
                                    background-color: #0a0118; z-index: 25;"></div>
                    </div>

                    <div class="card-body p-4 d-flex flex-column justify-content-between flex-grow-1" style="z-index: 2;">
                        <div>
                            <h5 class="card-title text-white fw-bold mb-3" style="font-size: 1.35rem; line-height: 1.4;">
                                ${item.title}
                            </h5>
                            <div class="row g-2" style="color: rgba(255,255,255,0.5); font-size: 0.8rem;">
                                <div class="col-sm-6">
                                    <i class="fa-solid fa-calendar text-brand-pink me-2"></i>${displayDate}
                                </div>
                                ${timeBlock}
                                <div class="col-sm-6">
                                    <i class="fa-solid fa-location-dot text-brand-pink me-2"></i>${item.cityName || 'Toàn quốc'}
                                </div>
                                <div class="col-sm-6">
                                    <i class="fa-solid fa-ticket text-brand-pink me-2"></i>${price}
                                </div>
                            </div>
                        </div>

                        ${reasonBlock}
                        ${buyButton}
                    </div>

                    <div class="d-none d-lg-flex flex-column justify-content-center align-items-center text-center p-4"
                         style="border-left: 1px solid rgba(255,255,255,0.08);
                                background: rgba(30,41,59,0.2);
                                width: 110px; flex-shrink: 0;
                                border-radius: 0 24px 24px 0;">
                        <span style="color: #ec4899; font-family: monospace; font-size: 10px;
                                     text-transform: uppercase; letter-spacing: 2px; display: block; line-height: 1;">
                            ${cal.month}
                        </span>
                        <span class="text-white fw-black" style="font-size: 2rem; line-height: 1; display: block; margin-top: 4px;">
                            ${cal.day}
                        </span>
                        <span style="color: rgba(255,255,255,0.4); font-family: monospace; font-size: 9px;
                                     text-transform: uppercase; letter-spacing: 1px; display: block; margin-top: 12px;">
                            ${item.cityName || 'Hà Nội'}
                        </span>
                    </div>
                </div>
            </div>`;
    };

    const renderResults = (recs) => {
        recommendationsContainer.innerHTML = '';

        if (!recs || recs.length === 0) {
            recommendationsContainer.innerHTML = `
                <div class="col-12 text-center py-5">
                    <i class="fa-solid fa-calendar-xmark fa-2x text-secondary mb-3 d-block"></i>
                    <p style="color: rgba(255,255,255,0.5);">
                        Không tìm thấy gợi ý phù hợp. Vui lòng thử lại sau!
                    </p>
                </div>`;
            return;
        }

        // --- ĐÃ SỬA: Đồng bộ lại biến logic ẩn/hiện hộp AI lý do ---
        const hasReason = recs.some(r => r.reason);
        if (hasReason && aiBox && aiReasoningText) {
            aiBox.classList.remove('d-none');
            aiReasoningText.textContent = 'Dựa trên lịch sử tham gia và sở thích của bạn, Gemini đã chọn lọc và xếp hạng những sự kiện phù hợp nhất.';
        } else if (aiBox) {
            aiBox.classList.add('d-none');
        }

        recs.forEach((item, index) => {
            recommendationsContainer.insertAdjacentHTML('beforeend', buildCard(item, index));
        });
    };

    // ─── Main Click Handler ───────────────────────────────────────────────────

    if (triggerRecBtn) {
        triggerRecBtn.addEventListener('click', async () => {
            // Reset và ẩn wrapper kết quả cũ
            if (resultsWrapper) resultsWrapper.classList.add('d-none');
            if (loadingState) loadingState.classList.remove('d-none');

            triggerRecBtn.disabled = true;
            triggerRecBtn.innerHTML = '<i class="fa-solid fa-circle-notch fa-spin me-2"></i> Đang phân tích';

            try {
                const response = await fetch('/api/recommendations');

                if (response.status === 401) {
                    window.location.href = '/auth/login'; // Đồng bộ lại link redirect từ Controller của bạn
                    return;
                }
                if (!response.ok) {
                    throw new Error('Máy chủ trả về lỗi ' + response.status);
                }

                const recs = await response.json();
                renderResults(recs);

                // --- ĐÃ SỬA: Hiển thị đúng thẻ cha wrapper ---
                if (resultsWrapper) resultsWrapper.classList.remove('d-none');

            } catch (err) {
                console.error('Recommendation error:', err);
                if (recommendationsContainer) {
                    recommendationsContainer.innerHTML = `
                        <div class="col-12 text-center py-4">
                            <div class="p-4 rounded-4" style="background: rgba(239,68,68,0.1); border: 1px solid rgba(239,68,68,0.2);">
                                <i class="fa-solid fa-circle-exclamation text-danger fa-2x mb-3 d-block"></i>
                                <h5 class="text-white fw-bold mb-2">Đường truyền gặp lỗi kết nối</h5>
                                <p class="mb-0" style="color: rgba(239,68,68,0.8); font-size: 0.8rem;">${err.message}</p>
                            </div>
                        </div>`;
                }
                if (aiBox) aiBox.classList.add('d-none');
                if (resultsWrapper) resultsWrapper.classList.remove('d-none');

            } finally {
                if (loadingState) loadingState.classList.add('d-none');
                triggerRecBtn.disabled = false;
                triggerRecBtn.innerHTML = '<i class="fa-solid fa-compass me-2"></i> Tìm kiếm Sự kiện <i class="fa-solid fa-arrow-right ms-1"></i>';
            }
        });
    }
});