# Tareas: Robustez Total v3.6 (Persistencia Completa) - COMPLETADO

- [x] **Fase 1: Refuerzo del Backend (Railway)**
    - [x] Expandir tabla `compras` en `Entities.kt`
    - [x] Actualizar `ProductoDto` en `Dtos.kt`
    - [x] Implementar lógica CRUD en `ProductoRepository.kt`
    - [x] Crear endpoints en `ProductRoutes.kt`

- [x] **Fase 2: Expansión de Room (Android)**
    - [x] Definir `CompraEntity` y `ProductoRama2Entity`
    - [x] Implementar `CompraDao` y `ProductoRama2Dao`
    - [x] Actualizar `AppDatabase.kt` (Versión 2 + Migración Destructiva)

- [x] **Fase 3: Repositorio Inteligente (Android)**
    - [x] Integrar Compras en `OfflineRepository`
    - [x] Integrar Rama 2 en `OfflineRepository`

- [x] **Fase 4: Conexión de Vistas (Android)**
    - [x] Refactorizar `ListaComprasScreen.kt`
    - [x] Refactorizar `ProductosScreen.kt`

- [x] **Fase 5: Verificación**
    - [x] Validar compilación dual (Backend + App)
    - [x] Pruebas de persistencia offline
