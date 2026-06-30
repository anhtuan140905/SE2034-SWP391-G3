/**
 * event_detail.js
 * Path: src/main/resources/static/moderator/js/event_detail.js
 */

document.addEventListener('DOMContentLoaded', () => {

    /* 1. APPROVE EVENT (Duyệt sự kiện + Lời nhắn đi kèm)*/
    window.approveEvent = async (eventId) => {
        if (eventId === null || eventId === undefined) return;

        const confirmed = confirm('Are you sure you want to APPROVE this event?');
        if (!confirmed) return;

        // Lấy lời nhắn động từ textarea (nếu có)
        const textarea = document.getElementById('deactivateReason');
        const message  = textarea ? textarea.value.trim() : '';

        try {
            const res = await fetch(`/moderator/events/${eventId}/approve`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    [document.querySelector('meta[name="_csrf_header"]')?.content || 'X-CSRF-TOKEN']:
                        document.querySelector('meta[name="_csrf"]')?.content || ''
                },
                body: JSON.stringify({ message: message }) // Gửi lời nhắn lên Server
            });

            if (res.ok) {
                showToast('Event approved successfully!', 'success');
                setTimeout(() => window.location.href = '/moderator/events', 1200);
            } else {
                showToast('Failed to approve event. Please try again.', 'error');
            }
        } catch (err) {
            console.error('Approve error:', err);
            showToast('Network error. Please try again.', 'error');
        }
    };


    /* 2. Tắt sự kiện */
    window.submitDeactivate = function () {
        const textarea = document.getElementById('deactivateReason');
        const reason = textarea ? textarea.value.trim() : '';

        if (!reason) {
            textarea?.classList.add('textarea-error');
            textarea?.focus();
            alert('Vui lòng nhập lý do tắt sự kiện trước khi xác nhận.');
            return;
        }

        textarea?.classList.remove('textarea-error');

        const confirmed = confirm('Bạn có chắc muốn TẮT sự kiện này không?');
        if (!confirmed) return;

        const reasonInput = document.getElementById('reasonInput');
        const form = document.getElementById('deactivateForm');

        if (!reasonInput || !form) {
            alert('Không tìm thấy form tắt sự kiện.');
            return;
        }

        reasonInput.value = reason;
        form.submit();
    };

});