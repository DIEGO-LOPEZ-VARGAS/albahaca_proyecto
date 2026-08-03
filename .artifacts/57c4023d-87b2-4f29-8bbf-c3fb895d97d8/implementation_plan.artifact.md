# Plan de Corrección: CRUD, Cámara y Validación de Datos

Este plan soluciona los fallos de eliminación, edición, cámara e ingreso de datos detectados en la versión v3.1.

## Cambios Propuestos

### 1. Integridad de Datos (CRUD Local + Remoto)

Actualmente, las funciones de borrar y editar solo afectan al servidor, dejando el celular con datos desactualizados.

#### [MODIFICAR] [OfflineRepository.kt](file:///C:/Users/Darkar/StudioProjects/albahaca_proyecto/androidApp/src/main/kotlin/com/example/albahacaproyecto/database/OfflineRepository.kt)
*   **Eliminar**: Actualizar `eliminarFruta` y añadir `eliminarReceta` para que borren el registro de la base de datos Room (`localId`) además de llamar a la API.
*   **Actualizar**: Implementar la actualización local inmediata en `actualizarFruta` y `actualizarReceta`.

### 2. Cámara y Visión IA

La cámara falla probablemente por falta de permisos o por el tamaño excesivo de la imagen.

#### [MODIFICAR] [FrutaFormView.kt](file:///C:/Users/Darkar/StudioProjects/albahaca_proyecto/androidApp/src/main/kotlin/com/example/albahacaproyecto/FrutaFormView.kt)
*   **Permisos**: Añadir `rememberLauncherForActivityResult` para solicitar el permiso de **CÁMARA** antes de abrir el escáner.
*   **Validación**: Verificar que el permiso esté concedido antes de mostrar el `AlertDialog` de la cámara.

#### [MODIFICAR] [CameraPreview.kt](file:///C:/Users/Darkar/StudioProjects/albahaca_proyecto/androidApp/src/main/kotlin/com/example/albahacaproyecto/CameraPreview.kt)
*   **Optimización**: Añadir una función de compresión para que la foto capturada no pese varios megabytes, evitando errores de "Timeout" o "Payload Too Large" al enviarla a Gemini.

### 3. Validación de Entradas (Solo Números)

#### [MODIFICAR] [RecetaView.kt](file:///C:/Users/Darkar/StudioProjects/albahaca_proyecto/androidApp/src/main/kotlin/com/example/albahacaproyecto/RecetaView.kt) y [FrutaFormView.kt](file:///C:/Users/Darkar/StudioProjects/albahaca_proyecto/androidApp/src/main/kotlin/com/example/albahacaproyecto/FrutaFormView.kt)
*   **Teclado Numérico**: Configurar `keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)` en los campos de **Cantidad**.
*   **Filtro de Texto**: Modificar los `onValueChange` para que ignoren cualquier carácter que no sea un número en el campo de cantidad.
*   **Fechas**: Asegurar que el campo de caducidad solo permita números y guiones (`YYYY-MM-DD`).

## Verificación Plan

### Manual Verification
1.  **CRUD**: Borrar una fruta y verificar que desaparece al instante. Editar una receta y ver el cambio reflejado sin recargar.
2.  **Cámara**: Al tocar el icono de cámara, la app debe pedir permiso. Al tomar la foto, debe ser rápida al subirla (gracias a la compresión).
3.  **Validación**: Intentar escribir letras en el campo de "Cantidad" y verificar que no aparecen.
