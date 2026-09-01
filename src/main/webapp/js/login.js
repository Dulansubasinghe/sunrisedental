document.addEventListener("DOMContentLoaded", function () {
    const loginForm = document.getElementById("loginForm");

    if (loginForm) {
        loginForm.addEventListener("submit", function (e) {
            e.preventDefault();
            const usernameInput = document.getElementById("username").value.trim();
            const passwordInput = document.getElementById("password").value.trim();

            // Send auth request to backend login servlet
            fetch('api/login', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({
                    username: usernameInput,
                    password: passwordInput
                })
            })
                .then(response => {
                    return response.json().then(data => {
                        return { ok: response.ok, data: data };
                    });
                })
                .then(result => {
                    const data = result.data;

                    if (result.ok && data.status === 'success') {
                        // Redirect user based on database role
                        const userRole = data.role ? data.role.toUpperCase() : '';

                        if (userRole === 'ADMIN') {
                            window.location.href = 'admin/dashboard.html';
                        } else if (userRole === 'RECEPTIONIST') {
                            window.location.href = 'receptionist/dashboard.html';
                        } else {
                            alert("User role is invalid!");
                        }
                    } else {
                        // Display error for invalid login or disabled account
                        alert(data.message || "Invalid Username or Password!");
                    }
                })
                .catch(error => {
                    console.error('Login Error:', error);
                    alert("Server Connection Error! Please check Tomcat / Database.");
                });
        });
    }
});