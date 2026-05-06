const BASE_URL = 'http://localhost:8081/api';

const savedUser = sessionStorage.getItem('sms-user');
let currentUser = savedUser
    ? JSON.parse(savedUser)
    : { username: 'admin', password: 'admin123' };

function getCredentials() {
    return btoa(currentUser.username + ':' + currentUser.password);
}

async function apiCall(url, method = 'GET', body = null) {
    const options = {
        method,
        headers: {
            'Content-Type': 'application/json',
            'Authorization': 'Basic ' + getCredentials()
        }
    };
    if (body) options.body = JSON.stringify(body);

    const res = await fetch(BASE_URL + url, options);

    if (res.status === 401) {
        sessionStorage.removeItem('sms-user');
        window.location.href = 'login.html';
        return;
    }

    if (!res.ok) {
        const err = await res.text();
        throw new Error(err || 'API Error');
    }
    const text = await res.text();
    return text ? JSON.parse(text) : {};
}

function showToast(message, type = 'success') {
    const toast = document.getElementById('toast');
    if (!toast) return;
    toast.textContent = message;
    toast.className = `toast show ${type}`;
    setTimeout(() => { toast.className = 'toast'; }, 3000);
}

function openModal(id) {
    document.getElementById(id).classList.add('open');
}
function closeModal(id) {
    document.getElementById(id).classList.remove('open');
}

function formatCurrency(amount) {
    return '₹' + Number(amount || 0).toLocaleString('en-IN');
}

function gradeClass(grade) {
    if (!grade) return '';
    if (grade === 'A+' || grade === 'A') return 'badge-success';
    if (grade === 'B') return 'badge-info';
    if (grade === 'C' || grade === 'D') return 'badge-warning';
    return 'badge-danger';
}

function statusBadge(status) {
    const map = {
        'Active':   'badge-success',
        'Present':  'badge-success',
        'Paid':     'badge-success',
        'Pass':     'badge-success',
        'Absent':   'badge-danger',
        'Pending':  'badge-danger',
        'Fail':     'badge-danger',
        'Leave':    'badge-warning',
        'Partial':  'badge-warning',
        'Inactive': 'badge-warning',
    };
    const cls = map[status] || 'badge-info';
    return `<span class="badge ${cls}">${status}</span>`;
}

function setActiveNav() {
    const page = window.location.pathname.split('/').pop();
    document.querySelectorAll('.nav-links a').forEach(a => {
        a.classList.remove('active');
        if (a.getAttribute('href') === page) a.classList.add('active');
    });
}

document.addEventListener('DOMContentLoaded', setActiveNav);