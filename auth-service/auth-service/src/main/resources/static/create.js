(function(){
  const $ = (sel) => document.querySelector(sel);

  function setResult(msg, ok){
    const box = $('#result');
    box.textContent = msg || '';
    box.className = 'result ' + (ok ? 'ok' : 'err');
  }

  function validate(username, password){
    if(!username || username.trim().length < 3) return 'Username must be at least 3 characters';
    if(!password || password.length < 6) return 'Password must be at least 6 characters';
    return null;
  }

  window.addEventListener('DOMContentLoaded', () => {
    const form = $('#create-form');
    const btn = $('#submit-btn');

    form.addEventListener('submit', async (e) => {
      e.preventDefault();
      setResult('', true);

      const username = $('#username').value;
      const password = $('#password').value;
      const authority = $('#authority') ? $('#authority').value : 'student';
      const err = validate(username, password);
      if (err) { setResult(err, false); return; }

      btn.disabled = true;
      btn.textContent = 'Creating...';
      try {
        const res = await fetch('/test/create', {
          method: 'POST',
          headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
          body: new URLSearchParams({ username, password, authority })
        });
        const text = await res.text();
        if (res.ok) {
          setResult(text || 'Created successfully', true);
          form.reset();
        } else {
          setResult(text || 'Failed to create user', false);
        }
      } catch (e) {
        setResult(e?.message || 'Network error', false);
      } finally {
        btn.disabled = false;
        btn.textContent = 'Create';
      }
    });
  });
})();
