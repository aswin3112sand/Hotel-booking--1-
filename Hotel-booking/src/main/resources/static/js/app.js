(function parallaxHero(){
  const hero = document.querySelector('[data-parallax]');
  if(!hero) return;
  window.addEventListener('scroll', () => {
    const offset = window.scrollY * 0.15;
    hero.style.transform = `translateY(${offset}px)`;
  });
})();

(function floatIcons(){
  const icons = document.querySelectorAll('.floating-icons span');
  icons.forEach((icon, idx) => {
    icon.style.animationDelay = `${idx * 0.8}s`;
  });
})();

(function fadeAlerts(){
  document.querySelectorAll('.alert').forEach((el) => {
    setTimeout(() => {
      el.style.transition = 'opacity .5s ease';
      el.style.opacity = '0';
      setTimeout(() => el.remove(), 500);
    }, 3500);
  });
})();

(function bookingChart(){
  const canvas = document.getElementById('bookingChart');
  if(!canvas) return;
  const ctx = canvas.getContext('2d');
  const bookings = Number(canvas.dataset.bookings || 0);
  const users = Number(canvas.dataset.users || 0);
  const max = Math.max(bookings, users, 10);
  const barWidth = 120;
  const gap = 80;
  const height = canvas.height - 30;
  const colors = ['#f9c46b', '#4cc9f0'];
  [bookings, users].forEach((value, index) => {
    const scaled = (value / max) * height;
    const x = 80 + index * (barWidth + gap);
    const y = canvas.height - scaled;
    ctx.fillStyle = colors[index];
    ctx.beginPath();
    if (ctx.roundRect) {
      ctx.roundRect(x, y, barWidth, scaled, 16);
    } else {
      ctx.rect(x, y, barWidth, scaled);
    }
    ctx.fill();
    ctx.fillStyle = '#fff';
    ctx.font = '16px Poppins';
    ctx.fillText(value, x + barWidth / 3, y - 10);
  });
})();

(function roomFilters(){
  const filterForm = document.getElementById('roomFilters');
  const grid = document.getElementById('roomGrid');
  if(!filterForm || !grid) return;
  const cards = Array.from(grid.querySelectorAll('.room-card'));
  const searchInput = filterForm.querySelector('[data-filter="search"]');
  const cityInput = filterForm.querySelector('[data-filter="city"]');
  const typeInput = filterForm.querySelector('[data-filter="type"]');
  const minInput = filterForm.querySelector('[data-filter="minPrice"]');
  const maxInput = filterForm.querySelector('[data-filter="maxPrice"]');

  const applyFilters = () => {
    const search = searchInput?.value.trim().toLowerCase() || '';
    const city = cityInput?.value.trim().toLowerCase() || '';
    const type = typeInput?.value || '';
    const minPrice = Number(minInput?.value);
    const maxPrice = Number(maxInput?.value);

    cards.forEach(card => {
      const price = Number(card.dataset.price || 0);
      const cardCity = (card.dataset.city || '').toLowerCase();
      const cardTitle = (card.dataset.title || '').toLowerCase();
      const cardType = card.dataset.type || '';
      const matchesSearch = !search || cardTitle.includes(search) || cardCity.includes(search);
      const matchesCity = !city || cardCity.includes(city);
      const matchesType = !type || cardType === type;
      const matchesMin = isNaN(minPrice) || price >= minPrice;
      const matchesMax = isNaN(maxPrice) || price <= maxPrice;
      card.classList.toggle('room-card--hidden', !(matchesSearch && matchesCity && matchesType && matchesMin && matchesMax));
    });
  };

  [searchInput, cityInput, typeInput, minInput, maxInput].forEach(input => {
    input?.addEventListener('input', applyFilters);
  });

  document.getElementById('jumpToFilters')?.addEventListener('click', () => {
    filterForm.scrollIntoView({ behavior: 'smooth', block: 'start' });
    searchInput?.focus();
  });
})();

(function bookingModal(){
  const modal = document.getElementById('bookingModal');
  const backdrop = document.getElementById('bookingBackdrop');
  const form = document.getElementById('modalBookingForm');
  if(!modal || !form || !backdrop) return;

  const titleEl = modal.querySelector('#modalTitle');
  const availabilityEl = modal.querySelector('#modalAvailability');
  const roomIdInput = form.querySelector('[name="roomId"]');
  const toggleVisibility = (visible) => {
    modal.classList.toggle('modal--visible', visible);
    backdrop.classList.toggle('modal--visible', visible);
  };

  const close = () => toggleVisibility(false);

  document.querySelectorAll('.js-open-booking').forEach(btn => {
    btn.addEventListener('click', () => {
      const roomId = btn.dataset.roomId;
      const roomTitle = btn.dataset.roomTitle;
      const available = btn.dataset.roomAvailable === 'true';
      form.action = `/rooms/${roomId}/book`;
      roomIdInput.value = roomId;
      titleEl.textContent = `Reserve ${roomTitle}`;
      availabilityEl.textContent = available ? 'Available now—confirm your dates.' : 'Limited inventory. Confirm soon to lock it in.';
      toggleVisibility(true);
    });
  });

  backdrop.addEventListener('click', close);
  modal.querySelectorAll('[data-close-modal]').forEach(el => el.addEventListener('click', close));
  document.addEventListener('keydown', event => {
    if(event.key === 'Escape') {
      close();
    }
  });
})();

(function fallbackImages(){
  const FALLBACK = 'https://images.unsplash.com/photo-1522708323590-d24dbb6b0267?auto=format&fit=crop&w=1200&q=80';
  document.querySelectorAll('img[data-fallback]').forEach(img => {
    img.addEventListener('error', () => {
      if (img.dataset.fallbackApplied === 'true') return;
      const custom = img.getAttribute('data-fallback') || FALLBACK;
      img.dataset.fallbackApplied = 'true';
      img.src = custom;
    }, { once: false });
  });
})();
