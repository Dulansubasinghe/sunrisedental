const CONTEXT_PATH = window.location.pathname.substring(0, window.location.pathname.indexOf('/', 1));

// Fetch today's appointment count and active dentists on page load
document.addEventListener("DOMContentLoaded", function () {
    loadTodayAppointmentCount();
    loadActiveDentists(); // load Active Dentists
});

// Function to load today's appointment count in real time
function loadTodayAppointmentCount() {
    fetch(CONTEXT_PATH + '/appointment?action=todayCount')
        .then(res => res.json())
        .then(data => {
            if (data && data.count !== undefined) {
                const countElem = document.getElementById('todayCount');
                if (countElem) {
                    countElem.innerText = data.count;
                }
            }
        })
        .catch(err => console.error("Error loading today's count:", err));
}

// Load and render active dentists list from database
function loadActiveDentists() {
    fetch(CONTEXT_PATH + '/dentist?action=activeList')
        .then(res => res.json())
        .then(data => {
            const listElem = document.getElementById('dentistList');
            if (!listElem) return;

            if (Array.isArray(data) && data.length > 0) {
                listElem.innerHTML = data.map(dentist => {
                    const statusStr = (dentist.status || 'Available').trim();
                    const isConsulting = statusStr.toLowerCase().includes('consult');
                    const badgeClass = isConsulting ? 'badge-consulting' : 'badge-available';
                    const dotStyle = isConsulting ? 'style="background:#d97706;"' : '';

                    return `
                        <div class="dentist-item">
                            <span class="dentist-name">${dentist.name} — <span style="font-weight:400; color:#64748b;">${dentist.specialization || 'General'}</span></span>
                            <span class="${badgeClass}"><span class="status-dot" ${dotStyle}></span> ${statusStr}</span>
                        </div>
                    `;
                }).join('');
            } else {
                listElem.innerHTML = '<div style="color:#64748b; font-size:13px; padding: 4px 0;">No active dentists available.</div>';
            }
        })
        .catch(err => console.error("Error loading active dentists:", err));
}

function logout() {
    window.location.href = '../index.html';
}

// Modal Display Functions
function openHelpModal() {
    document.getElementById('helpModal').style.display = 'flex';
}

function closeHelpModal() {
    document.getElementById('helpModal').style.display = 'none';
}

window.onclick = function(event) {
    const modal = document.getElementById('helpModal');
    if (event.target === modal) {
        modal.style.display = 'none';
    }
}

// Accordion Toggle
function toggleFaq(button) {
    const faqItem = button.parentElement;
    document.querySelectorAll('.faq-item').forEach(item => {
        if (item !== faqItem) item.classList.remove('active');
    });
    faqItem.classList.toggle('active');
}

// Form Submission
function submitTicket(event) {
    event.preventDefault();
    alert('Your support ticket has been submitted to the IT team!');
    document.getElementById('supportForm').reset();
    closeHelpModal();
}