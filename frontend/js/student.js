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

    try {
        const res = await fetch('/api/student/profile', {
            headers: { 'Authorization': `Bearer ${sessionId}` }
        });
        const data = await res.json();
        
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
            
            loadMarks(st.rollNo);
        } else {
            window.location.href = '/index.html';
        }
    } catch (err) {
        console.error(err);
    }

    async function loadMarks(rollNo) {
        try {
            const res = await fetch(`/api/marks?rollNo=${encodeURIComponent(rollNo)}`);
            const data = await res.json();
            if (data.success && data.data) {
                const tbody = document.getElementById('marksBody');
                tbody.innerHTML = '';
                let totalMarks = 0;
                
                // The backend API wraps the array in an extra 'data' object
                let marksArray = Array.isArray(data.data) ? data.data : data.data.data;
                if (!marksArray) marksArray = [];
                
                let count = marksArray.length;
                
                if (count === 0) {
                    tbody.innerHTML = '<tr><td colspan="2">No marks available</td></tr>';
                    return;
                }

                marksArray.forEach(m => {
                    totalMarks += m.marks;
                    const tr = document.createElement('tr');
                    tr.innerHTML = `<td>${m.subjectName}</td><td>${m.marks}</td>`;
                    tbody.appendChild(tr);
                });
                
                let avg = totalMarks / count;
                let cgpa = (avg / 10).toFixed(1);
                document.getElementById('sCgpa').textContent = cgpa;
            }
        } catch (e) {
            console.error(e);
        }
    }
});
