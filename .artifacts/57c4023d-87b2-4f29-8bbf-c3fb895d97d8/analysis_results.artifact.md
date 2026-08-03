# Análisis de Limpieza: Albahaca App

Este documento identifica componentes redundantes o innecesarios que pueden ser eliminados o simplificados para mejorar la salud del proyecto.

## 🗑️ Elementos Innecesarios Detectados

| Elemento | Ubicación | Razón | Acción sugerida |
| :--- | :--- | :--- | :--- |
| **Retrofit** | `build.gradle.kts` | No se usa. La app usa Ktor. | Eliminar dependencias. |
| **RailwayStatusScreen** | `RailwayStatusScreen.kt` | Es una herramienta de depuración, no una función para el usuario. | Mover a carpeta de test o eliminar. |
| **FrutaApiClient** | `FrutaService.kt` | Duplica la lógica de `KtorClient`. | Unificar en un solo cliente global. |
| **Modelos duplicados** | Varios archivos | `Fruta` y `Receta` están definidos localmente en múltiples sitios. | Mover a `shared` o a un archivo `Models.kt` único. |

## 🛠️ Redundancias Técnicas

### Clientes de Red Fragmentados
Actualmente, la app inicializa un motor HTTP en:
1. `KtorClient.kt`
2. `ProductoService.kt`
3. `FrutaService.kt` (vía `FrutaApiClient`)

**Impacto:** Mayor uso de memoria RAM y posibles fugas de conexión.

### Almacenamiento Local Disperso
*   `RecetaStorage` en `RecetaView.kt`
*   `LocalStorage` en `FrutaService.kt`
*   `ProductosRepository` en `ProductoService.kt`

**Impacto:** Es difícil rastrear dónde se guardan los datos. Debería centralizarse en un solo `LocalCacheManager`.

---

> [!IMPORTANT]
> Limpiar estas redundancias reducirá el tiempo de compilación y evitará errores de "Sesión Expirada" al tener un solo punto de control para el Token de seguridad.
