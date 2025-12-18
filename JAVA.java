/* -------------------------------
    TEACHER DATABASE (30 TEACHERS)
--------------------------------- */

const teachers = [
    // ---- MATHEMATICS (10) ----
    ...Array.from({
        length: 10
    }, (_, i) => ({
        name: `Math Teacher ${i + 1}`,
        subject: "math",
        rating: (4 + Math.random()).toFixed(1),
        fee: 500,
        reviews: ["Very helpful", "Explains clearly"]
    })),

    // ---- SCIENCE (10) ----
    ...Array.from({
        length: 10
    }, (_, i) => ({
        name: `Science Teacher ${i + 1}`,
        subject: "science",
        rating: (4 + Math.random()).toFixed(1),
        fee: 600,
        reviews: ["Great teaching", "Very knowledgeable"]
    })),

    // ---- ENGLISH (10) ----
    ...Array.from({
        length: 10
    }, (_, i) => ({
        name: `English Teacher ${i + 1}`,
        subject: "english",
        rating: (4 + Math.random()).toFixed(1),
        fee: 450,
        reviews: ["Good explanation", "Makes learning easy"]
    })),

    // ---- COMPUTER SCIENCE (10) ----
    ...Array.from({
        length: 10
    }, (_, i) => ({
        name: `CS Teacher ${i + 1}`,
        subject: "computer",
        rating: (4 + Math.random()).toFixed(1),
        fee: 700,
        reviews: ["Expert coder", "Very practical"]
    }))
];

/* ----------------------------------------------------
    RENDER TEACHERS ON PAGE
---------------------------------------------------- */

const teacherContainer = document.getElementById("teacherContainer");

function renderTeachers(filter = "all") {
    teacherContainer.innerHTML = "";

    teachers
        .filter(t => filter === "all" || t.subject === filter)
        .forEach(t => {
            const div = document.createElement("div");
            div.className = "teacher-card";

            div.innerHTML = `
                <div class="teacher-icon">${t.name.charAt(0)}</div>
                <h3>${t.name}</h3>
                <p><strong>Subject:</strong> ${t.subject.toUpperCase()}</p>
                <p class="rating">⭐ ${t.rating}</p>
                <p><strong>Fee:</strong> ₹${t.fee}/hour</p>
            `;

            div.addEventListener("click", () => selectTeacher(t));
            teacherContainer.appendChild(div);
        });
}

renderTeachers();

/* ----------------------------------------------------
    FILTER TEACHERS
---------------------------------------------------- */

document.getElementById("subjectFilter").addEventListener("change", e => {
    renderTeachers(e.target.value);
});

/* ----------------------------------------------------
    LOGIN SYSTEM
---------------------------------------------------- */

let currentStudent = null;

document.getElementById("loginBtn").onclick = () => {
    document.getElementById("loginSection").classList.remove("hidden");
};

document.getElementById("submitLogin").onclick = () => {
    const name = document.getElementById("studentName").value;
    const email = document.getElementById("studentEmail").value;

    if (!name || !email) return alert("Please fill all fields");

    currentStudent = {
        name,
        email
    };
    localStorage.setItem("student", JSON.stringify(currentStudent));

    document.getElementById("loginSection").classList.add("hidden");
    document.getElementById("loginBtn").classList.add("hidden");
    document.getElementById("logoutBtn").classList.remove("hidden");

    loadDashboard();
};

document.getElementById("logoutBtn").onclick = () => {
    localStorage.removeItem("student");
    location.reload();
};

/* ----------------------------------------------------
    BOOKING
---------------------------------------------------- */

let selectedTeacher = null;

function selectTeacher(t) {
    selectedTeacher = t;

    document.getElementById("selectedTeacherName").textContent = t.name;
    document.getElementById("bookingBox").classList.remove("hidden");

    // 🔥 Auto-scroll to booking section
    document.getElementById("booking").scrollIntoView({
        behavior: "smooth",
        block: "start"
    });
}


document.getElementById("confirmBooking").onclick = () => {
    if (!currentStudent) return alert("Please login first");

    const mode = document.getElementById("classMode").value;
    const date = document.getElementById("bookingDate").value;
    const time = document.getElementById("bookingTime").value;

    if (!date || !time) return alert("Select date & time");

    const appointment = {
        teacher: selectedTeacher.name,
        subject: selectedTeacher.subject,
        fee: selectedTeacher.fee,
        mode,
        date,
        time
    };

    let data = JSON.parse(localStorage.getItem("appointments") || "[]");
    data.push(appointment);
    localStorage.setItem("appointments", JSON.stringify(data));

    alert("Appointment Confirmed!");
    loadDashboard();
};

/* ----------------------------------------------------
    STUDENT DASHBOARD
---------------------------------------------------- */

function loadDashboard() {
    const student = JSON.parse(localStorage.getItem("student"));
    if (!student) return;

    document.getElementById("dashboardContent").classList.remove("hidden");
    document.getElementById("dashName").textContent = student.name;

    let appointments = JSON.parse(localStorage.getItem("appointments") || "[]");

    let list = document.getElementById("appointmentList");
    list.innerHTML = "";

    let total = 0;

    appointments.forEach(a => {
        total += a.fee;

        const li = document.createElement("li");
        li.innerHTML = `
            <strong>${a.teacher}</strong> (${a.subject})<br>
            ${a.mode} — ${a.date}, ${a.time} <br>
            Fee: ₹${a.fee}
        `;
        list.appendChild(li);
    });

    document.getElementById("totalFees").textContent = total;
}

loadDashboard();

/* ----------------------------------------------------
    THEME TOGGLE
---------------------------------------------------- */

document.getElementById("themeToggle").onclick = () => {
    document.body.classList.toggle("dark");
};