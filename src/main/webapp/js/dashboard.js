function logout() {
    window.location.href = '../index.html';
}

// Modal Display Functions
function openHelpModal() {
    document.getElementById('helpModal').style.display = 'flex';
}

function closeHelpModal() {
    document.getElementById('helpModal').style.display = 'none';
}

window.onclick = function(event) {
    const modal = document.getElementById('helpModal');
    if (event.target === modal) {
        modal.style.display = 'none';
    }
}

// Accordion Toggle
function toggleFaq(button) {
    const faqItem = button.parentElement;
    document.querySelectorAll('.faq-item').forEach(item => {
        if (item !== faqItem) item.classList.remove('active');
    });
    faqItem.classList.toggle('active');
}

// Form Submission
function submitTicket(event) {
    event.preventDefault();
    alert('Your support ticket has been submitted to the IT team!');
    document.getElementById('supportForm').reset();
    closeHelpModal();
}