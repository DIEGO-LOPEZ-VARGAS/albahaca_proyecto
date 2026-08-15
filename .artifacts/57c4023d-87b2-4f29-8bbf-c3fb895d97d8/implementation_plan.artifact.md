# Plan de Reparación: Selector de Fecha y Visibilidad v4.2.1

Este plan soluciona el fallo donde la fecha de caducidad no se muestra o no se puede seleccionar correctamente.

## Problemas Detectados

### 1. Intercepción de Eventos de Toque
*   **Causa**: El `OutlinedTextField` consume los eventos de clic para el foco, impidiendo que el `Modifier.clickable` del padre (el Column) se active de manera confiable.
*   **Solución**: Utilizar `enabled = false` con colores personalizados o un `Box` superpuesto para garantizar la captura del clic.

### 2. Error de Desfase de Fecha (Timezone)
*   **Causa**: `selectedDateMillis` devuelve UTC, pero `SimpleDateFormat` usa la zona horaria local, lo que puede causar desfases de un día.
*   **Solución**: Forzar `TimeZone("UTC")` en el formateador de fecha.

### 3. Falta de Retroalimentación en la Lista
*   **Causa**: La tarjeta del producto (`FoodItemCard`) solo muestra los días restantes, no la fecha exacta seleccionada.
*   **Solución**: Añadir la fecha de caducidad explícita en el diseño de la tarjeta.

## Cambios Propuestos

### Componente: Android App

#### [MODIFICAR] [FrutaFormView.kt](file:///C:/Users/Darkar/StudioProjects/albahaca_proyecto/androidApp/src/main/kotlin/com/example/albahacaproyecto/FrutaFormView.kt)
*   Refactorizar `VerduritasInputField` para manejar clics de forma robusta cuando es `readOnly`.
*   Asegurar que la selección de fecha use UTC.
*   Actualizar `FoodItemCard` para mostrar la fecha de caducidad guardada.

## Plan de Verificación
1.  **Prueba de Toque**: Verificar que al tocar cualquier parte del campo "Caducidad", el calendario se abra instantáneamente.
2.  **Prueba de Exactitud**: Seleccionar el día de hoy en el calendario y verificar que aparezca el día correcto en el campo.
3.  **Prueba de Visualización**: Confirmar que los productos en la lista ahora muestran su fecha (ej. "Caduca: 2026-08-30").
