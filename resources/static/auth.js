// ═══════════════════════════════════════════════════════
//  auth.js  —  Role-based access control for all pages
// ═══════════════════════════════════════════════════════

const BASE_URL = 'http://localhost:8081/api';

// ── Get current user from server ──────────────────────────────────────────
async function getCurrentUser() {
  try {
    const res = await fetch('/api/auth/me', {
      method: 'GET',
      credentials: 'include',
      headers: { 'Content-Type': 'application/json' }
    });
    if (!res.ok) return null;
    const data = await res.json();
    return data.success ? data : null;
  } catch(e) {
    return null;
  }
}

// ── Check login and redirect if not authenticated ─────────────────────────
async function requireLogin() {
  const user = await getCurrentUser();
  if (!user || !user.success) {
    window.location.href = 'login.html';
    return null;
  }
  return user;
}

// ── Check role — redirect if not allowed ──────────────────────────────────
async function requireRole(...allowedRoles) {
  const user = await requireLogin();
  if (!user) return null;
  if (!allowedRoles.includes(user.role)) {
    alert('Access denied! You do not have permission to view this page.');
    window.location.href = getRoleHomePage(user.role);
    return null;
  }
  return user;
}

// ── Get home page for each role ───────────────────────────────────────────
function getRoleHomePage(role) {
  if (role === 'ADMIN')   return 'index.html';
  if (role === 'TEACHER') return 'attendance.html';
  if (role === 'STUDENT') return 'student-dashboard.html';
  return 'login.html';
}

// ── Show / hide elements based on role ───────────────────────────────────
function applyRoleVisibility(role) {
  // Show elements for specific roles
  document.querySelectorAll('[data-role]').forEach(el => {
    const allowed = el.getAttribute('data-role').split(',').map(r => r.trim());
    el.style.display = allowed.includes(role) ? '' : 'none';
  });
  // Hide elements for specific roles
  document.querySelectorAll('[data-hide-role]').forEach(el => {
    const hidden = el.getAttribute('data-hide-role').split(',').map(r => r.trim());
    if (hidden.includes(role)) el.style.display = 'none';
  });
}

// ── Render navbar with role info ──────────────────────────────────────────
function renderNavbar(user) {
  const roleColors = {
    ADMIN:   { bg: '#fbbf24', color: '#78350f' },
    TEACHER: { bg: '#34d399', color: '#064e3b' },
    STUDENT: { bg: '#60a5fa', color: '#1e3a5f' },
  };
  const rc = roleColors[user.role] || { bg: '#e2e8f0', color: '#334155' };

  const navEl = document.getElementById('navbar-user');
  if (navEl) {
    navEl.innerHTML = `
      <span style="background:${rc.bg};color:${rc.color};padding:3px 10px;border-radius:20px;font-size:12px;font-weight:600;margin-right:10px;">
        ${user.role}
      </span>
      <span style="color:#bfdbfe;font-size:13px;margin-right:12px;">${user.fullName || user.username}</span>
    `;
  }
}

// ── Logout ────────────────────────────────────────────────────────────────
async function doLogout() {
  try {
    await fetch('/api/auth/logout', { method: 'POST', credentials: 'include' });
  } catch(e) {}
  sessionStorage.clear();
  window.location.href = 'login.html';
}

// ── Generic API call with credentials ────────────────────────────────────
async function apiCall(url, method = 'GET', body = null) {
  const options = {
    method,
    credentials: 'include',
    headers: { 'Content-Type': 'application/json' }
  };
  if (body) options.body = JSON.stringify(body);
  const res = await fetch(BASE_URL + url, options);
  if (res.status === 401) { window.location.href = 'login.html'; return; }
  if (res.status === 403) { showToast('Access denied for your role', 'error'); return; }
  if (!res.ok) throw new Error(await res.text());
  const text = await res.text();
  return text ? JSON.parse(text) : {};
}

// ── Toast notification ────────────────────────────────────────────────────
function showToast(message, type = 'success') {
  const toast = document.getElementById('toast');
  if (!toast) return;
  toast.textContent = message;
  toast.className = `toast show ${type}`;
  setTimeout(() => { toast.className = 'toast'; }, 3000);
}

// ── Open / close modal ────────────────────────────────────────────────────
function openModal(id)  { document.getElementById(id).classList.add('open');    }
function closeModal(id) { document.getElementById(id).classList.remove('open'); }

// ── Status badge ──────────────────────────────────────────────────────────
function statusBadge(status) {
  const map = {
    'Active':'badge-success','Present':'badge-success',
    'Paid':'badge-success','Pass':'badge-success',
    'Absent':'badge-danger','Pending':'badge-danger','Fail':'badge-danger',
    'Leave':'badge-warning','Partial':'badge-warning','Inactive':'badge-warning',
  };
  return `<span class="badge ${map[status]||'badge-info'}">${status}</span>`;
}

function formatCurrency(amount) {
  return '₹' + Number(amount || 0).toLocaleString('en-IN');
}
