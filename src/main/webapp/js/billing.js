function loadBillData() {
    const inputVal = document.getElementById('apptSearchInput').value.trim();
    if (!inputVal) {
        alert('Please enter an Appointment ID');
        return;
    }
    document.getElementById('pToken').innerText = inputVal;
    document.getElementById('recToken').innerText = inputVal;
    alert('Loaded details for ' + inputVal);
}

function finalizeReceipt() {
    alert('Receipt generated and finalized successfully!');
}