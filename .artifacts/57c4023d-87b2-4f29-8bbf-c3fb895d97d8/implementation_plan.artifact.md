# Plan de Emergencia: Limpieza de Conflictos de Git

Este plan soluciona los errores de compilación masivos causados por marcas de conflicto (`<<<<<<<`, `=======`, `>>>>>>>`) en el código fuente.

## Problemas Detectados

### 1. Archivos Corruptos por Merge/Stash
*   **MainActivity.kt**: Código duplicado en el bloque `setContent`.
*   **loggin.kt**: El disparador de huella y el botón de login están entrelazados con marcas de Git.
*   **ListaComprasScreen.kt**: La lógica de sincronización y el diseño de la lista tienen bloques de código en conflicto.

### 2. Duplicidad de Declaraciones
*   Variables como `filtro` e `isLoggedIn` aparecen dos veces debido a la mezcla de versiones.

## Cambios Propuestos

### Componente: Android App (Limpieza Quirúrgica)

#### [MODIFICAR] [MainActivity.kt](file:///C:/Users/Darkar/StudioProjects/albahaca_proyecto/androidApp/src/main/kotlin/com/example/albahacaproyecto/MainActivity.kt)
*   Remover marcas de conflicto.
*   Mantener la lógica de sesión persistente corregida.

#### [MODIFICAR] [loggin.kt](file:///C:/Users/Darkar/StudioProjects/albahaca_proyecto/androidApp/src/main/kotlin/com/example/albahacaproyecto/loggin.kt)
*   Limpiar el `LaunchedEffect` de la huella.
*   Restaurar el botón de login con mensajes de error detallados.

#### [MODIFICAR] [ListaComprasScreen.kt](file:///C:/Users/Darkar/StudioProjects/albahaca_proyecto/androidApp/src/main/kotlin/com/example/albahacaproyecto/ListaComprasScreen.kt)
*   Unificar la pantalla de compras usando el `OfflineRepository` persistente.
*   Eliminar el código repetido de la tarjeta de sincronización.

## Plan de Verificación
1.  **Limpieza Visual**: Verificar que no queden marcas `<<<<`, `====` o `>>>>` en ningún archivo.
2.  **Compilación**: Ejecutar build y asegurar que el estado sea **SUCCESS**.
3.  **Funcionalidad**: Probar que la App abre, pide huella y muestra la lista de compras sin errores.
