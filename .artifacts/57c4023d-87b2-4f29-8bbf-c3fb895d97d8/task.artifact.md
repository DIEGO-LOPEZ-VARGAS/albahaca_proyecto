# Tareas: Notificaciones de Caducidad (v4.4) - COMPLETADO

- [x] **Módulo de Notificaciones (NotificationHelper.kt)**
    - [x] Crear clase central para gestionar canales de Android
    - [x] Implementar función para alertas de caducidad
    - [x] Migrar lógica de notificación de éxito de recetas
- [x] **Activación en Inicio (MainActivity.kt)**
    - [x] Integrar disparo de notificación en `checkCaducidad`
    - [x] Asegurar solicitud de permisos al arrancar
- [x] **Limpieza de UI (RecetaView.kt)**
    - [x] Eliminar lógica duplicada de notificaciones
    - [x] Llamar al nuevo helper para avisos de guardado
- [x] **Verificación**
    - [x] Validar que la notificación aparezca en la barra de estado
    - [x] Confirmar que el permiso se solicita correctamente en Android 13+
