# Hoja de Ruta: Recomendaciones para Albahaca App

Este documento detalla las mejoras sugeridas para transformar el prototipo actual en una aplicación de nivel producción.

## 🚀 Próximos Pasos Recomendados

### 🟢 Nivel: Esencial (Estabilidad y Calidad)
*   **Cifrado de Sesión**: Migrar de `SharedPreferences` estándar a `EncryptedSharedPreferences`.
*   **Manejo de Errores Visual**: En lugar de Toasts (mensajes que desaparecen), usar "Empty States" (ilustraciones bonitas cuando no hay recetas) o "Snackbars" con botón de reintentar.
*   **Refactorización a Shared**: Mover los modelos de datos a `commonMain` para facilitar el soporte futuro de iOS.

### 🟡 Nivel: Funcionalidad Pro (Valor Agregado)
*   **Alertas de Caducidad**: Enviar una notificación push cuando una fruta o verdura en el inventario esté cerca de caducar (según la fecha guardada).
*   **Compartir Recetas**: Generar un enlace o código QR para que un usuario pueda enviarle una de sus recetas a otro amigo que use la app.
*   **Modo Lectura de Cocina**: Una pantalla que muestre los pasos uno por uno en letras grandes, optimizada para tablets o para leer de lejos mientras se cocina.

### 🔴 Nivel: Innovación (IA y Futuro)
*   **Gemini Vision**: Implementar el escaneo de tickets de compra o fotos de la despensa para llenar el inventario automáticamente.
*   **Planificador Semanal**: Que la IA genere un menú completo de lunes a domingo basado únicamente en lo que el usuario tiene en su inventario para evitar el desperdicio de comida.
*   **Web Dashboard**: Activar el módulo `webApp` para que los usuarios puedan gestionar sus compras desde una computadora de escritorio.

---

> [!TIP]
> **Mi recomendación personal:** Empezar por el **Modo Lectura de Cocina**. Es una función muy valorada por los usuarios y aprovecha los datos que ya estamos guardando correctamente en el servidor.
