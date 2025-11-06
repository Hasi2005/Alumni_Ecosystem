(function () {
  const $ = (sel) => document.querySelector(sel);
  const $$ = (sel) => Array.from(document.querySelectorAll(sel));

  function setText(el, text) { if (el) el.textContent = text; }
  function setHTML(el, html) { if (el) el.innerHTML = html; }

  async function doFetch(path, opts = {}) {
    const options = { method: opts.method || 'GET', credentials: 'include', headers: Object.assign({}, opts.headers || {}) };
    if (opts.body) { options.headers['Content-Type'] = 'application/json'; options.body = JSON.stringify(opts.body); }
    const res = await fetch(path, options);
    let payload; try { payload = await res.json(); } catch { payload = await res.text(); }
    if (!res.ok) throw new Error(typeof payload === 'string' ? payload : JSON.stringify(payload));
    return payload;
  }

  function escapeHtml(s){ return String(s).replaceAll('&','&amp;').replaceAll('<','&lt;').replaceAll('>','&gt;').replaceAll('"','&quot;').replaceAll("'",'&#039;'); }

  function renderReferral(ref) {
    return `<div class=\"item\">\n      <div>Referral <strong>#${escapeHtml(ref.id)}</strong> for Job <strong>#${escapeHtml(ref.jobId)}</strong></div>\n      <div>Student: ${escapeHtml(ref.studentUsername || '-')}</div>\n      <div>Alumni: ${escapeHtml(ref.alumniUsername || '-')}</div>\n      <div>Status: <span class=\"pill\">${escapeHtml(ref.status || '-')}</span></div>\n    </div>`;
  }

  const refsListEl = $('#referrals-list');
  const refsMetaEl = $('#referrals-meta');

  const btnMyRefsStudent = $('#btn-my-referrals-student');
  if (btnMyRefsStudent) {
    btnMyRefsStudent.addEventListener('click', async () => {
      setHTML(refsListEl, 'Loading...'); setText(refsMetaEl, 'Loading referrals...');
      try {
        const list = await doFetch('/api/referrals/student/me');
        if (!Array.isArray(list)) throw new Error('Unexpected response');
        const count = list.length;
        setText(refsMetaEl, count ? `${count} referral${count!==1?'s':''}` : 'No referrals found');
        setHTML(refsListEl, count ? list.map(renderReferral).join('') : '<div class="empty-state">No referrals to show.</div>');
      } catch (e) {
        setHTML(refsListEl, `<div class=\"alert alert-danger\">${escapeHtml(e.message)}</div>`);
        setText(refsMetaEl, '');
      }
    });
  }

  const btnMyRefsAlumni = $('#btn-my-referrals-alumni');
  if (btnMyRefsAlumni) {
    btnMyRefsAlumni.addEventListener('click', async () => {
      setHTML(refsListEl, 'Loading...'); setText(refsMetaEl, 'Loading referrals...');
      try {
        const list = await doFetch('/api/referrals/alumni/me');
        if (!Array.isArray(list)) throw new Error('Unexpected response');
        const count = list.length;
        setText(refsMetaEl, count ? `${count} referral${count!==1?'s':''}` : 'No referrals found');
        setHTML(refsListEl, count ? list.map(renderReferral).join('') : '<div class="empty-state">No referrals to show.</div>');
      } catch (e) {
        setHTML(refsListEl, `<div class=\"alert alert-danger\">${escapeHtml(e.message)}</div>`);
        setText(refsMetaEl, '');
      }
    });
  }

  const btnAllRefs = $('#btn-all-referrals');
  if (btnAllRefs) {
    btnAllRefs.addEventListener('click', async () => {
      setHTML(refsListEl, 'Loading...'); setText(refsMetaEl, 'Loading referrals...');
      try {
        const list = await doFetch('/api/referrals');
        if (!Array.isArray(list)) throw new Error('Unexpected response');
        const count = list.length;
        setText(refsMetaEl, count ? `${count} referral${count!==1?'s':''} (all)` : 'No referrals found');
        setHTML(refsListEl, count ? list.map(renderReferral).join('') : '<div class="empty-state">No referrals available.</div>');
      } catch (e) {
        setHTML(refsListEl, `<div class=\"alert alert-danger\">${escapeHtml(e.message)}</div>`);
        setText(refsMetaEl, '');
      }
    });
  }

  const btnRequestRef = $('#btn-request-referral');
  if (btnRequestRef) {
    btnRequestRef.addEventListener('click', async () => {
      const jobIdInput = $('#ref-job-id');
      const alumniInput = $('#ref-alumni-username');
      const jobId = jobIdInput.value.trim();
      const alumniUsername = alumniInput.value.trim();
      const out = $('#ref-request-result');
      if (!jobId || !alumniUsername) { setText(out, 'Enter job ID and alumni username'); return; }
      setText(out, 'Submitting...'); out.classList.remove('error');
      try {
        const res = await doFetch('/api/referrals/request', { method: 'POST', body: { jobId, alumniUsername } });
        setHTML(out, `<div class=\"alert alert-success\">Referral requested for job <strong>#${escapeHtml(res.jobId)}</strong> → alumni <strong>${escapeHtml(res.alumniUsername)}</strong> (id <strong>#${escapeHtml(res.id)}</strong>)</div>`);
        jobIdInput.value = ''; alumniInput.value = '';
      } catch (e) {
        setHTML(out, `<div class=\"alert alert-danger\">${escapeHtml(e.message)}</div>`);
        out.classList.add('error');
      }
    });
  }

  const btnUpdateRef = $('#btn-update-referral');
  if (btnUpdateRef) {
    btnUpdateRef.addEventListener('click', async () => {
      const id = $('#ref-update-id').value.trim();
      const status = $('#ref-update-status').value;
      const out = $('#ref-update-result');
      if (!id) { setText(out, 'Enter referral ID'); return; }
      setText(out, 'Updating...'); out.classList.remove('error');
      try {
        const res = await doFetch(`/api/referrals/${encodeURIComponent(id)}/status`, { method: 'PUT', body: { status } });
        setHTML(out, `<div class=\"alert alert-success\">Referral <strong>#${escapeHtml(res.id)}</strong> set to <strong>${escapeHtml(res.status)}</strong></div>`);
      } catch (e) {
        setHTML(out, `<div class=\"alert alert-danger\">${escapeHtml(e.message)}</div>`);
        out.classList.add('error');
      }
    });
  }
})();
