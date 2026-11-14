let token = '', user = null, tab = 'timetable';

const $ = id => document.getElementById(id);

const escapeHTML = s => String(s ?? '').replace(
    /[&<>"']/g,
    c => ({
        '&': '&amp;',
        '<': '&lt;',
        '>': '&gt;',
        '"': '&quot;',
        "'": '&#39;'
    }[c])
);

$('showSignup').onclick = () => {
    $('signupForm').hidden = !$('signupForm').hidden;
};

$('signupForm').onsubmit = async e => {
    e.preventDefault();

    try {
        await api('signup', Object.fromEntries(new FormData(e.target)));

        $('loginError').textContent = 'Account activated. Please sign in.';
        $('signupForm').hidden = true;

    } catch (err) {
        $('loginError').textContent = err.message;
    }
};

async function api(path, values) { const res = await fetch('/api/' + path, { method: values ? 'POST' : 'GET', headers: { ...(token ? { Authorization: 'Bearer ' + token } : {}), ...(values ? { 'Content-Type': 'application/x-www-form-urlencoded' } : {}) }, body: values ? new URLSearchParams(values) : undefined }); const data = await res.json(); if (!res.ok) throw Error(data.error || 'Request failed'); return data; }
$('loginForm').onsubmit = async e => { e.preventDefault(); try { const data = await api('login', { id: $('collegeId').value, password: $('password').value }); token = data.token; user = data.user; $('login').hidden = true; $('app').hidden = false; $('who').textContent = user.name; $('role').textContent = user.role; $('myRole').textContent = user.role; $('today').textContent = new Date().toLocaleDateString(); document.querySelectorAll('.restricted').forEach(b => b.hidden = b.dataset.tab === 'issues' ? !['CR', 'FACULTY', 'ADMIN'].includes(user.role) : !['FACULTY', 'ADMIN'].includes(user.role)); await show('timetable'); } catch (err) { $('loginError').textContent = err.message } };
$('logout').onclick = async () => { try { await api('logout', {}); } catch (e) { } location.reload(); }; document.querySelectorAll('nav button').forEach(b => b.onclick = () => show(b.dataset.tab));
const table = (rows, cols) => '<div class="scroll"><table><thead><tr>' + cols.map(c => '<th>' + escapeHTML(c[1]) + '</th>').join('') + '</tr></thead><tbody>' + rows.map(r => '<tr>' + cols.map(c => '<td>' + escapeHTML(r[c[0]]) + '</td>').join('') + '</tr>').join('') + '</tbody></table></div>' + (rows.length ? '' : '<p>No records yet.</p>');

function weeklyTimetableTable(rows) {

    const days = [
        'Monday', 'Tuesday', 'Wednesday',
        'Thursday', 'Friday', 'Saturday', 'Sunday'
    ];

    rows.sort((a, b) =>
        days.indexOf(a.day) - days.indexOf(b.day) ||
        String(a.start).localeCompare(String(b.start))
    );

    const counts = {};

    rows.forEach(r => {
        counts[r.day] = (counts[r.day] || 0) + 1;
    });

    const printed = new Set();

    let html = `
        <div class="scroll">
        <table>
        <thead>
        <tr>
            <th>Day</th>
            <th>Start</th>
            <th>End</th>
            <th>Subject</th>
            <th>Faculty</th>
            <th>Division</th>
            <th>Room</th>
            <th>Status</th>
        </tr>
        </thead>
        <tbody>
    `;

    rows.forEach(r => {

        html += '<tr>';

        if (!printed.has(r.day)) {
            html += `
                <td rowspan="${counts[r.day]}">
                    <strong>${escapeHTML(r.day)}</strong>
                </td>
            `;
            printed.add(r.day);
        }

        html += `
            <td>${escapeHTML(r.start)}</td>
            <td>${escapeHTML(r.end)}</td>
            <td>${escapeHTML(r.subject)}</td>
            <td>${escapeHTML(r.faculty)}</td>
            <td>${escapeHTML(r.division)}</td>
            <td>${escapeHTML(r.room)}</td>
            <td>${escapeHTML(r.status)}</td>
        </tr>`;
    });

    html += '</tbody></table></div>';

    if (rows.length === 0) {
        html += '<p>No lectures found.</p>';
    }

    return html;
}

async function show(t) {
    tab = t; document.querySelectorAll('nav button').forEach(b => b.classList.toggle('active', b.dataset.tab === t)); $('heading').textContent = ({ timetable: 'My Timetable', rooms: 'Classroom Availability', notifications: 'Notifications', issues: 'Report an Issue', bookings: 'Room Bookings' })[t]; $('toast').textContent = ''; try {
        if (t === 'timetable') { let rows = await api('timetable'); $('content').innerHTML = '<h2>Scheduled lectures</h2>' + weeklyTimetableTable(rows) + (user.role === 'FACULTY' || user.role === 'ADMIN' ? '<h3>Change a lecture</h3><form id="change"><div class="row"><label>Session ID<input name="id" required type="number"></label><label>New room (for shift)<input name="room" placeholder="A102"></label></div><button class="action" name="action" value="shift">Shift room</button> <button class="action danger" name="action" value="cancel">Cancel lecture</button></form>' : ''); let f = $('change'); if (f) f.onsubmit = async e => { e.preventDefault(); let v = Object.fromEntries(new FormData(f)); let action = e.submitter?.value || 'shift'; delete v.action; await execute(action, v) }; }
        if (t === 'rooms') { let rows = await api('rooms'); $('content').innerHTML = '<h2>Current room availability</h2><p>Red = Empty · Green = Occupied. Calculated from active sessions and approved bookings.</p><div class="room-grid">' + rows.map(r => '<div class="room ' + (r.status === 'EMPTY' ? 'empty' : 'occupied') + '"><strong>' + escapeHTML(r.code) + '</strong><small>' + escapeHTML(r.status) + ' · Capacity ' + escapeHTML(r.capacity) + '</small></div>').join('') + '</div><p>Refresh to update current room status.</p>' + (['CR', 'FACULTY', 'ADMIN'].includes(user.role) ? '<h3>Update room status</h3><form id="roomForm"><label>Room code<input name="room" required placeholder="9101"></label><label>Status<select name="status"><option value="EMPTY">Empty (red)</option><option value="OCCUPIED">Occupied (green)</option></select></label><button class="action">Update room</button></form>' : ''); let f = $('roomForm'); if (f) f.onsubmit = async e => { e.preventDefault(); await execute('room-status', Object.fromEntries(new FormData(f))) }; }
        if (t === 'notifications') { $('content').innerHTML = '<h2>My alerts</h2>' + table(await api('notifications'), [['created_at', 'Time'], ['message', 'Message']]); }
        if (t === 'issues') { $('content').innerHTML = '<h2>Report classroom issue</h2><form id="issueForm"><label>Room code<input name="room" required placeholder="A101"></label><label>Problem description<textarea name="description" required placeholder="Digital board not working"></textarea></label><button class="action">Send to admin</button></form>' + (user.role === 'ADMIN' ? '<h2>Reported issues</h2>' + table(await api('issues'), [['id', 'ID'], ['room_code', 'Room'], ['reporter_id', 'Reported by'], ['description', 'Issue'], ['status', 'Status']]) : ''); $('issueForm').onsubmit = async e => { e.preventDefault(); await execute('issues', Object.fromEntries(new FormData(e.target))) }; }
        if (t === 'bookings') { $('content').innerHTML = '<h2>Booking requests</h2>' + table(await api('bookings'), [['id', 'ID'], ['room_code', 'Room'], ['faculty_id', 'Faculty'], ['booking_date', 'Date'], ['start_time', 'Start'], ['end_time', 'End'], ['status', 'Status']]) + (user.role === 'FACULTY' ? '<h3>Request a classroom</h3><form id="bookingForm"><div class="row"><label>Room<input name="room" required placeholder="A102"></label><label>Date<input type="date" name="date" required></label><label>Start<input type="time" name="start" required></label><label>End<input type="time" name="end" required></label></div><label>Reason<input name="reason" required placeholder="Extra lecture"></label><button class="action">Request booking</button></form>' : '<h3>Approve request</h3><form id="approveForm"><label>Booking ID<input name="id" required type="number"></label><button class="action">Approve if room free</button></form>'); let f = $('bookingForm'); if (f) f.onsubmit = async e => { e.preventDefault(); await execute('bookings', Object.fromEntries(new FormData(f))) }; f = $('approveForm'); if (f) f.onsubmit = async e => { e.preventDefault(); await execute('approve', Object.fromEntries(new FormData(f))) }; }
    }
    catch (err) { $('toast').textContent = err.message; }
}
async function execute(action, data) { try { await api(action, data); await show(tab); $('toast').textContent = 'Saved successfully.'; } catch (err) { $('toast').textContent = err.message; } }