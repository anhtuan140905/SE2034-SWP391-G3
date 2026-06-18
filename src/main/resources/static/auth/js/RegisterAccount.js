var currentRole = 'user';
    /* ── PANEL ROUTER ── */
    function showPanel(id) {
      document.querySelectorAll('.panel').forEach(p => p.classList.remove('active'));
      document.getElementById(id).classList.add('active');
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

    /* ── REGISTER SUCCESS ── */
    function goSuccess() {
      document.getElementById('successTitle').textContent = 'Account Created!';
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