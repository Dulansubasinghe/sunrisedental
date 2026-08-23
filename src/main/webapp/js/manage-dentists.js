let selectedRow = null;

function openAddModal() {
    document.getElementById('addDentistForm').reset();
    document.getElementById('addModal').style.display = 'flex';
}

function closeAddModal() {
    document.getElementById('addModal').style.display = 'none';
}

function handleAddDentist(event) {
    event.preventDefault();

    const id = document.getElementById('addId').value.trim();
    const name = document.getElementById('addName').value.trim();
    const spec = document.getElementById('addSpec').value;
    const feeInput = parseFloat(document.getElementById('addFee').value);
    const formattedFee = feeInput.toLocaleString('en-US', { minimumFractionDigits: 2, maximumFractionDigits: 2 });
    const contact = document.getElementById('addContact').value.trim();
    const status = document.getElementById('addStatus').value;

    const tableBody = document.getElementById('dentistTableBody');
    const newRow = tableBody.insertRow();
    const badgeClass = status === 'Active' ? 'badge active' : 'badge inactive';

    newRow.innerHTML = `
        <td><strong>#${id}</strong></td>
        <td>${name}</td>
        <td>${spec}</td>
        <td>LKR ${formattedFee}</td>
        <td>${contact}</td>
        <td><span class="${badgeClass}">${status}</span></td>
        <td>
            <div class="action-dropdown">
                <button class="btn-action">Options ▾</button>
                <div class="dropdown-content">
                    <a href="javascript:void(0)" onclick="openUpdateModal(this)">✏️ Update Details</a>
                    <a href="javascript:void(0)" class="danger-text" onclick="deleteDentist(this)">🗑️ Delete (Deactivate)</a>
                </div>
            </div>
        </td>
    `;

    closeAddModal();
    alert("New Dentist #" + id + " Added Successfully!");
}

function openUpdateModal(element) {
    selectedRow = element.closest('tr');

    const id = selectedRow.cells[0].innerText.replace('#', '').trim();
    const name = selectedRow.cells[1].innerText.trim();

    // Clean fee text using Regex (removes 'LKR', commas, and extra spaces)
    const rawFeeText = selectedRow.cells[3].innerText;
    const cleanFee = rawFeeText.replace(/[^0-9.]/g, '');

    const contact = selectedRow.cells[4].innerText.trim();
    const status = selectedRow.cells[5].innerText.trim();

    document.getElementById('updateDentistName').value = name + ' (' + id + ')';
    document.getElementById('updateFee').value = cleanFee ? parseFloat(cleanFee) : '';
    document.getElementById('updateContact').value = contact;
    document.getElementById('updateStatus').value = status;

    document.getElementById('updateModal').style.display = 'flex';
}

function closeUpdateModal() {
    document.getElementById('updateModal').style.display = 'none';
}

function handleUpdateDentist(event) {
    event.preventDefault();

    if (selectedRow) {
        const feeInput = parseFloat(document.getElementById('updateFee').value);
        const formattedFee = feeInput.toLocaleString('en-US', { minimumFractionDigits: 2, maximumFractionDigits: 2 });
        const contact = document.getElementById('updateContact').value.trim();
        const status = document.getElementById('updateStatus').value;

        selectedRow.cells[3].innerText = 'LKR ' + formattedFee;
        selectedRow.cells[4].innerText = contact;

        const badgeClass = status === 'Active' ? 'badge active' : 'badge inactive';
        selectedRow.cells[5].innerHTML = `<span class="${badgeClass}">${status}</span>`;

        closeUpdateModal();
        alert("Dentist Details Updated Successfully!");
    }
}

function deleteDentist(element) {
    const row = element.closest('tr');
    const dentistName = row.cells[1].innerText;

    if (confirm("Are you sure you want to set " + dentistName + " to Inactive?")) {
        row.cells[5].innerHTML = `<span class="badge inactive">Inactive</span>`;
        alert(dentistName + " status changed to Inactive!");
    }
}