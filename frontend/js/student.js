document.addEventListener('DOMContentLoaded', async () => {
    document.getElementById('navLogout').addEventListener('click', (e) => {
        e.preventDefault();
        localStorage.clear();
        window.location.href = '/index.html';
    });

    const sessionId = localStorage.getItem('sessionId');
    if (!sessionId) {
        window.location.href = '/index.html';
        return;
    }

    let currentRollNo = null;

    async function loadMarks(rollNo) {
        currentRollNo = rollNo;
        const tbody = document.getElementById('marksBody');
        tbody.innerHTML = '<tr><td colspan="3">Loading marks...</td></tr>';
        try {
console.log("Loading marks for:", rollNo);

const res = await fetch(`/api/marks?rollNo=${encodeURIComponent(rollNo)}`, {
    headers: {
        'Authorization': `Bearer ${sessionId}`
    }
});

console.log("Marks API Status:", res.status);

const data = await res.json();

console.log("Marks API Response:", data);            
            
            if (data.success && data.data) {
                const marksArray = Array.isArray(data.data) ? data.data : (data.data.data || []);
                
                if (marksArray.length > 0) {
                    let totalMarks = marksArray.reduce((sum, m) => sum + m.marks, 0);
                    let avg = totalMarks / marksArray.length;
                    document.getElementById('sCgpa').textContent = (avg / 10).toFixed(2);
                } else {
                    document.getElementById('sCgpa').textContent = "0.0";
                }

                renderGroupedMarks(marksArray);
            } else {
                tbody.innerHTML = '<tr><td colspan="3">No marks available</td></tr>';
                document.getElementById('sCgpa').textContent = "0.0";
            }
        } catch (e) {
    console.error("loadMarks() crashed:", e);

    tbody.innerHTML =
        '<tr><td colspan="3">Failed to load marks</td></tr>';

    document.getElementById("sCgpa").textContent = "0.0";
}
    }

    function getGrade(marks) {
        if (marks >= 90) return 'O';
        if (marks >= 80) return 'A+';
        if (marks >= 70) return 'A';
        if (marks >= 60) return 'B+';
        if (marks >= 50) return 'B';
        if (marks >= 40) return 'C';
        return 'F';
    }

    function renderGroupedMarks(marksArray) {
        const tbody = document.getElementById('marksBody');
        tbody.innerHTML = '';
        
        if (!marksArray || marksArray.length === 0) {
            tbody.innerHTML = '<tr><td colspan="3">No marks available</td></tr>';
            return;
        }

        // Group by semester
        const grouped = {};
        marksArray.forEach(m => {
            if (!grouped[m.semester]) grouped[m.semester] = [];
            grouped[m.semester].push(m);
        });

        // Sort semesters and render
        Object.keys(grouped).sort((a,b) => parseInt(a) - parseInt(b)).forEach(sem => {
            // Header row for semester
            const headerRow = document.createElement('tr');
            headerRow.style.backgroundColor = 'var(--background)';
            headerRow.innerHTML = `<td colspan="3" style="font-weight:bold; color:var(--primary); text-align:center;">Semester ${sem}</td>`;
            tbody.appendChild(headerRow);
            
            let semTotal = 0;
            grouped[sem].forEach(m => {
                semTotal += m.marks;
                const subjectDisplay = m.subjectName.replace(/\s*\(Sem\s*\d+\)/i, '');
                const escapeHTML = str => str.replace(/[&<>'"]/g, tag => ({'&': '&amp;','<': '&lt;','>': '&gt;',"'": '&#39;','"': '&quot;'}[tag]));
                const tr = document.createElement('tr');
                tr.innerHTML = `<td>${escapeHTML(subjectDisplay)}</td><td><span class="badge ${m.marks >= 40 ? 'badge-success' : 'badge-danger'}">${getGrade(m.marks)}</span></td><td>${m.marks}</td>`;
                tbody.appendChild(tr);
            });
        });
        
        // Hide semester GPA header since we're grouping
        const semGpaHeader = document.getElementById('sSemGpa');
        if (semGpaHeader && semGpaHeader.parentElement) {
            semGpaHeader.parentElement.style.display = 'none';
        }
    }

    try {
       const res = await fetch('/api/student/profile', {
    headers: {
        'Authorization': `Bearer ${sessionId}`
    }
});

const data = await res.json();

console.log("Profile Response:", data);

if (data.success) {
            const st = data.data;
            document.getElementById('welcomeText').textContent = `Welcome ${st.name}`;
            document.getElementById('sRoll').textContent = st.rollNo;
            document.getElementById('sName').textContent = st.name;
            document.getElementById('sDept').textContent = st.department;
            document.getElementById('sYear').textContent = st.year;
            document.getElementById('sEmail').textContent = st.email || 'N/A';
            document.getElementById('sPhone').textContent = st.phone || 'N/A';
            document.getElementById('sAttendance').textContent = `${st.attendance}%`;
            
        console.log("Student Profile Loaded:", st);

        await loadMarks(st.rollNo);

        console.log("Finished loading marks");        } else {
            window.location.href = '/index.html';
        }
    } catch (err) {
        console.error(err);
    }

});
