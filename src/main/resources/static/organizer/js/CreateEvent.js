"use strict";

// ─── STATE ────────────────────────────────────────────────────────────────────
let state = {
    bannerUrl: "",
    additionalImages: [],
    galleryFiles: [],
    tiers: [],
};

let tierIdCounter = 1;
function newTierId() {
    return "tier-" + Date.now() + "-" + tierIdCounter++;
}

// ─── INIT ─────────────────────────────────────────────────────────────────────
document.addEventListener("DOMContentLoaded", () => {
    setDefaultDate();

    // ✅ Gắn event listener SAU KHI DOM sẵn sàng — tránh lỗi null
    const dateInput   = document.getElementById("inp-date");
    const venueSelect = document.getElementById("venueSelect");

    // BƯỚC 1: Chọn ngày → load venue trống
    dateInput.addEventListener("change", function () {
        const date = this.value;

        // Reset venue + tier khi đổi ngày
        venueSelect.innerHTML = '<option value="">-- Please select verified venue --</option>';
        state.tiers = [];
        renderTiers();
        document.getElementById("venueDetail").classList.add("d-none");
        document.getElementById("venueEmpty").classList.remove("d-none");

        if (!date) return;

        fetch(`/organizer/api/venue?date=${date}`)
            .then(r => r.json())
            .then(venues => {
                venues.forEach(v => {
                    const opt = document.createElement("option");
                    opt.value       = v.venueID;
                    opt.textContent = v.venueName;
                    venueSelect.appendChild(opt);
                });
            })
            .catch(err => console.error("Error loading venues:", err));
    });

    // BƯỚC 2: Chọn venue → load địa chỉ
    // ✅ Chỉ 1 listener duy nhất — trước đó có 2 listener gây conflict
    venueSelect.addEventListener("change", function () {
        const venueId = this.value;

        // Reset tier khi đổi venue
        state.tiers = [];
        renderTiers();

        const venueDetail = document.getElementById("venueDetail");
        const venueEmpty  = document.getElementById("venueEmpty");

        if (!venueId) {
            venueDetail.classList.add("d-none");
            venueEmpty.classList.remove("d-none");
            return;
        }

        fetch(`/organizer/api/address?venueid=${venueId}`)
            .then(r => r.json())
            .then(venue => {
                venueDetail.classList.remove("d-none");
                venueEmpty.classList.add("d-none");
                venueDetail.innerHTML = `
                    <div class="venue-verified-badge">
                        <span class="material-symbols-outlined">verified</span>
                        VERIFIED REVENUE-SHARING VENUE ACTIVE
                    </div>
                    <div class="row g-3 mt-1">
                        <div class="col-6">
                            <span class="venue-detail-label">Venue Name</span>
                            <strong class="venue-detail-value d-block">
                                ${escHtml(venue.venueName || '')}
                            </strong>
                        </div>
                        <div class="col-6">
                            <span class="venue-detail-label">Address Line</span>
                            <span class="venue-detail-value d-block">
                                ${escHtml(venue.address?.specificAddress || 'N/A')}
                            </span>
                        </div>
                        <div class="col-6">
                            <span class="venue-detail-label">District Area</span>
                            <span class="venue-detail-value d-block">
                                ${escHtml(venue.address?.ward?.name || 'N/A')}
                            </span>
                        </div>
                        <div class="col-6">
                            <span class="venue-detail-label">Province / City</span>
                            <span class="venue-detail-value d-block">
                                ${escHtml(venue.address?.ward?.city?.name || 'N/A')}
                            </span>
                        </div>
                    </div>`;
            })
            .catch(err => console.error("Error loading venue address:", err));
    });
});

// ─── DATE ─────────────────────────────────────────────────────────────────────
function setDefaultDate() {
    const d = new Date();
    d.setDate(d.getDate() + 15);
    const iso = d.toISOString().split("T")[0];
    const dateEl = document.getElementById("inp-date");
    if (dateEl) dateEl.value = iso;
}

function handleDateChange(val) {
    document.getElementById("showtimeDateBadge").textContent = val || "Not set yet";
}

// ─── BANNER ───────────────────────────────────────────────────────────────────
function handleBannerFile(e) {
    const file = e.target.files[0];
    if (!file) return;
    const url = URL.createObjectURL(file);
    setBanner(url);
    document.getElementById("bannerUrl").value = url;
    state.bannerUrl = url;
}

function syncBannerFromUrl(url) {
    state.bannerUrl = url;
    setBanner(url);
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
    document.getElementById("bannerPreview").classList.add("d-none");
    document.getElementById("bannerHoverOverlay").classList.add("d-none");
    document.getElementById("bannerPlaceholder").classList.remove("d-none");
    state.bannerUrl = "";
}

// ─── GALLERY ──────────────────────────────────────────────────────────────────
function handleGalleryFiles(e) {
    const files = Array.from(e.target.files || []);
    files.forEach(file => {
        state.galleryFiles.push(file);
        state.additionalImages.push(URL.createObjectURL(file));
    });
    renderGallery();
}

function addGalleryUrl() {
    const input = document.getElementById("galleryUrlInput");
    const url   = input.value.trim();
    if (!url) return;
    state.additionalImages.push(url);
    input.value = "";
    renderGallery();
}

function removeGalleryImage(idx) {
    state.additionalImages.splice(idx, 1);
    state.galleryFiles.splice(idx, 1);
    renderGallery();
}

function renderGallery() {
    const grid  = document.getElementById("galleryGrid");
    const count = document.getElementById("galleryCount");
    count.textContent = state.additionalImages.length + " ADDED";

    if (state.additionalImages.length === 0) {
        grid.classList.add("d-none");
        grid.innerHTML = "";
        return;
    }

    grid.classList.remove("d-none");
    grid.innerHTML = state.additionalImages.map((url, i) => `
        <div class="gallery-item">
            <img src="${escHtml(url)}" alt="Gallery ${i + 1}" loading="lazy"/>
            <button class="gallery-item-del" type="button"
                    onclick="removeGalleryImage(${i})" title="Remove">
                <span class="material-symbols-outlined">close</span>
            </button>
        </div>
    `).join("");
}

// ─── LOGO ─────────────────────────────────────────────────────────────────────
function handleLogoFile(e) {
    const file = e.target.files[0];
    if (!file) return;
    const url = URL.createObjectURL(file);
    document.getElementById("logoPreview").src = url;
    document.getElementById("logoPreview").classList.remove("d-none");
    document.getElementById("logoPlaceholder").classList.add("d-none");
}

// ─── TICKET TIERS ─────────────────────────────────────────────────────────────

// BƯỚC 3: Thêm tier → fetch zones của venue đang chọn
async function addTicketTier() {
    const venueId = document.getElementById("venueSelect").value;
    if (!venueId) {
        alert("Please select a venue first.");
        return;
    }

    let zones = [];
    try {
        const response = await fetch(`/organizer/api/venueid?id=${venueId}`);
        zones = await response.json();
        console.log(zones)
    } catch (e) {
        console.error("Error loading zones:", e);
    }

    state.tiers.push({
        id:          newTierId(),
        name:        "",
        price:       0,
        isFree:      false,
        qty:         0,
        desc:        "",
        zoneId:      "",      // ✅ lưu ID số — gửi lên server
        zone:        "",      // lưu zoneName — chỉ để hiển thị
        maxCapacity: 0,       // ✅ lưu capacity — để validate qty
        zones:       zones    // danh sách zones để render <option>
    });

    renderTiers();
}

function deleteTier(id) {
    state.tiers = state.tiers.filter(t => t.id !== id);
    renderTiers();
}

function updateTierField(id, field, value) {
    const tier = state.tiers.find(t => t.id === id);
    if (!tier) return;

    if (field === "isFree" && value === true) {
        tier.price = 0;
    }

    tier[field] = value;

    // Chỉ re-render khi toggle isFree — tránh re-render mỗi keystroke
    if (field === "isFree") {
        renderTiers();
    }
}

// ✅ Hàm xử lý khi chọn zone — lưu zoneId + maxCapacity vào state
function handleZoneSelectChange(tierId, selectEl) {
    const tier = state.tiers.find(t => t.id === tierId);
    if (!tier) return;

    const chosen = selectEl.options[selectEl.selectedIndex];

    tier.zoneId      = selectEl.value;                          // ID số → gửi server
    tier.zone        = chosen.dataset.name || "";               // tên → hiển thị
    tier.maxCapacity = parseInt(chosen.dataset.max || "0");     // capacity → validate
    tier.qty         = 0;                                       // reset qty khi đổi zone
    console.log("zoneId saved:", tier.zoneId, typeof tier.zoneId);
    // renderTiers();
    // ❌ Bỏ renderTiers() — không cần re-render, chỉ cần cập nhật label qty
    const qtyLabel = document.querySelector(`#card-${tierId} .tier-field-label span.text-muted`);
    if (qtyLabel) {
        qtyLabel.textContent = tier.maxCapacity ? `(tối đa ${tier.maxCapacity})` : "";
    }

    // Reset qty input
    const qtyInput = document.querySelector(`#card-${tierId} input[name*=".stock"]`);
    if (qtyInput) {
        qtyInput.value = "";
        qtyInput.max   = tier.maxCapacity || "";
    }
}

// ✅ Validate qty — dùng lỗi inline thay vì alert
function validateTicketQty(id, qty) {
    const tier  = state.tiers.find(t => t.id === id);
    if (!tier) return;

    const max   = tier.maxCapacity || 0;
    const errEl = document.getElementById(`qtyerr-${id}`);

    if (max > 0 && qty > max) {
        tier.qty = max;

        // Sửa thẳng value input — không re-render cả list
        const inputEl = document.querySelector(`#card-${id} input[name*=".stock"]`);
        if (inputEl) inputEl.value = max;

        if (errEl) {
            errEl.textContent   = `Zone này chỉ có ${max} ghế.`;
            errEl.style.display = "";
        }
    } else {
        tier.qty = qty;
        if (errEl) errEl.style.display = "none";
    }
}

// ✅ renderTiers — đã sửa toàn bộ 4 lỗi
function renderTiers() {
    const container  = document.getElementById("ticketTiersContainer");
    const emptyState = document.getElementById("ticketTiersEmpty");

    if (state.tiers.length === 0) {
        emptyState.style.display = "";
        container.innerHTML = "";
        return;
    }

    emptyState.style.display = "none";

    container.innerHTML = state.tiers.map((t, idx) => `

        <div class="ticket-tier-card fade-in" id="card-${t.id}">

            <div class="ticket-tier-header">
                <div class="d-flex align-items-center gap-2">
                    <span class="tier-num-badge">${idx + 1}</span>
                    <span class="tier-config-label">Ticket Tier Configuration</span>
                </div>
                <button type="button" class="btn-delete-tier"
                        onclick="deleteTier('${t.id}')">
                    <span class="material-symbols-outlined">delete</span>
                    Remove Tier
                </button>
            </div>

            <div class="row g-3 mb-3">

                <!-- Tên loại vé -->
                <div class="col-12 col-sm-6 col-md-3">
                    <label class="tier-field-label">
                        Category Name <span style="color:#ef4444;">*</span>
                    </label>
                    <input
                        type="text"
                        class="tier-input"
                        placeholder="e.g. Standard Pass, VIP Area"
                        name="ticketTypes[${idx}].typeName"
                        value="${escHtml(t.name || '')}"
                        oninput="updateTierField('${t.id}', 'name', this.value)" />
                </div>

                <!-- Zone select -->
                <div class="col-12 col-sm-6 col-md-3">
                    <label class="tier-field-label">Zone / Seat Location</label>
                    <select
                        class="tier-input"
                        name="ticketTypes[${idx}].zoneID"
                        onchange="handleZoneSelectChange('${t.id}', this)">

                        <option value="">-- Select Zone --</option>

${(t.zones || []).map(zone => `
    <option
        value="${zone.zoneID || ''}"                 
        data-name="${escHtml(zone.zoneName)}"
        data-max="${zone.rows * zone.seatsPerRow}"
        ${String(t.zoneId) === String(zone.zoneID) ? "selected" : ""}>   
        ${escHtml(zone.zoneName)}
        (max ${zone.rows * zone.seatsPerRow})
    </option>
`).join("")}

                    </select>
                </div>

                <!-- Giá -->
                <div class="col-12 col-sm-6 col-md-3">
                    <div class="d-flex justify-content-between align-items-center mb-1">
                        <label class="tier-field-label mb-0">Price (₫)</label>
                        <button type="button"
                                class="btn-free-toggle ${t.isFree ? 'is-free' : 'is-paid'}"
                                onclick="updateTierField('${t.id}', 'isFree', ${!t.isFree})">
                            ${t.isFree ? "FREE" : "PAID"}
                        </button>
                    </div>
                    <div class="price-input-wrap">
                        <input
                            type="number"
                            class="tier-input tier-input-mono ${t.isFree ? 'tier-input-free' : ''}"
                            name="ticketTypes[${idx}].price"
                            placeholder="e.g. 250000"
                            ${t.isFree ? "disabled" : ""}
                            value="${t.isFree ? 0 : (t.price || 0)}"
                            min="0"
                            oninput="updateTierField('${t.id}', 'price',
                                     Math.max(0, Number(this.value) || 0))" />
                        ${!t.isFree ? '<span class="price-suffix">₫</span>' : ""}
                    </div>
                    <!-- ✅ Hidden input — Spring đọc được isFree = false -->
                    <input type="hidden"
                           name="ticketTypes[${idx}].isFree"
                           value="${t.isFree}" />
                </div>

                <!-- Số lượng -->
                <div class="col-12 col-sm-6 col-md-3">
                    <label class="tier-field-label">
                        Quantity
                        <span class="text-muted fw-normal" style="font-size:11px;">
                            ${t.maxCapacity ? `(tối đa ${t.maxCapacity})` : ""}
                        </span>
                    </label>
                    <input
                        type="number"
                        class="tier-input tier-input-mono"
                        name="ticketTypes[${idx}].stock"
                        placeholder="e.g. 200"
                        min="1"
                        ${t.maxCapacity ? `max="${t.maxCapacity}"` : ""}
                        value="${t.qty || ''}"
                        oninput="validateTicketQty('${t.id}',
                                 Math.max(0, parseInt(this.value) || 0))" />
                    <div id="qtyerr-${t.id}"
                         style="color:#ef4444; font-size:12px; margin-top:4px; display:none;">
                    </div>
                </div>

            </div>

            <!-- Description -->
            <div class="border-top pt-3">
                <label class="tier-field-label">
                    Benefits, Perks &amp; Tier Description
                </label>
                <textarea
                    class="tier-input"
                    name="ticketTypes[${idx}].description"
                    placeholder="e.g. Welcome drink, Front row seats..."
                    oninput="updateTierField('${t.id}', 'desc', this.value)"
                >${escHtml(t.desc || "")}</textarea>
            </div>

        </div>

    `).join("");
}

// ─── SUBMIT ───────────────────────────────────────────────────────────────────
function handleSubmit(isDraft) {
    const eventName = getVal("eventName").trim();
    const venueId   = getVal("venueSelect").trim();

    if (!eventName) {
        showError("Please specify an Event Name before publishing!");
        document.getElementById("section-event-info")
            .scrollIntoView({ behavior: "smooth" });
        return;
    }
    if (!venueId) {
        showError("Please select a verified hosting venue!");
        document.getElementById("section-date-venue")
            .scrollIntoView({ behavior: "smooth" });
        return;
    }
    if (state.tiers.length === 0) {
        showError("Please configure at least one Ticket Tier!");
        document.getElementById("section-showtimes")
            .scrollIntoView({ behavior: "smooth" });
        return;
    }

    // Validate từng tier trước khi submit
    for (let i = 0; i < state.tiers.length; i++) {
        const t = state.tiers[i];
        if (!t.zoneId) {
            showError(`Loại vé #${i + 1}: chưa chọn Zone.`);
            return;
        }
        if (!t.qty || t.qty < 1) {
            showError(`Loại vé #${i + 1}: số lượng phải ít nhất là 1.`);
            return;
        }
    }

    // ✅ Đẩy file gallery vào input file trước khi submit form
    const dataTransfer = new DataTransfer();
    state.galleryFiles.forEach(file => dataTransfer.items.add(file));
    const galleryInput = document.getElementById("galleryFileInput");
    if (galleryInput) galleryInput.files = dataTransfer.files;

    // Submit form thật (Thymeleaf + Spring MVC)
    document.getElementById("eventForm").submit();
}

// ─── ERROR HELPERS ────────────────────────────────────────────────────────────
function showError(msg) {
    const el = document.getElementById("validationError");
    document.getElementById("validationErrorMsg").textContent = msg;
    el.classList.remove("d-none");
    el.classList.add("d-flex");
    el.scrollIntoView({ behavior: "smooth", block: "nearest" });
}

function dismissError() {
    const el = document.getElementById("validationError");
    el.classList.add("d-none");
    el.classList.remove("d-flex");
}

// ─── UTILS ────────────────────────────────────────────────────────────────────
function getVal(id) {
    const el = document.getElementById(id);
    return el ? el.value : "";
}

function setValue(id, val) {
    const el = document.getElementById(id);
    if (el) el.value = val;
}

function escHtml(str) {
    return String(str)
        .replace(/&/g, "&amp;")
        .replace(/"/g, "&quot;")
        .replace(/</g, "&lt;")
        .replace(/>/g, "&gt;");
}