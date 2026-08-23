let myChart = null;

document.addEventListener("DOMContentLoaded", function() {
    handlePeriodChange();
});

function handlePeriodChange() {
    const period = document.getElementById('periodFilter').value;
    const fromWrapper = document.getElementById('fromDateWrapper');
    const toWrapper = document.getElementById('toDateWrapper');

    if (period === 'CUSTOM') {
        fromWrapper.style.display = 'flex';
        toWrapper.style.display = 'flex';
    } else {
        fromWrapper.style.display = 'none';
        toWrapper.style.display = 'none';
        applyFilters();
    }
}

function getDateRange(period) {
    const today = new Date('2026-08-20');
    let start = new Date(today);
    let end = new Date(today);

    if (period === 'TODAY') {
        // retain today
    } else if (period === 'THIS_WEEK') {
        const day = today.getDay();
        const diffToMon = today.getDate() - day + (day === 0 ? -6 : 1);
        start = new Date(today.setDate(diffToMon));
        end = new Date(start);
        end.setDate(start.getDate() + 6);
    } else if (period === 'LAST_WEEK') {
        const day = today.getDay();
        const diffToLastMon = today.getDate() - day - 6 + (day === 0 ? -6 : 1);
        start = new Date(today.setDate(diffToLastMon));
        end = new Date(start);
        end.setDate(start.getDate() + 6);
    } else if (period === 'THIS_MONTH') {
        start = new Date(today.getFullYear(), today.getMonth(), 1);
        end = new Date(today.getFullYear(), today.getMonth() + 1, 0);
    } else if (period === 'CUSTOM') {
        const f = document.getElementById('fromDate').value;
        const t = document.getElementById('toDate').value;
        if (f) start = new Date(f);
        if (t) end = new Date(t);
    }

    const formatDate = d => d.toISOString().split('T')[0];
    return { start: formatDate(start), end: formatDate(end) };
}

function applyFilters() {
    const period = document.getElementById('periodFilter').value;
    const selectedDoctor = document.getElementById('doctorFilter').value;
    const selectedTreatment = document.getElementById('treatmentFilter') ? document.getElementById('treatmentFilter').value : 'ALL';
    const range = getDateRange(period);
    const rows = document.querySelectorAll('#reportTableBody tr');

    let totalRevenue = 0;
    let totalPatients = 0;
    let totalAppointments = 0;

    const doctorStats = {
        'Dr. Perera': { spec: 'Orthodontist', patients: 0, revenue: 0 },
        'Dr. Silva': { spec: 'Dental Surgeon', patients: 0, revenue: 0 },
        'Dr. Wickramasinghe': { spec: 'General Dentist', patients: 0, revenue: 0 }
    };

    const treatmentStats = {};

    rows.forEach(row => {
        const rDate = row.getAttribute('data-date');
        const rDoctor = row.getAttribute('data-doctor');
        const rTreatment = row.getAttribute('data-treatment');
        const rFee = parseInt(row.getAttribute('data-fee')) || 0;

        let matchesDate = (rDate >= range.start && rDate <= range.end);
        let matchesDoctor = (selectedDoctor === 'ALL' || rDoctor === selectedDoctor);
        let matchesTreatment = (selectedTreatment === 'ALL' || rTreatment === selectedTreatment);

        if (matchesDate && matchesDoctor && matchesTreatment) {
            row.style.display = '';
            totalRevenue += rFee;
            totalPatients++;
            totalAppointments++;

            if (doctorStats[rDoctor]) {
                doctorStats[rDoctor].patients++;
                doctorStats[rDoctor].revenue += rFee;
            }

            if (!treatmentStats[rTreatment]) {
                treatmentStats[rTreatment] = { count: 0, revenue: 0 };
            }
            treatmentStats[rTreatment].count++;
            treatmentStats[rTreatment].revenue += rFee;
        } else {
            row.style.display = 'none';
        }
    });

    // Update KPI Cards
    document.getElementById('kpiTotalRevenue').innerText = `LKR ${totalRevenue.toLocaleString()}`;
    document.getElementById('kpiTotalPatients').innerText = `${totalPatients} Patients`;

    if (document.getElementById('kpiTotalAppointments')) {
        document.getElementById('kpiTotalAppointments').innerText = `${totalAppointments} Bookings`;
    }

    // Dentist Summary Table Update
    const dBody = document.getElementById('dentistSummaryBody');
    dBody.innerHTML = '';

    for (const [docName, data] of Object.entries(doctorStats)) {
        dBody.innerHTML += `
            <tr>
                <td><strong>${docName}</strong></td>
                <td>${data.spec}</td>
                <td>${data.patients}</td>
                <td class="amount-text">LKR ${data.revenue.toLocaleString()}</td>
            </tr>
        `;
    }

    // Treatment Summary Table Update
    const tBody = document.getElementById('treatmentSummaryBody');
    tBody.innerHTML = '';
    const chartLabels = [];
    const chartData = [];
    let topTreatmentName = '—';
    let maxTreatmentRev = -1;

    for (const [tName, data] of Object.entries(treatmentStats)) {
        if (data.revenue > maxTreatmentRev) {
            maxTreatmentRev = data.revenue;
            topTreatmentName = tName;
        }
        chartLabels.push(tName);
        chartData.push(data.revenue);

        tBody.innerHTML += `
            <tr>
                <td>${tName}</td>
                <td>${data.count}</td>
                <td class="amount-text">LKR ${data.revenue.toLocaleString()}</td>
            </tr>
        `;
    }

    document.getElementById('kpiTopTreatment').innerText = topTreatmentName;

    renderChart(chartLabels, chartData);
}

function renderChart(labels, data) {
    if (typeof Chart === 'undefined') return;

    const ctx = document.getElementById('treatmentChart').getContext('2d');
    if (myChart) {
        myChart.destroy();
    }

    myChart = new Chart(ctx, {
        type: 'bar',
        data: {
            labels: labels.length ? labels : ['No Data'],
            datasets: [{
                label: 'Income (LKR)',
                data: data.length ? data : [0],
                backgroundColor: ['#0f9f87', '#3b82f6', '#f59e0b', '#ec4899', '#8b5cf6'],
                borderRadius: 6
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            plugins: { legend: { display: false } }
        }
    });
}

function exportToCSV() {
    let csv = [];
    const rows = document.querySelectorAll("#appointmentsReportTable tr");
    for (let i = 0; i < rows.length; i++) {
        if (rows[i].style.display !== 'none') {
            let row = [], cols = rows[i].querySelectorAll("td, th");
            for (let j = 0; j < cols.length; j++) {
                row.push('"' + cols[j].innerText.trim() + '"');
            }
            csv.push(row.join(","));
        }
    }
    const csvFile = new Blob([csv.join("\n")], { type: "text/csv" });
    const downloadLink = document.createElement("a");
    downloadLink.download = "Management_Report.csv";
    downloadLink.href = window.URL.createObjectURL(csvFile);
    downloadLink.style.display = "none";
    document.body.appendChild(downloadLink);
    downloadLink.click();
}