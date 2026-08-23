document.addEventListener("DOMContentLoaded", function () {
    // Initial Mock Data
    if (!localStorage.getItem("clinicAppointments")) {
        const initialData = [
            {
                id: "APT-1001",
                patientName: "Nimal Fernando",
                dentist: "Dr. Perera — Orthodontist",
                dateTime: "2026-08-20T10:30"
            },
            {
                id: "APT-1002",
                patientName: "Sunil Silva",
                dentist: "Dr. Silva — Dental Surgeon",
                dateTime: "2026-08-20T14:00"
            }
        ];
        localStorage.setItem("clinicAppointments", JSON.stringify(initialData));
    }

    generateNextApptId();
});

// Auto Appointment ID Generator
function generateNextApptId() {
    const appointments = JSON.parse(localStorage.getItem("clinicAppointments")) || [];

    if (appointments.length === 0) {
        document.getElementById('appointmentNo').value = "APT-1001";
        return;
    }

    const lastAppt = appointments[appointments.length - 1];
    const lastNumber = parseInt(lastAppt.id.replace("APT-", ""));
    const nextNumber = lastNumber + 1;

    document.getElementById('appointmentNo').value = "APT-" + nextNumber;
}

// Step 7 & 8: Dynamic Dentist Filter based on Selected Treatment
const treatmentSelect = document.getElementById('treatment');
const dentistSelect = document.getElementById('dentist');

treatmentSelect.addEventListener('change', function() {
    const selectedTreatment = this.value;
    dentistSelect.innerHTML = '';

    if (!selectedTreatment) {
        dentistSelect.innerHTML = '<option value="">-- Select Treatment First --</option>';
        return;
    }

    if (selectedTreatment === "Dental Braces") {
        // Only Orthodontist can do Dental Braces
        dentistSelect.innerHTML = `
                <option value="">-- Select Dentist --</option>
                <option value="Dr. Perera — Orthodontist">Dr. Perera — Orthodontist</option>
            `;
    } else {
        // General & Surgeon do all other treatments EXCEPT Dental Braces (Orthodontist excluded)
        dentistSelect.innerHTML = `
                <option value="">-- Select Dentist --</option>
                <option value="Dr. Silva — Dental Surgeon">Dr. Silva — Dental Surgeon</option>
                <option value="Dr. Wickramasinghe — General">Dr. Wickramasinghe — General</option>
            `;
    }
});

// Form Submit & Validations
document.getElementById('appointmentForm').addEventListener('submit', function(e) {
    e.preventDefault();

    const appointmentNo = document.getElementById('appointmentNo').value.trim();
    const contact = document.getElementById('contact').value.trim();
    const patientName = document.getElementById('patientName').value.trim();
    const address = document.getElementById('address').value.trim();
    const consultationFee = document.getElementById('consultationFee').value.trim();
    const appointmentDate = document.getElementById('appointmentDate').value.trim();

    const alertSuccess = document.getElementById('alertSuccess');
    const alertError = document.getElementById('alertError');
    const errorMsgText = document.getElementById('errorMsgText');

    // Check 1: Empty Fields
    if (!appointmentNo || !contact || !patientName || !address || !dentistSelect.value || !treatmentSelect.value || !consultationFee || !appointmentDate) {
        alertSuccess.style.display = 'none';
        errorMsgText.innerText = "Please fill in all required details before registering the appointment.";
        alertError.style.display = 'flex';
        return;
    }

    // Check 2: Phone Number Validation (Exactly 10 Digits, Numbers Only)
    const phoneRegex = /^[0-9]{10}$/;
    if (!phoneRegex.test(contact)) {
        alertSuccess.style.display = 'none';
        errorMsgText.innerText = "Invalid Phone Number! Must contain exactly 10 numeric digits (e.g., 0771234567) with no letters or symbols.";
        alertError.style.display = 'flex';
        return;
    }

    const selectedDentist = dentistSelect.value;
    const appointments = JSON.parse(localStorage.getItem("clinicAppointments")) || [];
    const newTime = new Date(appointmentDate).getTime();
    const TEN_MINUTES_MS = 10 * 60 * 1000;

    // Check 3 & 4: Time Conflict Check (Same Doctor cannot be booked within 10 minutes)
    const isConflict = appointments.some(appt => {
        if (appt.dentist !== selectedDentist) return false;

        const existingTime = new Date(appt.dateTime).getTime();
        const timeDifference = Math.abs(newTime - existingTime);

        return timeDifference < TEN_MINUTES_MS;
    });

    if (isConflict) {
        alertSuccess.style.display = 'none';
        errorMsgText.innerHTML = `<strong>Doctor Unavailable:</strong> ${selectedDentist} has an appointment scheduled near this time. Appointments for the same doctor must be at least 10 minutes apart!`;
        alertError.style.display = 'flex';
        return;
    }

    // Save New Appointment
    const newAppointment = {
        id: appointmentNo,
        patientName: patientName,
        dentist: selectedDentist,
        dateTime: appointmentDate
    };
    appointments.push(newAppointment);
    localStorage.setItem("clinicAppointments", JSON.stringify(appointments));

    // Display Success Alert
    alertError.style.display = 'none';
    alertSuccess.style.display = 'flex';
    document.getElementById('successMsg').innerText = `Appointment ${appointmentNo} registered successfully! Receipt generated below.`;

    // Update Bill Receipt dynamically
    const selectedOption = treatmentSelect.options[treatmentSelect.selectedIndex];
    const treatmentName = selectedOption.value;
    const treatmentPrice = parseInt(selectedOption.getAttribute('data-price')) || 0;
    const conFee = parseInt(consultationFee) || 0;
    const total = conFee + treatmentPrice;

    document.getElementById('recAppointmentId').innerText = appointmentNo;
    document.getElementById('recPatientName').innerText = patientName;
    document.getElementById('recDentistName').innerText = selectedDentist;
    document.getElementById('recConsultationFee').innerText = 'LKR ' + conFee.toLocaleString();
    document.getElementById('recTreatmentName').innerText = treatmentName;
    document.getElementById('recTreatmentFee').innerText = 'LKR ' + treatmentPrice.toLocaleString();
    document.getElementById('recTotalBill').innerText = 'LKR ' + total.toLocaleString();

    // Reset Inputs
    document.getElementById('contact').value = '';
    document.getElementById('patientName').value = '';
    document.getElementById('address').value = '';
    document.getElementById('treatment').value = '';
    document.getElementById('dentist').innerHTML = '<option value="">-- Select Treatment First --</option>';
    document.getElementById('appointmentDate').value = '';

    generateNextApptId();
});