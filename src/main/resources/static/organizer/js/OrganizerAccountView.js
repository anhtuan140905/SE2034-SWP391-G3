/* ============================================================
   EventHub Organizer Portal – script.js
   ============================================================ */

/* ---------- Sidebar toggle (mobile) ---------- */
function toggleSidebar(open) {
    const sidebar = document.getElementById("sidebar");
    const overlay = document.getElementById("sidebarOverlay");
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

/* ---------- Logout ---------- */
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

/* ---------- Search ---------- */
function handleSearch(value) {
    var clearBtn = document.getElementById("searchClear");
    if (value.length > 0) {
        clearBtn.classList.remove("d-none");
    } else {
        clearBtn.classList.add("d-none");
    }
}

function clearSearch() {
    var input = document.getElementById("searchInput");
    input.value = "";
    document.getElementById("searchClear").classList.add("d-none");
    input.focus();
}

/* ---------- Save profile form ---------- */
function handleSave(event) {
    event.preventDefault();
    showToast(
        "Profile Updated",
        "Organizer profile and payout settings have been successfully updated.",
        "success",
    );
}

/* ---------- Secret key toggle ---------- */
var secretVisible = false;
var SECRET_VALUE = "evh_secret_332a76f23b2c159082ac9bc0";
var SECRET_MASKED = "••••••••••••••••••••••••••••••••";

function toggleSecret() {
    secretVisible = !secretVisible;
    document.getElementById("secretDisplay").textContent = secretVisible
        ? SECRET_VALUE
        : SECRET_MASKED;
    document.getElementById("secretIcon").textContent = secretVisible
        ? "visibility_off"
        : "visibility";
}

/* ---------- Toast notification ---------- */
var toastTimeout = null;

function showToast(title, desc, type) {
    var wrap = document.getElementById("toastWrap");
    var toast = document.getElementById("customToast");
    var titleEl = document.getElementById("toastTitle");
    var descEl = document.getElementById("toastDesc");
    var iconEl = toast.querySelector(".toast-icon");

    // Set content
    titleEl.textContent = title;
    descEl.textContent = desc;

    // Set border/icon color based on type
    toast.style.borderLeftColor =
        type === "warning" ? "#f59e0b" : type === "info" ? "#3b82f6" : "#10b981";
    iconEl.style.color =
        type === "warning" ? "#f59e0b" : type === "info" ? "#3b82f6" : "#10b981";
    iconEl.textContent =
        type === "warning" ? "warning" : type === "info" ? "info" : "check_circle";

    // Show
    toast.classList.add("show");

    // Auto-hide after 3.5s
    if (toastTimeout) clearTimeout(toastTimeout);
    toastTimeout = setTimeout(function () {
        toast.classList.remove("show");
    }, 3500);
}
