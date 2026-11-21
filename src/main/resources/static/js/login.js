// Mostrar u ocultar la contraseña
    document.getElementById('verClave').addEventListener('click', function () {
        const campo = document.getElementById('password');
        const visible = campo.type === 'text';
        campo.type = visible ? 'password' : 'text';
        this.textContent = visible ? 'Ver' : 'Ocultar';
    });
