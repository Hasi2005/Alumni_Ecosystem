(function () {
  const $ = (sel) => document.querySelector(sel);
  const $$ = (sel) => Array.from(document.querySelectorAll(sel));

  // Tabs (only relevant on the combined console page)
  $$(".tab-btn").forEach((btn) => {
    btn.addEventListener("click", () => {
      const tab = btn.getAttribute("data-tab");
      $$(".tab-btn").forEach((b) => b.setAttribute("aria-selected", String(b === btn)));
      $$(".tab").forEach((s) => s.classList.toggle("active", s.id === `tab-${tab}`));
    });
  });

  function setText(el, text) { if (!el) return; el.textContent = text; }
  function setHTML(el, html) { if (!el) return; el.innerHTML = html; }

  async function doFetch(path, opts = {}) {
    const options = { method: opts.method || "GET", credentials: "include", headers: Object.assign({}, opts.headers || {}) };
    if (opts.body) { options.headers["Content-Type"] = "application/json"; options.body = JSON.stringify(opts.body); }
    const res = await fetch(path, options);
    let payload; try { payload = await res.json(); } catch { payload = await res.text(); }
    if (!res.ok) { throw new Error(typeof payload === 'string' ? payload : JSON.stringify(payload)); }
    return payload;
  }

  // Rendering helpers
  function escapeHtml(s) {
    return String(s).replaceAll('&','&amp;').replaceAll('<','&lt;').replaceAll('>','&gt;').replaceAll('"','&quot;').replaceAll("'",'&#039;');
  }

  function renderJob(job) {
    const title = escapeHtml(job.title || 'Untitled');
    const type = escapeHtml(job.type || '-');
    const location = escapeHtml(job.location || '');
    const id = escapeHtml(job.id);
    const desc = escapeHtml(job.description || 'No description');
    return `<div class="item job-card">
      <div class="item-header">
        <div class="item-title"><strong>${title}</strong> <span class="pill">${type}</span></div>
        ${location ? `<div class="item-sub">${location}</div>` : ''}
      </div>
      <div class="item-actions">
        <button class="secondary btn-view-desc" data-target="desc-${id}">View description</button>
      </div>
      <div class="item-desc hidden" id="desc-${id}"><pre>${desc}</pre></div>
      <div class="item-meta"><small>ID: ${id}</small></div>
    </div>`;
  }

  // Attach a single delegated handler for description toggles
  document.addEventListener('click', (e) => {
    const btn = e.target.closest('.btn-view-desc');
    if (!btn) return;
    const targetId = btn.getAttribute('data-target');
    const panel = document.getElementById(targetId);
    if (!panel) return;
    const isHidden = panel.classList.contains('hidden');
    panel.classList.toggle('hidden', !isHidden);
    btn.textContent = isHidden ? 'Hide description' : 'View description';
  });

  function renderApplication(app) {
    return `<div class="item">
      <div>Application <strong>#${escapeHtml(app.id)}</strong> for Job <strong>#${escapeHtml(app.jobId)}</strong></div>
      <div>Student: ${escapeHtml(app.studentUsername || "-")}</div>
      <div>Status: <span class="pill">${escapeHtml(app.status || "-")}</span></div>
    </div>`;
  }

  function renderReferral(ref) {
    return `<div class="item">
      <div>Referral <strong>#${escapeHtml(ref.id)}</strong> for Job <strong>#${escapeHtml(ref.jobId)}</strong></div>
      <div>Student: ${escapeHtml(ref.studentUsername || "-")}</div>
      <div>Alumni: ${escapeHtml(ref.alumniUsername || "-")}</div>
      <div>Status: <span class="pill">${escapeHtml(ref.status || "-")}</span></div>
    </div>`;
  }

  // Jobs
  const jobsListEl = $("#jobs-list");
  const jobDetailEl = $("#job-detail");
  const jobsMetaEl = $("#jobs-meta");

  const btnLoadJobs = $("#btn-load-jobs");
  if (btnLoadJobs) {
    btnLoadJobs.addEventListener("click", async () => {
      if (jobDetailEl) jobDetailEl.innerHTML = "";
      if (jobsMetaEl) jobsMetaEl.textContent = "Loading jobs...";

      const alumni = $("#jobs-alumni-filter").value.trim();
      const url = alumni ? `/api/jobs?alumniUsername=${encodeURIComponent(alumni)}` : "/api/jobs";
      jobsListEl && (jobsListEl.innerHTML = "Loading...");
      try {
        const list = await doFetch(url);
        if (!Array.isArray(list)) throw new Error("Unexpected response");
        const count = list.length;
        if (jobsMetaEl) jobsMetaEl.textContent = count ? `${count} job${count!==1?'s':''} found${alumni?` for @${alumni}`:''}` : "No jobs found";
        jobsListEl && (jobsListEl.innerHTML = count ? list.map(renderJob).join("") : '<div class="empty-state">No jobs to show.</div>');
      } catch (e) {
        jobsListEl && (jobsListEl.innerHTML = `<div class="alert alert-danger">${escapeHtml(e.message)}</div>`);
        if (jobsMetaEl) jobsMetaEl.textContent = "";
      }
    });
  }

  const btnLoadJobById = $("#btn-load-job-by-id");
  if (btnLoadJobById) {
    btnLoadJobById.addEventListener("click", async () => {
      const id = $("#job-by-id").value.trim();
      if (!id) { jobDetailEl && (jobDetailEl.textContent = "Enter a job ID"); return; }
      jobsListEl && (jobsListEl.innerHTML = "");
      if (jobsMetaEl) jobsMetaEl.textContent = `Showing job #${id}`;

      jobDetailEl && (jobDetailEl.innerHTML = "Loading...");
      try {
        const job = await doFetch(`/api/jobs/${encodeURIComponent(id)}`);
        jobDetailEl && (jobDetailEl.innerHTML = renderJob(job));
      } catch (e) {
        jobDetailEl && (jobDetailEl.innerHTML = `<div class="alert alert-danger">${escapeHtml(e.message)}</div>`);
        if (jobsMetaEl) jobsMetaEl.textContent = "";
      }
    });
  }

  const btnCreateJob = $("#btn-create-job");
  if (btnCreateJob) {
    btnCreateJob.addEventListener("click", async () => {
      const title = $("#job-title").value.trim();
      const description = $("#job-description").value.trim();
      const type = $("#job-type").value;
      const location = $("#job-location").value.trim();
      const out = $("#create-job-result");
      setText(out, "Submitting..."); out.classList.remove('error');
      try {
        const result = await doFetch("/api/jobs", { method: "POST", body: { title, description, type, location } });
        setHTML(out, `<div class="alert alert-success">Created job <strong>#${escapeHtml(result.id)}</strong>: ${escapeHtml(result.title)}</div>`);
        // Optional: clear inputs after successful create
        $("#job-title").value = ""; $("#job-description").value = ""; $("#job-location").value = "";
      } catch (e) {
        setHTML(out, `<div class="alert alert-danger">${escapeHtml(e.message)}</div>`);
        out.classList.add("error");
      }
    });
  }

  // Applications
  const appsListEl = $("#applications-list");
  const appsMetaEl = $("#applications-meta");

  const btnMyApps = $("#btn-my-applications");
  if (btnMyApps) {
    btnMyApps.addEventListener("click", async () => {
      appsListEl && (appsListEl.innerHTML = "Loading...");
      appsMetaEl && (appsMetaEl.textContent = "Loading applications...");
      try {
        const list = await doFetch("/api/applications/me");
        if (!Array.isArray(list)) throw new Error("Unexpected response");
        const count = list.length;
        appsMetaEl && (appsMetaEl.textContent = count ? `${count} application${count!==1?'s':''}` : "No applications found");
        appsListEl && (appsListEl.innerHTML = count ? list.map(renderApplication).join("") : '<div class="empty-state">No applications yet.</div>');
      } catch (e) {
        appsListEl && (appsListEl.innerHTML = `<div class="alert alert-danger">${escapeHtml(e.message)}</div>`);
        appsMetaEl && (appsMetaEl.textContent = "");
      }
    });
  }

  const btnAllApps = $("#btn-all-applications");
  if (btnAllApps) {
    btnAllApps.addEventListener("click", async () => {
      appsListEl && (appsListEl.innerHTML = "Loading...");
      appsMetaEl && (appsMetaEl.textContent = "Loading applications...");
      try {
        const list = await doFetch("/api/applications");
        if (!Array.isArray(list)) throw new Error("Unexpected response");
        const count = list.length;
        appsMetaEl && (appsMetaEl.textContent = count ? `${count} application${count!==1?'s':''} (all)` : "No applications found");
        appsListEl && (appsListEl.innerHTML = count ? list.map(renderApplication).join("") : '<div class="empty-state">No applications available.</div>');
      } catch (e) {
        appsListEl && (appsListEl.innerHTML = `<div class="alert alert-danger">${escapeHtml(e.message)}</div>`);
        appsMetaEl && (appsMetaEl.textContent = "");
      }
    });
  }

  const btnApply = $("#btn-apply");
  if (btnApply) {
    btnApply.addEventListener("click", async () => {
      const jobId = $("#apply-job-id").value.trim();
      const out = $("#apply-result");
      if (!jobId) { setText(out, "Enter a job ID"); return; }
      setText(out, "Submitting..."); out.classList.remove('error');
      try {
        const result = await doFetch("/api/applications", { method: "POST", body: { jobId } });
        setHTML(out, `<div class="alert alert-success">Applied to job <strong>#${escapeHtml(result.jobId)}</strong> — application <strong>#${escapeHtml(result.id)}</strong></div>`);
      } catch (e) {
        setHTML(out, `<div class="alert alert-danger">${escapeHtml(e.message)}</div>`);
        out.classList.add("error");
      }
    });
  }

  const btnUpdateApp = $("#btn-update-application");
  if (btnUpdateApp) {
    btnUpdateApp.addEventListener("click", async () => {
      const id = $("#app-update-id").value.trim();
      const status = $("#app-update-status").value;
      const out = $("#app-update-result");
      if (!id) { setText(out, "Enter application ID"); return; }
      setText(out, "Updating..."); out.classList.remove('error');
      try {
        const result = await doFetch(`/api/applications/${encodeURIComponent(id)}/status`, { method: "PUT", body: { status } });
        setHTML(out, `<div class="alert alert-success">Application <strong>#${escapeHtml(result.id)}</strong> set to <strong>${escapeHtml(result.status)}</strong></div>`);
      } catch (e) {
        setHTML(out, `<div class="alert alert-danger">${escapeHtml(e.message)}</div>`);
        out.classList.add("error");
      }
    });
  }

  // Referrals (legacy UI in this app)
  const refsListEl = $("#referrals-list");
  const refsMetaEl = $("#referrals-meta");

  const btnMyRefsStudent = $("#btn-my-referrals-student");
  if (btnMyRefsStudent) {
    btnMyRefsStudent.addEventListener("click", async () => {
      refsListEl && (refsListEl.innerHTML = "Loading...");
      refsMetaEl && (refsMetaEl.textContent = "Loading referrals...");
      try {
        const list = await doFetch("/api/referrals/student/me");
        if (!Array.isArray(list)) throw new Error("Unexpected response");
        const count = list.length;
        refsMetaEl && (refsMetaEl.textContent = count ? `${count} referral${count!==1?'s':''}` : "No referrals found");
        refsListEl && (refsListEl.innerHTML = count ? list.map(renderReferral).join("") : '<div class="empty-state">No referrals to show.</div>');
      } catch (e) {
        refsListEl && (refsListEl.innerHTML = `<div class="alert alert-danger">${escapeHtml(e.message)}</div>`);
        refsMetaEl && (refsMetaEl.textContent = "");
      }
    });
  }

  const btnMyRefsAlumni = $("#btn-my-referrals-alumni");
  if (btnMyRefsAlumni) {
    btnMyRefsAlumni.addEventListener("click", async () => {
      refsListEl && (refsListEl.innerHTML = "Loading...");
      refsMetaEl && (refsMetaEl.textContent = "Loading referrals...");
      try {
        const list = await doFetch("/api/referrals/alumni/me");
        if (!Array.isArray(list)) throw new Error("Unexpected response");
        const count = list.length;
        refsMetaEl && (refsMetaEl.textContent = count ? `${count} referral${count!==1?'s':''}` : "No referrals found");
        refsListEl && (refsListEl.innerHTML = count ? list.map(renderReferral).join("") : '<div class="empty-state">No referrals to show.</div>');
      } catch (e) {
        refsListEl && (refsListEl.innerHTML = `<div class="alert alert-danger">${escapeHtml(e.message)}</div>`);
        refsMetaEl && (refsMetaEl.textContent = "");
      }
    });
  }

  const btnAllRefs = $("#btn-all-referrals");
  if (btnAllRefs) {
    btnAllRefs.addEventListener("click", async () => {
      refsListEl && (refsListEl.innerHTML = "Loading...");
      refsMetaEl && (refsMetaEl.textContent = "Loading referrals...");
      try {
        const list = await doFetch("/api/referrals");
        if (!Array.isArray(list)) throw new Error("Unexpected response");
        const count = list.length;
        refsMetaEl && (refsMetaEl.textContent = count ? `${count} referral${count!==1?'s':''} (all)` : "No referrals found");
        refsListEl && (refsListEl.innerHTML = count ? list.map(renderReferral).join("") : '<div class="empty-state">No referrals available.</div>');
      } catch (e) {
        refsListEl && (refsListEl.innerHTML = `<div class="alert alert-danger">${escapeHtml(e.message)}</div>`);
        refsMetaEl && (refsMetaEl.textContent = "");
      }
    });
  }

  const btnRequestRef = $("#btn-request-referral");
  if (btnRequestRef) {
    btnRequestRef.addEventListener("click", async () => {
      const jobId = $("#ref-job-id").value.trim();
      const alumniUsername = $("#ref-alumni-username").value.trim();
      const out = $("#ref-request-result");
      if (!jobId || !alumniUsername) { setText(out, "Enter job ID and alumni username"); return; }
      setText(out, "Submitting..."); out.classList.remove('error');
      try {
        const result = await doFetch("/api/referrals/request", { method: "POST", body: { jobId, alumniUsername } });
        setHTML(out, `<div class="alert alert-success">Referral requested for job <strong>#${escapeHtml(result.jobId)}</strong> → alumni <strong>${escapeHtml(result.alumniUsername)}</strong></div>`);
      } catch (e) {
        setHTML(out, `<div class="alert alert-danger">${escapeHtml(e.message)}</div>`);
        out.classList.add("error");
      }
    });
  }

  const btnUpdateRef = $("#btn-update-referral");
  if (btnUpdateRef) {
    btnUpdateRef.addEventListener("click", async () => {
      const id = $("#ref-update-id").value.trim();
      const status = $("#ref-update-status").value;
      const out = $("#ref-update-result");
      if (!id) { setText(out, "Enter referral ID"); return; }
      setText(out, "Updating..."); out.classList.remove('error');
      try {
        const result = await doFetch(`/api/referrals/${encodeURIComponent(id)}/status`, { method: "PUT", body: { status } });
        setHTML(out, `<div class="alert alert-success">Referral <strong>#${escapeHtml(result.id)}</strong> set to <strong>${escapeHtml(result.status)}</strong></div>`);
      } catch (e) {
        setHTML(out, `<div class="alert alert-danger">${escapeHtml(e.message)}</div>`);
        out.classList.add("error");
      }
    });
  }

  // Remove auto-preload to avoid mixing list and single-item views by default
  // const preloadBtn = $("#btn-load-jobs");
  // if (preloadBtn) preloadBtn.click();
})();
