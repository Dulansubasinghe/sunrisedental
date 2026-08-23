let selectedRow = null;

function openAddModal() {
    document.getElementById('addTreatmentForm').reset();
    document.getElementById('addModal').style.display = 'flex';
}

function closeAddModal() {
    document.getElementById('addModal').style.display = 'none';
}

function handleAddTreatment(event) {
    event.preventDefault();

    const id = document.getElementById('addId').value.trim();
    const name = document.getElementById('addName').value.trim();
    const feeInput = parseFloat(document.getElementById('addFee').value);
    const formattedFee = feeInput.toLocaleString('en-US', { minimumFractionDigits: 2, maximumFractionDigits: 2 });
    const duration = document.getElementById('addDuration').value;
    const status = document.getElementById('addStatus').value;

    const tableBody = document.getElementById('treatmentTableBody');
    const newRow = tableBody.insertRow();
    const badgeClass = status === 'Active' ? 'badge active' : 'badge inactive';

    newRow.innerHTML = `
        <td><strong>#${id}</strong></td>
        <td>${name}</td>
        <td>LKR ${formattedFee}</td>
        <td>${duration}</td>
        <td><span class="${badgeClass}">${status}</span></td>
        <td>
            <div class="action-dropdown">
                <button class="btn-action">Options ▾</button>
                <div class="dropdown-content">
                    <a href="javascript:void(0)" onclick="openUpdateModal(this)">✏️ Update Details</a>
                    <a href="javascript:void(0)" class="danger-text" onclick="deleteTreatment(this)">🗑️ Delete (Deactivate)</a>
                </div>
            </div>
        </td>
    `;

    closeAddModal();
    alert("✅ Treatment #" + id + " Added Successfully!");
}

function openUpdateModal(element) {
    selectedRow = element.closest('tr');

    const id = selectedRow.cells[0].innerText.replace('#', '').trim();
    const name = selectedRow.cells[1].innerText.trim();

    // Clean fee text using Regex (removes 'LKR', commas, and extra spaces)
    const rawFeeText = selectedRow.cells[2].innerText;
    const cleanFee = rawFeeText.replace(/[^0-9.]/g, '');

    const duration = selectedRow.cells[3].innerText.trim();
    const status = selectedRow.cells[4].innerText.trim();

    document.getElementById('updateTargetId').value = '#' + id;
    document.getElementById('updateName').value = name;
    document.getElementById('updateFee').value = cleanFee ? parseFloat(cleanFee) : '';
    document.getElementById('updateDuration').value = duration;
    document.getElementById('updateStatus').value = status;

    document.getElementById('updateModal').style.display = 'flex';
}

function closeUpdateModal() {
    document.getElementById('updateModal').style.display = 'none';
}

function handleUpdateTreatment(event) {
    event.preventDefault();

    if (selectedRow) {
        const name = document.getElementById('updateName').value.trim();
        const feeInput = parseFloat(document.getElementById('updateFee').value);
        const formattedFee = feeInput.toLocaleString('en-US', { minimumFractionDigits: 2, maximumFractionDigits: 2 });
        const duration = document.getElementById('updateDuration').value;
        const status = document.getElementById('updateStatus').value;

        selectedRow.cells[1].innerText = name;
        selectedRow.cells[2].innerText = 'LKR ' + formattedFee;
        selectedRow.cells[3].innerText = duration;

        const badgeClass = status === 'Active' ? 'badge active' : 'badge inactive';
        selectedRow.cells[4].innerHTML = `<span class="${badgeClass}">${status}</span>`;

        closeUpdateModal();
        alert("✅ Treatment Details Updated Successfully!");
    }
}

function deleteTreatment(element) {
    const row = element.closest('tr');
    const trtName = row.cells[1].innerText;

    if (confirm("Are you sure you want to set " + trtName + " to Inactive?")) {
        row.cells[4].innerHTML = `<span class="badge inactive">Inactive</span>`;
        alert(trtName + " status changed to Inactive!");
    }
}