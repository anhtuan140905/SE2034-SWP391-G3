"use strict";

// ─── STATE ────────────────────────────────────────────────────────────────────
let state = {
    bannerUrl: "",

    // Ảnh thư viện MỚI (file chưa lưu DB) — vẫn dùng blob URL để preview
    additionalImages: [],
    galleryFiles: [],

    // Ảnh thư viện ĐÃ CÓ trong DB khi ở chế độ Edit: [{ id, url }]
    existingImages: [],
    // id các ảnh cũ user bấm xoá -> gửi lên server để controller xoá thật
    deletedImageIds: [],

    // id = key nội bộ để React-less render/lookup trong DOM (luôn có, sinh phía client)
    // dbId = id thật trong DB (null nếu là dòng mới thêm, có giá trị nếu load từ server khi Edit)
    agenda: [],          // { id, dbId, time, desc }
    tiers: [],           // { id, dbId, name, zoneName, ... }

    // dbId thật của các tier/agenda đã bị user xoá khỏi form khi Edit -> gửi cho server để DELETE
    deletedTierIds: [],
    deletedAgendaIds: [],

    tierErrors: {},      // { [id]: { name?, price?, qty? } }
    agendaErrors: {},    // { [id]: { time?, desc? } }
};

let tierIdCounter = 1;
function newTierId() {
    return "tier-" + Date.now() + "-" + tierIdCounter++;
}

let agendaIdCounter = 1;
function newAgendaId() {
    return "agenda-" + Date.now() + "-" + agendaIdCounter++;
}

// ─── KHỞI TẠO ─────────────────────────────────────────────────────────────────
document.addEventListener("DOMContentLoaded", () => {
    // Khôi phục danh sách vé: xảy ra khi (a) Edit sự kiện có sẵn, hoặc
    // (b) Create nhưng server trả lại form sau lỗi validate (redisplay).
    if (typeof serverTickets !== "undefined" && serverTickets && serverTickets.length > 0) {
        state.tiers = serverTickets.map((ticket) => {
            const row = ticket.seat?.row || 1;
            const cols = ticket.seat?.seatNumber || 1;
            return {
                id: newTierId(),
                dbId: ticket.id ?? null,   // có giá trị nếu đây là ticket đã tồn tại trong DB
                name: ticket.displayOrder || "",
                zoneName: ticket.zoneName || "",
                rowLetter: row,
                cols: cols,
                totalQty: row * cols,
                qty: ticket.stock || 1,
                price: ticket.price || 0,
                desc: ticket.description || "",
            };
        });
        renderTiers();
    }

    // Khôi phục mốc thời gian (agenda/timeline) tương tự ticket ở trên
    if (typeof serverAgenda !== "undefined" && serverAgenda && serverAgenda.length > 0) {
        state.agenda = serverAgenda.map((item) => ({
            id: newAgendaId(),
            dbId: item.id ?? null,
            time: item.time || "",
            desc: item.active || item.desc || "",
        }));
        renderAgenda();
    }

    // Khôi phục ảnh thư viện đã có trong DB (chỉ có ở chế độ Edit)
    if (typeof serverGallery !== "undefined" && serverGallery && serverGallery.length > 0) {
        state.existingImages = serverGallery.map((img) => ({ id: img.id, url: img.url }));
        renderGallery();
    }

    const form = document.getElementById("eventForm");
    if (form) {
        form.addEventListener("submit", handleFormSubmit);
    }

    const startTimeEl = document.getElementById("startTime");
    const endTimeEl = document.getElementById("endTime");
    if (startTimeEl) startTimeEl.addEventListener("change", revalidateAgendaTimes);
    if (endTimeEl) endTimeEl.addEventListener("change", revalidateAgendaTimes);

    // Xử lý banner: validate kích thước ảnh (1280×720)
    const bannerInput = document.getElementById("bannerFileInput");
    if (bannerInput) {
        bannerInput.addEventListener("change", function () {
            const file = this.files[0];
            const error = document.getElementById("bannerError");
            if (error) error.textContent = "";
            if (!file) return;

            // Preview ngay lập tức
            handleBannerFile({ target: this });

            // Kiểm tra kích thước sau khi load
            const img = new Image();
            img.onload = function () {
                if (img.width !== 1280 || img.height !== 720) {
                    if (error) error.textContent = "Ảnh phải có kích thước 1280×720 px.";
                    bannerInput.value = "";
                    clearBanner();
                }
                URL.revokeObjectURL(img.src);
            };
            img.src = URL.createObjectURL(file);
        });
    }

    // Dropdown ward theo city
    const cityEl = document.getElementById("province");
    const wardEl = document.getElementById("ward");
    if (cityEl && wardEl) {
        cityEl.addEventListener("change", function () {
            const cityValue = this.value;
            if (!cityValue) return;
            fetch(`/organizer/api/city?cityId=${cityValue}`)
                .then((r) => r.json())
                .then((wards) => {
                    wardEl.innerHTML = '<option value="">--Chọn quận/huyện--</option>';
                    wards.forEach((w) => {
                        wardEl.innerHTML += `<option value="${w.wardId}">${w.name}</option>`;
                    });
                })
                .catch((err) => console.error(err));
        });
    }
});

// ─── ĐỒNG BỘ NGÀY ─────────────────────────────────────────────────────────────
function handleDateChange(val) {
    const badge = document.getElementById("showtimeDateBadge");
    if (badge) badge.textContent = val || "Chưa thiết lập";
}

// ─── ẢNH BÌA (BANNER) ─────────────────────────────────────────────────────────
function handleBannerFile(e) {
    const file = e.target.files[0];
    if (!file) return;
    setBanner(URL.createObjectURL(file));
}

function setBanner(url) {
    state.bannerUrl = url;
    const preview     = document.getElementById("bannerPreview");
    const placeholder = document.getElementById("bannerPlaceholder");
    const overlay     = document.getElementById("bannerHoverOverlay");
    if (url) {
        preview.src = url;
        preview.classList.remove("d-none");
        overlay.classList.remove("d-none");
        placeholder.classList.add("d-none");
    } else {
        clearBanner();
    }
}

function clearBanner() {
    const preview     = document.getElementById("bannerPreview");
    const placeholder = document.getElementById("bannerPlaceholder");
    const overlay     = document.getElementById("bannerHoverOverlay");
    if (preview)     preview.classList.add("d-none");
    if (overlay)     overlay.classList.add("d-none");
    if (placeholder) placeholder.classList.remove("d-none");
    state.bannerUrl = "";
}

// ─── THƯ VIỆN ẢNH ─────────────────────────────────────────────────────────────
// Có 2 nhóm ảnh hiển thị chung 1 lưới:
//   1) state.existingImages: ảnh đã lưu trong DB (chỉ có khi Edit) -> xoá = đưa id vào deletedImageIds
//   2) state.additionalImages/galleryFiles: ảnh mới chọn ở máy user -> xoá = bỏ khỏi mảng, không gọi server
function handleGalleryFiles(e) {
    Array.from(e.target.files || []).forEach((file) => {
        state.galleryFiles.push(file);
        state.additionalImages.push(URL.createObjectURL(file));
    });
    syncGalleryInput();
    renderGallery();
}

function removeGalleryImage(idx) {
    URL.revokeObjectURL(state.additionalImages[idx]);
    state.additionalImages.splice(idx, 1);
    state.galleryFiles.splice(idx, 1);
    syncGalleryInput();
    renderGallery();
}

// Xoá 1 ảnh đã có sẵn trong DB (chế độ Edit)
function removeExistingImage(imageId) {
    state.deletedImageIds.push(imageId);
    state.existingImages = state.existingImages.filter((img) => img.id !== imageId);
    renderGallery();
}

function syncGalleryInput() {
    const dt = new DataTransfer();
    state.galleryFiles.forEach((f) => dt.items.add(f));
    const input = document.getElementById("galleryFileInput");
    if (input) input.files = dt.files;
}

function renderGallery() {
    const grid  = document.getElementById("galleryGrid");
    const count = document.getElementById("galleryCount");
    const total = state.existingImages.length + state.additionalImages.length;
    if (count) count.textContent = total + " ĐÃ THÊM";
    if (!grid) return;

    if (total === 0) {
        grid.classList.add("d-none");
        grid.innerHTML = "";
        return;
    }

    grid.classList.remove("d-none");

    const existingHtml = state.existingImages
        .map(
            (img) => `
        <div class="gallery-item">
            <img src="${escHtml(img.url)}" alt="Ảnh đã lưu" loading="lazy"/>
            <button class="gallery-item-del" type="button"
                    onclick="removeExistingImage(${img.id})" title="Xóa">
                <span class="material-symbols-outlined">close</span>
            </button>
        </div>`,
        )
        .join("");

    const newHtml = state.additionalImages
        .map(
            (url, i) => `
        <div class="gallery-item">
            <img src="${escHtml(url)}" alt="Ảnh mới ${i + 1}" loading="lazy"/>
            <span class="mono-tag" style="position:absolute;top:6px;left:6px;">MỚI</span>
            <button class="gallery-item-del" type="button"
                    onclick="removeGalleryImage(${i})" title="Xóa">
                <span class="material-symbols-outlined">close</span>
            </button>
        </div>`,
        )
        .join("");

    grid.innerHTML = existingHtml + newHtml;
}

// ─── MỐC THỜI GIAN SỰ KIỆN (AGENDA) ───────────────────────────────────────────
function revalidateAgendaTimes() {
    if (state.agenda.length === 0) return;
    validateAllAgenda();
    renderAgenda();
}

function addAgendaItem() {
    state.agenda.push({ id: newAgendaId(), dbId: null, time: "", desc: "" });
    renderAgenda();
}

function deleteAgendaItem(id) {
    const item = state.agenda.find((a) => a.id === id);
    // Nếu mốc này đã tồn tại trong DB (đang Edit) -> ghi nhận để server xoá thật khi lưu
    if (item && item.dbId) {
        state.deletedAgendaIds.push(item.dbId);
    }
    state.agenda = state.agenda.filter((a) => a.id !== id);
    delete state.agendaErrors[id];
    renderAgenda();
}

function updateAgendaField(id, field, value) {
    const item = state.agenda.find((a) => a.id === id);
    if (!item) return;
    item[field] = value;

    // Xóa lỗi field này ngay khi user sửa
    if (state.agendaErrors[id]?.[field]) {
        delete state.agendaErrors[id][field];
        if (Object.keys(state.agendaErrors[id]).length === 0) {
            delete state.agendaErrors[id];
        }
        renderAgenda();
    }
}

function validateAgendaItem(a) {
    const errors = {};

    if (!a.time || !String(a.time).trim()) {
        errors.time = "Vui lòng chọn thời gian.";
    } else {
        const startTime = getVal("startTime");
        const endTime   = getVal("endTime");

        if (startTime && endTime) {
            if (a.time < startTime || a.time > endTime) {
                errors.time = `Thời gian phải nằm trong khung ${startTime} – ${endTime}.`;
            } else if (state.agenda.filter((x) => x.time === a.time).length > 1) {
                errors.time = "Thời gian bị trùng với mốc khác.";
            }
        }
    }

    if (!a.desc || !String(a.desc).trim()) {
        errors.desc = "Vui lòng nhập nội dung hoạt động.";
    }

    return errors;
}

function validateAllAgenda() {
    state.agendaErrors = {};
    let isValid = true;
    state.agenda.forEach((a) => {
        const errs = validateAgendaItem(a);
        if (Object.keys(errs).length > 0) {
            state.agendaErrors[a.id] = errs;
            isValid = false;
        }
    });
    return isValid;
}

function renderAgenda() {
    const list = document.getElementById("agendaList");
    if (!list) return;

    if (state.agenda.length === 0) {
        list.innerHTML = `<div class="agenda-empty" id="agendaEmpty">
            Chưa có mốc thời gian nào được cấu hình. Nhấp "Thêm mốc thời gian mới" phía dưới để bắt đầu!
        </div>`;
        return;
    }

    list.innerHTML = state.agenda
        .map((a, idx) => {
            const errs = state.agendaErrors[a.id] || {};
            return `
        <div class="agenda-row" id="ag-${a.id}">
            <input type="hidden" name="timeLine[${idx}].id" value="${a.dbId ?? ''}" />
            <div class="agenda-dot-wrap">
                <div class="agenda-dot"></div>
                ${idx < state.agenda.length - 1 ? '<div class="agenda-line"></div>' : ""}
            </div>
            <div class="agenda-fields">
                <div class="agenda-time-wrap">
                    <label class="tier-field-label">Thời Gian</label>
                    <input type="time"
                           class="tier-input${errs.time ? " is-invalid-field" : ""}"
                           value="${escHtml(a.time)}"
                           name="timeLine[${idx}].time"
                           onchange="updateAgendaField('${a.id}','time',this.value)" />
                    ${errDiv(errs.time)}
                </div>
                <div class="agenda-desc-wrap">
                    <label class="tier-field-label">Nội Dung Hoạt Động / Chương Trình</label>
                    <input type="text"
                           class="tier-input${errs.desc ? " is-invalid-field" : ""}"
                           value="${escHtml(a.desc)}"
                           name="timeLine[${idx}].active"
                           placeholder="Ví dụ: Mở cửa đón khách và nhạc khởi động pre-show"
                           oninput="updateAgendaField('${a.id}','desc',this.value)" />
                    ${errDiv(errs.desc)}
                </div>
                <button type="button" class="agenda-del-btn"
                        onclick="deleteAgendaItem('${a.id}')" title="Xóa">
                    <span class="material-symbols-outlined">delete</span>
                </button>
            </div>
        </div>`;
        })
        .join("");
}

// ─── HẠNG VÉ (TICKET TIERS) ───────────────────────────────────────────────────

/** Tính sức chứa: hàng × số ghế mỗi hàng */
function computeTotalQty(row, cols) {
    const r = Math.min(26, Math.max(1, parseInt(row) || 1));
    const c = Math.min(100, Math.max(1, parseInt(cols) || 1));
    return r * c;
}

function addTicketTier() {
    state.tiers.push({
        id: newTierId(),
        dbId: null,
        name: "",
        zoneName: "",
        rowLetter: 1,
        cols: 1,
        totalQty: 1,   // 1 hàng × 1 ghế
        qty: 1,
        price: 0,
        desc: "",
    });
    renderTiers();
}

function deleteTier(id) {
    const tier = state.tiers.find((t) => t.id === id);
    // Nếu hạng vé này đã tồn tại trong DB (đang Edit) -> ghi nhận để server xoá thật khi lưu
    if (tier && tier.dbId) {
        state.deletedTierIds.push(tier.dbId);
    }
    state.tiers = state.tiers.filter((t) => t.id !== id);
    delete state.tierErrors[id];
    renderTiers();
}

function updateTierField(id, field, value) {
    const tier = state.tiers.find((t) => t.id === id);
    if (!tier) return;

    tier[field] = value;

    // Khi thay đổi hàng hoặc số ghế → tính lại sức chứa
    if (field === "rowLetter" || field === "cols") {
        tier.totalQty = computeTotalQty(tier.rowLetter, tier.cols);

        // Nếu qty đang vượt sức chứa mới → clamp và thông báo lỗi real-time
        if (tier.qty > tier.totalQty) {
            tier.qty = tier.totalQty;
        }
        // Xóa lỗi qty nếu đang hợp lệ sau khi sức chứa thay đổi
        if (state.tierErrors[id]?.qty && tier.qty >= 1 && tier.qty <= tier.totalQty) {
            delete state.tierErrors[id].qty;
        }
        renderTiers();
        return;
    }

    // Khi user nhập số lượng vé → validate real-time ngay lập tức
    if (field === "qty") {
        const numQty = parseInt(value) || 0;
        if (numQty > tier.totalQty) {
            if (!state.tierErrors[id]) state.tierErrors[id] = {};
            state.tierErrors[id].qty = `Số lượng vé không được vượt sức chứa (${tier.totalQty}).`;
        } else if (numQty < 1) {
            if (!state.tierErrors[id]) state.tierErrors[id] = {};
            state.tierErrors[id].qty = "Số lượng vé phải lớn hơn hoặc bằng 1.";
        } else {
            // Hợp lệ → xóa lỗi qty
            if (state.tierErrors[id]?.qty) {
                delete state.tierErrors[id].qty;
                if (Object.keys(state.tierErrors[id]).length === 0) {
                    delete state.tierErrors[id];
                }
            }
        }
        renderTiers();
        return;
    }

    // Các field khác: xóa lỗi ngay khi user sửa
    if (state.tierErrors[id]?.[field]) {
        delete state.tierErrors[id][field];
        if (Object.keys(state.tierErrors[id]).length === 0) {
            delete state.tierErrors[id];
        }
        renderTiers();
    }
}

function validateTier(t) {
    const errors = {};

    if (!t.name || !String(t.name).trim()) {
        errors.name = "Vui lòng nhập Display Order.";
    }

    if (!t.zoneName || !String(t.zoneName).trim()) {
        errors.zoneName = "Vui lòng nhập Tên Khu Vực.";
    }

    if (t.price == null || Number(t.price) < 0) {
        errors.price = "Mệnh giá không được âm.";
    }

    const qty      = parseInt(t.qty) || 0;
    const totalQty = parseInt(t.totalQty) || 1;
    if (qty < 1) {
        errors.qty = "Số lượng vé phải lớn hơn hoặc bằng 1.";
    } else if (qty > totalQty) {
        errors.qty = `Số lượng vé không được vượt sức chứa (${totalQty}).`;
    }

    return errors;
}

function validateAllTiers() {
    state.tierErrors = {};
    let isValid = true;
    state.tiers.forEach((t) => {
        const errs = validateTier(t);
        if (Object.keys(errs).length > 0) {
            state.tierErrors[t.id] = errs;
            isValid = false;
        }
    });
    return isValid;
}

function renderTiers() {
    const container  = document.getElementById("ticketTiersContainer");
    const emptyState = document.getElementById("ticketTiersEmpty");
    if (!container) return;

    if (state.tiers.length === 0) {
        if (emptyState) emptyState.style.display = "";
        container.innerHTML = "";
        return;
    }

    if (emptyState) emptyState.style.display = "none";

    container.innerHTML = state.tiers
        .map((t, idx) => {
            const errs = state.tierErrors[t.id] || {};
            return `
        <div class="ticket-tier-card fade-in" id="card-${t.id}">
            <input type="hidden" name="ticketTypes[${idx}].id" value="${t.dbId ?? ''}" />

            <div class="ticket-tier-header">
                <div class="d-flex align-items-center gap-2">
                    <span class="tier-num-badge">${idx + 1}</span>
                    <span class="tier-config-label">Cấu Hình Chi Tiết Hạng Vé</span>
                </div>
                <button type="button" class="btn-delete-tier" onclick="deleteTier('${t.id}')">
                    <span class="material-symbols-outlined">delete</span>
                    Gỡ bỏ hạng vé
                </button>
            </div>

            <!-- Cấu hình sơ đồ ghế ngồi & sức chứa -->
            <div class="seat-config-box mt-3">
                <div class="seat-config-title">
                    <span class="material-symbols-outlined">grid_view</span>
                    Cấu Hình Sơ Đồ Ghế Ngồi &amp; Sức Chứa Tự Động
                </div>

                <div class="row g-3">

                    <!-- Hàng dọc (1–26) -->
                    <div class="col-12 col-sm-4">
                        <label class="tier-field-label">
                            <span class="material-symbols-outlined" style="font-size:14px;">unfold_more</span>
                            Hàng Dọc (1–26)
                        </label>
                        <input
                            class="tier-input row-select"
                            type="number"
                            min="1" max="26"
                            value="${escHtml(t.rowLetter)}"
                            name="ticketTypes[${idx}].seat.row"
                            placeholder="Nhập số hàng"
                            onblur ="updateTierField(
                                '${t.id}',
                                'rowLetter',
                                Math.min(26, Math.max(1, parseInt(this.value) || 1))
                            )" />
                        <div class="row-stepper-sub">Giới hạn 1 – 26 hàng</div>
                    </div>

                    <!-- Số ghế mỗi hàng -->
                    <div class="col-12 col-sm-4">
                        <label class="tier-field-label">
                            <span class="material-symbols-outlined" style="font-size:14px;vertical-align:-2px;">view_week</span>
                            Số Ghế / Hàng
                        </label>
                        <input
                            type="number"
                            class="tier-input"
                            name="ticketTypes[${idx}].seat.seatNumber"
                            min="1" max="100"
                            value="${escHtml(t.cols)}"
                            onblur ="updateTierField(
                                '${t.id}',
                                'cols',
                                Math.min(100, Math.max(1, parseInt(this.value) || 1))
                            )" />
                        <div class="row-stepper-sub">Giới hạn 1 – 100 ghế</div>
                    </div>

                    <!-- Sức chứa (tự động) -->
                    <div class="col-12 col-sm-4">
                        <label class="tier-field-label">
                            <span class="material-symbols-outlined" style="font-size:14px;vertical-align:-2px;">lock</span>
                            Sức Chứa (QTY)
                        </label>
                        <div class="computed-inp">
                            <span id="totalQty-${t.id}">${t.totalQty}</span>
                            <span class="computed-auto">Tự động</span>
                        </div>
                        <div class="row-stepper-sub">= Hàng × Ghế/hàng</div>
                        <input type="hidden" name="ticketTypes[${idx}].capacity" value="${t.totalQty}" />
                    </div>

                </div>
            </div>

            <div class="row g-3 align-items-start mt-1">

                <!-- Display Order -->
                <div class="col-12 col-sm-6 col-md-3">
                    <label class="tier-field-label">
                        Display Order <span style="color:#ef4444;">*</span>
                    </label>
                    <input
                        type="number"
                        class="tier-input${errs.name ? " is-invalid-field" : ""}"
                        placeholder="Ví dụ: 1, 2"
                        name="ticketTypes[${idx}].displayOrder"
                        value="${escHtml(t.name || "")}"
                        onblur ="updateTierField('${t.id}', 'name', this.value)" />
                    ${errDiv(errs.name)}
                </div>

                <!-- Loại / Tên khu vực -->
                <div class="col-12 col-sm-6 col-md-3">
                    <label class="tier-field-label">Loại / Khu Vực</label>
                    <input
                        type="text"
                        class="tier-input${errs.zoneName ? " is-invalid-field" : ""}"
                        placeholder="Ví dụ: Standee A, Zone VIP..."
                        name="ticketTypes[${idx}].zoneName"
                        value="${escHtml(t.zoneName || "")}"
                        onblur ="updateTierField('${t.id}', 'zoneName', this.value)" />
                    ${errDiv(errs.zoneName)}
                </div>

                <!-- Mệnh giá -->
                <div class="col-12 col-sm-6 col-md-3">
                    <label class="tier-field-label mb-0">Mệnh Giá (đ)</label>
                    <div class="price-input-wrap mt-1">
                        <input
                            type="number"
                            class="tier-input tier-input-mono${errs.price ? " is-invalid-field" : ""}"
                            name="ticketTypes[${idx}].price"
                            placeholder="Ví dụ: 250000"
                            value="${escHtml(t.price)}"
                            min="0"
                            onblur ="updateTierField('${t.id}', 'price', Math.max(0, Number(this.value) || 0))" />
                        <span class="price-suffix">đ</span>
                    </div>
                    ${errDiv(errs.price)}
                </div>

                <!-- Số lượng vé -->
                <div class="col-12 col-sm-6 col-md-3">
                    <label class="tier-field-label">
                        Số Lượng Vé (QTY) <span style="color:#ef4444;">*</span>
                    </label>
                    <input
                        type="number"
                        class="tier-input tier-input-mono${errs.qty ? " is-invalid-field" : ""}"
                        name="ticketTypes[${idx}].stock"
                        min="1"
                        max="${t.totalQty}"
                        value="${escHtml(t.qty)}"
                        onblur ="updateTierField('${t.id}', 'qty', parseInt(this.value) || 0)" />
                    ${errDiv(errs.qty)}
                    <div class="row-stepper-sub">Tối đa: ${t.totalQty} (theo sức chứa)</div>
                </div>

            </div>

            <!-- Mô tả -->
            <div class="border-top pt-3 mt-3">
                <label class="tier-field-label">
                    Đặc Quyền Ghế &amp; Mô Tả Hạng Vé
                </label>
                <textarea
                    class="tier-input"
                    name="ticketTypes[${idx}].description"
                    rows="3"
                    placeholder="Ví dụ: Đồ uống chào mừng miễn phí, Ghế ngồi hàng đầu cận cảnh..."
                    onblur ="updateTierField('${t.id}', 'desc', this.value)"
                >${escHtml(t.desc || "")}</textarea>
            </div>

        </div>`;
        })
        .join("");
}

// ─── GỬI FORM ─────────────────────────────────────────────────────────────────
// Chèn hidden input (name lặp lại) để Spring bind vào List<Long> ở phía controller.
// Ví dụ: <input type="hidden" name="deletedTicketTypeIds" value="5"> x N dòng.
function injectDeletedIdInputs(form, name, ids) {
    form.querySelectorAll(`input[data-deleted-group="${name}"]`).forEach((el) => el.remove());
    ids.forEach((id) => {
        const input = document.createElement("input");
        input.type = "hidden";
        input.name = name;
        input.value = id;
        input.setAttribute("data-deleted-group", name);
        form.appendChild(input);
    });
}

function handleFormSubmit(e) {
    const tiersValid  = validateAllTiers();
    const agendaValid = validateAllAgenda();

    renderTiers();
    renderAgenda();

    if (!tiersValid || !agendaValid) {
        e.preventDefault();
        scrollToFirstError();
        return false;
    }

    // Chỉ cần thiết ở chế độ Edit, nhưng gọi luôn cũng vô hại (mảng rỗng ở Create)
    const form = e.target || document.getElementById("eventForm");
    injectDeletedIdInputs(form, "deletedTicketTypeIds", state.deletedTierIds);
    injectDeletedIdInputs(form, "deletedTimeLineIds", state.deletedAgendaIds);
    injectDeletedIdInputs(form, "deletedImageIds", state.deletedImageIds);

    dismissError();
    return true;
}

function handleCancel() {
    if (confirm("Hủy bỏ tạo sự kiện? Tất cả thay đổi sẽ bị mất.")) {
        window.history.back();
    }
}

// ─── THÔNG BÁO LỖI ────────────────────────────────────────────────────────────
function dismissError() {
    const banner = document.getElementById("validationError");
    if (banner) banner.classList.add("d-none");
}

function scrollToFirstError() {
    const firstErrorEl = document.querySelector(
        ".is-invalid-field, .ticket-tier-card .text-danger, .agenda-row .text-danger",
    );
    if (firstErrorEl) {
        firstErrorEl.scrollIntoView({ behavior: "smooth", block: "center" });
    }
}

function showToast(isDraft) {
    const t = document.getElementById("successToast");
    if (!t) return;
    const span = t.querySelector("span:last-child");
    if (span) {
        span.textContent = isDraft
            ? " Đã lưu bản nháp thành công!"
            : " Sự kiện đã được đăng thành công!";
    }
    t.classList.remove("d-none");
    setTimeout(() => t.classList.add("d-none"), 4000);
}

// ─── HÀM TIỆN ÍCH ─────────────────────────────────────────────────────────────
function getVal(id) {
    const el = document.getElementById(id);
    return el ? el.value : "";
}

function escHtml(str) {
    return String(str ?? "")
        .replace(/&/g, "&amp;")
        .replace(/"/g, "&quot;")
        .replace(/</g, "&lt;")
        .replace(/>/g, "&gt;");
}

function errDiv(msg) {
    return msg
        ? `<div class="text-danger" style="font-size:small;">${escHtml(msg)}</div>`
        : "";
}