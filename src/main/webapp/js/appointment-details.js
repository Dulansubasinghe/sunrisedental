let currentActiveApptId = null;
let currentActiveRow = null;

function filterTable() {
    const searchInput = document.getElementById('searchInput').value.toLowerCase();
    const dateFilter = document.getElementById('dateFilter').value;
    const statusFilter = document.getElementById('statusFilter').value;
    const doctorFilter = document.getElementById('doctorFilter').value;

    const table = document.getElementById('appointmentsTable');
    const tr = table.getElementsByTagName('tr');

    for (let i = 1; i < tr.length; i++) {
        let row = tr[i];
        let apptNo = row.cells[0].innerText.toLowerCase();
        let name = row.cells[1].innerText.toLowerCase();
        let phone = row.cells[2].innerText.toLowerCase();
        let doctor = row.cells[3].innerText;
        let dateTime = row.cells[5].innerText;
        let status = row.cells[6].innerText;

        let matchesSearch = apptNo.includes(searchInput) || name.includes(searchInput) || phone.includes(searchInput);
        let matchesStatus = (statusFilter === 'ALL') || status.includes(statusFilter);
        let matchesDoctor = (doctorFilter === 'ALL') || doctor.includes(doctorFilter);
        let matchesDate = (!dateFilter) || dateTime.includes(dateFilter);

        if (matchesSearch && matchesStatus && matchesDoctor && matchesDate) {
            row.style.display = '';
        } else {
            row.style.display = 'none';
        }
    }
}

function openModal(apptNo, name, contact, doctor, treatment, dateTime, status, address, fee) {
    currentActiveApptId = apptNo;

    // Find active row reference
    const rows = document.querySelectorAll('#appointmentsTable tbody tr');
    for (let row of rows) {
        if (row.cells[0].innerText.trim() === apptNo) {
            currentActiveRow = row;
            break;
        }
    }

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

    document.getElementById('modalError').style.display = 'none';
    document.getElementById('viewModal').style.display = 'flex';
}

// Enable Editing Phone Number
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
    currentActiveRow = null;
}

// Save Phone Number Changes
function saveContactNumber() {
    const contactInput = document.getElementById('mContactInput');
    const modalError = document.getElementById('modalError');

    // Save changes only when edit mode is active
    if (contactInput.style.display !== 'none') {
        const newContact = contactInput.value.trim();
        const phoneRegex = /^[0-9]{10}$/;

        if (!phoneRegex.test(newContact)) {
            modalError.innerText = "Invalid Phone Number! Must contain exactly 10 digits (e.g. 0771234567).";
            modalError.style.display = 'block';
            return;
        }

        if (currentActiveRow) {
            // Update Table Cell
            currentActiveRow.cells[2].innerText = newContact;

            // Update View Button onclick function parameters
            const viewBtn = currentActiveRow.cells[7].querySelector('button');
            const apptNo = document.getElementById('mApptNo').innerText;
            const name = document.getElementById('mName').innerText;
            const doctor = document.getElementById('mDoctor').innerText;
            const treatment = document.getElementById('mTreatment').innerText;
            const dateTime = document.getElementById('mDateTime').innerText;
            const status = document.getElementById('mStatus').innerText;
            const address = document.getElementById('mAddress').innerText;
            const fee = document.getElementById('mFee').innerText;

            viewBtn.setAttribute('onclick', `openModal('${apptNo}', '${name}', '${newContact}', '${doctor}', '${treatment}', '${dateTime}', '${status}', '${address}', '${fee}')`);
        }
    }

    closeModal();
}

// Delete Appointment Logic
function deleteAppointment() {
    if (!currentActiveApptId) return;

    const confirmDelete = confirm(`Are you sure you want to delete appointment ${currentActiveApptId}?`);
    if (confirmDelete) {
        if (currentActiveRow) {
            currentActiveRow.remove();
        }

        let appointments = JSON.parse(localStorage.getItem("clinicAppointments")) || [];
        appointments = appointments.filter(appt => appt.id !== currentActiveApptId);
        localStorage.setItem("clinicAppointments", JSON.stringify(appointments));

        closeModal();
    }
}