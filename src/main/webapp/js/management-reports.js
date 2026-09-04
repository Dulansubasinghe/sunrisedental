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
    const today = new Date();
    let start = new Date(today);
    let end = new Date(today);

    if (period === 'TODAY') {
        // retain today
    } else if (period === 'THIS_WEEK') {
        const day = today.getDay();
        const diffToMon = today.getDate() - day + (day === 0 ? -6 : 1);
        start = new Date(today.getFullYear(), today.getMonth(), diffToMon);
        end = new Date(start);
        end.setDate(start.getDate() + 6);
    } else if (period === 'LAST_WEEK') {
        const day = today.getDay();
        const diffToLastMon = today.getDate() - day - 6 + (day === 0 ? -6 : 1);
        start = new Date(today.getFullYear(), today.getMonth(), diffToLastMon);
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

    const formatDate = d => {
        const year = d.getFullYear();
        const month = String(d.getMonth() + 1).padStart(2, '0');
        const day = String(d.getDate()).padStart(2, '0');
        return `${year}-${month}-${day}`;
    };

    return { start: formatDate(start), end: formatDate(end) };
}

function applyFilters() {
    const period = document.getElementById('periodFilter').value;
    const selectedDoctor = document.getElementById('doctorFilter').value;
    const selectedTreatment = document.getElementById('treatmentFilter') ? document.getElementById('treatmentFilter').value : 'ALL';
    const range = getDateRange(period);

    // Corrected Servlet URL
    const url = `../ReportServlet?startDate=${range.start}&endDate=${range.end}&doctor=${encodeURIComponent(selectedDoctor)}&treatment=${encodeURIComponent(selectedTreatment)}`;

    fetch(url)
        .then(response => {
            if (!response.ok) throw new Error("Servlet API not reachable, processing dynamic DOM rows");
            return response.json();
        })
        .then(data => {
            // Populate Doctors & Treatments Dropdowns
            if (data.dentists || data.treatments) {
                populateDropdowns(data.dentists, data.treatments);
            }

            let appointments = Array.isArray(data) ? data : (data.appointments || []);
            renderReportData(appointments, selectedDoctor, selectedTreatment, range);
        })
        .catch(err => {
            console.warn("Using DOM fallback mode:", err.message);
            processDOMRows(selectedDoctor, selectedTreatment, range);
        });
}

function populateDropdowns(dentists, treatments) {
    const docSelect = document.getElementById('doctorFilter');
    const treatSelect = document.getElementById('treatmentFilter');

    if (dentists && docSelect && docSelect.options.length <= 1) {
        dentists.forEach(d => {
            const opt = document.createElement('option');
            opt.value = d;
            opt.textContent = d;
            docSelect.appendChild(opt);
        });
    }

    if (treatments && treatSelect && treatSelect.options.length <= 1) {
        treatments.forEach(t => {
            const opt = document.createElement('option');
            opt.value = t;
            opt.textContent = t;
            treatSelect.appendChild(opt);
        });
    }
}

function renderReportData(appointments, selectedDoctor, selectedTreatment, range) {
    const tbody = document.getElementById('reportTableBody');
    tbody.innerHTML = '';

    let totalRevenue = 0;
    let totalPatients = 0;
    let totalAppointments = 0;

    const doctorStats = {};
    const treatmentStats = {};

    appointments.forEach(appt => {
        const rDate = appt.appointmentDate || appt.date;
        const rDoctor = appt.dentistName || appt.doctorName || 'Unassigned';
        const rSpec = appt.specialization || 'Dental Specialist';
        const rTreatment = appt.treatmentName || appt.treatment || 'General';
        const rStatus = appt.status || 'Completed';
        const rFee = parseFloat(appt.billingFee || appt.fee || 0);

        const isCompleted = rStatus.toLowerCase() === 'completed';

        let matchesDate = (!range.start || !range.end) || (rDate >= range.start && rDate <= range.end);
        let matchesDoctor = (selectedDoctor === 'ALL' || rDoctor === selectedDoctor);
        let matchesTreatment = (selectedTreatment === 'ALL' || rTreatment === selectedTreatment);

        if (isCompleted && matchesDate && matchesDoctor && matchesTreatment) {
            totalRevenue += rFee;
            totalPatients++;
            totalAppointments++;

            if (!doctorStats[rDoctor]) {
                doctorStats[rDoctor] = { spec: rSpec, patients: 0, revenue: 0 };
            }
            doctorStats[rDoctor].patients++;
            doctorStats[rDoctor].revenue += rFee;

            if (!treatmentStats[rTreatment]) {
                treatmentStats[rTreatment] = { count: 0, revenue: 0 };
            }
            treatmentStats[rTreatment].count++;
            treatmentStats[rTreatment].revenue += rFee;

            const tr = document.createElement('tr');
            tr.setAttribute('data-date', rDate);
            tr.setAttribute('data-doctor', rDoctor);
            tr.setAttribute('data-treatment', rTreatment);
            tr.setAttribute('data-fee', rFee);

            tr.innerHTML = `
                <td><strong>${appt.appointmentCode || appt.apptId || 'APT'}</strong></td>
                <td>${rDate}</td>
                <td>${appt.patientName || 'N/A'}</td>
                <td>${rDoctor}</td>
                <td>${rTreatment}</td>
                <td><span style="color: #059669; font-weight: 600;">Completed</span></td>
                <td class="amount-text">LKR ${rFee.toLocaleString()}</td>
            `;
            tbody.appendChild(tr);
        }
    });

    if (totalAppointments === 0) {
        tbody.innerHTML = `<tr><td colspan="7" style="text-align:center; color:#6b7280; padding:16px;">No completed appointments found for selected filter</td></tr>`;
    }

    updateKPIsAndSummaries(totalRevenue, totalPatients, totalAppointments, doctorStats, treatmentStats);
}

function processDOMRows(selectedDoctor, selectedTreatment, range) {
    const rows = document.querySelectorAll('#reportTableBody tr');

    let totalRevenue = 0;
    let totalPatients = 0;
    let totalAppointments = 0;

    const doctorStats = {};
    const treatmentStats = {};

    rows.forEach(row => {
        const rDate = row.getAttribute('data-date');
        const rDoctor = row.getAttribute('data-doctor');
        const rTreatment = row.getAttribute('data-treatment');
        const rFee = parseInt(row.getAttribute('data-fee')) || 0;
        const rStatusText = row.children[5] ? row.children[5].innerText.trim() : 'Completed';

        const isCompleted = rStatusText.toLowerCase() === 'completed';

        let matchesDate = (!range.start || !range.end) || (rDate >= range.start && rDate <= range.end);
        let matchesDoctor = (selectedDoctor === 'ALL' || rDoctor === selectedDoctor);
        let matchesTreatment = (selectedTreatment === 'ALL' || rTreatment === selectedTreatment);

        if (isCompleted && matchesDate && matchesDoctor && matchesTreatment) {
            row.style.display = '';
            totalRevenue += rFee;
            totalPatients++;
            totalAppointments++;

            if (rDoctor) {
                if (!doctorStats[rDoctor]) {
                    doctorStats[rDoctor] = { spec: 'Dental Specialist', patients: 0, revenue: 0 };
                }
                doctorStats[rDoctor].patients++;
                doctorStats[rDoctor].revenue += rFee;
            }

            if (rTreatment) {
                if (!treatmentStats[rTreatment]) {
                    treatmentStats[rTreatment] = { count: 0, revenue: 0 };
                }
                treatmentStats[rTreatment].count++;
                treatmentStats[rTreatment].revenue += rFee;
            }
        } else {
            row.style.display = 'none';
        }
    });

    updateKPIsAndSummaries(totalRevenue, totalPatients, totalAppointments, doctorStats, treatmentStats);
}

function updateKPIsAndSummaries(totalRevenue, totalPatients, totalAppointments, doctorStats, treatmentStats) {
    document.getElementById('kpiTotalRevenue').innerText = `LKR ${totalRevenue.toLocaleString()}`;
    document.getElementById('kpiTotalPatients').innerText = `${totalPatients} Patients`;

    if (document.getElementById('kpiTotalAppointments')) {
        document.getElementById('kpiTotalAppointments').innerText = `${totalAppointments} Bookings`;
    }

    const dBody = document.getElementById('dentistSummaryBody');
    dBody.innerHTML = '';
    if (Object.keys(doctorStats).length === 0) {
        dBody.innerHTML = `<tr><td colspan="4" style="text-align:center; color:#6b7280; padding:12px;">No doctor data available</td></tr>`;
    } else {
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
    }

    const tBody = document.getElementById('treatmentSummaryBody');
    tBody.innerHTML = '';
    const chartLabels = [];
    const chartData = [];
    let topTreatmentName = '—';
    let maxTreatmentRev = -1;

    if (Object.keys(treatmentStats).length === 0) {
        tBody.innerHTML = `<tr><td colspan="3" style="text-align:center; color:#6b7280; padding:12px;">No treatment data available</td></tr>`;
    } else {
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