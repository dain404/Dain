/**
 * TOUS LES JOURS - CUSTOM JAVASCRIPT
 */

document.addEventListener("DOMContentLoaded", function() {
    
    // 1. Tự động ẩn Flash Messages sau 3.5 giây
    const alerts = document.querySelectorAll('.alert:not(.alert-danger)');
    alerts.forEach(function(alert) {
        setTimeout(function() {
            let bsAlert = new bootstrap.Alert(alert);
            bsAlert.close();
        }, 3500);
    });

    // 2. Validate hai mật khẩu phải giống nhau ở form đổi MK / Đăng ký
    const formDoiMk = document.querySelector('form[action="/profile/password"]');
    if (formDoiMk) {
        formDoiMk.addEventListener('submit', function(e) {
            const mkMoi = formDoiMk.querySelector('input[name="matKhauMoi"]').value;
            const xacNhan = formDoiMk.querySelector('input[name="xacNhanMatKhau"]').value;
            if (mkMoi !== xacNhan) {
                e.preventDefault();
                alert("Mật khẩu xác nhận không khớp!");
            }
        });
    }

    const formDangKy = document.querySelector('form[action="/register"]');
    if (formDangKy) {
        formDangKy.addEventListener('submit', function(e) {
            const mkMoi = formDangKy.querySelector('input[name="matKhau"]').value;
            const xacNhan = formDangKy.querySelector('input[name="xacNhanMatKhau"]').value;
            if (mkMoi !== xacNhan) {
                e.preventDefault();
                alert("Mật khẩu xác nhận không khớp!");
            }
        });
    }

    // 3. Khởi tạo Tooltip Bootstrap
    const tooltipTriggerList = document.querySelectorAll('[data-bs-toggle="tooltip"]');
    const tooltipList = [...tooltipTriggerList].map(tooltipTriggerEl => new bootstrap.Tooltip(tooltipTriggerEl));
    
});
