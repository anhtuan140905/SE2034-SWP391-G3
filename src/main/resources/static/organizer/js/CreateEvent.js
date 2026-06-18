"use strict";


// ─── DANH SÁCH HÀNG A-Z ────────────────────────────────────────────────────────
const ROW_LETTERS = Array.from({ length: 26 }, (_, i) =>
    String.fromCharCode(65 + i),
);

// ─── STATE ────────────────────────────────────────────────────────────────────
let state = {
    bannerUrl: "",
    additionalImages: [],
    galleryFiles: [],
    agenda: [], // { id, time, desc }
    tiers: [],
    tierErrors: {},   // { [tierId]: { name?, price?, qty? } }
    agendaErrors: {}, // { [agendaId]: { time?, desc? } }
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
    const form = document.getElementById("eventForm");
    if (form) {
        form.addEventListener("submit", handleFormSubmit);
    }

    // Khi đổi giờ mở cửa / kết thúc, kiểm tra lại các mốc thời gian đã nhập
    // (vì khung giờ hợp lệ phụ thuộc vào 2 giá trị này)
    const startTimeEl = document.getElementById("startTime");
    const endTimeEl = document.getElementById("endTime");
    if (startTimeEl) startTimeEl.addEventListener("change", revalidateAgendaTimes);
    if (endTimeEl) endTimeEl.addEventListener("change", revalidateAgendaTimes);
});

function revalidateAgendaTimes() {
    if (state.agenda.length === 0) return;
    validateAllAgenda();
    renderAgenda();
}

function handleDateChange(val) {
    document.getElementById("showtimeDateBadge").textContent =
        val || "Chưa thiết lập";
}
// ─── ĐỊA ĐIỂM TỔ CHỨC ──────────────────────────────────────────────────────────
function updateAddressPreview() {
    const place = getVal("placeName").trim();
    const prov = getVal("province").trim();
    const dist = getVal("district").trim();
    const street = getVal("street").trim();
    const parts = [place, street, dist, prov].filter(Boolean);

    const el = document.getElementById("addressPreview");
    if (parts.length === 0) {
        el.textContent =
            "Vui lòng nhập các trường Địa điểm chi tiết phía trên để hiển thị bản xem trước địa chỉ in phôi vé.";
        el.classList.add("address-preview-empty");
    } else {
        el.textContent = parts.join(", ");
        el.classList.remove("address-preview-empty");
    }
}

// ─── ẢNH BÌA (BANNER) ─────────────────────────────────────────────────────────
function handleBannerFile(e) {
    const file = e.target.files[0];
    if (!file) return;
    const url = URL.createObjectURL(file);
    setBanner(url);
}

function setBanner(url) {
    state.bannerUrl = url;
    const preview = document.getElementById("bannerPreview");
    const placeholder = document.getElementById("bannerPlaceholder");
    const overlay = document.getElementById("bannerHoverOverlay");
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
    document.getElementById("bannerPreview").classList.add("d-none");
    document.getElementById("bannerHoverOverlay").classList.add("d-none");
    document.getElementById("bannerPlaceholder").classList.remove("d-none");
    state.bannerUrl = "";
}

// ─── THƯ VIỆN ẢNH ─────────────────────────────────────────────────────────────
function handleGalleryFiles(e) {
    const files = Array.from(e.target.files || []);
    files.forEach((file) => {
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

// Đồng bộ lại state.galleryFiles vào input[type=file] thật
function syncGalleryInput() {
    const dt = new DataTransfer();
    state.galleryFiles.forEach((file) => dt.items.add(file));
    document.getElementById("galleryFileInput").files = dt.files;
}

function renderGallery() {
    const grid = document.getElementById("galleryGrid");
    const count = document.getElementById("galleryCount");
    count.textContent = state.additionalImages.length + " ĐÃ THÊM";

    if (state.additionalImages.length === 0) {
        grid.classList.add("d-none");
        grid.innerHTML = "";
        return;
    }

    grid.classList.remove("d-none");
    grid.innerHTML = state.additionalImages
        .map(
            (url, i) => `
        <div class="gallery-item">
            <img src="${escHtml(url)}" alt="Ảnh ${i + 1}" loading="lazy"/>
            <button class="gallery-item-del" type="button"
                    onclick="removeGalleryImage(${i})" title="Xóa">
                <span class="material-symbols-outlined">close</span>
            </button>
        </div>
    `,
        )
        .join("");
}

// ─── MỐC THỜI GIAN SỰ KIỆN (AGENDA) ───────────────────────────────────────────
function addAgendaItem() {
    state.agenda.push({ id: newAgendaId(), time: "HH:mm", desc: "" });
    renderAgenda();
}

function deleteAgendaItem(id) {
    state.agenda = state.agenda.filter((a) => a.id !== id);
    delete state.agendaErrors[id];
    renderAgenda();
}

function updateAgendaField(id, field, value) {
    const item = state.agenda.find((a) => a.id === id);
    if (item) item[field] = value;

    // Xóa lỗi của field này ngay khi người dùng sửa, để phản hồi tức thì
    if (state.agendaErrors[id] && state.agendaErrors[id][field]) {
        delete state.agendaErrors[id][field];
        renderAgenda();
    }
}

// Validate một mốc thời gian: bắt buộc nhập, và phải nằm trong khung
// [Giờ Mở Cửa Đón Khách, Giờ Kết Thúc Dự Kiến]
function validateAgendaItem(a) {
    const errors = {};

    if (!a.time || a.time === "HH:mm" || !String(a.time).trim()) {
        errors.time = "Vui lòng chọn thời gian.";
    } else {
        const startTime = getVal("startTime"); // dạng "HH:mm"
        const endTime = getVal("endTime");

        if (startTime && endTime) {
            // So sánh chuỗi "HH:mm" trực tiếp vẫn đúng vì luôn 2 số - 2 số
            if (a.time < startTime || a.time > endTime) {
                errors.time = `Thời gian phải nằm trong khung ${startTime} - ${endTime}.`;
            }
        }
    }

    if (!a.desc || !String(a.desc).trim()) {
        errors.desc = "Vui lòng nhập nội dung hoạt động.";
    }

    return errors;
}

// Validate toàn bộ danh sách mốc thời gian, set vào state.agendaErrors
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
    const empty = document.getElementById("agendaEmpty");

    if (state.agenda.length === 0) {
        list.innerHTML = "";
        list.appendChild(empty || mkAgendaEmpty());
        return;
    }

    list.innerHTML = state.agenda
        .map((a, idx) => {
            const errs = state.agendaErrors[a.id] || {};
            return `
        <div class="agenda-row" id="ag-${a.id}">
            <div class="agenda-dot-wrap">
                <div class="agenda-dot"></div>
                ${idx < state.agenda.length - 1 ? '<div class="agenda-line"></div>' : ""}
            </div>
            <div class="agenda-fields">
                <div class="agenda-time-wrap">
                    <label class="tier-field-label">Thời Gian</label>
                    <input type="time" class="tier-input${errs.time ? " is-invalid-field" : ""}" value="${escHtml(a.time)}"
                        name="timeLine[${idx}].time"
                        onchange="updateAgendaField('${a.id}','time',this.value)" />
                    ${errDiv(errs.time)}
                </div>
                <div class="agenda-desc-wrap">
                    <label class="tier-field-label">Nội Dung Hoạt Động / Chương Trình</label>
                    <input type="text" class="tier-input${errs.desc ? " is-invalid-field" : ""}" value="${escHtml(a.desc)}"
                        name="timeLine[${idx}].active"
                        placeholder="Ví dụ: Mở cửa đón khách và nhạc khởi động pre-show"
                        oninput="updateAgendaField('${a.id}','desc',this.value)" />
                    ${errDiv(errs.desc)}
                </div>
                <button type="button" class="agenda-del-btn" onclick="deleteAgendaItem('${a.id}')" title="Xóa">
                    <span class="material-symbols-outlined">delete</span>
                </button>
            </div>
        </div>
    `;
        })
        .join("");
}

function mkAgendaEmpty() {
    const div = document.createElement("div");
    div.className = "agenda-empty";
    div.id = "agendaEmpty";
    div.textContent =
        'Chưa có mốc thời gian nào được cấu hình. Nhấp "Thêm mốc thời gian mới" phía dưới để bắt đầu!';
    return div;
}

// ─── HẠNG VÉ (TICKET TIERS) ───────────────────────────────────────────────────
function addTicketTier() {
    state.tiers.push({
        id: newTierId(),
        name: "",
        zoneName: "",
        rowLetter: "A",
        cols: 1,
        totalQty: 1,
        qty: 1,
        price: 0,
        desc: "",
    });

    renderTiers();
}

function deleteTier(id) {
    state.tiers = state.tiers.filter((t) => t.id !== id);
    delete state.tierErrors[id];
    renderTiers();
}

function updateTierField(id, field, value) {
    const tier = state.tiers.find((t) => t.id === id);
    if (!tier) return;

    if (field === "isFree" && value === true) {
        tier.price = 0;
    }

    tier[field] = value;
    if (field === "cols" || field === "rowLetter") {
        tier.totalQty = computeRows(tier.rowLetter) * (tier.cols || 1);

        // Nếu số vé vượt quá sức chứa thì giảm xuống bằng sức chứa
        if (tier.qty > tier.totalQty) {
            tier.qty = tier.totalQty;
        }
    }

    // Xóa lỗi của field này ngay khi người dùng sửa, để phản hồi tức thì
    if (state.tierErrors[id] && state.tierErrors[id][field]) {
        delete state.tierErrors[id][field];
    }

    // Chỉ render lại khi đổi trạng thái MIỄN PHÍ / TRẢ PHÍ, hàng dọc, số ghế/hàng, số lượng,
    // loại vé hoặc giá (vì những thay đổi này ảnh hưởng tới hiển thị / lỗi)
    if (

        field === "cols" ||
        field === "rowLetter"

    ) {
        renderTiers();
    }
}

// Số hàng tương ứng với chữ cái (A=1, B=2, ... Z=26)
function computeRows(letter) {
    const l = (
        String(letter || "A")
            .toUpperCase()
            .replace(/[^A-Z]/g, "") || "A"
    ).charCodeAt(0);
    return l - 65 + 1;
}

// Chuỗi phân bổ ghế tự động: "TKT-A01 to TKT-A01 (1 hàng A-A x 1 ghế)"
function computeZone(rowLetter, qty) {
    const rows = computeRows(rowLetter);
    const qtyStr = String(qty || 0).padStart(2, "0");
    const rowsRange = rows > 1 ? `A-${rowLetter}` : "A-A";
    return `TKT-A01 to TKT-${rowLetter}${qtyStr} (${rows} hàng ${rowsRange} x ${qty || 0} ghế)`;
}


// Validate một hạng vé (card trong ảnh thứ 2): Loại, Mệnh giá, Số lượng vé
function validateTier(t) {
    const errors = {};

    if (!t.name ||Number(t.price) <= 0) {
        errors.name = "Vui lòng nhập Loại vé.";
    }

    if (!t.zoneName || !String(t.zoneName).trim()) {
        errors.zoneName = "Vui lòng nhập Tên Khu Vực.";
    }

        if (!t.price || Number(t.price) <= 0) {
            errors.price = "Vui lòng nhập mệnh giá lớn hơn hoặc bằng  0";
        }


    if (!t.qty || Number(t.qty) < 1) {
        errors.qty = "Số lượng vé phải lớn hơn hoặc bằng 1.";
    } else if (Number(t.qty) > Number(t.totalQty)) {
        errors.qty = `Số lượng vé không được vượt sức chứa (${t.totalQty}).`;
    }

    return errors;
}

// Validate toàn bộ hạng vé, set vào state.tierErrors
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
    const container = document.getElementById("ticketTiersContainer");
    const emptyState = document.getElementById("ticketTiersEmpty");

    if (state.tiers.length === 0) {
        emptyState.style.display = "";
        container.innerHTML = "";
        return;
    }

    emptyState.style.display = "none";

    container.innerHTML = state.tiers
        .map((t, idx) => {
            const rows = computeRows(t.rowLetter);
            const zone = computeZone(t.rowLetter, t.totalQty);
            const errs = state.tierErrors[t.id] || {};
            return `

        <div class="ticket-tier-card fade-in" id="card-${t.id}">

            <div class="ticket-tier-header">
                <div class="d-flex align-items-center gap-2">
                    <span class="tier-num-badge">${idx + 1}</span>
                    <span class="tier-config-label">Cấu Hình Chi Tiết Hạng Vé</span>
                </div>
                <button type="button" class="btn-delete-tier"
                        onclick="deleteTier('${t.id}')">
                    <span class="material-symbols-outlined">delete</span>
                    Gỡ bỏ hạng vé
                </button>
            </div>
            <!-- Cấu hình sơ đồ ghế ngồi & sức chứa tự động -->
            <div class="seat-config-box mt-3">
                <div class="seat-config-title">
                    <span class="material-symbols-outlined">grid_view</span>
                    Cấu Hình Sơ Đồ Ghế Ngồi &amp; Sức Chứa Tự Động
                </div>

                <div class="row g-3">

                    <!-- Hàng dọc (select A-Z) -->
                    <div class="col-12 col-sm-4">
                        <div class="row-stepper-label">
                            <span class="material-symbols-outlined" style="font-size:14px;">unfold_more</span>
                            Hàng Dọc (A-Z)
                        </div>
                        <select class="tier-input row-select"
                                name="ticketTypes[${idx}].seat.row"
                                onchange="updateTierField('${t.id}', 'rowLetter', this.value)">
                            ${ROW_LETTERS.map((l) => `<option value="${l}" ${t.rowLetter === l ? "selected" : ""}>HÀNG ${l}</option>`).join("")}
                        </select>
                        <div class="row-stepper-sub">Tạo ${rows} hàng: ${rows > 1 ? "A-" + t.rowLetter : "A"}</div>
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
                            value="${t.cols}"
                            oninput="updateTierField('${t.id}', 'cols', Math.min(100, Math.max(1, parseInt(this.value) || 1)))" />
                        <div class="row-stepper-sub">Giới hạn 1 - 100 ghế</div>
                    </div>

                    <!-- Sức chứa ghế (auto) -->
                    <div class="col-12 col-sm-4">
                        <label class="tier-field-label">
                            <span class="material-symbols-outlined" style="font-size:14px;vertical-align:-2px;">lock</span>
                            Sức Chứa (QTY)
                        </label>
                        <div class="computed-inp">
                            <span>${t.totalQty}</span>
                            <span class="computed-auto">Tự động</span>
                        </div>
                        <div class="row-stepper-sub">Số lượng vé tổng</div>
                        <input type="hidden" name="ticketTypes[${idx}].capacity" value="${t.totalQty}" />
                    </div>

                </div>
            </div>

            <!-- Thanh phân bổ ghế tự động -->
            <div class="seat-alloc-bar" style="margin-bottom:15px;">
                <div class="seat-alloc-left">
                    <span class="material-symbols-outlined">event_seat</span>
                    Phân bổ ghế tự động: <span class="seat-alloc-code">${escHtml(zone)}</span>
                </div>
                <span class="seat-alloc-right">✓ AUTO ALLOCATION ENABLED</span>
            </div>
            <div class="row g-3 align-items-start">

                <!-- Loại vé -->
                <div class="col-12 col-sm-6 col-md-3">
                    <label class="tier-field-label">
                        Loại <span style="color:#ef4444;">*</span>
                    </label>
                    <input
                        type="number"
                        class="tier-input${errs.name ? " is-invalid-field" : ""}"
                        placeholder="Ví dụ:1,2"
                        name="ticketTypes[${idx}].DisplayOrder"
                        value="${escHtml(t.name || "")}"
                        oninput="updateTierField('${t.id}', 'name', this.value)" />
                    ${errDiv(errs.name)}
                </div>

                <!-- Phân khu / Zone name -->
                <div class="col-12 col-sm-6 col-md-3">
                    <label class="tier-field-label">
                        Phân Khu / Zone Name
                    </label>
                    <input
                        type="number"
                        class="tier-input"
                        placeholder="Ví dụ: Standee A, Zone VIP..."
                        name="ticketTypes[${idx}].zoneName"
                        value="${escHtml(t.zoneName || "")}"
                        oninput="updateTierField('${t.id}', 'zoneName', this.value)" />
                </div>

                <!-- Mệnh giá -->
                <div class="col-12 col-sm-6 col-md-3">
                    <div class="price-header">
                        <label class="tier-field-label mb-0">Mệnh Giá (đ)</label>
                    </div>
                    <div class="price-input-wrap mt-1">
                        <input
                            type= "number"
                            class="tier-input tier-input-mono"${errs.price ? " is-invalid-field" : ""}
                            name="ticketTypes[${idx}].price"
                            placeholder= "Ví dụ: 250000"
                            value="${t.price}"
                            min="0"
                            oninput="updateTierField('${t.id}', 'price', Math.max(0, Number(this.value) || 0))" />
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
                    name="ticketTypes[${idx}].Stock"
                    min="1"
                    max="${t.totalQty}"
                    value="${t.qty}"
                    oninput="
                        updateTierField(
                            '${t.id}',
                            'qty',
                            Math.min(
                                ${t.totalQty},
                                Math.max(1, parseInt(this.value) || 1)
                            )
                        )
                    " />
                    ${errDiv(errs.qty)}
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
                    placeholder="Ví dụ: Đồ uống chào mừng miễn phí, Ghế ngồi hàng đầu cận cảnh, Trọn bộ quà tặng kèm khi check-in lối đi riêng..."
                    oninput="updateTierField('${t.id}', 'desc', this.value)"
                >${escHtml(t.desc || "")}</textarea>
            </div>

        </div>

        `;
        })
        .join("");
}

// ─── GỬI FORM ─────────────────────────────────────────────────────────────────

function handleFormSubmit(e) {
    const tiersValid = validateAllTiers();
    const agendaValid = validateAllAgenda();

    // Vẽ lại để hiển thị / xóa các thông báo lỗi tương ứng
    renderTiers();
    renderAgenda();

    if (!tiersValid || !agendaValid) {
        e.preventDefault();
        showValidationBanner(
            "Vui lòng kiểm tra lại các trường được đánh dấu lỗi ở phần Mốc Thời Gian Sự Kiện và Phân Khúc Vé Mở Bán.",
        );
        scrollToFirstError();
        return false;
    }

    dismissError();
    return true;
}

function handleCancel() {
    if (confirm("Hủy bỏ tạo sự kiện? Tất cả thay đổi sẽ bị mất.")) {
        window.history.back();
    }
}

// ─── THÔNG BÁO LỖI / THÀNH CÔNG ────────────────────────────────────────────────
function showValidationBanner(msg) {
    const banner = document.getElementById("validationError");
    const msgEl = document.getElementById("validationErrorMsg");
    if (!banner || !msgEl) return;
    msgEl.textContent = msg;
    banner.classList.remove("d-none");
    banner.classList.add("d-flex");
}

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
    t.querySelector("span:last-child")
        ? (t.lastChild.textContent = isDraft
            ? " Đã lưu bản nháp thành công!"
            : " Sự kiện đã được đăng thành công!")
        : null;
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

// Tạo khối hiển thị lỗi text-danger, đồng bộ style với phần lỗi tĩnh (Thymeleaf) của form
function errDiv(msg) {
    return msg
        ? `<div class="text-danger" style="font-size: small;">${escHtml(msg)}</div>`
        : "";
}


const city = document.getElementById("province");
const ward = document.getElementById("ward")

city.addEventListener("change",function (){
    const cityValue = this.value;
    if (!cityValue){return}
    fetch(`/organizer/api/city?cityId=${cityValue}`)
        .then(r => r.json())
        .then(wards =>{
            wards.forEach(w=>{
                ward.innerHTML +=  ` <option value="${w.wardId}">${w.name}</option>`
            })
        }) .catch(err => console.error("Error loading ward:", err));
})