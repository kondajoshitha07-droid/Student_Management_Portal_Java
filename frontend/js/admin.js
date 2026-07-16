document.addEventListener('DOMContentLoaded', () => {
    const navDashboard = document.getElementById('navDashboard');
    const navStudents = document.getElementById('navStudents');
    const navMarks = document.getElementById('navMarks');
    const navLogout = document.getElementById('navLogout');
    
    const sectionDashboard = document.getElementById('sectionDashboard');
    const sectionStudents = document.getElementById('sectionStudents');
    const sectionMarks = document.getElementById('sectionMarks');

    const role = localStorage.getItem('role');
    if (role === 'faculty') {
        const addBtn = document.getElementById('addStudentBtn');
        if (addBtn) addBtn.style.display = 'none';
    } else {
        const addBtn = document.getElementById('addStudentBtn');
        if (addBtn) addBtn.style.display = 'flex';
    }
    
    // Update Welcome Banner
    const welcomeHeader = document.querySelector('.welcome-banner h1');
    if (welcomeHeader) {
        welcomeHeader.textContent = role === 'principal' ? 'Welcome, Principal 👋' : 'Welcome, Faculty 👋';
    }

    // Quick Actions
    const btnUploadSyllabus = document.getElementById('btnUploadSyllabus');
    const btnSendAnnouncement = document.getElementById('btnSendAnnouncement');
    const btnViewDefaulters = document.getElementById('btnViewDefaulters');

    if (btnUploadSyllabus) {
        btnUploadSyllabus.addEventListener('click', () => {
            alert("Demo Feature\nSyllabus uploaded successfully.");
        });
    }

    if (btnSendAnnouncement) {
        btnSendAnnouncement.addEventListener('click', () => {
            const title = prompt("Enter announcement title:");
            if (title) {
                const msg = prompt("Enter announcement message:");
                if (msg) {
                    alert("Announcement sent successfully.");
                }
            }
        });
    }

    if (btnViewDefaulters) {
        btnViewDefaulters.addEventListener('click', async () => {
            try {
                const res = await fetch('/students');
                const data = await res.json();
                if (data.success) {
                    const defaulters = data.data.filter(s => s.attendance < 75);
                    if (defaulters.length === 0) {
                        alert("No defaulters found.");
                    } else {
                        const list = defaulters.map(s => `${s.name} (${s.rollNo}) - ${s.attendance}%`).join("\n");
                        alert("Defaulters (Attendance < 75%):\n\n" + list);
                    }
                }
            } catch(e) {
                alert("Error fetching defaulters");
            }
        });
    }

    function switchSection(activeNav, activeSection) {
        [navDashboard, navStudents, navMarks].forEach(n => n.classList.remove('active'));
        [sectionDashboard, sectionStudents, sectionMarks].forEach(s => s.classList.remove('active'));
        activeNav.classList.add('active');
        activeSection.classList.add('active');
    }

    navDashboard.addEventListener('click', (e) => {
        e.preventDefault();
        switchSection(navDashboard, sectionDashboard);
        loadStats();
    });

    navStudents.addEventListener('click', (e) => {
        e.preventDefault();
        switchSection(navStudents, sectionStudents);
        loadStudents();
    });

    navMarks.addEventListener('click', (e) => {
        e.preventDefault();
        switchSection(navMarks, sectionMarks);
    });

    navLogout.addEventListener('click', (e) => {
        e.preventDefault();
        localStorage.clear();
        window.location.href = '/index.html';
    });

    // Dashboard Stats
    async function loadStats() {
        try {
            const res = await fetch('/api/admin/stats');
            const data = await res.json();
            if (data.success) {
                document.getElementById('statTotal').textContent = data.data.totalStudents;
                document.getElementById('statCse').textContent = data.data.cseStudents;
                document.getElementById('statEce').textContent = data.data.eceStudents;
                document.getElementById('statAtt').textContent = data.data.averageAttendance + '%';
            }
        } catch (e) { console.error(e); }
    }
    loadStats(); // Load initially

    // Student Management
    const studentTableBody = document.getElementById('studentTableBody');
    const searchInput = document.getElementById('searchInput');
    const searchBtn = document.getElementById('searchBtn');
    
    const slidePanel = document.getElementById('slidePanel');
    const slidePanelOverlay = document.getElementById('slidePanelOverlay');
    const closePanelBtn = document.getElementById('closePanelBtn');
    const panelTitle = document.getElementById('panelTitle');
    const studentForm = document.getElementById('studentForm');
    const panelError = document.getElementById('panelError');
    const formRollNo = document.getElementById('formRollNo');
    
    let isEditing = false;

    let allStudents = [];

    async function loadStudents(query = '') {
        try {
            let url = '/students';
            if (query) url += `?search=${encodeURIComponent(query)}`;
            const res = await fetch(url);
            const data = await res.json();
            
            if (data.success) {
                allStudents = data.data;
                renderTable(allStudents);
            }
        } catch (err) { console.error(err); }
    }

    function renderTable(students) {
        studentTableBody.innerHTML = '';
        if (!students || students.length === 0) {
            studentTableBody.innerHTML = '<tr><td colspan="7">No students found.</td></tr>';
            return;
        }
        students.forEach(student => {
            const tr = document.createElement('tr');
            let actionHtml = `<button class="btn-edit" onclick='editStudent("${escapeHTML(student.rollNo)}")'>Edit</button>`;
            if (localStorage.getItem('role') !== 'faculty') {
                actionHtml += `<button class="btn-danger" onclick='deleteStudent("${escapeHTML(student.rollNo)}")'>Delete</button>`;
            }
            tr.innerHTML = `
                <td>${escapeHTML(student.rollNo)}</td>
                <td>${escapeHTML(student.name)}</td>
                <td>${escapeHTML(student.department)}</td>
                <td>${student.year}</td>
                <td>${escapeHTML(student.email || '')}</td>
                <td>${escapeHTML(student.phone || '')}</td>
                <td>
                    <div class="action-buttons">
                        ${actionHtml}
                    </div>
                </td>
            `;
            studentTableBody.appendChild(tr);
        });
    }

    function escapeHTML(str) {
        if (!str) return '';
        return str.replace(/[&<>'"]/g, tag => ({'&': '&amp;','<': '&lt;','>': '&gt;',"'": '&#39;','"': '&quot;'}[tag]));
    }

    searchBtn.addEventListener('click', () => loadStudents(searchInput.value.trim()));
    searchInput.addEventListener('keypress', (e) => { if(e.key === 'Enter') loadStudents(searchInput.value.trim()); });

    function openPanel(title, editMode) {
        isEditing = editMode;
        panelTitle.textContent = title;
        panelError.style.display = 'none';
        slidePanelOverlay.style.display = 'block';
        setTimeout(() => slidePanel.classList.add('open'), 10);
        if (editMode) formRollNo.disabled = true;
        else { studentForm.reset(); formRollNo.disabled = false; }
    }

    function closePanel() {
        slidePanel.classList.remove('open');
        setTimeout(() => { slidePanelOverlay.style.display = 'none'; studentForm.reset(); }, 300);
    }

    const addBtn = document.getElementById('addStudentBtn');
    if (addBtn) {
        addBtn.addEventListener('click', () => openPanel('Add Student', false));
    }
    closePanelBtn.addEventListener('click', closePanel);
    slidePanelOverlay.addEventListener('click', closePanel);

    studentForm.addEventListener('submit', async (e) => {
        e.preventDefault();
        panelError.style.display = 'none';
        const studentData = {
            rollNo: formRollNo.value.trim(),
            name: document.getElementById('formName').value.trim(),
            passwordHash: document.getElementById('formPassword').value,
            department: document.getElementById('formDepartment').value.trim(),
            year: parseInt(document.getElementById('formYear').value, 10),
            email: document.getElementById('formEmail').value.trim(),
            phone: document.getElementById('formPhone').value.trim()
        };
        try {
            const method = isEditing ? 'PUT' : 'POST';
            const res = await fetch('/students', {
                method: method,
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(studentData)
            });
            const data = await res.json();
            if (data.success) { closePanel(); loadStudents(); loadStats(); } 
            else { panelError.textContent = data.message || 'Operation failed'; panelError.style.display = 'block'; }
        } catch (err) { panelError.textContent = 'Network error'; panelError.style.display = 'block'; }
    });

    window.editStudent = function(rollNo) {
        const student = allStudents.find(s => s.rollNo === rollNo);
        if (!student) return;
        
        openPanel('Edit Student', true);
        formRollNo.value = student.rollNo;
        document.getElementById('formName').value = student.name;
        document.getElementById('formDepartment').value = student.department;
        document.getElementById('formYear').value = student.year;
        document.getElementById('formEmail').value = student.email || '';
        document.getElementById('formPhone').value = student.phone || '';
    };

    window.deleteStudent = async function(rollNo) {
        if (!confirm(`Are you sure you want to delete student ${rollNo}?`)) return;
        try {
            const res = await fetch(`/students?rollNo=${encodeURIComponent(rollNo)}`, { method: 'DELETE' });
            const data = await res.json();
            if (data.success) { loadStudents(); loadStats(); }
            else alert(data.message || 'Failed to delete student');
        } catch (err) { alert('Network error'); }
    };

    // Marks and Attendance Management
    let currentMarksRollNo = null;

    document.getElementById('marksSearchBtn').addEventListener('click', async () => {
        const rollNo = document.getElementById('marksSearchInput').value.trim();
        if (!rollNo) return;
        
        // Fetch student to get attendance
        try {
            const sRes = await fetch('/students');
            const sData = await sRes.json();
            const student = sData.data.find(s => s.rollNo.toUpperCase() === rollNo.toUpperCase());
            
            if (student) {
                currentMarksRollNo = student.rollNo;
                document.getElementById('marksContainer').style.display = 'block';
                document.getElementById('editAttendance').value = student.attendance;
                document.getElementById('studentInfoCard').style.display = 'block';

                document.getElementById('studentName').textContent = student.name;
                document.getElementById('studentRollNo').textContent = student.rollNo;
                document.getElementById('studentDepartment').textContent = student.department;
                document.getElementById('studentYear').textContent = student.year;
                document.getElementById('studentEmail').textContent = student.email;
                loadAdminMarks(student.rollNo);
            } else {
                alert('Student not found');
                document.getElementById('marksContainer').style.display='none';
                document.getElementById('studentInfoCard').style.display='none';
            }
        } catch(e) { console.error(e); }
    });

    async function loadAdminMarks(rollNo) {
        try {
            const res = await fetch(`/api/marks?rollNo=${encodeURIComponent(rollNo)}`);
            const data = await res.json();
            const tbody = document.getElementById('adminMarksBody');
            tbody.innerHTML = '';
            
            if (data.success && data.data) {
                const marksArray = Array.isArray(data.data) ? data.data : (data.data.data || []);
                if (marksArray.length === 0) {
                    tbody.innerHTML = '<tr><td colspan="2">No marks yet</td></tr>';
                    return;
                }

                const grouped = {};
                marksArray.forEach(m => {
                    if (!grouped[m.semester]) grouped[m.semester] = [];
                    grouped[m.semester].push(m);
                });

                Object.keys(grouped).sort((a,b) => parseInt(a) - parseInt(b)).forEach(sem => {
                    const headerRow = document.createElement('tr');
                    headerRow.style.backgroundColor = '#F1F5F9';
                    headerRow.innerHTML = `<td colspan="2" style="font-weight:bold; color:#334155; padding-top: 12px; padding-bottom: 12px;">Semester ${sem}</td>`;
                    tbody.appendChild(headerRow);
                    
                    grouped[sem].forEach(m => {
                        const subjectDisplay = m.subjectName.replace(/\s*\(Sem\s*\d+\)/i, '');
                        const tr = document.createElement('tr');
                        tr.innerHTML = `<td>${escapeHTML(subjectDisplay)}</td><td>${m.marks}</td>`;
                        tbody.appendChild(tr);
                    });
                });
            }
        } catch (e) { console.error(e); }
    }

    document.getElementById('saveAttendanceBtn').addEventListener('click', async () => {
        if (!currentMarksRollNo) return;
        const att = document.getElementById('editAttendance').value;
        try {
            const res = await fetch('/api/admin/attendance', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ rollNo: currentMarksRollNo, attendance: parseInt(att, 10) })
            });
            const data = await res.json();
            if (data.success) { alert('Attendance updated'); loadStats(); }
            else alert('Failed to update attendance');
        } catch (e) { console.error(e); }
    });

    document.getElementById('addMarkBtn').addEventListener('click', async () => {
        if (!currentMarksRollNo) return;
        const semester = parseInt(document.getElementById('markSemester').value);
        const subject = document.getElementById('markSubject').value.trim();
        const marks = parseInt(document.getElementById('markValue').value);

        if (!subject || isNaN(marks)) {
            alert('Please provide valid subject and marks');
            return;
        }

        const payload = { rollNo: currentMarksRollNo, semester: semester, subjectName: subject, marks: marks };
        try {
            const res = await fetch('/api/marks', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(payload)
            });
            const data = await res.json();
            if (data.success) {
                document.getElementById('markSubject').value = '';
                document.getElementById('markValue').value = '';
                loadAdminMarks(currentMarksRollNo);
            } else alert('Failed to save mark');
        } catch (e) { console.error(e); }
    });
});
