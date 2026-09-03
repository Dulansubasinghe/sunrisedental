let selectedTreatmentId = null;

// Load treatments from database on page load
document.addEventListener("DOMContentLoaded", loadTreatments);

// Get all treatments from DB
function loadTreatments() {
    fetch('/sunrisedental_war_exploded/treatment')
        .then(response => response.json())
        .then(treatments => {
            const tableBody = document.getElementById('treatmentTableBody');
            if (!tableBody) return;
            tableBody.innerHTML = ''; // clear table

            treatments.forEach(treatment => {
                const badgeClass = treatment.status === 'Active' ? 'badge active' : 'badge inactive';
                const feeValue = parseFloat(treatment.standardFee || 0);
                const formattedFee = feeValue.toLocaleString('en-US', { minimumFractionDigits: 2, maximumFractionDigits: 2 });
                const trtCode = treatment.treatmentCode || treatment.id;
                const durationText = treatment.durationMins ? `${treatment.durationMins} Mins` : 'N/A';

                const newRow = document.createElement('tr');
                newRow.innerHTML = `
                    <td><strong>#${trtCode}</strong></td>
                    <td>${treatment.treatmentName}</td>
                    <td>LKR ${formattedFee}</td>
                    <td>${durationText}</td>
                    <td><span class="${badgeClass}">${treatment.status}</span></td>
                    <td>
                        <div class="action-dropdown">
                            <button class="btn-action">Options ▾</button>
                            <div class="dropdown-content">
                                <a href="javascript:void(0)" onclick="openUpdateModal(${treatment.id}, '${trtCode}', '${treatment.treatmentName}', ${feeValue}, '${durationText}', '${treatment.status}')">✏️ Update Details</a>
                                <a href="javascript:void(0)" class="danger-text" onclick="deleteTreatment(${treatment.id}, '${trtCode}', '${treatment.treatmentName}', ${feeValue}, '${durationText}')">🗑️ Delete (Deactivate)</a>
                            </div>
                        </div>
                    </td>
                `;
                tableBody.appendChild(newRow);
            });
        })
        .catch(err => console.error("Error loading treatments:", err));
}

function openAddModal() {
    document.getElementById('addTreatmentForm').reset();
    document.getElementById('addModal').style.display = 'flex';
}

function closeAddModal() {
    document.getElementById('addModal').style.display = 'none';
}

// Add new treatment to database
function handleAddTreatment(event) {
    event.preventDefault();

    const id = document.getElementById('addId').value.trim();
    const name = document.getElementById('addName').value.trim();
    const feeInput = parseFloat(document.getElementById('addFee').value);
    const duration = document.getElementById('addDuration').value;
    const status = document.getElementById('addStatus').value;

    const treatmentData = {
        treatmentCode: id,
        treatmentName: name,
        standardFee: feeInput,
        durationMins: parseInt(duration),
        status: status
    };

    fetch('/sunrisedental_war_exploded/treatment', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(treatmentData)
    })
        .then(async response => {
            const result = await response.json();
            if (response.ok) {
                alert("✅ Treatment #" + id + " Added Successfully!");
                closeAddModal();
                loadTreatments(); // Reload table with fresh data from MySQL DB
            } else {
                alert("❌ Error: " + (result.message || "Failed to save treatment"));
            }
        })
        .catch(err => {
            console.error("Error adding treatment:", err);
            alert("❌ Server connection error!");
        });
}

function openUpdateModal(id, code, name, fee, duration, status) {
    selectedTreatmentId = id;

    document.getElementById('updateTargetId').value = '#' + code;
    document.getElementById('updateName').value = name;
    document.getElementById('updateFee').value = fee;
    document.getElementById('updateDuration').value = duration;
    document.getElementById('updateStatus').value = status;

    document.getElementById('updateModal').style.display = 'flex';
}

function closeUpdateModal() {
    document.getElementById('updateModal').style.display = 'none';
}

// Update existing treatment in database
function handleUpdateTreatment(event) {
    event.preventDefault();

    if (!selectedTreatmentId) return;

    const code = document.getElementById('updateTargetId').value.replace('#', '').trim();
    const name = document.getElementById('updateName').value.trim();
    const feeInput = parseFloat(document.getElementById('updateFee').value);
    const duration = document.getElementById('updateDuration').value;
    const status = document.getElementById('updateStatus').value;

    const updateData = {
        id: selectedTreatmentId,
        treatmentCode: code,
        treatmentName: name,
        standardFee: feeInput,
        durationMins: parseInt(duration),
        status: status
    };

    fetch('/sunrisedental_war_exploded/treatment', {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(updateData)
    })
        .then(async response => {
            const result = await response.json();
            if (response.ok) {
                alert("✅ Treatment Details Updated Successfully!");
                closeUpdateModal();
                loadTreatments();
            } else {
                alert("❌ Error: " + (result.message || "Failed to update treatment"));
            }
        })
        .catch(err => {
            console.error("Error updating treatment:", err);
            alert("❌ Server connection error!");
        });
}

// Deactivate treatment in database
function deleteTreatment(id, code, trtName, fee, duration) {
    if (confirm("Are you sure you want to set " + trtName + " to Inactive?")) {
        const updateData = {
            id: id,
            treatmentCode: code,
            treatmentName: trtName,
            standardFee: fee,
            durationMins: parseInt(duration),
            status: "Inactive"
        };

        fetch('/sunrisedental_war_exploded/treatment', {
            method: 'PUT',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(updateData)
        })
            .then(async response => {
                if (response.ok) {
                    alert("✅ " + trtName + " status changed to Inactive!");
                    loadTreatments();
                } else {
                    alert("❌ Failed to update treatment status.");
                }
            })
            .catch(err => console.error("Error deactivating treatment:", err));
    }
}