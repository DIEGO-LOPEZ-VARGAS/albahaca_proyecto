# Plan de Notificaciones de Caducidad (Versión Ligera) v4.4

Este plan implementa las notificaciones del sistema para productos por caducar, pero eliminando cualquier proceso de vigilancia en segundo plano. La app solo avisará cuando esté abierta.

## Cambios Propuestos

### 1. Centralización de Notificaciones

#### [NUEVO] `NotificationHelper.kt`
*   Crear una utilidad que gestione los canales de Android.
*   Función `enviarAlertaCaducidad(context, listaProductos)`: Genera una notificación real en la barra de estado con el resumen de lo que va a vencer.

### 2. Activación al Iniciar la App

#### [MODIFICAR] [MainActivity.kt](file:///C:/Users/Darkar/StudioProjects/albahaca_proyecto/androidApp/src/main/kotlin/com/example/albahacaproyecto/MainActivity.kt)
*   **Permisos**: Solicitar permiso de notificaciones (Android 13+) nada más entrar.
*   **Lógica de Aviso**: Dentro de la función `checkCaducidad()`, además de mostrar el cuadro de texto (AlertDialog), ahora también disparará una **notificación oficial** en el celular.
*   **Sin Segundo Plano**: Se elimina cualquier idea de usar `WorkManager`. La app solo revisa cuando tú la abres.

### 3. Limpieza de Código

#### [MODIFICAR] [RecetaView.kt](file:///C:/Users/Darkar/StudioProjects/albahaca_proyecto/androidApp/src/main/kotlin/com/example/albahacaproyecto/RecetaView.kt)
*   Mover la función `enviarNotificacionExito` al nuevo `NotificationHelper` para que todo esté ordenado en un solo lugar.

## Beneficios
*   **Privacidad**: No hay procesos ocultos corriendo cuando cierras la app.
*   **Visibilidad**: Si minimizas la app para hacer otra cosa, la notificación te recordará lo que tienes que cocinar antes de que caduque.
*   **Orden**: El código de notificaciones estará separado de las pantallas.

## Plan de Verificación
1.  **Aceptación de Permisos**: Abrir la app y confirmar que pide permiso para notificar.
2.  **Disparo de Alerta**: Tener un producto que caduque en 1 o 2 días y abrir la app.
3.  **Resultado**: Debe aparecer el aviso arriba en el celular (junto al reloj y la batería).

¿Te parece bien este enfoque más sencillo y respetuoso con el uso de tu celular?
