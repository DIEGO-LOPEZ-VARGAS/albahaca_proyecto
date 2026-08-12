# Plan de Mejora UX: Selector de Fecha (DatePicker)

Este plan tiene como objetivo facilitar el ingreso de la fecha de caducidad sustituyendo la escritura manual por un selector de calendario (DatePicker) moderno.

## Cambios Propuestos

### 1. Integración de Material 3 DatePicker (Android)

#### [MODIFICAR] [FrutaFormView.kt](file:///C:/Users/Darkar/StudioProjects/albahaca_proyecto/androidApp/src/main/kotlin/com/example/albahacaproyecto/FrutaFormView.kt)
*   **Estado**: Añadir `DatePickerState` para gestionar la selección de fecha.
*   **Interfaz**:
    *   Hacer que el campo "Caducidad" sea de solo lectura para evitar errores de formato.
    *   Añadir un icono de calendario que, al pulsarlo, abra un `DatePickerDialog`.
    *   Convertir automáticamente la selección del calendario al formato `YYYY-MM-DD` que requiere el servidor.
*   **Diálogo de Edición**: Aplicar la misma lógica en `EditarFrutaDialog` para mantener la consistencia.

## Beneficios
*   **Cero Errores**: Se elimina la posibilidad de escribir fechas con formato incorrecto (ej. meses mayores a 12).
*   **Rapidez**: Es mucho más rápido tocar un día en el calendario que escribir 10 caracteres.
*   **Estética**: La app se verá más profesional usando los componentes nativos de Material 3.

## Plan de Verificación
1.  **Registro Nuevo**: Tocar el campo de caducidad, elegir una fecha en el calendario y verificar que el campo se llena solo.
2.  **Edición**: Abrir el diálogo de editar y cambiar la fecha usando el calendario.
3.  **Formato**: Confirmar que al guardar, la fecha llega al servidor como `YYYY-MM-DD`.

**¿Deseas que implemente el selector de calendario ahora mismo?**
