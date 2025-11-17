/* =======================================================
   REGISTER VALIDATION - prevent stuck loading on mismatch
   - If mismatch: show error, stop submit, clear loading, reset guard
   - If match   : allow normal submit; app.js applies loading state
======================================================= */

document.addEventListener("DOMContentLoaded", () => {
  const form = document.getElementById("registerForm");
  if (!form) return;

  const pass = document.getElementById("password");
  const cpass = document.getElementById("confirmPassword");
  const button = form.querySelector("button[type='submit']");

  // Grab or create error box
  let errorBox = document.getElementById("passwordError");
  if (!errorBox) {
    errorBox = document.createElement("div");
    errorBox.id = "passwordError";
    errorBox.style.color = "#dc3545";
    errorBox.style.fontWeight = "600";
    errorBox.style.marginTop = "12px";
    errorBox.style.textAlign = "center";
    errorBox.style.display = "none";
    form.appendChild(errorBox);
  }

  function clearErrorVisuals() {
    if (pass) pass.style.border = "1.8px solid var(--border)";
    if (cpass) cpass.style.border = "1.8px solid var(--border)";
    errorBox.style.display = "none";
    errorBox.textContent = "";
    if (button) {
      button.classList.remove("loading");
      button.disabled = false;
    }
    form.dataset.submitted = "false";
  }

  pass?.addEventListener("input", clearErrorVisuals);
  cpass?.addEventListener("input", clearErrorVisuals);

  form.addEventListener("submit", (e) => {
    const p1 = pass?.value?.trim() ?? "";
    const p2 = cpass?.value?.trim() ?? "";

    // Validate first; if invalid, stop submit and bubbling
    if (p1 === "" || p2 === "" || p1 !== p2) {
      e.preventDefault();
      if (typeof e.stopImmediatePropagation === "function") e.stopImmediatePropagation();
      e.stopPropagation();

      errorBox.textContent = "Passwords do not match!";
      errorBox.style.display = "block";

      if (pass) pass.style.border = "2px solid #dc3545";
      if (cpass) cpass.style.border = "2px solid #dc3545";
      cpass?.focus();

      if (button) {
        button.classList.remove("loading");
        button.disabled = false;
      }
      form.dataset.submitted = "false"; // allow retry
      return false;
    }

    // Valid: let app.js handle loading via its global submit listener
    errorBox.style.display = "none";
    if (pass) pass.style.border = "2px solid #28a745";
    if (cpass) cpass.style.border = "2px solid #28a745";
  });
});

