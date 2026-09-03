let selectedDentistId = null;

// Load dentists from database on page load
document.addEventListener("DOMContentLoaded", loadDentists);

// 1. Get All Dentists from Database
function loadDentists() {
    fetch('/sunrisedental_war_exploded/dentist')
        .then(response => response.json())
        .then(dentists => {
            const tableBody = document.getElementById('dentistTableBody');
            if (!tableBody) return;
            tableBody.innerHTML = '';

            dentists.forEach(dentist => {
                const badgeClass = dentist.status === 'Active' ? 'badge active' : 'badge inactive';
                const formattedFee = (dentist.consultationFee || 0).toLocaleString('en-US', { minimumFractionDigits: 2, maximumFractionDigits: 2 });
                const codeDisplay = dentist.dentistCode ? dentist.dentistCode : `D-${dentist.dentistId}`;

                const newRow = document.createElement('tr');
                newRow.innerHTML = `
                    <td><strong>#${codeDisplay}</strong></td>
                    <td>${dentist.name}</td>
                    <td>${dentist.specialization}</td>
                    <td>LKR ${formattedFee}</td>
                    <td>${dentist.contactNumber}</td>
                    <td><span class="${badgeClass}">${dentist.status}</span></td>
                    <td>
                        <div class="action-dropdown">
                            <button class="btn-action">Options ▾</button>
                            <div class="dropdown-content">
                                <a href="javascript:void(0)" onclick="openUpdateModal(${dentist.dentistId}, '${codeDisplay}', '${dentist.name}', ${dentist.consultationFee}, '${dentist.contactNumber}', '${dentist.status}')">✏️ Update Details</a>
                                <a href="javascript:void(0)" class="danger-text" onclick="deleteDentist(${dentist.dentistId}, '${dentist.name}')">🗑️ Delete (Deactivate)</a>
                            </div>
                        </div>
                    </td>
                `;
                tableBody.appendChild(newRow);
            });
        })
        .catch(err => console.error("Error loading dentists:", err));
}

function openAddModal() {
    document.getElementById('addDentistForm').reset();
    document.getElementById('addModal').style.display = 'flex';
}

function closeAddModal() {
    document.getElementById('addModal').style.display = 'none';
}

// 2. Register New Dentist
function handleAddDentist(event) {
    event.preventDefault();

    const id = document.getElementById('addId').value.trim();
    const name = document.getElementById('addName').value.trim();
    const spec = document.getElementById('addSpec').value;
    const feeInput = parseFloat(document.getElementById('addFee').value);
    const contact = document.getElementById('addContact').value.trim();
    const status = document.getElementById('addStatus').value;

    const dentistData = {
        dentistCode: id,
        name: name,
        specialization: spec,
        consultationFee: feeInput,
        contactNumber: contact,
        status: status
    };

    fetch('/sunrisedental_war_exploded/dentist', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(dentistData)
    })
        .then(async response => {
            const result = await response.json();
            if (response.ok) {
                alert("✅ New Dentist #" + id + " Added Successfully!");
                closeAddModal();
                loadDentists();
            } else {
                alert("❌ Error: " + result.message);
            }
        })
        .catch(err => {
            console.error("Error adding dentist:", err);
            alert("❌ Server connection error!");
        });
}

function openUpdateModal(dentistId, codeDisplay, name, fee, contact, status) {
    selectedDentistId = dentistId;

    document.getElementById('updateDentistName').value = `${name} (#${codeDisplay})`;
    document.getElementById('updateFee').value = fee;
    document.getElementById('updateContact').value = contact;
    document.getElementById('updateStatus').value = status;

    document.getElementById('updateModal').style.display = 'flex';
}

function closeUpdateModal() {
    document.getElementById('updateModal').style.display = 'none';
}

// 3. Update Dentist Details in Database
function handleUpdateDentist(event) {
    event.preventDefault();

    if (!selectedDentistId) return;

    const feeInput = parseFloat(document.getElementById('updateFee').value);
    const contact = document.getElementById('updateContact').value.trim();
    const status = document.getElementById('updateStatus').value;

    const updateData = {
        dentistId: selectedDentistId,
        consultationFee: feeInput,
        contactNumber: contact,
        status: status
    };

    fetch('/sunrisedental_war_exploded/dentist', {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(updateData)
    })
        .then(async response => {
            const result = await response.json();
            if (response.ok) {
                alert("✅ Dentist Details Updated Successfully!");
                closeUpdateModal();
                loadDentists();
            } else {
                alert("❌ Error: " + result.message);
            }
        })
        .catch(err => {
            console.error("Error updating dentist:", err);
            alert("❌ Server connection error!");
        });
}

// 4. Deactivate Dentist in Database
function deleteDentist(dentistId, dentistName) {
    if (confirm("Are you sure you want to set " + dentistName + " to Inactive?")) {
        const updateData = {
            dentistId: dentistId,
            status: "Inactive"
        };

        fetch('/sunrisedental_war_exploded/dentist', {
            method: 'PUT',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(updateData)
        })
            .then(async response => {
                if (response.ok) {
                    alert("✅ " + dentistName + " status changed to Inactive!");
                    loadDentists();
                } else {
                    alert("❌ Failed to update status.");
                }
            })
            .catch(err => console.error("Error deactivating dentist:", err));
    }
}