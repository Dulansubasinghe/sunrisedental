const CONTEXT_PATH = window.location.pathname.substring(0, window.location.pathname.indexOf('/', 1));

// Global array to store appointments from database
let existingAppointments = [];

// DOM Ready Check
if (document.readyState === 'loading') {
    document.addEventListener("DOMContentLoaded", initApp);
} else {
    initApp();
}

// Sequential Execution
async function initApp() {
    await loadTreatments();
    await loadDentists();
    await loadExistingAppointments(); // Load database appointments into memory
    await generateNextApptId();
    setupReceiptListeners();
    setupAvailabilityChecker(); // Setup real time conflict checking
}

// Fetch existing appointments for frontend validation
async function loadExistingAppointments() {
    try {
        const res = await fetch(CONTEXT_PATH + '/appointment');
        if (res.ok) {
            existingAppointments = await res.json();
            console.log("Loaded Existing Appointments for FE Validation:", existingAppointments);
        }
    } catch (err) {
        console.error("Error fetching existing appointments:", err);
    }
}

// Get Treatments
async function loadTreatments() {
    try {
        const res = await fetch(CONTEXT_PATH + '/treatment');
        if (!res.ok) throw new Error(`HTTP Error Status: ${res.status}`);
        const treatments = await res.json();

        const treatmentSelect = document.getElementById('treatment');
        treatmentSelect.innerHTML = '<option value="">-- Select Treatment --</option>';

        if (Array.isArray(treatments)) {
            treatments.forEach(t => {
                const price = t.standardFee || t.price || t.treatmentFee || 0;
                const name = t.treatmentName || t.name || 'Treatment';
                const id = t.id || t.treatmentId;

                const opt = document.createElement('option');
                opt.value = id;
                opt.setAttribute('data-name', name);
                opt.setAttribute('data-price', price);
                opt.textContent = `${name} - LKR ${price.toLocaleString()}`;
                treatmentSelect.appendChild(opt);
            });
        }
    } catch (err) {
        console.error("Error loading treatments:", err);
    }
}

// Fetch Dentists
async function loadDentists() {
    try {
        const res = await fetch(CONTEXT_PATH + '/dentist');
        if (!res.ok) throw new Error(`HTTP Error Status: ${res.status}`);
        const dentists = await res.json();

        const dentistSelect = document.getElementById('dentist');
        dentistSelect.innerHTML = '<option value="">-- Select Dentist --</option>';

        if (Array.isArray(dentists)) {
            dentists.forEach(d => {
                const id = d.dentistId || d.id;
                const name = d.name || 'Unknown';
                const spec = d.specialization || 'General';
                const conFee = d.consultationFee !== undefined && d.consultationFee !== null ? d.consultationFee : 2000;

                const opt = document.createElement('option');
                opt.value = id;
                opt.setAttribute('data-name', name);
                opt.setAttribute('data-confee', conFee);
                opt.textContent = `Dr. ${name} - ${spec}`;
                dentistSelect.appendChild(opt);
            });
        }
    } catch (err) {
        console.error("Error loading dentists:", err);
    }
}

// Auto Appointment ID Generator
async function generateNextApptId() {
    try {
        const res = await fetch(CONTEXT_PATH + '/appointment?action=nextCode');
        if (!res.ok) throw new Error(`HTTP Error Status: ${res.status}`);
        const data = await res.json();

        if (data && data.nextCode) {
            const apptInput = document.getElementById('appointmentNo');
            if (apptInput) apptInput.value = data.nextCode;
            const recApptId = document.getElementById('recAppointmentId');
            if (recApptId) recApptId.innerText = data.nextCode;
        } else {
            document.getElementById('appointmentNo').value = "APT-1001";
        }
    } catch (err) {
        console.error("Error fetching next appointment code:", err);
        document.getElementById('appointmentNo').value = "APT-1001";
    }
}

// Live Receipt Updates
function setupReceiptListeners() {
    const treatmentSelect = document.getElementById('treatment');
    const dentistSelect = document.getElementById('dentist');
    const consultationInput = document.getElementById('consultationFee');
    const chkConsultation = document.getElementById('chkConsultation');
    const patientInput = document.getElementById('patientName');

    const updateReceipt = () => {
        const patientName = patientInput.value.trim() || '-';
        const apptId = document.getElementById('appointmentNo').value || '-';

        const selectedDentistOpt = dentistSelect.options[dentistSelect.selectedIndex];
        const dentistName = selectedDentistOpt && selectedDentistOpt.value ? selectedDentistOpt.getAttribute('data-name') : '-';

        const selectedTreatmentOpt = treatmentSelect.options[treatmentSelect.selectedIndex];
        const treatmentName = selectedTreatmentOpt && selectedTreatmentOpt.value ? selectedTreatmentOpt.getAttribute('data-name') : 'Treatment';
        const treatmentPrice = selectedTreatmentOpt && selectedTreatmentOpt.value ? parseFloat(selectedTreatmentOpt.getAttribute('data-price')) || 0 : 0;

        const conFee = parseFloat(consultationInput.value) || 0;
        const total = conFee + treatmentPrice;

        document.getElementById('recPatientName').innerText = patientName;
        document.getElementById('recAppointmentId').innerText = apptId;
        document.getElementById('recDentistName').innerText = dentistName !== '-' ? `Dr. ${dentistName}` : '-';
        document.getElementById('recConsultationFee').innerText = 'LKR ' + conFee.toLocaleString();
        document.getElementById('recTreatmentName').innerText = treatmentName;
        document.getElementById('recTreatmentFee').innerText = 'LKR ' + treatmentPrice.toLocaleString();
        document.getElementById('recTotalBill').innerText = 'LKR ' + total.toLocaleString();
    };

    if (chkConsultation) {
        chkConsultation.addEventListener('change', () => {
            const selectedDentistOpt = dentistSelect.options[dentistSelect.selectedIndex];
            if (chkConsultation.checked) {
                if (selectedDentistOpt && selectedDentistOpt.hasAttribute('data-confee') && selectedDentistOpt.value !== "") {
                    consultationInput.value = selectedDentistOpt.getAttribute('data-confee');
                } else {
                    consultationInput.value = 2000;
                }
            } else {
                consultationInput.value = 0;
            }
            updateReceipt();
        });
    }

    dentistSelect.addEventListener('change', () => {
        const selectedDentistOpt = dentistSelect.options[dentistSelect.selectedIndex];
        if (selectedDentistOpt && selectedDentistOpt.hasAttribute('data-confee') && selectedDentistOpt.value !== "") {
            const doctorConFee = selectedDentistOpt.getAttribute('data-confee');
            if (chkConsultation && chkConsultation.checked) {
                consultationInput.value = doctorConFee;
            }
        }
        updateReceipt();
    });

    consultationInput.addEventListener('input', updateReceipt);
    treatmentSelect.addEventListener('change', updateReceipt);
    patientInput.addEventListener('input', updateReceipt);
}

// 3. Real time Doctor Availability Check Logic
function setupAvailabilityChecker() {
    const dentistSelect = document.getElementById('dentist');
    const dateInput = document.getElementById('appointmentDate');

    const checkConflict = () => {
        const selectedDentist = dentistSelect.value;
        const selectedDateStr = dateInput.value;

        const alertError = document.getElementById('alertError');
        const errorMsgText = document.getElementById('errorMsgText');
        const submitBtn = document.querySelector('#appointmentForm button[type="submit"]');

        if (!selectedDentist || !selectedDateStr) return;

        // Selected Time (Assume 30 min duration window)
        const selectedStart = new Date(selectedDateStr);
        const selectedEnd = new Date(selectedStart.getTime() + 30 * 60000);

        // Compare against existing database appointments
        const conflict = existingAppointments.find(appt => {
            if (String(appt.dentistId) === String(selectedDentist) && appt.status !== 'Cancelled') {
                const apptDateFormatted = appt.appointmentDate ? appt.appointmentDate.replace(" ", "T") : "";
                const existingStart = new Date(apptDateFormatted);
                const existingEnd = new Date(existingStart.getTime() + 30 * 60000);

                // Check Time Overlap
                return (selectedStart < existingEnd && selectedEnd > existingStart);
            }
            return false;
        });

        if (conflict) {
            errorMsgText.innerText = `⚠️ Selected Dr. already has an appointment (${conflict.appointmentCode || 'APT'}) around this time. Please choose another time slot or date.`;
            alertError.style.display = 'flex';
            if (submitBtn) {
                submitBtn.disabled = true;
                submitBtn.style.opacity = '0.5';
                submitBtn.style.cursor = 'not-allowed';
            }
        } else {
            if (alertError.innerText.includes("⚠️ Selected Dr.")) {
                alertError.style.display = 'none';
            }
            if (submitBtn) {
                submitBtn.disabled = false;
                submitBtn.style.opacity = '1';
                submitBtn.style.cursor = 'pointer';
            }
        }
    };

    dentistSelect.addEventListener('change', checkConflict);
    dateInput.addEventListener('change', checkConflict);
    dateInput.addEventListener('input', checkConflict);
}

// 4. Save Appointment Handler
document.getElementById('appointmentForm').addEventListener('submit', function (e) {
    e.preventDefault();

    const alertSuccess = document.getElementById('alertSuccess');
    const alertError = document.getElementById('alertError');
    const errorMsgText = document.getElementById('errorMsgText');

    alertSuccess.style.display = 'none';
    alertError.style.display = 'none';

    const appointmentNo = document.getElementById('appointmentNo').value.trim();
    const contact = document.getElementById('contact').value.trim();
    const patientName = document.getElementById('patientName').value.trim();
    const address = document.getElementById('address').value.trim();
    const treatmentId = document.getElementById('treatment').value;
    const dentistId = document.getElementById('dentist').value;
    const appointmentDate = document.getElementById('appointmentDate').value.trim();
    const consultationFee = parseFloat(document.getElementById('consultationFee').value) || 0;

    if (!appointmentNo || !contact || !patientName || !address || !treatmentId || !dentistId || !appointmentDate) {
        errorMsgText.innerText = "Please fill in all required details before registering the appointment.";
        alertError.style.display = 'flex';
        return;
    }

    const payload = {
        appointmentCode: appointmentNo,
        appointmentDate: appointmentDate.replace("T", " "),
        patientName: patientName,
        contactNumber: contact,
        address: address,
        dentistId: parseInt(dentistId),
        treatmentId: parseInt(treatmentId),
        consultationFee: consultationFee,
        status: "Pending"
    };

    fetch(CONTEXT_PATH + '/appointment', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(payload)
    })
        .then(async res => {
            const data = await res.json();
            if (!res.ok) {
                throw new Error(data.message || "Failed to save appointment in database.");
            }
            return data;
        })
        .then(async () => {
            alertSuccess.style.display = 'flex';
            document.getElementById('successMsg').innerText = `Appointment ${appointmentNo} registered successfully!`;

            // Reset Form Fields
            document.getElementById('contact').value = '';
            document.getElementById('patientName').value = '';
            document.getElementById('address').value = '';
            document.getElementById('treatment').value = '';
            document.getElementById('dentist').value = '';
            document.getElementById('appointmentDate').value = '';

            const chkConsultation = document.getElementById('chkConsultation');
            if (chkConsultation) chkConsultation.checked = true;
            document.getElementById('consultationFee').value = '2000';

            // Update local cache and ID sequence after new appointment save
            await loadExistingAppointments();
            await generateNextApptId();

            document.getElementById('patientName').dispatchEvent(new Event('input'));
        })
        .catch(err => {
            errorMsgText.innerText = err.message;
            alertError.style.display = 'flex';
        });
});