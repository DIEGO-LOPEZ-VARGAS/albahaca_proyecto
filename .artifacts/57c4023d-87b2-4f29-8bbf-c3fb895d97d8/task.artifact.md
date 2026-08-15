# Tareas: Eliminación de Duplicados v4.3

- [ ] **Backend: Retorno de Identidad**
    - [ ] Modificar `ProductoRepository.kt` para devolver objetos insertados
    - [ ] Modificar `RecetaRepository.kt` para devolver objetos insertados
    - [ ] Actualizar rutas en `ProductRoutes.kt` (POST) para devolver JSON
- [ ] **Android: Vínculo de ID Remoto**
    - [ ] Actualizar `OfflineRepository.kt` para capturar el `remoteId` tras guardar
    - [ ] Implementar limpieza de duplicados por nombre durante la sincronización
- [ ] **Verificación**
    - [ ] Validar compilación dual
    - [ ] Confirmar que no se duplican items al guardar/sincronizar
