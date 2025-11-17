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
