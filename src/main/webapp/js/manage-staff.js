let selectedRow = null;

// Validation Patterns:
// Username: At least 1 Uppercase, 1 Lowercase, 1 Number
const usernameRegex = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)[a-zA-Z0-9_]{4,}$/;

// Password: At least 1 Uppercase, 1 Lowercase, 1 Number, 1 Special Char ($@#%!*?&)
const passwordRegex = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[$@#%!*?&])[A-Za-z\d$@#%!*?&]{6,}$/;

function openAddModal() {
    document.getElementById('addUserForm').reset();
    document.getElementById('addModal').style.display = 'flex';
}

function closeAddModal() {
    document.getElementById('addModal').style.display = 'none';
}

function handleAddUser(event) {
    event.preventDefault();

    const id = document.getElementById('addUserId').value.trim();
    const name = document.getElementById('addName').value.trim();
    const role = document.getElementById('addRole').value;
    const username = document.getElementById('addUsername').value.trim();
    const password = document.getElementById('addPassword').value.trim();
    const status = document.getElementById('addStatus').value;

    // Username Validation Check
    if (!usernameRegex.test(username)) {
        alert("❌ Invalid Username!\nUsername must contain:\n- At least one Uppercase letter (A-Z)\n- At least one Lowercase letter (a-z)\n- At least one Number (0-9)");
        return;
    }

    // Password Validation Check
    if (!passwordRegex.test(password)) {
        alert("❌ Invalid Password!\nPassword must contain:\n- At least one Uppercase letter (A-Z)\n- At least one Lowercase letter (a-z)\n- At least one Number (0-9)\n- At least one Special character (e.g. $, @, #, %)");
        return;
    }

    const tableBody = document.getElementById('staffTableBody');
    const newRow = tableBody.insertRow();
    const badgeClass = status === 'Active' ? 'badge active' : 'badge inactive';

    newRow.innerHTML = `
        <td><strong>#${id}</strong></td>
        <td>${name}</td>
        <td><span class="role-badge">${role}</span></td>
        <td>${username}</td>
        <td><code>${password}</code></td>
        <td><span class="${badgeClass}">${status}</span></td>
        <td>
            <div class="action-dropdown">
                <button class="btn-action">Options ▾</button>
                <div class="dropdown-content">
                    <a href="#" onclick="openUpdateModal(this)">✏️ Update Credentials</a>
                    <a href="#" class="danger-text" onclick="deleteUser(this)">🗑️ Delete (Deactivate)</a>
                </div>
            </div>
        </td>
    `;

    closeAddModal();
    alert("✅ Staff User #" + id + " Created Successfully!");
}

function openUpdateModal(element) {
    selectedRow = element.closest('tr');

    const id = selectedRow.cells[0].innerText;
    const name = selectedRow.cells[1].innerText;
    const username = selectedRow.cells[3].innerText;
    const password = selectedRow.cells[4].innerText;
    const status = selectedRow.cells[5].innerText.trim();

    document.getElementById('updateTarget').value = name + ' (' + id + ')';
    document.getElementById('updateUsername').value = username;
    document.getElementById('updatePassword').value = password;
    document.getElementById('updateStatus').value = status;

    document.getElementById('updateModal').style.display = 'flex';
}

function closeUpdateModal() {
    document.getElementById('updateModal').style.display = 'none';
}

function handleUpdateUser(event) {
    event.preventDefault();

    if (selectedRow) {
        const username = document.getElementById('updateUsername').value.trim();
        const password = document.getElementById('updatePassword').value.trim();
        const status = document.getElementById('updateStatus').value;

        // Username Validation Check
        if (!usernameRegex.test(username)) {
            alert("❌ Invalid Username!\nUsername must contain:\n- At least one Uppercase letter (A-Z)\n- At least one Lowercase letter (a-z)\n- At least one Number (0-9)");
            return;
        }

        // Password Validation Check
        if (!passwordRegex.test(password)) {
            alert("❌ Invalid Password!\nPassword must contain:\n- At least one Uppercase letter (A-Z)\n- At least one Lowercase letter (a-z)\n- At least one Number (0-9)\n- At least one Special character (e.g. $, @, #, %)");
            return;
        }

        selectedRow.cells[3].innerText = username;
        selectedRow.cells[4].innerHTML = `<code>${password}</code>`;

        const badgeClass = status === 'Active' ? 'badge active' : 'badge inactive';
        selectedRow.cells[5].innerHTML = `<span class="${badgeClass}">${status}</span>`;

        closeUpdateModal();
        alert("✅ User Credentials Updated Successfully!");
    }
}

function deleteUser(element) {
    const row = element.closest('tr');
    const userName = row.cells[1].innerText;

    if (confirm("Are you sure you want to deactivate account for " + userName + "?")) {
        row.cells[5].innerHTML = `<span class="badge inactive">Inactive</span>`;
        alert(userName + "'s account is now Inactive!");
    }
}