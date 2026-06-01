/**
 * Create Event Wizard — wizard.js
 * Vanilla JS logic converted from React/TypeScript
 */

// ════════════════════════════════════════════
//  DATA: Predefined Locations
// ════════════════════════════════════════════
const PREDEFINED_LOCATIONS = [
    {
        id: "secc",
        placeName: "Saigon Exhibition and Convention Center (SECC) - Hall A",
        street: "799 Nguyen Van Linh",
        district: "District 7",
        province: "Ho Chi Minh City",
        zones: [
            "Standee Area A (VIP)",
            "Standee Area B",
            "Standard Standee Area",
            "Gold Zone Pit",
        ],
    },
    {
        id: "phutho",
        placeName: "Phu Tho Indoor Stadium",
        street: "1 Lu Gia",
        district: "District 11",
        province: "Ho Chi Minh City",
        zones: [
            "VIP Floor Seats",
            "Stand A (Ground floor)",
            "Stand B (1st Floor)",
            "Stand C (2nd Floor)",
        ],
    },
    {
        id: "hoabinh",
        placeName: "Hoa Binh Theatre",
        street: "240 3 Thang 2 Street",
        district: "District 10",
        province: "Ho Chi Minh City",
        zones: [
            "VIP Seats 1st Floor",
            "Standard Seats 1st Floor",
            "Auditorium Level 2 - Front",
            "Auditorium Level 2 - Back",
        ],
    },
    {
        id: "national-convention",
        placeName: "National Convention Center (NCC)",
        street: "Gate 1, Thang Long Boulevard",
        district: "Nam Tu Liem District",
        province: "Hanoi",
        zones: [
            "VIP Stage Front",
            "Stand 1st Floor Center",
            "Stand 2nd Floor",
            "Left/Right Wings",
        ],
    },
    {
        id: "hanoi-opera",
        placeName: "Hanoi Opera House",
        street: "1 Trang Tien",
        district: "Hoan Kiem District",
        province: "Hanoi",
        zones: [
            "VIP Center Seats",
            "Mezzanine Royal Box",
            "Wing Premium Seats",
            "Level 3 General Admission",
        ],
    },
    {
        id: "mydinh",
        placeName: "My Dinh National Stadium",
        street: "Le Duc Tho Street",
        district: "Nam Tu Liem District",
        province: "Hanoi",
        zones: [
            "VIP Stand A - Center",
            "A-Standard Seats",
            "Stand B - Standee",
            "Stand C/D",
        ],
    },
    {
        id: "tienson",
        placeName: "Tien Son Sports Complex",
        street: "Phan Dang Luu",
        district: "District of Hai Chau",
        province: "Da Nang",
        zones: ["VIP Field Area", "1st Floor Stands", "2nd Floor Stands"],
    },
];

// ════════════════════════════════════════════
//  DATA: Thematic Presets
// ════════════════════════════════════════════
const THEMATIC_PRESETS = [
    {
        name: "Cosmic Rave Space Festival 2026",
        category: "Entertainment",
        placeName: "Saigon Exhibition and Convention Center (SECC) - Hall A",
        street: "799 Nguyen Van Linh",
        district: "District 7",
        province: "Ho Chi Minh City",
        bannerHorizontal:
            "https://images.unsplash.com/photo-1470225620780-dba8ba36b745?w=1600&auto=format&fit=crop",
        organizerName: "Minh Event Corporation",
        organizerPhone: "0901 000 999",
        description: `Event Description:\nA premium signature EDM & light-mapping concert with immersive surround systems, mesmerizing holographic visual lasers, and rich base drops.\n\nEvent Details:\n- Main Stage: Live continuous DJ lineup, non-stop laser sequences, and stage pyrotechnics.\n- Guest Artists: Global top-10 electronic music co-headliners and premium ambient visualists.\n- Special Experiences: Sensory light domes, glowing cocktail stations, and custom photo booths.\n\nTerms and Conditions:\n- Aged 18+ valid government photo ID card required at entry gates.\n- Strictly prohibited outside foods, liquids, and flammable equipment.`,
        additionalImages: [
            "https://images.unsplash.com/photo-1506157786151-b8491531f063?w=800&auto=format&fit=crop",
            "https://images.unsplash.com/photo-1459749411175-04bf5292ceea?w=800&auto=format&fit=crop",
            "https://images.unsplash.com/photo-1470229722913-7c0e2dbbafd3?w=800&auto=format&fit=crop",
        ],
        showtimes: [
            {
                id: "slot-default",
                date: "2026-06-25",
                startTime: "19:00",
                endTime: "22:30",
                ticketTiers: [
                    {
                        id: "tkt-default-std",
                        name: "Standard Ticket",
                        price: 350000,
                        isFree: false,
                        totalQuantity: 1000,
                        description: "General admission wristband, standard standing area.",
                        zone: "Standard Standee Area",
                    },
                    {
                        id: "tkt-default-vip",
                        name: "VIP Super Fan Pass",
                        price: 1200000,
                        isFree: false,
                        totalQuantity: 150,
                        description:
                            "Special side-stage access, dedicated fast-track entrance, and custom light-stick merchandise package.",
                        zone: "Standee Area A (VIP)",
                    },
                ],
            },
        ],
    },
    {
        name: "Vietnam Horizon AI Technology Summit",
        category: "Technology",
        placeName: "National Convention Center (NCC)",
        street: "Gate 1, Thang Long Boulevard",
        district: "Nam Tu Liem District",
        province: "Hanoi",
        bannerHorizontal:
            "https://images.unsplash.com/photo-1540575467063-178a50c2df87?w=1600&auto=format&fit=crop",
        organizerName: "TechVibe Alliance LTD",
        organizerPhone: "0912 111 222",
        description: `Event Description:\nDelve into the future of Artificial Intelligence, Large Language Models, and deep robotic system integrations driving Southeast Asia's tech growth.\n\nEvent Details:\n- Panel Conversations: Engaging live dialogues with world-tier computer vision and NLP researchers.\n- Demo Exhibition Hall: Interact directly with prototype humanoid hardware and innovative SaaS suites.\n\nTerms and Conditions:\n- Registration is required prior to entrance.\n- Smart casual dress code.`,
        additionalImages: [
            "https://images.unsplash.com/photo-1485827404703-89b55fcc595e?w=800&auto=format&fit=crop",
            "https://images.unsplash.com/photo-1515187029135-18ee286d815b?w=800&auto=format&fit=crop",
        ],
        showtimes: [
            {
                id: "slot-tech",
                date: "2026-07-15",
                startTime: "08:30",
                endTime: "17:30",
                ticketTiers: [
                    {
                        id: "tkt-tech-std",
                        name: "General Access Pass",
                        price: 150000,
                        isFree: false,
                        totalQuantity: 800,
                        description:
                            "Access to main panel stages and the general exhibit booths.",
                        zone: "Stand 1st Floor Center",
                    },
                    {
                        id: "tkt-tech-vip",
                        name: "VIP Executive Pass",
                        price: 850000,
                        isFree: false,
                        totalQuantity: 100,
                        description:
                            "Includes front-row seating, catered buffet lunch, and access to private networking lounge.",
                        zone: "VIP Stage Front",
                    },
                ],
            },
        ],
    },
    {
        name: "Chroma Light Art Exposition",
        category: "Art",
        placeName: "Hanoi Opera House",
        street: "1 Trang Tien",
        district: "Hoan Kiem District",
        province: "Hanoi",
        bannerHorizontal:
            "https://images.unsplash.com/photo-1531058020387-3be344559be6?w=1600&auto=format&fit=crop",
        organizerName: "Saison d'Art",
        organizerPhone: "0933 876 543",
        description: `Event Description:\nAn ambient gallery showcasing progressive fusion art that merges physical canvas brushstrokes with real-time reactive projection mapping.\n\nEvent Details:\n- Artists Talks: Exclusive evening discussions detailing modern digital crafting workflows.\n- Interactive Projection Wall: Create continuous geometric watercolor shapes using bodily gestures.\n\nTerms and Conditions:\n- Flash photography is strictly forbidden near physical oil paintings.\n- Children under 12 must be accompanied by an adult.`,
        additionalImages: [
            "https://images.unsplash.com/photo-1513364776144-60967b0f800f?w=800&auto=format&fit=crop",
            "https://images.unsplash.com/photo-1501183007986-d0d080b147f9?w=800&auto=format&fit=crop",
        ],
        showtimes: [
            {
                id: "slot-art",
                date: "2026-08-05",
                startTime: "10:00",
                endTime: "21:00",
                ticketTiers: [
                    {
                        id: "tkt-art-std",
                        name: "Standard Gallery Admission",
                        price: 90000,
                        isFree: false,
                        totalQuantity: 300,
                        description:
                            "Access to the interactive exhibits for 1 entire custom timespan.",
                        zone: "Level 3 General Admission",
                    },
                    {
                        id: "tkt-art-prem",
                        name: "Premium Collector Ticket",
                        price: 250000,
                        isFree: false,
                        totalQuantity: 50,
                        description:
                            "Guided curator tour and souvenir premium artwork catalogue collection.",
                        zone: "VIP Center Seats",
                    },
                ],
            },
        ],
    },
];

// ════════════════════════════════════════════
//  STATE
// ════════════════════════════════════════════
let state = {
    eventName: "",
    category: "Entertainment",
    description: "",
    bannerHorizontal: "",
    additionalImages: [],
    organizerName: "",
    organizerPhone: "",
    organizerLogo: "",
    placeName: "",
    province: "",
    district: "",
    street: "",
    eventDate: "",
    showtimes: [],
    selectedShowtimeId: null,
};

// ════════════════════════════════════════════
//  INIT
// ════════════════════════════════════════════
document.addEventListener("DOMContentLoaded", () => {
    initDefaultDate();
    renderPresets();
    renderVenueOptions();
});

function initDefaultDate() {
    const d = new Date();
    d.setDate(d.getDate() + 15);
    const dateStr = d.toISOString().split("T")[0];
    state.eventDate = dateStr;
    document.getElementById("eventDate").value = dateStr;

    const slot = createShowtimeSlot(dateStr, "19:00", "22:30", []);
    state.showtimes = [slot];
    state.selectedShowtimeId = slot.id;
    renderShowtimes();
}

// ════════════════════════════════════════════
//  PRESETS
// ════════════════════════════════════════════
function renderPresets() {
    const grid = document.getElementById("presetGrid");
    grid.innerHTML = THEMATIC_PRESETS.map(
        (p, i) => `
    <div class="col-6 col-sm-4 col-md-3 col-lg-2">
      <div class="preset-card" onclick="applyPreset(${i})">
        <img src="${p.bannerHorizontal}" alt="${p.name}" />
        <div class="preset-card-body">
          <div class="preset-card-name" title="${p.name}">${p.name}</div>
          <div class="preset-card-cat">${p.category}</div>
        </div>
      </div>
    </div>
  `,
    ).join("");
}

function applyPreset(idx) {
    const p = THEMATIC_PRESETS[idx];

    // Fill form fields
    setValue("eventName", p.name);
    document.getElementById("nameLen").textContent = p.name.length;
    setValue("eventCategory", p.category);
    setValue("eventDescription", p.description);
    setValue("organizerName", p.organizerName);
    setValue("organizerPhone", p.organizerPhone || "");

    // Banner
    state.bannerHorizontal = p.bannerHorizontal;
    setValue("bannerUrlInput", p.bannerHorizontal);
    updateCoverPreview(p.bannerHorizontal);

    // Gallery
    state.additionalImages = [...(p.additionalImages || [])];
    renderGallery();

    // Venue
    const loc = PREDEFINED_LOCATIONS.find((l) => l.placeName === p.placeName);
    if (loc) {
        state.placeName = loc.placeName;
        state.street = loc.street;
        state.district = loc.district;
        state.province = loc.province;
        document.getElementById("venueSelect").value = loc.id;
        showVenueDetails(loc);
    }

    // Date & showtimes
    if (p.showtimes && p.showtimes.length > 0) {
        const src = p.showtimes[0];
        state.eventDate = src.date;
        document.getElementById("eventDate").value = src.date;

        const cloned = {
            id: `slot-${Date.now()}`,
            date: src.date,
            startTime: src.startTime,
            endTime: src.endTime,
            ticketTiers: src.ticketTiers.map((t) => ({
                ...t,
                id: `tkt-${Date.now()}-${Math.random().toString(36).slice(2, 6)}`,
            })),
        };
        state.showtimes = [cloned];
        state.selectedShowtimeId = cloned.id;
        renderShowtimes();
    }

    dismissAlert();
}

// ════════════════════════════════════════════
//  COVER IMAGE
// ════════════════════════════════════════════
function handleCoverFile(e) {
    const file = e.target.files[0];
    if (!file) return;
    const url = URL.createObjectURL(file);
    state.bannerHorizontal = url;
    document.getElementById("bannerUrlInput").value = "";
    updateCoverPreview(url);
}

function syncBannerUrl(url) {
    state.bannerHorizontal = url;
    updateCoverPreview(url);
}

function updateCoverPreview(url) {
    const preview = document.getElementById("coverPreview");
    const placeholder = document.getElementById("coverPlaceholder");
    if (url) {
        preview.src = url;
        preview.style.display = "block";
        placeholder.style.display = "none";
    } else {
        preview.style.display = "none";
        placeholder.style.display = "";
    }
}

// ════════════════════════════════════════════
//  GALLERY
// ════════════════════════════════════════════
function handleGalleryFiles(e) {
    const files = e.target.files;
    if (!files) return;
    for (const file of files) {
        state.additionalImages.push(URL.createObjectURL(file));
    }
    renderGallery();
}

function addGalleryUrl() {
    const input = document.getElementById("galleryUrlInput");
    const url = input.value.trim();
    if (!url) return;
    state.additionalImages.push(url);
    input.value = "";
    renderGallery();
}

function removeGalleryImage(idx) {
    state.additionalImages.splice(idx, 1);
    renderGallery();
}

function renderGallery() {
    const grid = document.getElementById("galleryGrid");
    const count = document.getElementById("galleryCount");
    count.textContent = state.additionalImages.length;

    if (state.additionalImages.length === 0) {
        grid.style.display = "none";
        grid.innerHTML = "";
        return;
    }

    grid.style.display = "grid";
    grid.innerHTML = state.additionalImages
        .map(
            (img, i) => `
    <div class="gallery-item">
      <img src="${img}" alt="Gallery ${i}" />
      <button class="gallery-item-del" onclick="removeGalleryImage(${i})" title="Delete">&times;</button>
    </div>
  `,
        )
        .join("");
}

// ════════════════════════════════════════════
//  ORGANIZER LOGO
// ════════════════════════════════════════════
function handleLogoFile(e) {
    const file = e.target.files[0];
    if (!file) return;
    const url = URL.createObjectURL(file);
    state.organizerLogo = url;
    document.getElementById("logoPreview").src = url;
    document.getElementById("logoPreview").style.display = "block";
    document.getElementById("logoPlaceholder").style.display = "none";
}

// ════════════════════════════════════════════
//  VENUE
// ════════════════════════════════════════════
function renderVenueOptions() {
    const sel = document.getElementById("venueSelect");
    PREDEFINED_LOCATIONS.forEach((loc) => {
        const opt = document.createElement("option");
        opt.value = loc.id;
        opt.textContent = `${loc.placeName} (${loc.province})`;
        sel.appendChild(opt);
    });
}

function handleVenueChange(id) {
    const loc = PREDEFINED_LOCATIONS.find((l) => l.id === id);
    if (loc) {
        state.placeName = loc.placeName;
        state.street = loc.street;
        state.district = loc.district;
        state.province = loc.province;
        showVenueDetails(loc);
    } else {
        state.placeName = state.street = state.district = state.province = "";
        document.getElementById("venueDetails").style.display = "none";
        document.getElementById("venueEmpty").style.display = "";
    }
}

function showVenueDetails(loc) {
    document.getElementById("vd-name").textContent = loc.placeName;
    document.getElementById("vd-street").textContent = loc.street;
    document.getElementById("vd-district").textContent = loc.district;
    document.getElementById("vd-province").textContent = loc.province;
    document.getElementById("venueDetails").style.display = "";
    document.getElementById("venueEmpty").style.display = "none";
}

// ════════════════════════════════════════════
//  DATE
// ════════════════════════════════════════════
function handleDateChange(dateStr) {
    if (!dateStr) return;
    state.eventDate = dateStr;
    // Sync all showtimes dates
    state.showtimes = state.showtimes.map((s) => ({ ...s, date: dateStr }));
    renderShowtimes();
}

// ════════════════════════════════════════════
//  SHOWTIMES
// ════════════════════════════════════════════
function createShowtimeSlot(date, startTime, endTime, ticketTiers) {
    return {
        id: `slot-${Date.now()}-${Math.random().toString(36).slice(2, 6)}`,
        date,
        startTime,
        endTime,
        ticketTiers: ticketTiers || [],
    };
}

function addNewShowtime() {
    const date = state.eventDate || "";
    const slot = createShowtimeSlot(date, "19:00", "22:00", [
        {
            id: `tkt-${Date.now()}`,
            name: "General Admission",
            price: 150000,
            isFree: false,
            totalQuantity: 100,
            description: "Basic entry ticket",
            zone: "",
        },
    ]);
    state.showtimes.push(slot);
    state.selectedShowtimeId = slot.id;
    renderShowtimes();
}

function deleteShowtime(slotId) {
    if (state.showtimes.length === 1) {
        alert("Event must retain at least one configured showtime.");
        return;
    }
    state.showtimes = state.showtimes.filter((s) => s.id !== slotId);
    if (state.selectedShowtimeId === slotId) {
        state.selectedShowtimeId = state.showtimes[0]?.id || null;
    }
    renderShowtimes();
}

function selectShowtime(slotId) {
    state.selectedShowtimeId = slotId;
    renderShowtimes();
}

function updateShowtimeField(slotId, field, value) {
    state.showtimes = state.showtimes.map((s) =>
        s.id === slotId ? { ...s, [field]: value } : s,
    );
}

function renderShowtimes() {
    const tabsEl = document.getElementById("showtimeTabs");
    const panelEl = document.getElementById("showtimePanel");
    const emptyEl = document.getElementById("showtimeEmpty");

    if (state.showtimes.length === 0) {
        tabsEl.innerHTML = "";
        panelEl.innerHTML = "";
        emptyEl.style.display = "";
        return;
    }

    emptyEl.style.display = "none";

    // Tabs
    tabsEl.innerHTML = state.showtimes
        .map(
            (slot, idx) => `
    <button type="button"
      class="showtime-tab ${slot.id === state.selectedShowtimeId ? "active" : ""}"
      onclick="selectShowtime('${slot.id}')">
      <span class="material-symbols-outlined" style="font-size:13px">schedule</span>
      Show #${idx + 1}: ${slot.date} ${slot.startTime ? `(${slot.startTime})` : ""}
    </button>
  `,
        )
        .join("");

    // Active panel
    const active = state.showtimes.find((s) => s.id === state.selectedShowtimeId);
    if (!active) {
        panelEl.innerHTML = "";
        return;
    }

    const sIdx = state.showtimes.indexOf(active);

    const ticketsHtml =
        active.ticketTiers.length === 0
            ? `<div class="empty-state">No ticket tiers configured. Add a category above!</div>`
            : `<div class="ticket-grid">
        ${active.ticketTiers
                .map(
                    (t) => `
          <div class="ticket-card">
            <button class="ticket-card-del" onclick="deleteTicket('${active.id}','${t.id}')" title="Delete">&times;</button>
            <div>
              <div class="d-flex flex-wrap gap-1">
                ${t.isFree ? `<span class="ticket-badge-free">FREE</span>` : ""}
                ${t.zone ? `<span class="ticket-badge-zone">${escHtml(t.zone)}</span>` : ""}
              </div>
              <div class="ticket-name">${escHtml(t.name)}</div>
              <div class="ticket-desc">${escHtml(t.description || "")}</div>
            </div>
            <div class="ticket-footer">
              <div>
                <span class="ticket-price-label">Price</span>
                <span class="ticket-price-value">${t.isFree ? "Free" : t.price.toLocaleString("vi-VN") + " ₫"}</span>
              </div>
              <div class="text-end">
                <span class="ticket-qty-label">Inventory</span>
                <span class="ticket-qty-value">${t.totalQuantity} items</span>
              </div>
            </div>
          </div>
        `,
                )
                .join("")}
      </div>`;

    // Date options for the slot selector
    const dateOptions = state.eventDate
        ? `<option value="${state.eventDate}">${state.eventDate}</option>`
        : '<option value="">-- Choose Date --</option>';

    panelEl.innerHTML = `
    <div class="showtime-panel animate-fade-in">
      <div class="showtime-panel-badge">Editing Show #${sIdx + 1}</div>

      <div class="d-flex flex-wrap justify-content-between align-items-center gap-2 mb-3 pt-2">
        <span class="mono-tag">Configure Schedule Hours</span>
        <button type="button" class="btn-outline-danger btn-sm"
          onclick="deleteShowtime('${active.id}')">
          <span class="material-symbols-outlined" style="font-size:13px">delete</span>
          Remove Slot
        </button>
      </div>

      <div class="row g-3 mb-4 pb-3" style="border-bottom:1px solid rgba(0,0,0,.05)">
        <div class="col-sm-4">
          <div class="mono-tag mb-1">ASSIGNED DATE</div>
          <select class="wiz-select" onchange="updateShowtimeField('${active.id}','date',this.value);renderShowtimes()">
            ${dateOptions}
          </select>
        </div>
        <div class="col-sm-4">
          <div class="mono-tag mb-1">GATE OPENING HOUR</div>
          <input type="text" class="wiz-input" value="${active.startTime}"
            placeholder="e.g. 19:00"
            onchange="updateShowtimeField('${active.id}','startTime',this.value);renderShowtimes()" />
        </div>
        <div class="col-sm-4">
          <div class="mono-tag mb-1">EXPECTED ENDING HOUR</div>
          <input type="text" class="wiz-input" value="${active.endTime}"
            placeholder="e.g. 22:30"
            onchange="updateShowtimeField('${active.id}','endTime',this.value);renderShowtimes()" />
        </div>
      </div>

      <div class="d-flex align-items-center justify-content-between mb-3">
        <span class="mono-tag">Ticket Pricing &amp; Categories</span>
        <button type="button" class="btn-purple btn-sm" onclick="openTicketModal()">
          Create Ticket Tier +
        </button>
      </div>

      ${ticketsHtml}
    </div>
  `;
}

// ════════════════════════════════════════════
//  TICKET MODAL
// ════════════════════════════════════════════
function openTicketModal() {
    // Reset fields
    setValue("tkt-name", "");
    setValue("tkt-price", "");
    setValue("tkt-qty", "");
    setValue("tkt-desc", "");
    document.getElementById("tkt-free").checked = false;
    document.getElementById("tkt-price").disabled = false;

    // Populate zone options
    const sel = document.getElementById("tkt-zone");
    sel.innerHTML = "";
    const loc = PREDEFINED_LOCATIONS.find((l) => l.placeName === state.placeName);
    const zones = loc
        ? loc.zones
        : ["VIP Zone", "Standard Zone", "General Admission"];
    zones.forEach((z) => {
        const opt = document.createElement("option");
        opt.value = z;
        opt.textContent = z;
        sel.appendChild(opt);
    });

    document.getElementById("ticketModal").style.display = "flex";
}

function closeTicketModal() {
    document.getElementById("ticketModal").style.display = "none";
}

function confirmAddTicket() {
    const name = document.getElementById("tkt-name").value.trim();
    if (!name) {
        alert("Please specify a ticket category name!");
        return;
    }
    if (!state.selectedShowtimeId) {
        showAlert("Please select or create a Showtime Slot first!");
        return;
    }

    const isFree = document.getElementById("tkt-free").checked;
    const price = isFree
        ? 0
        : Number(document.getElementById("tkt-price").value) || 0;
    const qty = Number(document.getElementById("tkt-qty").value) || 100;
    const desc =
        document.getElementById("tkt-desc").value.trim() ||
        "No description provided.";
    const zone = document.getElementById("tkt-zone").value;

    const tier = {
        id: `tkt-${Date.now()}`,
        name,
        price,
        isFree,
        totalQuantity: qty,
        description: desc,
        zone,
    };

    state.showtimes = state.showtimes.map((slot) => {
        if (slot.id === state.selectedShowtimeId) {
            return { ...slot, ticketTiers: [...slot.ticketTiers, tier] };
        }
        return slot;
    });

    closeTicketModal();
    renderShowtimes();
}

function deleteTicket(showtimeId, ticketId) {
    state.showtimes = state.showtimes.map((slot) => {
        if (slot.id === showtimeId) {
            return {
                ...slot,
                ticketTiers: slot.ticketTiers.filter((t) => t.id !== ticketId),
            };
        }
        return slot;
    });
    renderShowtimes();
}

// ════════════════════════════════════════════
//  VALIDATION & SUBMIT
// ════════════════════════════════════════════
function handleSubmit(isDraft = false) {
    const eventName = document.getElementById("eventName").value.trim();
    if (!eventName) {
        showAlert("Please specify an Event Name before publishing!");
        scrollToSection("section-event-info");
        return;
    }

    if (!state.placeName) {
        showAlert(
            "Please select a verified hosting venue in the Date & Venue section!",
        );
        scrollToSection("section-date-venue");
        return;
    }

    if (state.showtimes.length === 0) {
        showAlert("Please configure at least one active Showtime Slot!");
        scrollToSection("section-showtimes");
        return;
    }

    // Compute totals
    let totalCapacity = 0;
    let expectedRevenue = 0;
    state.showtimes.forEach((slot) => {
        slot.ticketTiers.forEach((t) => {
            totalCapacity += t.totalQuantity;
            expectedRevenue += t.price * t.totalQuantity;
        });
    });
    if (totalCapacity === 0) totalCapacity = 1000;

    const firstSlot = state.showtimes[0];
    const displayDate = firstSlot ? firstSlot.date : "2026-06-15";
    const displayTimeline = firstSlot
        ? `${firstSlot.date} • ${firstSlot.startTime || "19:00"} - ${firstSlot.endTime || "22:30"}`
        : "2026-06-15 • 19:00 - 22:30";

    const result = {
        name: eventName,
        category: document.getElementById("eventCategory").value,
        status: isDraft ? "Draft" : "On Sale",
        date: displayDate,
        timeLine: displayTimeline,
        location: `${state.placeName}, ${state.street || ""}, ${state.district || ""}, ${state.province || ""}`,
        capacity: totalCapacity,
        totalTickets: totalCapacity,
        description:
            document.getElementById("eventDescription").value ||
            "No detailed description provided by the host.",
        expectedRevenue:
            expectedRevenue > 0
                ? `${(expectedRevenue / 1000000).toFixed(2)}M VND`
                : "0.00 VND",
        organizerName:
            document.getElementById("organizerName").value || "Independent Promoter",
        organizerPhone:
            document.getElementById("organizerPhone").value || "1900 6408",
        bannerImage:
            state.bannerHorizontal ||
            "https://images.unsplash.com/photo-1540575467063-178a50c2df87?w=1600&auto=format&fit=crop",
        additionalImages: state.additionalImages,
        showtimes: state.showtimes,
    };

    console.log("[Create Event] Saved result:", result);

    alert(
        `✅ Event "${result.name}" ${isDraft ? "saved as Draft" : "published successfully"}!\n\nCapacity: ${totalCapacity.toLocaleString()} tickets\nRevenue: ${result.expectedRevenue}\nStatus: ${result.status}`,
    );
}

// ════════════════════════════════════════════
//  CLEAR ALL
// ════════════════════════════════════════════
function handleClearAll() {
    if (!confirm("Restore all form fields back to defaults?")) return;

    setValue("eventName", "");
    setValue("eventCategory", "Entertainment");
    setValue("eventDescription", "");
    setValue("bannerUrlInput", "");
    setValue("organizerName", "");
    setValue("organizerPhone", "");
    setValue("organizerWebsite", "");
    setValue("venueSelect", "");
    document.getElementById("nameLen").textContent = "0";

    state.bannerHorizontal = "";
    state.additionalImages = [];
    state.placeName = state.street = state.district = state.province = "";
    state.organizerLogo = "";

    updateCoverPreview("");
    renderGallery();
    document.getElementById("venueDetails").style.display = "none";
    document.getElementById("venueEmpty").style.display = "";
    document.getElementById("logoPreview").style.display = "none";
    document.getElementById("logoPlaceholder").style.display = "";

    initDefaultDate();
    dismissAlert();
}

// ════════════════════════════════════════════
//  CLOSE (back to parent app)
// ════════════════════════════════════════════
function handleClose() {
    if (confirm("Discard changes and exit?")) {
        // In standalone mode: go back / close tab
        if (window.history.length > 1) window.history.back();
        else window.close();
    }
}

// ════════════════════════════════════════════
//  ALERT HELPERS
// ════════════════════════════════════════════
function showAlert(msg) {
    document.getElementById("validationMsg").textContent = msg;
    document.getElementById("validationAlert").style.display = "flex";
    document
        .getElementById("wizardBody")
        .scrollTo({ top: 0, behavior: "smooth" });
}

function dismissAlert() {
    document.getElementById("validationAlert").style.display = "none";
}

// ════════════════════════════════════════════
//  UTILITY HELPERS
// ════════════════════════════════════════════
function setValue(id, value) {
    const el = document.getElementById(id);
    if (el) el.value = value;
}

function scrollToSection(id) {
    const el = document.getElementById(id);
    if (el) el.scrollIntoView({ behavior: "smooth", block: "start" });
}

function escHtml(str) {
    return String(str)
        .replace(/&/g, "&amp;")
        .replace(/</g, "&lt;")
        .replace(/>/g, "&gt;")
        .replace(/"/g, "&quot;");
}
