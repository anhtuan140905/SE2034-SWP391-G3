/* ============================================================
   EventHub – Dashboard  |  script.js
   ============================================================ */

/* ============================================================
   DATA
   ============================================================ */
var weeklyRevenue = [
    { day: "Mon", amount: 25 },
    { day: "Tue", amount: 48 },
    { day: "Wed", amount: 125 },
    { day: "Thu", amount: 62 },
    { day: "Fri", amount: 88 },
    { day: "Sat", amount: 40 },
    { day: "Sun", amount: 112 },
];

var events = [
    {
        id: "evt-1",
        name: "Summer Music Fest 2024",
        category: "Entertainment",
        status: "On Sale",
        date: "15 Aug 2024",
        location: "Hanoi Stadium, Mỹ Đình",
        capacity: 15000,
        ticketsSold: 1200,
        bannerImage:
            "https://lh3.googleusercontent.com/aida-public/AB6AXuC4eS1ChxTB17daDwUhc-1cQVzlJWTJpu15fzW4Kl6QZVLJoUpFPkGpGks4wGKM4dBsMRe9U3eAnHVq1l6G0fg7rytXuOIxwNi_T4MnvLgdctLF5iQsTa8q8UhQYXKRCfWUVWAanxLVk32h8zwf8iHmLLjJGeTjcNHrAot5HBfN5meHWsYnGfUXYjl4EdGR6-cn120kWu0HAHrw6z9Ct76O02iohOu2GLvn9D25Uwcb1siOLgX9huePzMmabSnl-Rg20844dnRbdKMj",
    },
    {
        id: "evt-2",
        name: "Tech Innovators Workshop",
        category: "Technology",
        status: "On Sale",
        date: "22 Sep 2024",
        location: "Gem Center, HCMC",
        capacity: 500,
        ticketsSold: 450,
        bannerImage:
            "https://plus.unsplash.com/premium_photo-1661284827255-a0bc81d451a3?q=80&w=600&auto=format&fit=crop",
    },
    {
        id: "evt-5",
        name: "Hanoi Indie Music Festival",
        category: "Entertainment",
        status: "Ongoing",
        date: "Today, 14:00 - 23:00",
        location: "Thong Nhat Park, Hanoi",
        capacity: 2000,
        ticketsSold: 2000,
        bannerImage:
            "https://images.unsplash.com/photo-1459749411175-04bf5292ceea?q=80&w=600&auto=format&fit=crop",
    },
];

var selectedDay = "Wed";
var PEAK = 125; // peak amount for scaling

/* ============================================================
   INIT
   ============================================================ */
document.addEventListener("DOMContentLoaded", function () {
    renderChart();
    renderEvents();
    updateBanner();
});

/* ============================================================
   SIDEBAR
   ============================================================ */
function toggleSidebar(open) {
    var sidebar = document.getElementById("sidebar");
    var overlay = document.getElementById("sidebarOverlay");
    if (open) {
        sidebar.classList.add("open");
        overlay.classList.add("show");
        document.body.style.overflow = "hidden";
    } else {
        sidebar.classList.remove("open");
        overlay.classList.remove("show");
        document.body.style.overflow = "";
    }
}



function handleLogout(event) {
    event.preventDefault();
    if (confirm("Are you sure you want to sign out?")) {
        showToast(
            "Signed Out",
            "You have been successfully signed out.",
            "warning",
        );
    }
}

/* ============================================================
   SEARCH
   ============================================================ */
function handleSearch(value) {
    var clearBtn = document.getElementById("searchClear");
    clearBtn.classList.toggle("d-none", value.length === 0);
}

function clearSearch() {
    var input = document.getElementById("searchInput");
    input.value = "";
    document.getElementById("searchClear").classList.add("d-none");
    input.focus();
}

/* ============================================================
   BANNER
   ============================================================ */
function updateBanner() {
    var featured =
        events.find(function (e) {
            return e.status === "On Sale";
        }) || events[0];
    if (!featured) return;

    var pct = Math.round((featured.ticketsSold / featured.capacity) * 100);

    document.getElementById("bannerTitle").textContent = featured.name;
    document.getElementById("bannerCategory").textContent =
        featured.category.toUpperCase();
    document.getElementById("bannerDate").textContent = featured.date;
    document.getElementById("bannerLocation").textContent = featured.location;
    document.getElementById("bannerSoldLabel").textContent =
        featured.ticketsSold.toLocaleString("en-US") +
        " / " +
        featured.capacity.toLocaleString("en-US") +
        " tickets sold";
    document.getElementById("bannerPct").textContent = pct + "%";
    document.getElementById("bannerFill").style.width = pct + "%";

    var circleImg = document.getElementById("bannerCircleImg");
    if (circleImg && featured.bannerImage) {
        circleImg.src = featured.bannerImage;
        circleImg.alt = featured.name;
    }
}

function goToEvents() {
    showToast("Events", "Opening event management...", "info");
}

/* ============================================================
   BAR CHART
   ============================================================ */
function renderChart() {
    var container = document.getElementById("chartArea");
    container.innerHTML = "";

    weeklyRevenue.forEach(function (item) {
        var barH = Math.round((item.amount / PEAK) * 180); // max 180px
        var isActive = item.day === selectedDay;

        var col = document.createElement("div");
        col.className = "chart-col" + (isActive ? " active" : "");
        col.setAttribute("data-day", item.day);
        col.onclick = function () {
            selectDay(item.day);
        };

        col.innerHTML =
            '<div class="chart-tooltip">' +
            item.amount +
            "M ₫</div>" +
            '<div class="chart-bar" style="height:' +
            barH +
            'px;"></div>' +
            '<div class="chart-label">' +
            item.day +
            "</div>";

        container.appendChild(col);
    });
}

function selectDay(day) {
    selectedDay = day;
    renderChart();

    var rev = weeklyRevenue.find(function (r) {
        return r.day === day;
    });
    if (rev) {
        showToast(
            "View Metrics",
            "Revenue for " + day + " reached " + rev.amount + "M VND.",
            "payment",
        );
    }
}

/* ============================================================
   EVENTS GRID
   ============================================================ */
function renderEvents() {
    var grid = document.getElementById("eventsGrid");
    grid.innerHTML = "";

    var slice = events.slice(0, 3);
    slice.forEach(function (evt) {
        var pct = Math.round((evt.ticketsSold / evt.capacity) * 100);
        var badgeClass =
            evt.status === "Ongoing" ? "badge-ongoing" : "badge-onsale-card";
        var badgeLabel = evt.status === "Ongoing" ? "Ongoing" : "On Sale";

        var col = document.createElement("div");
        col.className = "col-md-6 col-lg-4";

        col.innerHTML =
            '<div class="event-card h-100" onclick="openEvent(\'' +
            evt.id +
            "')\">" +
            '<div class="event-card-img-wrap">' +
            '<img src="' +
            evt.bannerImage +
            '" alt="' +
            evt.name +
            '" class="event-card-img" referrerpolicy="no-referrer" />' +
            '<span class="event-card-badge ' +
            badgeClass +
            '">' +
            badgeLabel +
            "</span>" +
            "</div>" +
            '<div class="event-card-body">' +
            "<div>" +
            '<div class="event-card-cat">' +
            evt.category +
            "</div>" +
            '<div class="event-card-name">' +
            evt.name +
            "</div>" +
            '<div class="event-card-loc">' +
            '<span class="material-symbols-outlined">location_on</span>' +
            evt.location +
            "</div>" +
            "</div>" +
            '<div class="event-card-progress">' +
            '<div class="event-card-progress-info">' +
            "<span>Sold: <strong>" +
            evt.ticketsSold.toLocaleString("en-US") +
            "</strong> / " +
            evt.capacity.toLocaleString("en-US") +
            "</span>" +
            "<span>" +
            pct +
            "%</span>" +
            "</div>" +
            '<div class="progress-track"><div class="progress-fill" style="width:' +
            pct +
            '%"></div></div>' +
            "</div>" +
            "</div>" +
            "</div>";

        grid.appendChild(col);
    });
}

function openEvent(id) {
    showToast("Event Detail", "Opening event ID: " + id, "info");
}

/* ============================================================
   TOAST
   ============================================================ */
var toastTimeout = null;

function showToast(title, desc, type) {
    var toast = document.getElementById("customToast");
    var titleEl = document.getElementById("toastTitle");
    var descEl = document.getElementById("toastDesc");
    var iconEl = document.getElementById("toastIcon");

    titleEl.textContent = title;
    descEl.textContent = desc;

    var colors = {
        success: "#10b981",
        warning: "#f59e0b",
        info: "#3b82f6",
        payment: "#7c3aed",
    };
    var icons = {
        success: "check_circle",
        warning: "warning",
        info: "info",
        payment: "account_balance_wallet",
    };

    toast.style.borderLeftColor = colors[type] || colors.success;
    iconEl.style.color = colors[type] || colors.success;
    iconEl.textContent = icons[type] || icons.success;

    toast.classList.add("show");
    if (toastTimeout) clearTimeout(toastTimeout);
    toastTimeout = setTimeout(function () {
        toast.classList.remove("show");
    }, 3500);
}
