var currentRole = 'user';
    /* ── PANEL ROUTER ── */
    function showPanel(id) {
      document.querySelectorAll('.panel').forEach(p => p.classList.remove('active'));
      document.getElementById(id).classList.add('active');
    }

    /* ── ROLE SWITCH (left panel) ── */
    function selectLeftRole(role, el) {
      document.querySelectorAll('#leftRoleCards .role-card').forEach(c => c.classList.remove('selected'));
      el.classList.add('selected');
      switchRole(role);
    }

    /* ── ROLE TABS (right panel) ── */
    function switchRole(role) {
    currentRole = role;

    const isOrg = role === 'organizer';

    document.getElementById('tabUser')
        .classList.toggle('active', !isOrg);

    document.getElementById('tabOrg')
        .classList.toggle('active', isOrg);

    // HIDE/SHOW FORM
    document.getElementById('formUserRaw').style.display =
        isOrg ? 'none' : 'block';

    document.getElementById('formOrgRaw').style.display =
        isOrg ? 'block' : 'none';

    // Sync left cards
    document.querySelectorAll('#leftRoleCards .role-card')
        .forEach(c => {
            c.classList.toggle(
                'selected',
                c.dataset.role === role
            );
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

    /* ── PASSWORD STRENGTH ── */
    var strengthLabels = ['', 'Too weak', 'Could be stronger', 'Almost there', 'Strong password!'];
    function calcStrength(pw) {
      if (!pw) return 0;
      var s = 0;
      if (pw.length >= 8) s++;
      if (/[A-Z]/.test(pw)) s++;
      if (/[0-9]/.test(pw)) s++;
      if (/[^A-Za-z0-9]/.test(pw)) s++;
      return s;
    }
    function applyStrength(wrapperId, labelId, level) {
      var w = document.getElementById(wrapperId);
      w.className = 'pw-strength strength-' + level;
      document.getElementById(labelId).textContent = level === 0 ? 'Enter a password' : strengthLabels[level];
    }
    function checkStrength(v, wrapperId, labelId) {
    applyStrength(wrapperId, labelId, calcStrength(v));
}
    function checkStrengthNew(v) { applyStrength('pwStrengthNew', 'strengthLabelNew', calcStrength(v)); }
    document.getElementById('o_pw') && document.getElementById('o_pw').addEventListener('input', function(){ applyStrength('pwStrengthOrg','strengthLabelOrg',calcStrength(this.value)); });

    /* ── SELECT LABEL FLOAT ── */
    function handleSelect(el, fieldId) {
      document.getElementById(fieldId).classList.toggle('has-val', el.value !== '');
    }

    /* ── OTP INPUTS ── */
    var otpInputs = document.querySelectorAll('.otp-input');
    otpInputs.forEach(function(inp, i) {
    inp.addEventListener('input', function() {
        this.value = this.value.replace(/[^0-9]/g,'');
        if (this.value && i < 5) otpInputs[i+1].focus();
        this.classList.toggle('filled', !!this.value);
      });
      inp.addEventListener('keydown', function(e) {
        if (e.key === 'Backspace' && !this.value && i > 0) {
          otpInputs[i-1].focus();
          otpInputs[i-1].value = '';
          otpInputs[i-1].classList.remove('filled');
        }
      });
      inp.addEventListener('paste', function(e) {
        e.preventDefault();
        var paste = (e.clipboardData || window.clipboardData).getData('text').replace(/[^0-9]/g,'').slice(0,6);
        paste.split('').forEach(function(ch, idx) {
          if (otpInputs[idx]) { otpInputs[idx].value = ch; otpInputs[idx].classList.add('filled'); }
        });
        var next = Math.min(paste.length, 5);
        otpInputs[next].focus();
      });
    });

    /* ── FORGOT FLOW ── */
    function goOtp() {
      var email = document.getElementById('f_email').value.trim() || 'your email';
      document.getElementById('otpEmail').textContent = email;
      showPanel('panelForgot2');
      startOtpTimer(60);
    }

    function verifyOtp() {
      showPanel('panelForgot3');
    }

    function resetDone() {
      document.getElementById('successTitle').textContent = 'Password Reset! 🎉';
      document.getElementById('successMsg').textContent = 'Your password has been updated successfully. You can now sign in with your new password.';
      showPanel('panelSuccess');
    }

    /* ── OTP TIMER ── */
    var timerInterval;
    function startOtpTimer(seconds) {
      clearInterval(timerInterval);
      var timerEl = document.getElementById('otpTimer');
      timerEl.textContent = '(' + seconds + 's)';
      timerInterval = setInterval(function() {
        seconds--;
        if (seconds <= 0) { clearInterval(timerInterval); timerEl.textContent = ''; }
        else timerEl.textContent = '(' + seconds + 's)';
      }, 1000);
    }

    function resendOtp() {
      otpInputs.forEach(function(inp) { inp.value = ''; inp.classList.remove('filled'); });
      otpInputs[0].focus();
      startOtpTimer(60);
    }

    /* ── REGISTER SUCCESS ── */
    function goSuccess() {
      document.getElementById('successTitle').textContent = 'Account Created! 🎉';
      document.getElementById('successMsg').textContent = 'Welcome to EventHub! Your account is ready. Start exploring amazing events around you.';
      showPanel('panelSuccess');
    }

    /* ── AUTOFILL PLACEHOLDER FIX ── */
    setTimeout(function() {
      document.querySelectorAll('input[type=email], input[type=password], input[type=text], input[type=tel]').forEach(function(el) {
        el.setAttribute('placeholder', '\u00A0');
      });
    }, 300);

    let activeRole = document.getElementById('activeRoleVal').value;

window.onload = function () {
    switchRole(activeRole);
}
    const citySelect =
        document.getElementById("u_city");

    const wardSelect =
        document.getElementById("u_ward");

    citySelect.addEventListener("change", function () {

        const cityId = this.value;

        wardSelect.innerHTML =
            '<option value=""></option>';

        // reset label
        handleSelect(wardSelect, 'wardField');

        if (!cityId) {
            return;
        }

        fetch(`/auth/api/wards?cityId=${cityId}`)
            .then(response => response.json())
            .then(data => {

                data.forEach(ward => {

                    const option =
                        document.createElement("option");

                    option.value = ward.id;

                    option.textContent = ward.name;

                    wardSelect.appendChild(option);

                });

                // update floating label
                handleSelect(wardSelect, 'wardField');

            })
            .catch(error => {
                console.error(error);
            });

    });