/* =======================================================
   🔒 LOGIN PAGE CLEANUP SCRIPT (Strong Version)
   Prevents browser from auto-filling saved credentials
======================================================= */

window.addEventListener('DOMContentLoaded', function () {
  const email = document.getElementById('email');
  const pass  = document.getElementById('password');

  // Immediately clear any prefilled values
  if (email) email.value = '';
  if (pass)  pass.value  = '';

  // After short delay (covers Chrome autofill injection)
  setTimeout(() => {
    if (email && email.value.trim() !== '') email.value = '';
    if (pass && pass.value.trim() !== '') pass.value = '';

    email?.dispatchEvent(new Event('blur'));
    pass?.dispatchEvent(new Event('blur'));
  }, 120);

  console.log("✅ Autofill prevention active – form cleared on load.");
});
