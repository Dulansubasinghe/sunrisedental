const CONTEXT_PATH = window.location.pathname.substring(0, window.location.pathname.indexOf('/', 1));

let allAppointments = [];
let currentActiveApptId = null;
let currentAppointmentObj = null;

// Date and time formatting helper
function formatDateTime(dateTimeStr) {
    if (!dateTimeStr || dateTimeStr === 'N/A') return 'N/A';
    try {
        const d = new Date(dateTimeStr.replace(" ", "T"));
        if (isNaN(d.getTime())) return dateTimeStr;

        const date = d.toISOString().split('T')[0];
        const time = d.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit', hour12: true });
        return `${date} | ${time}`;
    } catch {
        return dateTimeStr;
    }
}

// Force reload page when user navigates back
window.addEventListener('pageshow', function(event) {
    loadAppointments();
});

// DOM Load Check
if (document.readyState === 'loading') {
    document.addEventListener("DOMContentLoaded", loadAppointments);
} else {
    loadAppointments();
}

// Load appointments from database
function loadAppointments() {
    fetch(CONTEXT_PATH + '/appointment?_t=' + new Date().getTime(), {
        cache: 'no-store'
    })
        .then(res => {
            if (!res.ok) throw new Error("Failed to fetch appointments from database");
            return res.json();
        })
        .then(data => {
            console.log("Appointments Loaded:", data);
            allAppointments = Array.isArray(data) ? data : [];
            renderTable(allAppointments);
            populateDoctorFilter(allAppointments);
        })
        .catch(err => {
            console.error("Error loading appointments:", err);
            const tbody = document.getElementById('tableBody');
            if (tbody) {
                tbody.innerHTML = `<tr><td colspan="8" style="text-align:center; color: #ef4444; padding: 20px;">Error loading data from server.</td></tr>`;
            }
        });
}

// 2. Render Data into HTML Table
function renderTable(dataList) {
    const tbody = document.getElementById('tableBody');
    if (!tbody) return;

    tbody.innerHTML = '';

    if (dataList.length === 0) {
        tbody.innerHTML = `<tr><td colspan="8" style="text-align:center; padding: 20px; color: #64748b;">No appointments found in database.</td></tr>`;
        return;
    }

    dataList.forEach(appt => {
        const id = appt.id || appt.appointmentId;
        const apptNo = appt.appointmentCode || 'APT-???';
        const name = appt.patientName || 'N/A';
        const contact = appt.contactNumber || appt.contact || 'N/A';
        const doctor = appt.dentistName ? `Dr. ${appt.dentistName}` : 'N/A';
        const treatment = appt.treatmentName || 'N/A';
        const dateTime = formatDateTime(appt.appointmentDate);
        const status = appt.status || 'Pending';

        let badgeClass = 'badge-pending';
        if (status.toLowerCase() === 'completed') badgeClass = 'badge-completed';
        else if (status.toLowerCase() === 'cancelled') badgeClass = 'badge-cancelled';

        const tr = document.createElement('tr');
        tr.innerHTML = `
            <td><strong>${apptNo}</strong></td>
            <td>${name}</td>
            <td>${contact}</td>
            <td>${doctor}</td>
            <td>${treatment}</td>
            <td>${dateTime}</td>
            <td><span class="badge ${badgeClass}">● ${status}</span></td>
            <td>
                <button class="btn-action" onclick="openModalById(${id})">View</button>
            </td>
        `;
        tbody.appendChild(tr);
    });
}

// Populate Doctors Filter Dropdown Dynamically
function populateDoctorFilter(dataList) {
    const doctorFilter = document.getElementById('doctorFilter');
    if (!doctorFilter) return;

    const doctors = new Set();
    dataList.forEach(a => {
        if (a.dentistName) doctors.add(`Dr. ${a.dentistName}`);
    });

    doctorFilter.innerHTML = '<option value="ALL">All Doctors</option>';
    doctors.forEach(doc => {
        const opt = document.createElement('option');
        opt.value = doc;
        opt.textContent = doc;
        doctorFilter.appendChild(opt);
    });
}

// 3. Search & Filter Table Logic
function filterTable() {
    const searchInput = document.getElementById('searchInput').value.toLowerCase();
    const dateFilter = document.getElementById('dateFilter').value;
    const statusFilter = document.getElementById('statusFilter').value;
    const doctorFilter = document.getElementById('doctorFilter').value;

    const table = document.getElementById('appointmentsTable');
    const tr = table.getElementsByTagName('tr');

    for (let i = 1; i < tr.length; i++) {
        let row = tr[i];
        if (row.cells.length < 8) continue;

        let apptNo = row.cells[0].innerText.toLowerCase();
        let name = row.cells[1].innerText.toLowerCase();
        let phone = row.cells[2].innerText.toLowerCase();
        let doctor = row.cells[3].innerText;
        let dateTime = row.cells[5].innerText;
        let status = row.cells[6].innerText;

        let matchesSearch = apptNo.includes(searchInput) || name.includes(searchInput) || phone.includes(searchInput);
        let matchesStatus = (statusFilter === 'ALL') || status.toLowerCase().includes(statusFilter.toLowerCase());
        let matchesDoctor = (doctorFilter === 'ALL') || doctor.includes(doctorFilter);
        let matchesDate = (!dateFilter) || dateTime.includes(dateFilter);

        if (matchesSearch && matchesStatus && matchesDoctor && matchesDate) {
            row.style.display = '';
        } else {
            row.style.display = 'none';
        }
    }
}

// 4. Open Modal with Selected Record Details
function openModalById(id) {
    const appt = allAppointments.find(a => (a.id === id || a.appointmentId === id));
    if (!appt) return;

    currentActiveApptId = id;
    currentAppointmentObj = appt;

    const apptNo = appt.appointmentCode || 'APT-???';
    const name = appt.patientName || 'N/A';
    const contact = appt.contactNumber || appt.contact || 'N/A';
    const doctor = appt.dentistName ? `Dr. ${appt.dentistName}` : 'N/A';
    const treatment = appt.treatmentName || 'N/A';
    const dateTime = formatDateTime(appt.appointmentDate);
    const status = appt.status || 'Pending';
    const address = appt.address || 'N/A';

    // Calculate and format total fee
    let totalValue = 0;
    if (appt.totalFee !== undefined && appt.totalFee !== null && appt.totalFee > 0) {
        totalValue = appt.totalFee;
    } else {
        const conFee = appt.consultationFee || 0;
        const treatFee = appt.treatmentFee || 0;
        totalValue = conFee + treatFee;
    }

    const fee = totalValue > 0 ? `LKR ${totalValue.toLocaleString()}` : 'LKR 0';

    document.getElementById('mApptNo').innerText = apptNo;
    document.getElementById('mName').innerText = name;

    document.getElementById('mContact').innerText = contact;
    document.getElementById('mContact').style.display = 'inline';

    const contactInput = document.getElementById('mContactInput');
    contactInput.value = contact;
    contactInput.style.display = 'none';

    document.getElementById('btnEditContact').style.display = 'inline-flex';

    document.getElementById('mDoctor').innerText = doctor;
    document.getElementById('mTreatment').innerText = treatment;
    document.getElementById('mDateTime').innerText = dateTime;
    document.getElementById('mStatus').innerText = status;
    document.getElementById('mAddress').innerText = address;
    document.getElementById('mFee').innerText = fee;

    const statusSelect = document.getElementById('mStatusSelect');
    if (statusSelect) {
        statusSelect.value = status;
    }

    document.getElementById('modalError').style.display = 'none';
    document.getElementById('viewModal').style.display = 'flex';
}

// Enable Phone Editing Input
function enableContactEdit() {
    document.getElementById('mContact').style.display = 'none';
    document.getElementById('btnEditContact').style.display = 'none';

    const contactInput = document.getElementById('mContactInput');
    contactInput.style.display = 'block';
    contactInput.focus();
}

function closeModal() {
    document.getElementById('viewModal').style.display = 'none';
    currentActiveApptId = null;
    currentAppointmentObj = null;
}

// 5. Save Status Changes and Phone Number to Backend DB
function saveContactNumber() {
    const contactInput = document.getElementById('mContactInput');
    const modalError = document.getElementById('modalError');
    const statusSelect = document.getElementById('mStatusSelect');

    let newContact = currentAppointmentObj ? (currentAppointmentObj.contactNumber || currentAppointmentObj.contact) : '';

    if (contactInput && contactInput.style.display !== 'none') {
        newContact = contactInput.value.trim();
        const phoneRegex = /^[0-9]{10}$/;

        if (!phoneRegex.test(newContact)) {
            modalError.innerText = "Invalid Phone Number! Must contain exactly 10 digits (e.g. 0771234567).";
            modalError.style.display = 'block';
            return;
        }
    }

    const newStatus = statusSelect ? statusSelect.value : (currentAppointmentObj ? currentAppointmentObj.status : 'Pending');

    const payload = {
        appointmentId: currentActiveApptId,
        contactNumber: newContact,
        status: newStatus
    };

    fetch(CONTEXT_PATH + '/appointment', {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(payload)
    })
        .then(res => {
            if (!res.ok) throw new Error("Failed to update status on server.");
            return res.json().catch(() => ({}));
        })
        .then(() => {
            // Sync local state only after DB update succeeds
            if (currentAppointmentObj) {
                currentAppointmentObj.contactNumber = newContact;
                currentAppointmentObj.contact = newContact;
                currentAppointmentObj.status = newStatus;
            }
            renderTable(allAppointments);
            filterTable();
            closeModal();
            alert("Successfully Updated!");
        })
        .catch(err => {
            console.error("Update Error:", err);
            // Alert user if database update fails
            alert("Database update failed! Please check Java Backend (doPut method).");
        });
}

// 6. Delete Appointment via Backend API Call
function deleteAppointment() {
    if (!currentActiveApptId) return;

    const codeDisplay = document.getElementById('mApptNo').innerText;
    const confirmDelete = confirm(`Are you sure you want to delete appointment ${codeDisplay}?`);

    if (confirmDelete) {
        fetch(CONTEXT_PATH + '/appointment?id=' + currentActiveApptId, {
            method: 'DELETE'
        })
            .then(res => {
                if (!res.ok) throw new Error("Failed to delete appointment on server.");
                return res.json().catch(() => ({}));
            })
            .then(() => {
                // Update local array on DB delete confirmation
                allAppointments = allAppointments.filter(a => (a.id !== currentActiveApptId && a.appointmentId !== currentActiveApptId));
                renderTable(allAppointments);
                filterTable();
                closeModal();
                alert("Successfully Deleted!");
            })
            .catch(err => {
                console.error("Delete Error:", err);
                // Show alert if item was not deleted from DB
                alert("Database delete failed! Please check Java Backend (doDelete method).");
            });
    }
}