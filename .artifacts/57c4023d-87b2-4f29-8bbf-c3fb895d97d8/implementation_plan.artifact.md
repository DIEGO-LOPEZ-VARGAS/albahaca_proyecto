# Plan de Robustez Total v3.6: Persistencia y CRUD Completo

Este plan soluciona la falta de persistencia en la Lista de Compras y los Productos de la Rama 2, corrigiendo la discrepancia entre el servidor y la aplicación.

## Problemas Críticos Detectados

### 1. Servidor "Sordo" (Backend)
*   La tabla `compras` en el servidor no soporta `fecha_caducidad` ni `tipo_almacenamiento`.
*   No existen rutas para eliminar o actualizar compras en el API.

### 2. Memoria de Corto Plazo (Android)
*   Los productos se pierden al cerrar la app porque no están en la base de datos Room.
*   Las pantallas de Compras y Productos Rama 2 no usan el `OfflineRepository`.

## Cambios Propuestos

### A. Repositorio Backend (Railway)

#### [MODIFICAR] [Entities.kt](file:///C:/Users/Darkar/StudioProjects/backend/src/main/kotlin/com/example/models/Entities.kt)
*   Expandir el objeto `Compras` con `fechaCaducidad` y `tipoAlmacenamiento`.

#### [MODIFICAR] [ProductoRepository.kt](file:///C:/Users/Darkar/StudioProjects/backend/src/main/kotlin/com/example/repository/ProductoRepository.kt)
*   Implementar `deleteCompra` y `updateCompra`.

#### [MODIFICAR] [ProductRoutes.kt](file:///C:/Users/Darkar/StudioProjects/backend/src/main/kotlin/com/example/routes/ProductRoutes.kt)
*   Exponer `DELETE /api/compras/{id}` y `PUT /api/compras/{id}`.

### B. Repositorio Android (App)

#### [MODIFICAR] [Entities.kt](file:///C:/Users/Darkar/StudioProjects/albahaca_proyecto/androidApp/src/main/kotlin/com/example/albahacaproyecto/database/Entities.kt)
*   Añadir `CompraEntity` y `ProductoRama2Entity`.

#### [MODIFICAR] [OfflineRepository.kt](file:///C:/Users/Darkar/StudioProjects/albahaca_proyecto/androidApp/src/main/kotlin/com/example/albahacaproyecto/database/OfflineRepository.kt)
*   Añadir lógica de sincronización para Compras y Productos.

#### [MODIFICAR] [ListaComprasScreen.kt](file:///C:/Users/Darkar/StudioProjects/albahaca_proyecto/androidApp/src/main/kotlin/com/example/albahacaproyecto/ListaComprasScreen.kt)
*   Sustituir el repositorio en memoria por el `OfflineRepository`.

## Plan de Verificación
1.  **Cierre Forzado**: Agregar items, cerrar app, abrir y verificar persistencia.
2.  **Sincronización Remota**: Borrar un item de la lista de compras y verificar que Railway también lo borre.
3.  **Consistencia de Datos**: Verificar que la fecha de caducidad se mantenga al viajar del cel al servidor y de vuelta.
