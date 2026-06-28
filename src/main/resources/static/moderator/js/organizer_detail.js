/**
 * organizer_detail.js
 * Path: src/main/resources/static/moderator/js/organizer_detail.js
 */

document.addEventListener('DOMContentLoaded', () => {

});

function submitDeactivateOrganizer() {
    const reason = document.getElementById('deactivateReason').value.trim();
    if (!reason) {
        alert('Vui lòng nhập lý do khóa tài khoản trước khi xác nhận.');
        return;
    }
    document.getElementById('reasonInput').value = reason;
    document.getElementById('deactivateForm').submit();
}