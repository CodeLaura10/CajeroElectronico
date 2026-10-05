# Cajero Electrónico

Aplicación Android que simula un cajero electrónico. Permite ingresar a la cuenta, realizar consignaciones y retiros sobre un saldo, valida que no se retire más dinero del disponible y conserva el saldo entre sesiones usando `SharedPreferences`.

Práctica de laboratorio **Implementar aplicaciones sencillas – Almacenamiento de datos**, Institución Universitaria Pascual Bravo.

## Funcionalidades

- **Pantalla de ingreso:** la aplicación inicia con una pantalla de bienvenida y un botón **Ingresar** (sin credenciales). Al ingresar se recupera el saldo guardado.
- **Consignar:** suma el monto ingresado al saldo.
- **Retirar:** resta el monto ingresado al saldo. Si el monto es mayor que el saldo disponible, la transacción se considera inválida y no se realiza.
- **Salir:** guarda el saldo actual y muestra un recordatorio con el saldo. Al confirmar, regresa a la pantalla de ingreso, desde donde se puede volver a entrar sin reiniciar la aplicación.
- **Persistencia:** el saldo se conserva al salir y al cerrar la aplicación por completo, para seguir realizando transacciones en el próximo ingreso.
- **Validaciones del monto:** no se aceptan valores vacíos, no numéricos ni menores o iguales a cero.
- **Mensajes de resultado:** en verde para transacciones exitosas y en rojo para errores.
- **Formato de moneda:** el saldo se muestra en pesos colombianos.

## Tecnologías

- Android Studio
- Java
- Layout en XML (plantilla *Empty Views Activity*)
- `SharedPreferences` para el almacenamiento local
- `AlertDialog` para el recordatorio de saldo

## Estructura principal

```
app/
└── src/main/
    ├── java/com/example/cajeroelectronico/
    │   └── MainActivity.java      # Lógica del cajero
    └── res/layout/
        └── activity_main.xml      # Interfaz de usuario
```

## Interfaz

La interfaz está en un solo layout con dos contenedores que se muestran u ocultan según el momento: `layoutInicio` (pantalla de ingreso) y `layoutCajero` (pantalla del cajero).

### Pantalla de ingreso (`layoutInicio`)

| Componente | id               | Función                                  |
|------------|------------------|------------------------------------------|
| TextView   | `tvTituloInicio` | Título de la aplicación                  |
| TextView   | `tvBienvenida`   | Mensaje de bienvenida                    |
| Button     | `btnIngresar`    | Recupera el saldo y abre el cajero       |

### Pantalla del cajero (`layoutCajero`)

| Componente | id             | Función                                             |
|------------|----------------|-----------------------------------------------------|
| TextView   | `tvTitulo`     | Título de la aplicación                             |
| TextView   | `tvSaldo`      | Muestra el saldo disponible                         |
| EditText   | `etMonto`      | Ingreso del monto de la transacción                 |
| Button     | `btnConsignar` | Realiza una consignación                            |
| Button     | `btnRetirar`   | Realiza un retiro                                   |
| Button     | `btnSalir`     | Guarda el saldo, muestra el recordatorio y sale     |
| TextView   | `tvMensaje`    | Muestra el resultado de cada transacción            |

## Métodos principales

| Método            | Descripción                                                                 |
|-------------------|-----------------------------------------------------------------------------|
| `ingresar()`      | Recupera el saldo de `SharedPreferences` y muestra la pantalla del cajero.  |
| `consignar()`     | Valida el monto y lo suma al saldo.                                         |
| `retirar()`       | Valida el monto y que no supere el saldo; si es válido, lo resta.           |
| `salir()`         | Guarda el saldo y muestra el recordatorio antes de volver al ingreso.       |
| `mostrarInicio()` | Oculta el cajero y muestra la pantalla de ingreso.                          |
| `leerMonto()`     | Lee y valida el monto escrito por el usuario.                               |
| `guardarSaldo()`  | Almacena el saldo en `SharedPreferences`.                                   |

## Funcionamiento del almacenamiento

El saldo se guarda en el archivo de preferencias `cajero_prefs` con la clave `saldo`.

- **Recuperación:** al presionar **Ingresar** se lee el saldo guardado. Si es la primera vez, el saldo inicia en 0.
- **Guardado:** el método `guardarSaldo()` almacena el saldo con `SharedPreferences.Editor` y `apply()`. Se llama después de cada transacción válida y al presionar **Salir**.

```java
// Recuperar
saldo = Double.parseDouble(preferencias.getString("saldo", "0"));

// Guardar
SharedPreferences.Editor editor = preferencias.edit();
editor.putString("saldo", String.valueOf(saldo));
editor.apply();
```

## Validación del retiro

```java
if (monto > saldo) {
    mostrarMensaje("Transacción inválida: saldo insuficiente", false);
    return;
}
```

## Recordatorio al salir

```java
new AlertDialog.Builder(this)
        .setTitle("Recordatorio de saldo")
        .setMessage("Su saldo actual es " + formatear(saldo)
                + ".\nEste saldo quedará guardado para su próximo ingreso.")
        .setPositiveButton("Salir", (dialogo, which) -> mostrarInicio())
        .setNegativeButton("Cancelar", null)
        .show();
```

## Cómo ejecutar

1. Clonar o descargar el proyecto.
2. Abrirlo en Android Studio y esperar a que termine la sincronización de Gradle.
3. Seleccionar un emulador o un dispositivo físico conectado.
4. Presionar **Run**.

## Pruebas sugeridas

1. Abrir la aplicación y verificar que aparezca la pantalla de ingreso.
2. Presionar **Ingresar** y verificar que se muestre el saldo (0 la primera vez).
3. Consignar un monto y verificar que el saldo aumente.
4. Retirar un monto menor al saldo y verificar que el saldo disminuya.
5. Intentar retirar un monto mayor al saldo y verificar que aparezca el mensaje de transacción inválida y el saldo no cambie.
6. Presionar **Consignar** o **Retirar** con el campo vacío o con 0 y verificar el mensaje de error.
7. Presionar **Salir**, verificar que el recordatorio muestre el saldo correcto y confirmar.
8. Presionar **Ingresar** de nuevo y verificar que aparezca el mismo saldo.
9. Cerrar la aplicación por completo, abrirla, ingresar y verificar que el saldo se conserve.

## Autora

Laura – Institución Universitaria Pascual Bravo