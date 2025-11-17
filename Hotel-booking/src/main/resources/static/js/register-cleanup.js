/* =======================================================
   🧾 REGISTER PAGE CLEANUP SCRIPT
   -------------------------------------------------------
   ✅ Clears any saved values (name, email, password)
   ✅ Prevents browser autofill on refresh/back
   ✅ Resets floating labels to placeholder position
======================================================= */

window.addEventListener('DOMContentLoaded', function () {
  const name  = document.getElementById('name');
  const email = document.getElementById('email');
  const pass  = document.getElementById('password');
  const cpass = document.getElementById('confirmPassword');

  // 🔹 Step 1: Clear any remembered values immediately
  if (name)  name.value  = '';
  if (email) email.value = '';
  if (pass)  pass.value  = '';
  if (cpass) cpass.value = '';

  // 🔹 Step 2: Handle Chrome/Edge delayed autofill (runs slightly after load)
  setTimeout(() => {
    if (name  && name.value.trim()  !== '') name.value  = '';
    if (email && email.value.trim() !== '') email.value = '';
    if (pass  && pass.value.trim()  !== '') pass.value  = '';
    if (cpass && cpass.value.trim() !== '') cpass.value = '';

    // Reset floating label positions
    name?.dispatchEvent(new Event('blur'));
    email?.dispatchEvent(new Event('blur'));
    pass?.dispatchEvent(new Event('blur'));
    cpass?.dispatchEvent(new Event('blur'));
  }, 150);

  console.log("✅ Register form cleared & autofill blocked.");
});
