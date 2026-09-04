// State variables for DB appointment data
let currentAppointment = null;
let currentTotalAmount = 0;
let currentConsultationFee = 0;
let currentTreatmentCost = 0;

// Check URL code parameter and autoload data on page load
document.addEventListener("DOMContentLoaded", function () {
    const urlParams = new URLSearchParams(window.location.search);
    const codeFromUrl = urlParams.get('code');

    if (codeFromUrl) {
        document.getElementById('apptSearchInput').value = codeFromUrl;
        loadBillData();
    }
});

// Load backend data and populate UI elements
function loadBillData() {
    const apptCode = document.getElementById('apptSearchInput').value.trim();

    if (!apptCode) {
        alert("Please enter an Appointment Code / Token!");
        return;
    }

    fetch(`../bill?appointmentCode=${encodeURIComponent(apptCode)}`)
        .then(response => {
            if (!response.ok) {
                throw new Error("No appointment details found for this code!");
            }
            return response.json();
        })
        .then(data => {
            console.log("Loaded Bill Data:", data);
            currentAppointment = data;

            // Dynamic Field Matching logic
            const parsedConFee = data.consultationFee !== undefined ? data.consultationFee : (data.consultation_fee !== undefined ? data.consultation_fee : data.doctorFee);
            const parsedTreatCost = data.treatmentCost !== undefined ? data.treatmentCost : (data.treatmentPrice !== undefined ? data.treatmentPrice : (data.standardFee !== undefined ? data.standardFee : (data.treatmentFee !== undefined ? data.treatmentFee : data.price)));

            currentConsultationFee = parseFloat(parsedConFee) || 0.00;
            currentTreatmentCost = parseFloat(parsedTreatCost) || 0.00;
            currentTotalAmount = currentConsultationFee + currentTreatmentCost;

            // Formatted Date
            const now = new Date();
            const formattedDate = now.toLocaleDateString('en-US', { month: 'short', day: 'numeric', year: 'numeric' }) +
                ' - ' + now.toLocaleTimeString('en-US', { hour: '2-digit', minute: '2-digit' });

            // --- Left Side UI Update ---
            document.getElementById('pName').innerText = data.patientName || 'N/A';
            document.getElementById('pToken').innerText = data.appointmentCode || apptCode;
            document.getElementById('pDoctor').innerText = data.dentistName ? (data.dentistName.startsWith('Dr.') ? data.dentistName : `Dr. ${data.dentistName}`) : 'N/A';

            document.getElementById('lblTreatmentName').innerText = `${data.treatmentName || 'Treatment Charge'}:`;
            document.getElementById('feeConsultation').innerText = `LKR ${currentConsultationFee.toLocaleString('en-US', {minimumFractionDigits: 2, maximumFractionDigits: 2})}`;
            document.getElementById('feeTreatment').innerText = `LKR ${currentTreatmentCost.toLocaleString('en-US', {minimumFractionDigits: 2, maximumFractionDigits: 2})}`;
            document.getElementById('lblTotalPayable').innerText = `LKR ${currentTotalAmount.toLocaleString('en-US', {minimumFractionDigits: 2, maximumFractionDigits: 2})}`;

            // --- Right Side Thermal Receipt Update ---
            document.getElementById('recDate').innerText = formattedDate;
            document.getElementById('recToken').innerText = data.appointmentCode || apptCode;
            document.getElementById('recPatient').innerText = data.patientName || 'N/A';
            document.getElementById('recDoctor').innerText = data.dentistName ? (data.dentistName.startsWith('Dr.') ? data.dentistName : `Dr. ${data.dentistName}`) : 'N/A';

            document.getElementById('recFeeConsultation').innerText = `LKR ${currentConsultationFee.toLocaleString('en-US', {minimumFractionDigits: 2, maximumFractionDigits: 2})}`;
            document.getElementById('recTreatmentName').innerText = `${data.treatmentName || 'Treatment Charge'}:`;
            document.getElementById('recFeeTreatment').innerText = `LKR ${currentTreatmentCost.toLocaleString('en-US', {minimumFractionDigits: 2, maximumFractionDigits: 2})}`;
            document.getElementById('recTotal').innerText = `LKR ${currentTotalAmount.toLocaleString('en-US', {minimumFractionDigits: 2, maximumFractionDigits: 2})}`;
        })
        .catch(error => {
            alert("Error: " + error.message);
        });
}

// Save bill to database and print receipt
function finalizeReceipt() {
    if (!currentAppointment || (!currentAppointment.appointmentId && !currentAppointment.id)) {
        alert("Please load a valid appointment bill first!");
        return;
    }

    const selectedPaymentMethodElement = document.querySelector('input[name="paymentMethod"]:checked');
    const selectedPaymentMethod = selectedPaymentMethodElement ? selectedPaymentMethodElement.value : 'Cash';

    const payload = {
        appointmentId: currentAppointment.appointmentId || currentAppointment.id,
        consultationFee: currentConsultationFee,
        treatmentCost: currentTreatmentCost,
        totalAmount: currentTotalAmount,
        paymentMethod: selectedPaymentMethod
    };

    fetch('../bill', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(payload)
    })
        .then(response => response.json())
        .then(data => {
            if (data.status === 'success' || data.status === 'created' || data.success) {
                alert(`Bill Saved Successfully!\nBill Number: ${data.billNumber || 'Generated'}\nPayment Method: ${selectedPaymentMethod}`);

                // Auto Print Trigger
                window.print();
            } else {
                alert("Failed to save bill: " + (data.message || "Unknown error"));
            }
        })
        .catch(error => {
            console.error("Error finalizing receipt:", error);
            alert("Server communication error while saving the bill.");
        });
}