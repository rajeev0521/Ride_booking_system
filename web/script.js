// ===== DOM Elements =====
const loginBtn = document.getElementById('loginBtn');
const registerBtn = document.getElementById('registerBtn');
const loginModal = document.getElementById('loginModal');
const registerModal = document.getElementById('registerModal');
const closeLogin = document.getElementById('closeLogin');
const closeRegister = document.getElementById('closeRegister');
const showRegister = document.getElementById('showRegister');
const showLogin = document.getElementById('showLogin');

// ===== Modal Functions =====
function openModal(modal) {
    modal.classList.add('active');
    document.body.style.overflow = 'hidden';
}

function closeModal(modal) {
    modal.classList.remove('active');
    document.body.style.overflow = '';
}

// ===== Event Listeners =====
loginBtn.addEventListener('click', () => openModal(loginModal));
registerBtn.addEventListener('click', () => openModal(registerModal));

closeLogin.addEventListener('click', () => closeModal(loginModal));
closeRegister.addEventListener('click', () => closeModal(registerModal));

showRegister.addEventListener('click', (e) => {
    e.preventDefault();
    closeModal(loginModal);
    setTimeout(() => openModal(registerModal), 200);
});

showLogin.addEventListener('click', (e) => {
    e.preventDefault();
    closeModal(registerModal);
    setTimeout(() => openModal(loginModal), 200);
});

// Close modal on outside click
window.addEventListener('click', (e) => {
    if (e.target === loginModal) closeModal(loginModal);
    if (e.target === registerModal) closeModal(registerModal);
});

// ===== Form Submissions =====
document.getElementById('loginForm').addEventListener('submit', (e) => {
    e.preventDefault();
    const email = document.getElementById('loginEmail').value;
    const password = document.getElementById('loginPassword').value;

    // Simulate login
    console.log('Login attempt:', { email, password });
    alert('Login successful! Welcome back.');
    closeModal(loginModal);
});

document.getElementById('registerForm').addEventListener('submit', (e) => {
    e.preventDefault();
    const formData = {
        name: document.getElementById('regName').value,
        email: document.getElementById('regEmail').value,
        phone: document.getElementById('regPhone').value,
        password: document.getElementById('regPassword').value,
        licenceNo: document.getElementById('licenceNo').value,
        licenceExp: document.getElementById('licenceExp').value
    };

    console.log('Registration:', formData);
    alert('Account created successfully! Please login.');
    closeModal(registerModal);
    openModal(loginModal);
});

document.getElementById('createRideForm').addEventListener('submit', (e) => {
    e.preventDefault();
    const rideData = {
        source: document.getElementById('source').value,
        destination: document.getElementById('destination').value,
        seats: document.getElementById('seats').value,
        fare: document.getElementById('fare').value,
        carBrand: document.getElementById('carBrand').value,
        carModel: document.getElementById('carModel').value,
        carNumber: document.getElementById('carNumber').value,
        rideDate: document.getElementById('rideDate').value
    };

    console.log('Create Ride:', rideData);
    alert('Ride created successfully!');
    e.target.reset();
});

// ===== Book Ride Buttons =====
document.querySelectorAll('.btn-book').forEach(btn => {
    btn.addEventListener('click', () => {
        alert('Please login to book this ride.');
        openModal(loginModal);
    });
});

// ===== Cancel Booking Buttons =====
document.querySelectorAll('.btn-cancel').forEach(btn => {
    btn.addEventListener('click', () => {
        if (confirm('Are you sure you want to cancel this booking?')) {
            alert('Booking cancelled successfully!');
            btn.closest('.booking-card').style.opacity = '0.5';
            btn.disabled = true;
            btn.textContent = 'Cancelled';
        }
    });
});

// ===== Smooth Scroll for Nav Links =====
document.querySelectorAll('.nav-link').forEach(link => {
    link.addEventListener('click', (e) => {
        e.preventDefault();
        const targetId = link.getAttribute('href');
        const target = document.querySelector(targetId);

        if (target) {
            target.scrollIntoView({ behavior: 'smooth' });

            // Update active state
            document.querySelectorAll('.nav-link').forEach(l => l.classList.remove('active'));
            link.classList.add('active');
        }
    });
});

// ===== Search Functionality =====
document.querySelector('.btn-search').addEventListener('click', () => {
    const from = document.getElementById('searchFrom').value;
    const to = document.getElementById('searchTo').value;

    if (from || to) {
        console.log('Searching rides:', { from, to });
        alert(`Searching for rides from "${from}" to "${to}"...`);
        document.querySelector('#rides').scrollIntoView({ behavior: 'smooth' });
    } else {
        alert('Please enter pickup location or destination.');
    }
});

// ===== Update Active Nav on Scroll =====
const sections = document.querySelectorAll('section[id]');
window.addEventListener('scroll', () => {
    const scrollY = window.pageYOffset;

    sections.forEach(section => {
        const sectionHeight = section.offsetHeight;
        const sectionTop = section.offsetTop - 100;
        const sectionId = section.getAttribute('id');
        const navLink = document.querySelector(`.nav-link[href="#${sectionId}"]`);

        if (scrollY > sectionTop && scrollY <= sectionTop + sectionHeight) {
            document.querySelectorAll('.nav-link').forEach(l => l.classList.remove('active'));
            if (navLink) navLink.classList.add('active');
        }
    });
});
