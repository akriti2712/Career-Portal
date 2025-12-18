/* app.js
   Updated: CAREERS_DB now contains 25 career options (mixed fields).
   The rest of the app (colleges, pagination, filters, auth, assessment)
   remains the same as before.
*/

/* ---------- Helper & Init ---------- */
const $ = id => document.getElementById(id);
const YEAR = () => (new Date()).getFullYear();
document.getElementById('year').innerText = YEAR();

function toggleMobileMenu() {
    const el = $('mobileMenu');
    el.style.display = el.style.display === 'none' ? 'block' : 'none';
}

let currentPage = 'home';

function navigate(page) {
    closeAuth();
    document.querySelectorAll('.page').forEach(p => p.style.display = 'none');
    const target = $(page);
    if (target) target.style.display = 'block';
    currentPage = page;
    updateAuthButton();
    if (page === 'careers') renderCareers();
    if (page === 'colleges') {
        populateStateFilter();
        renderColleges(1);
    }
    if (page === 'courses') renderCourses();
    if (page === 'dashboard') renderDashboard();
    if (page === 'assessment') renderAssessmentForm();
}

/* Auth modal helpers */
function openAuth() {
    $('authModal').style.display = 'flex';
    updateAuthTabUI();
}

function closeAuth() {
    $('authModal').style.display = 'none';
}

function openAuthTab(id) {
    document.querySelectorAll('.auth-tab').forEach(t => t.style.display = 'none');
    document.querySelectorAll('.tab-btn').forEach(b => b.classList.remove('active'));
    $(id).style.display = 'block';
    if (id === 'loginTab') document.querySelector('.tab-btn').classList.add('active');
    else document.querySelectorAll('.tab-btn')[1].classList.add('active');
}

function updateAuthTabUI() {
    openAuthTab('loginTab');
}

/* ---------- LocalStorage helpers ---------- */
const LS = {
    get(key, fallback = null) {
        try {
            const v = localStorage.getItem(key);
            return v ? JSON.parse(v) : fallback;
        } catch (e) {
            return fallback;
        }
    },
    set(key, val) {
        localStorage.setItem(key, JSON.stringify(val));
    },
    remove(key) {
        localStorage.removeItem(key);
    }
};

function getUsers() {
    return LS.get('cp_users', []);
}

function saveUsers(users) {
    LS.set('cp_users', users);
}

function getLoggedInUser() {
    return LS.get('cp_session', null);
}

function setLoggedInUser(user) {
    LS.set('cp_session', user);
    updateAuthButton();
}

function logout() {
    LS.remove('cp_session');
    updateAuthButton();
    navigate('home');
    alert('Logged out');
}

/* ---------- CAREERS DB (25 items) ---------- */
const CAREERS_DB = [{
        id: 'sw',
        title: 'Software Engineer',
        field: 'tech',
        short: 'Build software & applications.',
        description: 'Design, develop, and maintain software. Requires programming, algorithms, system design.',
        skills: ['Programming', 'Problem Solving', 'System Design'],
        courses: ['B.Tech CSE', 'BCA', 'MCA'],
        avgSalary: '4-12 LPA'
    },

    {
        id: 'fe',
        title: 'Frontend Developer',
        field: 'tech',
        short: 'Create interactive web interfaces.',
        description: 'Build responsive UI using HTML, CSS, JavaScript and frameworks like React or Vue.',
        skills: ['HTML', 'CSS', 'JavaScript', 'UI/UX'],
        courses: ['B.Tech CSE', 'BCA', 'Web Bootcamps'],
        avgSalary: '3-10 LPA'
    },

    {
        id: 'be',
        title: 'Backend Developer',
        field: 'tech',
        short: 'Build server-side systems & APIs.',
        description: 'Design backend services, databases and APIs with languages like Node, Java, Python.',
        skills: ['APIs', 'Databases', 'Security'],
        courses: ['B.Tech', 'MCA'],
        avgSalary: '4-12 LPA'
    },

    {
        id: 'ds',
        title: 'Data Scientist',
        field: 'tech',
        short: 'Analyze & model data to extract insights.',
        description: 'Use statistics, ML and data engineering to solve business problems.',
        skills: ['Statistics', 'Python', 'ML'],
        courses: ['B.Tech', 'MSc Data Science'],
        avgSalary: '6-18 LPA'
    },

    {
        id: 'ai',
        title: 'AI / ML Engineer',
        field: 'tech',
        short: 'Build ML models & AI systems.',
        description: 'Design, train and deploy machine learning models using frameworks like TensorFlow, PyTorch.',
        skills: ['Machine Learning', 'Python', 'Model Deployment'],
        courses: ['B.Tech', 'MTech', 'MSc AI'],
        avgSalary: '8-25 LPA'
    },

    {
        id: 'se',
        title: 'Systems Engineer',
        field: 'tech',
        short: 'Work on system reliability & infra.',
        description: 'Manage servers, cloud infrastructure, performance and reliability.',
        skills: ['Linux', 'Cloud', 'Networking'],
        courses: ['B.Tech', 'M.Tech'],
        avgSalary: '5-15 LPA'
    },

    {
        id: 'cy',
        title: 'Cybersecurity Analyst',
        field: 'tech',
        short: 'Protect systems from threats.',
        description: 'Monitor, detect and respond to security incidents; penetration testing.',
        skills: ['Security', 'Networking', 'Forensics'],
        courses: ['BTech', 'Certs'],
        avgSalary: '4-14 LPA'
    },

    {
        id: 'uiux',
        title: 'UI/UX Designer',
        field: 'design',
        short: 'Design user interfaces & experiences.',
        description: 'Research users, design wireframes, prototypes and visuals for apps and websites.',
        skills: ['Wireframing', 'Prototyping', 'Visual Design'],
        courses: ['B.Des', 'UX Bootcamp'],
        avgSalary: '3-10 LPA'
    },

    {
        id: 'gd',
        title: 'Graphic Designer',
        field: 'design',
        short: 'Create visual content for brands.',
        description: 'Work with Adobe tools to create posters, logos and marketing creatives.',
        skills: ['Photoshop', 'Illustrator', 'Creativity'],
        courses: ['B.Des', 'Diploma'],
        avgSalary: '2-8 LPA'
    },

    {
        id: 'arch',
        title: 'Architect',
        field: 'design',
        short: 'Design buildings & spaces.',
        description: 'Plan and design structures, coordinate with engineering and construction teams.',
        skills: ['Design', 'Drawing', 'CAD'],
        courses: ['B.Arch'],
        avgSalary: '3-12 LPA'
    },

    {
        id: 'doc',
        title: 'Doctor (MBBS)',
        field: 'medical',
        short: 'Diagnose and treat patients.',
        description: 'Requires MBBS and clinical training; work in hospitals or clinics.',
        skills: ['Clinical Knowledge', 'Empathy', 'Decision Making'],
        courses: ['MBBS'],
        avgSalary: '5-20 LPA'
    },

    {
        id: 'nurse',
        title: 'Nurse',
        field: 'medical',
        short: 'Provide patient care and support.',
        description: 'Work alongside doctors to provide medical care and patient monitoring.',
        skills: ['Patient Care', 'Communication'],
        courses: ['BSc Nursing'],
        avgSalary: '1.5-6 LPA'
    },

    {
        id: 'pharma',
        title: 'Pharmacist',
        field: 'medical',
        short: 'Dispense medications & manage pharmacy.',
        description: 'Work in hospitals, retail or pharma industry; requires B.Pharm.',
        skills: ['Drug Knowledge', 'Attention to Detail'],
        courses: ['B.Pharm'],
        avgSalary: '2-8 LPA'
    },

    {
        id: 'law',
        title: 'Lawyer',
        field: 'law',
        short: 'Legal counsel & courtroom representation.',
        description: 'Practice law in courts or work as legal counsel in firms or companies.',
        skills: ['Advocacy', 'Research', 'Logic'],
        courses: ['LLB', 'BA LLB'],
        avgSalary: '3-15 LPA'
    },

    {
        id: 'ca',
        title: 'Chartered Accountant (CA)',
        field: 'finance',
        short: 'Accounting, auditing & finance advisory.',
        description: 'Work in finance, taxation, audit and consulting after CA qualification.',
        skills: ['Accounting', 'Taxation', 'Finance'],
        courses: ['CA'],
        avgSalary: '3-20 LPA'
    },

    {
        id: 'cfo',
        title: 'Financial Analyst',
        field: 'finance',
        short: 'Analyze financial data & advise decisions.',
        description: 'Create financial models, reports and insights for businesses.',
        skills: ['Excel', 'Modeling', 'Analysis'],
        courses: ['B.Com', 'MBA'],
        avgSalary: '3-12 LPA'
    },

    {
        id: 'pm',
        title: 'Product Manager',
        field: 'business',
        short: 'Lead product strategy and execution.',
        description: 'Coordinate across engineering, design and marketing to build products.',
        skills: ['Communication', 'Strategy', 'Roadmapping'],
        courses: ['B.Tech', 'MBA'],
        avgSalary: '8-25 LPA'
    },

    {
        id: 'mm',
        title: 'Marketing Manager',
        field: 'business',
        short: 'Plan & execute marketing campaigns.',
        description: 'Drive customer acquisition, branding and market research.',
        skills: ['Marketing', 'Analytics', 'Creativity'],
        courses: ['BBA', 'MBA'],
        avgSalary: '4-15 LPA'
    },

    {
        id: 'hr',
        title: 'Human Resources Manager',
        field: 'business',
        short: 'Manage talent & HR processes.',
        description: 'Recruitment, employee relations, training & performance management.',
        skills: ['Communication', 'Empathy', 'Policy'],
        courses: ['BBA', 'MBA'],
        avgSalary: '3-12 LPA'
    },

    {
        id: 'teacher',
        title: 'Teacher / Lecturer',
        field: 'teaching',
        short: 'Teach in schools or colleges.',
        description: 'Plan lessons, assess students and guide learning.',
        skills: ['Subject Knowledge', 'Patience', 'Communication'],
        courses: ['B.Ed', 'M.Ed'],
        avgSalary: '2-8 LPA'
    },

    {
        id: 'civil',
        title: 'Civil Engineer',
        field: 'engineering',
        short: 'Design & build infrastructure.',
        description: 'Work on roads, bridges, buildings and urban infrastructure.',
        skills: ['Civil Design', 'Surveying', 'Project Management'],
        courses: ['B.Tech Civil'],
        avgSalary: '3-12 LPA'
    },

    {
        id: 'mech',
        title: 'Mechanical Engineer',
        field: 'engineering',
        short: 'Design mechanical systems & machines.',
        description: 'Work in manufacturing, automotive, aerospace and energy sectors.',
        skills: ['CAD', 'Thermodynamics', 'Manufacturing'],
        courses: ['B.Tech'],
        avgSalary: '3-10 LPA'
    },

    {
        id: 'dataeng',
        title: 'Data Engineer',
        field: 'tech',
        short: 'Design data pipelines & warehouses.',
        description: 'Build scalable data infrastructure for analytics and ML.',
        skills: ['SQL', 'ETL', 'Big Data'],
        courses: ['B.Tech', 'BSc'],
        avgSalary: '6-20 LPA'
    },

    {
        id: 'uxresearch',
        title: 'UX Researcher',
        field: 'design',
        short: 'Study users to inform product design.',
        description: 'Conduct interviews, usability tests and research to guide designers.',
        skills: ['Research', 'Interviewing', 'Analysis'],
        courses: ['MA', 'MDes'],
        avgSalary: '4-12 LPA'
    },

    {
        id: 'entre',
        title: 'Entrepreneur / Startup Founder',
        field: 'business',
        short: 'Start and run your own business.',
        description: 'Build a product or service, raise funding and manage growth.',
        skills: ['Leadership', 'Risk Taking', 'Strategy'],
        courses: ['Any Degree + Experience'],
        avgSalary: 'Varies widely'
    }
];

/* ---------- COURSES DB (kept minimal) ---------- */
const COURSES_DB = [{
        id: 'btech',
        name: 'B.Tech Computer Science',
        type: 'ug',
        duration: '4 years',
        eligibility: '10+2 with PCM'
    },
    {
        id: 'mbbs',
        name: 'MBBS',
        type: 'ug',
        duration: '5.5 years',
        eligibility: '10+2 with PCB'
    },
    {
        id: 'bdes',
        name: 'B.Des (Design)',
        type: 'ug',
        duration: '4 years',
        eligibility: '10+2 or Diploma'
    },
    {
        id: 'mba',
        name: 'MBA',
        type: 'pg',
        duration: '2 years',
        eligibility: 'Bachelor degree'
    },
    {
        id: 'bba',
        name: 'BBA',
        type: 'ug',
        duration: '3 years',
        eligibility: '10+2'
    }
];

/* ---------- 50 Colleges DB (unchanged from previous) ---------- */
const COLLEGES_DB = [{
        id: 'iitb',
        name: 'IIT Bombay',
        state: 'Maharashtra',
        type: 'engineering',
        website: 'https://www.iitb.ac.in'
    },
    {
        id: 'iitd',
        name: 'IIT Delhi',
        state: 'Delhi',
        type: 'engineering',
        website: 'https://home.iitd.ac.in'
    },
    {
        id: 'iitk',
        name: 'IIT Kanpur',
        state: 'Uttar Pradesh',
        type: 'engineering',
        website: 'https://www.iitk.ac.in'
    },
    {
        id: 'iitm',
        name: 'IIT Madras',
        state: 'Tamil Nadu',
        type: 'engineering',
        website: 'https://www.iitm.ac.in'
    },
    {
        id: 'iitr',
        name: 'IIT Roorkee',
        state: 'Uttarakhand',
        type: 'engineering',
        website: 'https://www.iitr.ac.in'
    },
    {
        id: 'iitkgp',
        name: 'IIT Kharagpur',
        state: 'West Bengal',
        type: 'engineering',
        website: 'https://www.iitkgp.ac.in'
    },
    {
        id: 'nitt',
        name: 'NIT Trichy',
        state: 'Tamil Nadu',
        type: 'engineering',
        website: 'https://www.nitt.edu'
    },
    {
        id: 'nite',
        name: 'NIT Warangal',
        state: 'Telangana',
        type: 'engineering',
        website: 'https://www.nitw.ac.in'
    },
    {
        id: 'bits',
        name: 'BITS Pilani',
        state: 'Rajasthan',
        type: 'engineering',
        website: 'https://www.bits-pilani.ac.in'
    },
    {
        id: 'vit',
        name: 'VIT Vellore',
        state: 'Tamil Nadu',
        type: 'engineering',
        website: 'https://www.vit.ac.in'
    },

    {
        id: 'aiims',
        name: 'AIIMS Delhi',
        state: 'Delhi',
        type: 'medical',
        website: 'https://www.aiims.edu'
    },
    {
        id: 'kims',
        name: 'KEM Hospital (Mumbai)',
        state: 'Maharashtra',
        type: 'medical',
        website: '#'
    },
    {
        id: 'jipmer',
        name: 'JIPMER Puducherry',
        state: 'Puducherry',
        type: 'medical',
        website: 'https://jipmer.edu.in'
    },
    {
        id: 'sgpgims',
        name: 'SGPGIMS Lucknow',
        state: 'Uttar Pradesh',
        type: 'medical',
        website: '#'
    },
    {
        id: 'king',
        name: 'King George Medical University',
        state: 'Uttar Pradesh',
        type: 'medical',
        website: '#'
    },

    {
        id: 'iima',
        name: 'IIM Ahmedabad',
        state: 'Gujarat',
        type: 'business',
        website: 'https://www.iima.ac.in'
    },
    {
        id: 'iimb',
        name: 'IIM Bangalore',
        state: 'Karnataka',
        type: 'business',
        website: 'https://www.iimb.ac.in'
    },
    {
        id: 'iipm',
        name: 'IIM Calcutta',
        state: 'West Bengal',
        type: 'business',
        website: 'https://www.iimcal.ac.in'
    },

    {
        id: 'nls',
        name: 'NLSIU Bangalore',
        state: 'Karnataka',
        type: 'law',
        website: 'https://www.nls.ac.in'
    },
    {
        id: 'nludel',
        name: 'NALSAR Hyderabad',
        state: 'Telangana',
        type: 'law',
        website: 'https://www.nalsar.ac.in'
    },

    {
        id: 'nift',
        name: 'NIFT Delhi',
        state: 'Delhi',
        type: 'design',
        website: 'https://www.nift.ac.in'
    },
    {
        id: 'nid',
        name: 'NID Ahmedabad',
        state: 'Gujarat',
        type: 'design',
        website: 'https://www.nid.edu'
    },

    {
        id: 'du',
        name: 'Delhi University',
        state: 'Delhi',
        type: 'general',
        website: 'https://www.du.ac.in'
    },
    {
        id: 'jnu',
        name: 'JNU (Jawaharlal Nehru University)',
        state: 'Delhi',
        type: 'general',
        website: 'https://www.jnu.ac.in'
    },
    {
        id: 'ju',
        name: 'Jadavpur University',
        state: 'West Bengal',
        type: 'general',
        website: 'http://www.jaduniv.edu.in'
    },
    {
        id: 'bhu',
        name: 'Banaras Hindu University',
        state: 'Uttar Pradesh',
        type: 'general',
        website: 'https://www.bhu.ac.in'
    },
    {
        id: 'mum',
        name: 'University of Mumbai',
        state: 'Maharashtra',
        type: 'general',
        website: 'https://mu.ac.in'
    },
    {
        id: 'pun',
        name: 'Savitribai Phule Pune University',
        state: 'Maharashtra',
        type: 'general',
        website: 'http://www.unipune.ac.in'
    },

    {
        id: 'amity',
        name: 'Amity University',
        state: 'Uttar Pradesh',
        type: 'private',
        website: 'https://www.amity.edu'
    },
    {
        id: 'manipal',
        name: 'Manipal Academy of Higher Education',
        state: 'Karnataka',
        type: 'private',
        website: 'https://manipal.edu'
    },
    {
        id: 'srm',
        name: 'SRM Institute of Science & Technology',
        state: 'Tamil Nadu',
        type: 'private',
        website: 'https://www.srmist.edu.in'
    },
    {
        id: 'ansal',
        name: 'Ansals University (Amity group)',
        state: 'Haryana',
        type: 'private',
        website: '#'
    },
    {
        id: 'jecrc',
        name: 'JECRC University',
        state: 'Rajasthan',
        type: 'private',
        website: '#'
    },

    {
        id: 'anna',
        name: 'Anna University',
        state: 'Tamil Nadu',
        type: 'engineering',
        website: 'https://www.annauniv.edu'
    },
    {
        id: 'osmania',
        name: 'Osmania University',
        state: 'Telangana',
        type: 'general',
        website: 'https://www.osmania.ac.in'
    },

    {
        id: 'klev',
        name: 'KLE Tech (Belgaum)',
        state: 'Karnataka',
        type: 'engineering',
        website: '#'
    },
    {
        id: 'tifr',
        name: 'TIFR (Research)',
        state: 'Maharashtra',
        type: 'research',
        website: 'https://www.tifr.res.in'
    },

    {
        id: 'bitsgoa',
        name: 'BITS Goa',
        state: 'Goa',
        type: 'engineering',
        website: 'https://www.bits-pilani.ac.in/goa'
    },
    {
        id: 'gitam',
        name: 'GITAM University',
        state: 'Andhra Pradesh',
        type: 'private',
        website: 'https://www.gitam.edu'
    },
    {
        id: 'ksou',
        name: 'Karnataka State Open University',
        state: 'Karnataka',
        type: 'general',
        website: '#'
    },
    {
        id: 'cal',
        name: 'University of Calcutta',
        state: 'West Bengal',
        type: 'general',
        website: 'https://www.caluniv.ac.in'
    },

    {
        id: 'amu',
        name: 'Aligarh Muslim University',
        state: 'Uttar Pradesh',
        type: 'general',
        website: 'https://www.amu.ac.in'
    },
    {
        id: 'hydu',
        name: 'University of Hyderabad',
        state: 'Telangana',
        type: 'general',
        website: 'https://uohyd.ac.in'
    },
    {
        id: 'madras',
        name: 'University of Madras',
        state: 'Tamil Nadu',
        type: 'general',
        website: 'https://www.unom.ac.in'
    },

    {
        id: 'iitgn',
        name: 'IIT Gandhinagar',
        state: 'Gujarat',
        type: 'engineering',
        website: 'https://iitgn.ac.in'
    },
    {
        id: 'nitk',
        name: 'NIT Karnataka (Surathkal)',
        state: 'Karnataka',
        type: 'engineering',
        website: 'https://nitk.ac.in'
    },

    {
        id: 'bhuai',
        name: 'BHU Institute of Medical Sciences',
        state: 'Uttar Pradesh',
        type: 'medical',
        website: 'https://www.bhu.ac.in'
    },
    {
        id: 'kgmu',
        name: 'KGMU Lucknow',
        state: 'Uttar Pradesh',
        type: 'medical',
        website: '#'
    },

    {
        id: 'manipalmed',
        name: 'Manipal College of Medical Sciences',
        state: 'Karnataka',
        type: 'medical',
        website: 'https://manipal.edu'
    },
    {
        id: 'amueng',
        name: 'Amity School of Engineering',
        state: 'Uttar Pradesh',
        type: 'engineering',
        website: 'https://www.amity.edu'
    },

    {
        id: 'srmun',
        name: 'SRM University Chennai',
        state: 'Tamil Nadu',
        type: 'private',
        website: 'https://www.srmist.edu.in'
    },
    {
        id: 'vitv',
        name: 'VIT Vellore',
        state: 'Tamil Nadu',
        type: 'private',
        website: 'https://www.vit.ac.in'
    },
    {
        id: 'kmc',
        name: 'KMC Manipal (Kasturba)',
        state: 'Karnataka',
        type: 'medical',
        website: 'https://manipal.edu'
    },
    {
        id: 'ujb',
        name: 'University of Jammu',
        state: 'Jammu & Kashmir',
        type: 'general',
        website: '#'
    }
];

/* ---------- Auth: register/login (multiple users support) ---------- */
function doRegister() {
    const name = $('reg_name').value.trim();
    const email = $('reg_email').value.trim().toLowerCase();
    const pass = $('reg_pass').value;
    const interests = [...document.querySelectorAll('.interest:checked')].map(i => i.value);

    if (!name || !email || !pass) {
        alert('Fill name, email and password');
        return;
    }

    let users = getUsers();
    if (users.some(u => u.email === email)) {
        alert('Email already registered');
        return;
    }

    const newUser = {
        id: 'u' + Date.now(),
        name,
        email,
        pass,
        interests,
        favorites: [],
        history: [],
        savedColleges: [],
        savedCourses: []
    };
    users.push(newUser);
    saveUsers(users);
    setLoggedInUser(newUser);
    closeAuth();
    navigate('dashboard');
    alert('Registration successful — welcome ' + name.split(' ')[0]);
}

function doLogin() {
    const email = $('login_email').value.trim().toLowerCase();
    const pass = $('login_pass').value;
    const users = getUsers();
    const user = users.find(u => u.email === email && u.pass === pass);
    if (!user) {
        alert('Invalid credentials');
        return;
    }
    setLoggedInUser(user);
    closeAuth();
    navigate('dashboard');
}

/* ---------- Careers: render, filter, details, favorites ---------- */
function renderCareers() {
    const grid = $('careerGrid');
    grid.innerHTML = '';
    const q = $('careerSearch').value.trim().toLowerCase();
    const cat = $('careerCategory').value;

    const list = CAREERS_DB.filter(c => {
        const matchesQ = (!q) || c.title.toLowerCase().includes(q) || c.short.toLowerCase().includes(q);
        const matchesCat = (cat === 'all') || c.field === cat;
        return matchesQ && matchesCat;
    });

    if (list.length === 0) {
        grid.innerHTML = '<div class="card">No careers found</div>';
        return;
    }

    list.forEach(c => {
        const div = document.createElement('div');
        div.className = 'career-card';
        div.innerHTML = `
      <h3>${c.title}</h3>
      <p class="small">${c.short}</p>
      <div class="career-meta small">
        <div>Field: <strong>${c.field}</strong></div>
        <div> • Avg: <strong>${c.avgSalary || '—'}</strong></div>
      </div>
      <div style="margin-top:12px;display:flex;gap:8px">
        <button onclick="openCareer('${c.id}')">View</button>
        <button onclick="toggleFavorite(event,'${c.id}')">♡ Favorite</button>
      </div>
    `;
        grid.appendChild(div);
    });
}

function openCareer(id) {
    const career = CAREERS_DB.find(c => c.id === id);
    if (!career) return alert('Career not found');
    const dest = $('careerDetailCard');
    dest.innerHTML = `
    <h2>${career.title}</h2>
    <p class="small">${career.short}</p>
    <p>${career.description}</p>
    <h4>Key skills:</h4>
    <ul>${career.skills.map(s=>'<li>'+s+'</li>').join('')}</ul>
    <h4>Recommended courses:</h4>
    <ul>${career.courses.map(s=>'<li>'+s+'</li>').join('')}</ul>
    <p><strong>Average salary:</strong> ${career.avgSalary || '—'}</p>
    <div style="margin-top:14px">
      <button onclick="addToFavorites('${career.id}')">Save to Favorites</button>
      <button class="outline" onclick="navigate('careers')">Back</button>
    </div>
  `;
    navigate('careerDetails');
}

function addToFavorites(id) {
    const user = getLoggedInUser();
    if (!user) {
        alert('Login to save favorites');
        openAuth();
        return;
    }
    const users = getUsers();
    const u = users.find(x => x.id === user.id);
    if (!u.favorites) u.favorites = [];
    if (!u.favorites.includes(id)) {
        u.favorites.push(id);
        saveUsers(users);
        setLoggedInUser(u);
        alert('Saved to favorites');
        renderDashboard();
    } else alert('Already in favorites');
}

function toggleFavorite(ev, id) {
    ev.stopPropagation();
    addToFavorites(id);
}

/* ---------- Colleges: pagination + filters (unchanged) ---------- */
let collegeCurrentPage = 1;
const COLLEGES_PER_PAGE = 8;

function populateStateFilter() {
    const stateSelect = $('collegeStateFilter');
    const states = Array.from(new Set(COLLEGES_DB.map(c => c.state))).sort();
    stateSelect.innerHTML = '<option value="all">All States</option>' + states.map(s => `<option value="${s}">${s}</option>`).join('');
}

function renderColleges(page = 1) {
    collegeCurrentPage = page;
    const grid = $('collegeGrid');
    grid.innerHTML = '';
    const q = $('collegeSearch').value.trim().toLowerCase();
    const filter = $('collegeFilter').value;
    const state = $('collegeStateFilter').value;

    const filtered = COLLEGES_DB.filter(c => {
        const matchesQ = !q || c.name.toLowerCase().includes(q) || c.location.toLowerCase().includes(q);
        const matchesType = filter === 'all' || c.type === filter;
        const matchesState = state === 'all' || c.state === state;
        return matchesQ && matchesType && matchesState;
    });

    const total = filtered.length;
    const totalPages = Math.max(1, Math.ceil(total / COLLEGES_PER_PAGE));
    if (page < 1) page = 1;
    if (page > totalPages) page = totalPages;
    const start = (page - 1) * COLLEGES_PER_PAGE;
    const pageItems = filtered.slice(start, start + COLLEGES_PER_PAGE);

    if (pageItems.length === 0) grid.innerHTML = '<div class="card">No colleges found</div>';
    else {
        pageItems.forEach(col => {
            const el = document.createElement('div');
            el.className = 'card';
            el.innerHTML = `
        <h3>${col.name}</h3>
        <p class="small">${col.state} • ${col.type}</p>
        <p class="small">${col.location || ''}</p>
        <div style="margin-top:8px">
          <a href="${col.website}" target="_blank"><button>Official Site</button></a>
          <button onclick="saveCollege(event,'${col.id}')">Save</button>
        </div>
      `;
            grid.appendChild(el);
        });
    }

    renderCollegePagination(total, totalPages, page);
}

function renderCollegePagination(totalItems, totalPages, activePage) {
    const el = $('collegePagination');
    if (!el) return;
    if (totalPages <= 1) {
        el.innerHTML = '';
        return;
    }

    let html = '';
    html += `<button class="page-nav" onclick="renderColleges(${Math.max(1,activePage-1)})">‹ Prev</button>`;

    let start = Math.max(1, activePage - 3);
    let end = Math.min(totalPages, activePage + 3);
    if (activePage <= 4) {
        start = 1;
        end = Math.min(totalPages, 7);
    }
    if (activePage > totalPages - 4) {
        start = Math.max(1, totalPages - 6);
        end = totalPages;
    }

    if (start > 1) html += `<button class="page-number" onclick="renderColleges(1)">1</button><span class="dot">...</span>`;

    for (let i = start; i <= end; i++) {
        html += `<button class="page-number ${i===activePage?'active':''}" onclick="renderColleges(${i})">${i}</button>`;
    }

    if (end < totalPages) html += `<span class="dot">...</span><button class="page-number" onclick="renderColleges(${totalPages})">${totalPages}</button>`;

    html += `<button class="page-nav" onclick="renderColleges(${Math.min(totalPages,activePage+1)})">Next ›</button>`;
    el.innerHTML = html;
}

function saveCollege(ev, id) {
    ev.preventDefault();
    const user = getLoggedInUser();
    if (!user) {
        alert('Login to save');
        openAuth();
        return;
    }
    const users = getUsers();
    const u = users.find(x => x.id === user.id);
    if (!u.savedColleges) u.savedColleges = [];
    if (!u.savedColleges.includes(id)) {
        u.savedColleges.push(id);
        saveUsers(users);
        setLoggedInUser(u);
        alert('College saved');
        renderDashboard();
    } else alert('Already saved');
}

/* ---------- Courses ---------- */
function renderCourses() {
    const grid = $('courseGrid');
    grid.innerHTML = '';
    const q = $('courseSearch').value.trim().toLowerCase();
    const filter = $('courseFilter').value;

    const list = COURSES_DB.filter(c => {
        const matchesQ = !q || c.name.toLowerCase().includes(q) || (c.eligibility && c.eligibility.toLowerCase().includes(q));
        const matchesF = filter === 'all' || c.type === filter;
        return matchesQ && matchesF;
    });

    if (list.length === 0) {
        grid.innerHTML = '<div class="card">No courses found</div>';
        return;
    }

    list.forEach(c => {
        const el = document.createElement('div');
        el.className = 'card';
        el.innerHTML = `
      <h3>${c.name}</h3>
      <p class="small">${c.duration} • ${c.eligibility}</p>
      <div style="margin-top:10px">
        <button onclick="saveCourse('${c.id}')">Save Course</button>
      </div>
    `;
        grid.appendChild(el);
    });
}

function saveCourse(id) {
    const user = getLoggedInUser();
    if (!user) {
        alert('Login to save course');
        openAuth();
        return;
    }
    const users = getUsers();
    const u = users.find(x => x.id === user.id);
    if (!u.savedCourses) u.savedCourses = [];
    if (!u.savedCourses.includes(id)) {
        u.savedCourses.push(id);
        saveUsers(users);
        setLoggedInUser(u);
        alert('Course saved');
        renderDashboard();
    } else alert('Already saved');
}

/* ---------- Assessment ---------- */
const ASSESS_QUESTIONS = [{
        q: 'Do you enjoy solving logical problems / puzzles?',
        key: 'tech'
    },
    {
        q: 'Do you like programming or building apps?',
        key: 'tech'
    },
    {
        q: 'Do you enjoy working with numbers and statistics?',
        key: 'tech'
    },
    {
        q: 'Do you enjoy drawing, sketching or designing visuals?',
        key: 'design'
    },
    {
        q: 'Are you creative and enjoy arts/visual work?',
        key: 'design'
    },
    {
        q: 'Do you like to help and care for people physically?',
        key: 'medical'
    },
    {
        q: 'Are you interested in human biology, health and wellness?',
        key: 'medical'
    },
    {
        q: 'Do you enjoy reading, debating and arguing points?',
        key: 'law'
    },
    {
        q: 'Are you good at persuasive speaking and negotiation?',
        key: 'law'
    },
    {
        q: 'Do you want to run or manage a business?',
        key: 'business'
    },
    {
        q: 'Do you enjoy teaching and explaining topics to others?',
        key: 'teaching'
    },
    {
        q: 'Are you fascinated by aircraft and flying?',
        key: 'aviation'
    },
    {
        q: 'Do you like discipline, fitness and leadership roles?',
        key: 'defence'
    },
    {
        q: 'Do you enjoy storytelling, music, or performing arts?',
        key: 'arts'
    },
    {
        q: 'Do you prefer working outdoors or with structures (architecture)?',
        key: 'design'
    }
];

function renderAssessmentForm() {
    const form = $('assessmentForm');
    form.innerHTML = '';
    ASSESS_QUESTIONS.forEach((item, i) => {
        const wrapper = document.createElement('div');
        wrapper.innerHTML = `
      <p>${i+1}. ${item.q}</p>
      <select id="ans${i}">
        <option value="0">Not really</option>
        <option value="1">A little</option>
        <option value="2">Yes</option>
      </select>
    `;
        form.appendChild(wrapper);
    });
}

function submitAssessment() {
    const scores = {};
    ASSESS_QUESTIONS.forEach((item, i) => {
        const val = Number($('ans' + i).value);
        if (val > 0) scores[item.key] = (scores[item.key] || 0) + val;
    });

    const entries = Object.entries(scores).sort((a, b) => b[1] - a[1]);
    if (entries.length === 0) {
        alert('Please answer at least one question');
        return;
    }
    const top = entries[0][0];
    const second = entries[1] ? entries[1][0] : null;

    const suggestions = CAREERS_DB.filter(c => c.field === top).slice(0, 4);

    const user = getLoggedInUser();
    const attempt = {
        id: 'a' + Date.now(),
        date: new Date().toISOString(),
        top,
        second,
        raw: scores,
        suggestions: suggestions.map(s => s.id)
    };
    if (user) {
        const users = getUsers();
        const u = users.find(x => x.id === user.id);
        if (!u.history) u.history = [];
        u.history.unshift(attempt);
        saveUsers(users);
        setLoggedInUser(u);
    } else {
        const ghist = LS.get('cp_guest_history', []);
        ghist.unshift(attempt);
        LS.set('cp_guest_history', ghist);
    }

    const resultBox = $('assessmentResult');
    resultBox.style.display = 'block';
    resultBox.innerHTML = `
    <h3>Recommended field: ${top.toUpperCase()}</h3>
    ${ second ? `<p>Also suitable: ${second.toUpperCase()}</p>` : '' }
    <p class="small">Suggested careers:</p>
    <div class="grid">${ suggestions.map(s => `<div class="card"><strong>${s.title}</strong><p class="small">${s.short}</p><div style="margin-top:8px"><button onclick="openCareer('${s.id}')">View</button></div></div>`).join('') }</div>
  `;
    navigate('assessment');
    renderDashboard();
}

function resetAssessment() {
    renderAssessmentForm();
    $('assessmentResult').style.display = 'none';
}

/* ---------- Dashboard ---------- */
function renderDashboard() {
    const user = getLoggedInUser();
    $('userWelcome').innerHTML = user ? `<div class="small">Welcome <strong>${user.name}</strong></div>` : `<div class="small">Not logged in. <a href="#" onclick="openAuth()">Login</a></div>`;
    const favEl = $('favList');
    if (!user || !user.favorites || user.favorites.length === 0) favEl.innerHTML = '<div class="small">No favorite careers yet.</div>';
    else {
        const list = user.favorites.map(id => {
            const c = CAREERS_DB.find(x => x.id === id);
            return `<div class="small">• ${c?c.title:id} <button onclick="removeFavorite('${id}')">Remove</button></div>`
        }).join('');
        favEl.innerHTML = list;
    }
    const histEl = $('historyList');
    const hist = user && user.history ? user.history : (LS.get('cp_guest_history', []));
    if (!hist || hist.length === 0) histEl.innerHTML = '<div class="small">No assessment attempts yet.</div>';
    else histEl.innerHTML = hist.map(h => `<div class="small"><strong>${(new Date(h.date)).toLocaleString()}</strong> — ${h.top} ${h.second?(' / '+h.second):''}</div>`).join('');
    const accEl = $('accountInfo');
    if (!user) accEl.innerHTML = '<div class="small">No account</div>';
    else accEl.innerHTML = `<div class="small">Name: ${user.name}<br/>Email: ${user.email}<br/>Interests: ${(user.interests||[]).join(', ') || '—'}</div>`;
}

function removeFavorite(id) {
    const user = getLoggedInUser();
    if (!user) return;
    const users = getUsers();
    const u = users.find(x => x.id === user.id);
    u.favorites = (u.favorites || []).filter(x => x !== id);
    saveUsers(users);
    setLoggedInUser(u);
    renderDashboard();
}

/* ---------- Helpers ---------- */
function updateAuthButton() {
    const btn = $('authBtn');
    const user = getLoggedInUser();
    if (user) {
        btn.innerText = user.name.split(' ')[0] + ' ▾';
        btn.onclick = () => navigate('dashboard');
    } else {
        btn.innerText = 'Login / Register';
        btn.onclick = openAuth;
    }
}

/* ---------- Init ---------- */
(function init() {
    document.addEventListener('click', function(ev) {
        if (ev.target.closest('.mobile-menu') === null && ev.target.closest('.hamburger') === null && window.innerWidth <= 880) {
            $('mobileMenu').style.display = 'none';
        }
    });
    if (!getUsers() || getUsers().length === 0) {
        const demo = {
            id: 'u_demo',
            name: 'Demo User',
            email: 'demo@example.com',
            pass: 'demo123',
            interests: ['tech', 'business'],
            favorites: [],
            history: [],
            savedColleges: [],
            savedCourses: []
        };
        saveUsers([demo]);
    }
    updateAuthButton();
    navigate('home');
})();