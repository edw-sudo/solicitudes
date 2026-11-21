// Muestra el campo de área o de código de estudiante según el rol elegido.
    (function () {
        const rol = document.getElementById('idRol');
        const grupoArea = document.getElementById('grupoArea');
        const grupoCodigo = document.getElementById('grupoCodigo');
        function actualizar() {
            const opcion = rol.options[rol.selectedIndex];
            const nombre = opcion ? opcion.getAttribute('data-nombre') : null;
            grupoArea.style.display = nombre === 'PERSONAL_ADMINISTRATIVO' ? '' : 'none';
            grupoCodigo.style.display = nombre === 'ESTUDIANTE' ? '' : 'none';
        }
        rol.addEventListener('change', actualizar);
        actualizar();
    })();
