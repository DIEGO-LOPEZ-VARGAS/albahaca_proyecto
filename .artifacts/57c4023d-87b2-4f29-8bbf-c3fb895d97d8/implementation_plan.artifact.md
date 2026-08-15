# Plan de Corrección de Duplicados v4.3

Este plan soluciona el problema de duplicidad de datos al asegurar un vínculo sólido entre los registros locales (Room) y los remotos (Railway) mediante el retorno de IDs desde el servidor.

## Problemas Detectados

### 1. Desconexión de Identidad
*   **Causa**: Al guardar un producto o receta, el servidor responde con un texto ("Guardado") en lugar del objeto creado. La App no sabe qué ID le asignó el servidor.
*   **Consecuencia**: En la siguiente sincronización, la App descarga el mismo objeto, pero como no conoce su ID, lo inserta como uno nuevo en el celular.

### 2. Lógica de Guardado "Ciega"
*   **Causa**: La App inserta en la base de datos local pero no recupera el `localId` generado para vincularlo con el futuro `remoteId`.

## Cambios Propuestos

### A. Repositorio Backend (Railway)

#### [MODIFICAR] [ProductoRepository.kt](file:///C:/Users/Darkar/StudioProjects/backend/src/main/kotlin/com/example/repository/ProductoRepository.kt) y [RecetaRepository.kt](file:///C:/Users/Darkar/StudioProjects/backend/src/main/kotlin/com/example/repository/RecetaRepository.kt)
*   Cambiar los métodos `add...` para que devuelvan el objeto completo recién insertado (usando `resultedValues`).

#### [MODIFICAR] [ProductRoutes.kt](file:///C:/Users/Darkar/StudioProjects/backend/src/main/kotlin/com/example/routes/ProductRoutes.kt)
*   Actualizar los endpoints `POST /api/frutas`, `POST /api/recetas` y `POST /api/compras` para que devuelvan el objeto en formato JSON.

### B. Aplicación Android (Lógica de Sincronización)

#### [MODIFICAR] [OfflineRepository.kt](file:///C:/Users/Darkar/StudioProjects/albahaca_proyecto/androidApp/src/main/kotlin/com/example/albahacaproyecto/database/OfflineRepository.kt)
*   **Refactorizar `guardar...`**:
    1.  Guardar localmente y obtener el `localId`.
    2.  Subir al servidor.
    3.  Al recibir la respuesta exitosa con el ID remoto, actualizar el registro local para "unirlos".
*   **Eliminar Duplicados Existentes**: Añadir una limpieza automática de items con `remoteId = 0` que ya existan en la nube por nombre/título durante la sincronización inicial.

## Plan de Verificación
1.  **Cero Duplicados**: Crear un producto con internet lento. Verificar que aparece una vez. Forzar sincronización y confirmar que no se repite.
2.  **Vínculo de ID**: Verificar en los logs que el `remoteId` se asigna correctamente al `localId` tras el guardado.
3.  **Compilación**: Validar que el Backend y la App sigan comunicándose sin errores de tipo.
