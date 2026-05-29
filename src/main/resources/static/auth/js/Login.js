// Toggle password visibility
    var pw = document.getElementById('pw');
    var es = document.getElementById('eShow');
    var eh = document.getElementById('eHide');
    document.getElementById('eyeBtn').onclick = function() {
      var t = pw.type === 'text';
      pw.type = t ? 'password' : 'text';
      es.style.display = t ? '' : 'none';
      eh.style.display = t ? 'none' : '';
    };

    // Show forgot panel
    document.getElementById('showForgot').onclick = function() {
      document.getElementById('pLogin').classList.add('gone');
      document.getElementById('pForgot').classList.add('active');
    };

    // Back to login
    document.getElementById('backBtn').onclick = function(e) {
      e.preventDefault();
      document.getElementById('pForgot').classList.remove('active');
      document.getElementById('pLogin').classList.remove('gone');
    };

    // Fix autofill label float
    setTimeout(function() {
      ['email','pw','fEmail'].forEach(function(id) {
        var el = document.getElementById(id);
        if (el) el.setAttribute('placeholder', '\u00A0');
      });
    }, 300);