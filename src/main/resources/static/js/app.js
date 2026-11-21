// Pide confirmacion antes de enviar formularios que tengan data-confirm.
// Sustituye a onsubmit="return confirm(...)", que la CSP bloquea.
document.addEventListener('submit', function (e) {
    var form = e.target;
    var mensaje = form.dataset.confirm;
    if (!mensaje) return;
    mensaje = mensaje.replace('{nombre}', form.dataset.nombre || '');
    if (!confirm(mensaje)) e.preventDefault();
});