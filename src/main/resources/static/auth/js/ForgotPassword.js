/* ── STATE ── */
    var currentStep = 1;
    var timerInterval = null;

    /* ── PANEL NAVIGATION ── */
    function showPanel(id) {
      document.querySelectorAll('.panel').forEach(function(p){ p.classList.remove('active'); });
      document.getElementById(id).classList.add('active');
    }

    function goToStep(step) {
      currentStep = step;
      updateLeftSteps(step);
      if (step === 1) showPanel('panelEmail');
      if (step === 2) showPanel('panelOtp');
      if (step === 3) showPanel('panelNewPw');
    }

    function goBack() {
      window.location.href = 'eventhub_login_v5.html';
    }

    function goToLogin() {
      window.location.href = 'eventhub_login_v5.html';
    }

    /* ── LEFT STEP INDICATOR ── */
    function updateLeftSteps(active) {
      for (var i = 1; i <= 3; i++) {
        var el = document.getElementById('leftStep' + i);
        if (!el) continue;
        el.classList.remove('active-step', 'done-step');
        if (i < active) el.classList.add('done-step');
        else if (i === active) el.classList.add('active-step');
      }
      // done-step: update inner num to checkmark
      for (var j = 1; j <= 3; j++) {
        var numEl = document.querySelector('#leftStep' + j + ' .sec-num');
        if (!numEl) continue;
        if (j < active) {
          numEl.innerHTML = '<svg width="14" height="14" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="3" d="M5 13l4 4L19 7"/></svg>';
        } else {
          numEl.textContent = j;
        }
      }
    }

    /* ── STEP 1: SEND OTP ── */
    function sendOtp() {
        var email = document.getElementById('emailInput').value.trim();
        if (!email || !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email)) {
            document.getElementById('emailField').classList.add('field-error');
            return;
        }

        var btn = document.querySelector('#panelEmail .btn-primary');
        btn.disabled = true;
        btn.textContent = 'Đang gửi...';

        fetch('/auth/forgot-password', {
            method: 'POST',
            headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
            body: 'email=' + encodeURIComponent(email)
        })
        .then(function(res) { return res.json(); })
        .then(function(data) {
            if (data.status === 'ok') {
                document.getElementById('emailDisplay').textContent = email;
                goToStep(2);
                initOtpInputs();
                startTimer(60);
                setTimeout(function(){ document.getElementById('otp0').focus(); }, 100);
            } else {
                document.getElementById('emailField').classList.add('field-error');
            }
        })
        .catch(function() {
            alert('Gửi email thất bại, vui lòng thử lại.');
        })
        .finally(function() {
            btn.disabled = false;
            btn.textContent = 'Gửi mã OTP';
        });
    }

    /* ── STEP 2: OTP ── */
    function initOtpInputs() {
      var inputs = document.querySelectorAll('.otp-input');
      inputs.forEach(function(inp, i) {
        inp.value = '';
        inp.classList.remove('filled', 'error');
        inp.oninput = function() {
          var val = this.value.replace(/[^0-9]/g,'');
          this.value = val ? val[val.length-1] : '';
          this.classList.toggle('filled', !!this.value);
          if (this.value && i < 5) inputs[i+1].focus();
          clearOtpError();
        };
        inp.onkeydown = function(e) {
          if (e.key === 'Backspace' && !this.value && i > 0) {
            inputs[i-1].focus();
            inputs[i-1].value = '';
            inputs[i-1].classList.remove('filled');
          }
        };
        inp.onpaste = function(e) {
          e.preventDefault();
          var paste = (e.clipboardData || window.clipboardData).getData('text').replace(/[^0-9]/g,'').slice(0,6);
          paste.split('').forEach(function(ch, idx) {
            if (inputs[idx]) { inputs[idx].value = ch; inputs[idx].classList.add('filled'); }
          });
          var next = Math.min(paste.length, 5);
          inputs[next].focus();
          clearOtpError();
        };
      });
    }

    function getOtpValue() {
      return Array.from(document.querySelectorAll('.otp-input')).map(function(i){ return i.value; }).join('');
    }

    function clearOtpError() {
      document.getElementById('otpError').textContent = '';
      document.querySelectorAll('.otp-input').forEach(function(i){ i.classList.remove('error'); });
    }

   function verifyOtp() {
       var otp = getOtpValue();
       if (otp.length < 6) {
           document.getElementById('otpError').textContent = 'Vui lòng nhập đủ 6 chữ số';
           document.querySelectorAll('.otp-input').forEach(function(i){
               if (!i.value) i.classList.add('error');
           });
           return;
       }

       var btn = document.querySelector('#panelOtp .btn-primary');
       btn.disabled = true;
       btn.textContent = 'Đang xác nhận...';

       fetch('/auth/verify-otp', {
           method: 'POST',
           headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
           body: 'otp=' + encodeURIComponent(otp)
       })
       .then(function(res) { return res.json(); })
       .then(function(data) {
           if (data.status === 'ok') {
               clearInterval(timerInterval);
               goToStep(3);
               setTimeout(function(){ document.getElementById('newPwInput').focus(); }, 100);
           } else {
               document.getElementById('otpError').textContent = data.message || 'Mã OTP không đúng.';
               document.querySelectorAll('.otp-input').forEach(function(i){ i.classList.add('error'); });
               setTimeout(function(){
                   document.querySelectorAll('.otp-input').forEach(function(i){
                       i.classList.remove('error'); i.value = ''; i.classList.remove('filled');
                   });
                   document.getElementById('otp0').focus();
                   clearOtpError();
               }, 1200);
           }
       })
       .catch(function() {
           document.getElementById('otpError').textContent = 'Lỗi kết nối, vui lòng thử lại.';
       })
       .finally(function() {
           btn.disabled = false;
           btn.textContent = 'Xác nhận mã OTP';
       });
   }

    /* ── OTP TIMER ── */
    function startTimer(seconds) {
      var resendBtn = document.getElementById('resendBtn');
      var timerSpan = document.getElementById('otpTimerSpan');
      resendBtn.disabled = true;
      clearInterval(timerInterval);
      timerSpan.textContent = '(' + seconds + 's)';
      timerInterval = setInterval(function() {
        seconds--;
        if (seconds <= 0) {
          clearInterval(timerInterval);
          timerSpan.textContent = '';
          resendBtn.disabled = false;
        } else {
          timerSpan.textContent = '(' + seconds + 's)';
        }
      }, 1000);
    }

    function resendOtp() {
      initOtpInputs();
      clearOtpError();
      startTimer(60);
      setTimeout(function(){ document.getElementById('otp0').focus(); }, 50);
    }

    /* ── STEP 3: NEW PASSWORD ── */
    var strengthLabels = ['Nhập mật khẩu', 'Quá yếu', 'Có thể mạnh hơn', 'Gần đủ rồi', 'Mật khẩu mạnh! ✓'];

    function calcStrength(pw) {
      if (!pw) return 0;
      var s = 0;
      if (pw.length >= 8) s++;
      if (/[A-Z]/.test(pw)) s++;
      if (/[0-9]/.test(pw)) s++;
      if (/[^A-Za-z0-9]/.test(pw)) s++;
      return s;
    }

    function onNewPwInput(val) {
      var level = calcStrength(val);
      var w = document.getElementById('pwStrength');
      w.className = 'pw-strength strength-' + level;
      document.getElementById('strengthLabel').textContent = strengthLabels[level];

      // Requirement chips
      document.getElementById('req-len').classList.toggle('met', val.length >= 8);
      document.getElementById('req-upper').classList.toggle('met', /[A-Z]/.test(val));
      document.getElementById('req-num').classList.toggle('met', /[0-9]/.test(val));
      document.getElementById('req-special').classList.toggle('met', /[^A-Za-z0-9]/.test(val));

      // Live confirm check
      var confirm = document.getElementById('confirmPwInput').value;
      if (confirm) onConfirmPwInput(confirm);
    }

    function onConfirmPwInput(val) {
      var pw = document.getElementById('newPwInput').value;
      var field = document.getElementById('confirmPwField');
      if (val && pw && val !== pw) {
        field.classList.add('field-error');
      } else {
        field.classList.remove('field-error');
      }
    }

    function resetPassword() {
        var pw = document.getElementById('newPwInput').value;
        var confirm = document.getElementById('confirmPwInput').value;

        if (!pw || calcStrength(pw) < 2) {
            document.getElementById('newPwInput').focus();
            return;
        }
        if (pw !== confirm) {
            document.getElementById('confirmPwField').classList.add('field-error');
            document.getElementById('confirmPwInput').focus();
            return;
        }

        var btn = document.querySelector('#panelNewPw .btn-primary');
        btn.disabled = true;
        btn.textContent = 'Đang xử lý...';

        fetch('/auth/reset-password', {
            method: 'POST',
            headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
            body: 'newPassword=' + encodeURIComponent(pw)
                + '&confirmPassword=' + encodeURIComponent(confirm)
        })
        .then(function(res) { return res.json(); })
        .then(function(data) {
            if (data.status === 'ok') {
                updateLeftSteps(4);
                showPanel('panelSuccess');
            } else {
                alert(data.message || 'Đặt lại mật khẩu thất bại.');
                btn.disabled = false;
                btn.textContent = 'Đặt lại mật khẩu';
            }
        })
        .catch(function() {
            alert('Lỗi kết nối, vui lòng thử lại.');
            btn.disabled = false;
            btn.textContent = 'Đặt lại mật khẩu';
        });
    }

    /* ── EYE TOGGLE ── */
    function toggleEye(inputId, btn) {
      var inp = document.getElementById(inputId);
      var isText = inp.type === 'text';
      inp.type = isText ? 'password' : 'text';
      btn.querySelector('.eye-show').style.display = isText ? '' : 'none';
      btn.querySelector('.eye-hide').style.display = isText ? 'none' : '';
    }

    /* ── AUTOFILL FIX ── */
    setTimeout(function() {
      document.querySelectorAll('input[type=email], input[type=password], input[type=text]').forEach(function(el) {
        el.setAttribute('placeholder', '\u00A0');
      });
    }, 300);

    /* Init */
    updateLeftSteps(1);