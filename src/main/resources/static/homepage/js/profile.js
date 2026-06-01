/**
 * EventHub Profile Management Script
 * Handles avatar changes, file loading, and interactive status saving
 */
document.addEventListener('DOMContentLoaded', () => {
    const avatarInput = document.getElementById('avatarFileInput');
    const avatarTrigger = document.getElementById('avatarUploadTrigger');
    const previewImage = document.getElementById('avatarPreviewImage');
    const presetItems = document.querySelectorAll('.avatar-preset-item');

    // 1. Trigger File Selection on Click
    if (avatarTrigger && avatarInput) {
        avatarTrigger.addEventListener('click', (e) => {
            e.preventDefault();
            avatarInput.click();
        });
    }

    // 2. Load Local Uploaded File as Avatar
    if (avatarInput) {
        avatarInput.addEventListener('change', (event) => {
            const file = event.target.files[0];
            if (file) {
                if (!file.type.startsWith('image/')) {
                    showValidationToast('Lỗi Thư Viện', 'Vui lòng chỉ tải lên các tệp tin hình ảnh hợp lệ.', 'error');
                    return;
                }

                const reader = new FileReader();
                reader.onload = (e) => {
                    const base64Image = e.target.result;
                    fadeElementOutIn(previewImage, () => {
                        previewImage.src = base64Image;
                        presetItems.forEach(item => item.classList.remove('active'));
                    });
                };
                reader.readAsDataURL(file);
            }
        });
    }

    // 3. TỰ ĐỘNG BẬT TOAST KHI SPRING BOOT REDIRECT THÀNH CÔNG
    // (Đã loại bỏ logic submit chặn form cũ)
    const toastEl = document.getElementById('profileSuccessToast');
    if (toastEl) {
        // Nếu phần tử toast tồn tại trong DOM (được sinh ra bởi th:if="${saveSuccess}")
        // Thêm class 'show' để kích hoạt CSS animation trượt vào góc màn hình
        toastEl.classList.add('show');

        // Tự động ẩn Toast đi sau 4 giây
        setTimeout(() => {
            toastEl.classList.remove('show');
        }, 4000);
    }

    function fadeElementOutIn(element, updateCallback) {
        element.style.transition = 'opacity 0.2s ease-out';
        element.style.opacity = '0';
        setTimeout(() => {
            updateCallback();
            element.style.opacity = '1';
        }, 220);
    }

    // Hàm bổ trợ sinh UI cho Toast (giữ nguyên cấu trúc giao diện lấp lánh của bạn)
    function showValidationToast(title, msg, type = 'success') {
        let toast = document.getElementById('profileSuccessToast');
        if (!toast) return; // Nếu không có thẻ trong DOM thì không chạy tiếp

        const isSuccess = type === 'success';
        const colorAccent = isSuccess ? '#00f0ff' : '#ff2a7a';
        const iconSymbol = isSuccess ? 'fa-circle-check text-cyan-glow' : 'fa-circle-exclamation text-pink';

        toast.style.borderColor = colorAccent;
        toast.innerHTML = `
            <i class="fa-solid ${iconSymbol} fs-5" style="color: ${colorAccent};"></i>
            <div>
                <strong class="d-block text-white" style="font-size: 13px;">${title}</strong>
                <span class="text-secondary-light" style="font-size: 11px;">${msg}</span>
            </div>
        `;
        toast.classList.add('show');
        setTimeout(() => { toast.classList.remove('show'); }, 4000);
    }
});
// ✅ Khai báo đúng chỗ (ngoài DOMContentLoaded là được)
const citySelect = document.getElementById("u_city");
const wardSelect = document.getElementById("u_ward");

if (citySelect && wardSelect) {
    // Change event — giữ nguyên logic cũ của bạn, KHÔNG thay bằng comment
    citySelect.addEventListener("change", function () {
        const cityId = this.value;
        wardSelect.innerHTML = '<option value=""></option>';

        if (typeof handleSelect === "function") {
            handleSelect(wardSelect, 'wardField');
        }

        if (!cityId) return;

        fetch(`/auth/api/wards?cityId=${cityId}`)
            .then(response => response.json())
            .then(data => {
                data.forEach(ward => {
                    const option = document.createElement("option");
                    option.value = ward.id;
                    option.textContent = ward.name;
                    wardSelect.appendChild(option);
                });

                if (typeof handleSelect === "function") {
                    handleSelect(wardSelect, 'wardField');
                }
            })
            .catch(error => console.error("Lỗi lấy danh sách Phường/Xã:", error));
    });

    const initialCityId = citySelect.value;
    const initialWardId = wardSelect.getAttribute("data-selected-ward");

    if (initialCityId) {
        fetch(`/auth/api/wards?cityId=${initialCityId}`)
            .then(response => response.json())
            .then(data => {
                data.forEach(ward => {
                    const option = document.createElement("option");
                    option.value = ward.id;
                    option.textContent = ward.name;
                    wardSelect.appendChild(option);
                });

                if (initialWardId) {
                    wardSelect.value = initialWardId;
                }

                if (typeof handleSelect === "function") {
                    handleSelect(wardSelect, 'wardField');
                }
            })
            .catch(error => console.error("Lỗi load ward ban đầu:", error));
    }
}

function handleSelect(element, fieldName) {
    console.log(`Đã chọn trường: ${fieldName}, giá trị: ${element.value}`);
}