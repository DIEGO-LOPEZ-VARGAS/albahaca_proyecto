# Notificaciones Reales de Caducidad v4.4

He implementado el sistema de notificaciones oficiales de Android para avisarte sobre los productos que están por vencer. Este sistema es directo y respetuoso con tu celular: solo se activa cuando tú abres la app.

## 🛠️ Mejoras Implementadas

### 1. Avisos en la Barra de Estado
*   **Antes**: Solo aparecía un mensaje dentro de la app que podías ignorar fácilmente.
*   **Ahora**: La aplicación dispara una **notificación real de Android**. Verás el icono de alerta arriba en tu celular, junto al reloj. Al deslizar hacia abajo, podrás leer exactamente qué productos vencen pronto.

### 2. Gestión Centralizada (`NotificationHelper`)
*   He creado un "Centro de Notificaciones" interno. Esto hace que la app sea más rápida y que todos los avisos (ya sea de comida por caducar o de recetas guardadas con éxito) se vean profesionales y uniformes.

### 3. Permisos Inteligentes
*   Si tienes un celular con **Android 13 o superior**, la app te pedirá permiso para mandarte notificaciones nada más abrirla. Esto asegura que la seguridad del sistema no bloquee tus alertas.

---

## 🚀 Cómo Probarlo

1.  Dale a **Run (▶️)** en Android Studio.
2.  **Acepta el permiso** de notificaciones cuando el celular te lo pregunte.
3.  Asegúrate de tener al menos un producto en tu inventario que caduque en los próximos **2 días**.
4.  Cierra la app y vuelve a abrirla.
5.  ¡Mira la barra de estado! Deberías ver el aviso: *"⚠️ ¡Alimentos por Caducar!"*.

> [!NOTE]
> Siguiendo tu petición, **no hay procesos ocultos** en segundo plano. La app solo revisa tus fechas en el momento en que tú decides usarla.

¡Ahora estarás siempre al tanto de lo que hay que cocinar hoy! ¿Qué te parece esta nueva forma de recibir avisos?
