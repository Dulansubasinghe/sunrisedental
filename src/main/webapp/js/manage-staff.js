let selectedUserId = null;

// Validation Patterns:
const usernameRegex = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)[a-zA-Z0-9_]{3,}$/;
const passwordRegex = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[$@#%!*?&])[A-Za-z\d$@#%!*?&]{6,}$/;

// Load users from database on page load
document.addEventListener("DOMContentLoaded", loadUsers);

// 1. Get All Users from Database
function loadUsers() {
    fetch('/sunrisedental_war_exploded/user')
        .then(response => response.json())
        .then(users => {
            const tableBody = document.getElementById('staffTableBody');
            if (!tableBody) return;
            tableBody.innerHTML = ''; // Table Clear

            users.forEach(user => {
                const badgeClass = user.status === 'Active' ? 'badge active' : 'badge inactive';
                const userCodeDisplay = user.userCode ? user.userCode : `S-${user.userId}`;

                const newRow = document.createElement('tr');
                newRow.innerHTML = `
                    <td><strong>#${userCodeDisplay}</strong></td>
                    <td>${user.fullName}</td>
                    <td><span class="role-badge">${user.role}</span></td>
                    <td>${user.username}</td>
                    <td><code>${user.password}</code></td>
                    <td><span class="${badgeClass}">${user.status}</span></td>
                    <td>
                        <div class="action-dropdown">
                            <button class="btn-action">Options ▾</button>
                            <div class="dropdown-content">
                                <a href="javascript:void(0)" onclick="openUpdateModal(${user.userId}, '${user.fullName}', '${userCodeDisplay}', '${user.username}', '${user.password}', '${user.status}')">✏️ Update Credentials</a>
                                <a href="javascript:void(0)" class="danger-text" onclick="deleteUser(${user.userId}, '${user.fullName}')">🗑️ Delete (Deactivate)</a>
                            </div>
                        </div>
                    </td>
                `;
                tableBody.appendChild(newRow);
            });
        })
        .catch(err => console.error("Error loading users:", err));
}

function openAddModal() {
    document.getElementById('addUserForm').reset();
    document.getElementById('addModal').style.display = 'flex';
}

function closeAddModal() {
    document.getElementById('addModal').style.display = 'none';
}

// 2. Add New User to Database
function handleAddUser(event) {
    event.preventDefault();

    const id = document.getElementById('addUserId').value.trim();
    const name = document.getElementById('addName').value.trim();
    const role = document.getElementById('addRole').value;
    const username = document.getElementById('addUsername').value.trim();
    const password = document.getElementById('addPassword').value.trim();
    const status = document.getElementById('addStatus').value;

    if (!usernameRegex.test(username)) {
        alert("❌ Invalid Username!\nMust contain Uppercase (A-Z), Lowercase (a-z), and Number (0-9).");
        return;
    }

    if (!passwordRegex.test(password)) {
        alert("❌ Invalid Password!\nMust contain Uppercase, Lowercase, Number, and Special character (e.g. $, @, #).");
        return;
    }

    const userData = {
        userCode: id,
        fullName: name,
        role: role,
        username: username,
        password: password,
        status: status
    };

    fetch('/sunrisedental_war_exploded/user', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(userData)
    })
        .then(async response => {
            const result = await response.json();
            if (response.ok) {
                alert("✅ Staff User #" + id + " Created Successfully!");
                closeAddModal();
                loadUsers();
            } else {
                alert("❌ Error: " + result.message);
            }
        })
        .catch(err => {
            console.error("Error adding user:", err);
            alert("❌ Server connection error!");
        });
}

function openUpdateModal(userId, name, userCode, username, password, status) {
    selectedUserId = userId;

    document.getElementById('updateTarget').value = `${name} (#${userCode})`;
    document.getElementById('updateUsername').value = username;
    document.getElementById('updatePassword').value = password;
    document.getElementById('updateStatus').value = status;

    document.getElementById('updateModal').style.display = 'flex';
}

function closeUpdateModal() {
    document.getElementById('updateModal').style.display = 'none';
}

// 3. Update User in Database
function handleUpdateUser(event) {
    event.preventDefault();

    if (!selectedUserId) return;

    const username = document.getElementById('updateUsername').value.trim();
    const password = document.getElementById('updatePassword').value.trim();
    const status = document.getElementById('updateStatus').value;

    if (!usernameRegex.test(username)) {
        alert("❌ Invalid Username!\nMust contain Uppercase (A-Z), Lowercase (a-z), and Number (0-9).");
        return;
    }

    if (!passwordRegex.test(password)) {
        alert("❌ Invalid Password!\nMust contain Uppercase, Lowercase, Number, and Special character (e.g. $, @, #).");
        return;
    }

    const updateData = {
        userId: selectedUserId,
        username: username,
        password: password,
        status: status
    };

    fetch('/sunrisedental_war_exploded/user', {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(updateData)
    })
        .then(async response => {
            const result = await response.json();
            if (response.ok) {
                alert("✅ User Credentials Updated Successfully!");
                closeUpdateModal();
                loadUsers();
            } else {
                alert("❌ Error: " + result.message);
            }
        })
        .catch(err => {
            console.error("Error updating user:", err);
            alert("❌ Server connection error!");
        });
}

// 4. Deactivate Account in Database
function deleteUser(userId, userName) {
    if (confirm("Are you sure you want to deactivate account for " + userName + "?")) {
        const updateData = {
            userId: userId,
            status: "Inactive"
        };

        fetch('/sunrisedental_war_exploded/user', {
            method: 'PUT',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(updateData)
        })
            .then(async response => {
                if (response.ok) {
                    alert("✅ " + userName + "'s account is now Inactive!");
                    loadUsers();
                } else {
                    alert("❌ Failed to deactivate user.");
                }
            })
            .catch(err => console.error("Error deactivating user:", err));
    }
}