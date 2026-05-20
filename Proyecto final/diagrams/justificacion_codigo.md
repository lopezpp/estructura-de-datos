# Justificación extensa del código del proyecto

Este documento explica en detalle cada decisión tomada en el proyecto, por qué se eligió cada clase, estructura de datos y mecanismo, qué intenta resolver y cómo encaja todo en el sistema académico.

## 1. Propósito general del proyecto

El sistema responde a un escenario académico completo: registrar estudiantes, gestionar materias, inscribir alumnos, manejar colas de espera, reservar aulas, calcular rutas entre edificios, registrar notas y reportes, y permitir deshacer/rehacer operaciones. El diseño busca:

- separar la lógica de presentación (interfaz de consola) de la lógica de dominio,
- usar estructuras de datos apropiadas para cada necesidad,
- tratar estados complejos de forma consistente,
- documentar claramente cada componente para justificar su existencia.

---

## 2. `Main.java`

### Qué hace

`Main` contiene el método `main` que arranca la aplicación de consola. Muestra un menú, recibe las entradas del usuario, invoca métodos auxiliares y maneja excepciones del dominio.

### Por qué se diseñó así

- Se utiliza `Scanner` con `useDelimiter("
")` para leer líneas completas y evitar problemas típicos con mezcla de `nextInt()` y `nextLine()`.
- El menú se mantiene en un bucle `while (!salir)` para permitir múltiples operaciones en una misma sesión.
- La lógica de entrada/salida no está mezclada con la lógica de negocio; `Main` solo invoca métodos de `SistemaAcademico`.
- Los métodos auxiliares (`registrarEstudiante`, `inscribir`, `reservarHorario`, etc.) aíslan la lectura de datos y la llamada al sistema, lo que hace el código más legible y mantenible.

### Decisiones precisas y su justificación

- `String op = sc.nextLine().trim();`
  - Normaliza la entrada del usuario y reduce errores por espacios.
- `switch (op)` en lugar de varios `if`.
  - Claridad para manejar las opciones del menú y facilidad para extender.
- Bloques `try/catch` específicos por excepción.
  - Permite mensajes de error más útiles y evita capturar excepciones sin contexto.
- `catch (Exception ex)` final.
  - Se añade como red de seguridad para manejar cualquier error inesperado sin cerrar el programa abruptamente.

### Qué se intentó lograr

- Interfaz de usuario sencilla y robusta.
- Control de errores centrado en el dominio.
- Separación clara entre presentación y lógica de negocio.

---

## 3. `SistemaAcademico.java`

### Qué hace

Es la fachada del sistema. Gestiona:
- estudiantes,
- materias,
- aulas,
- facultades,
- rutas entre edificios,
- notas y reportes,
- histórico de operaciones para deshacer/rehacer,
- procesamiento batch de inscripciones.

### Por qué existe

- Para centralizar la lógica de negocio y no dispersarla en la interfaz.
- Para mantener los datos y reglas encapsulados.
- Para poder reutilizar el sistema desde múltiples interfaces si se necesitara en el futuro.

### Estructuras de datos y decisiones concretas

- `Map<String, Estudiante> estudiantesPorId = new HashMap<>()`
  - Búsqueda en tiempo constante por identificador.
  - `HashMap` es apropiado porque no se necesita orden de inserción y se prioriza el acceso rápido.
- `Map<String, Materia> materiasPorCodigo = new HashMap<>()`
  - Similar a estudiantes, permite buscar materias por código sin iteraciones costosas.
- `NavigableMap<String, Aula> aulasPorNombre = new TreeMap<>()`
  - Seleccionado para que la lista de aulas quede ordenada por nombre cuando se muestre.
  - Un `TreeMap` aporta orden natural sin requerir una lista adicional.
- `Facultad[] facultades = new Facultad[5]`
  - Uso de array fijo para cumplir con la especificación de 5 facultades de ejemplo.
  - Evita complejidad innecesaria de listas cuando el tamaño es constante.
- `String[] nombresEdificios` y `int[][] distanciasEdificiosMetros`
  - Modelan un grafo con matriz de adyacencia.
  - `int[][]` permite distancias directas entre edificios y el valor `-1` indica ausencia de conexión.
- `GestorPilasDeshacer historialAcciones = new GestorPilasDeshacer()`
  - Mantiene un historial global de transacciones que pueden deshacerse y rehacerse.
- `Deque<String> pilaReportesParaAtras = new ArrayDeque<>()`
  - Implementa un historial de reportes recientemente generados para la navegación "atrás".

### Métodos internos y diseño para undo/redo

- `Estudiante estudianteSinChequeo(String id)` y `Materia materiaSinChequeo(String cod)`.
  - Crean rutas de acceso directas al estado interno sin lanzar excepciones.
  - Se usan exclusivamente desde comandos para rehacer/deshacer y evitar validaciones redundantes.
- `restaurarEstudiante(Estudiante e)`.
  - Restaura un estudiante eliminado junto con sus inscripciones actuales.
  - Reinscribe al estudiante en cada materia sin duplicar lógica externa.
- `eliminarEstudianteDefinitivoSinPila(String id)`.
  - Quita al estudiante del sistema sin registrar la operación en el historial, útil para `rehacer()`.
- `reaplicarInscripcion(...)` y `reaplicarCancelacion(...)`.
  - Permiten que los comandos de undo/redo reapliquen cambios con el mismo efecto que la operación original.

Estas decisiones muestran que se quiso diseñar un sistema consistente en el que el historial de transacciones no rompe la lógica de negocio.

### Gestión de estudiantes

- `registrarEstudiante(Estudiante e)`.
  - Inserta directamente en el mapa. No realiza validación externa porque la validación mínima se hace en la construcción de `Estudiante`.
- `buscarEstudiante(String id)`.
  - Separa el acceso seguro con excepción de dominio `EstudianteNoEncontradoException`.
- `listarEstudiantesOrdenados()`.
  - Devuelve una lista ordenada por ID con `Comparator.comparing(Persona::getId)`.
  - Esto permite una vista consistente sin alterar el almacenamiento interno.
- `eliminarEstudianteConPila(String id)`.
  - Elimina al estudiante y registra `OperacionEliminarEstudiante`.
  - Incluye `desvincularEstudianteDeMaterias(e)` para mantener consistencia en materias y colas.

### Por qué se hizo así

- Separar el borrado definitivo del borrado con historial permite deshacer con precisión.
- `desvincularEstudianteDeMaterias` evita que queden referencias a estudiantes eliminados en materias o colas.

### Gestión de materias y prerrequisitos

- `registrarMateria(Materia m)`.
  - Almacena el código en mayúsculas con `m.getCodigo().toUpperCase(Locale.ROOT)`.
  - Asegura consistencia en búsquedas independientemente de la entrada del usuario.
- `agregarPrerrequisito(String codigoMateria, String codigoPrerrequisito)`.
  - Agrega el prerrequisito a la lista enlazada de la materia.
- `mostrarPrerrequisitos(String codigoMateria)`.
  - Devuelve una lista textual clara, con manejo de casos de materia inexistente y materia sin prerrequisitos.

### Inscripciones y colas

- `inscribirEstudianteEnMateria(...)`.
  - Valida existencia del estudiante y materia.
  - Llama a `validarPrerrequisitos(e, m)` antes de hacer cualquier inscripción.
  - Si hay cupo, inscribe con `m.inscribirDirecto(e)` y registra la materia en el estudiante.
  - Si no hay cupo, agrega a la cola con `m.agregarAColaEspera(e)`.
  - Registra la operación para permitir undo.

- `validarPrerrequisitos(e, m)`.
  - No solo comprueba la existencia de prerrequisitos, sino que exige aprobación previa de cada uno.
  - Hace explícita la regla de negocio que una materia solo puede tomarse si se aprobaron todos sus prerrequisitos.

- `cancelarInscripcionConPila(...)` y `cancelarInscripcionInterno(...)`.
  - Primero verifica si el estudiante está inscrito o en cola.
  - Si está inscrito, quita la inscripción y promueve del frente de la cola si hay cupo.
  - Si está en la cola, lo retira sin afectar cupo.
  - Registra la operación en el historial junto al posible estudiante promovido.

- `mostrarColaEspera(String codigoMateria)`.
  - Da un retorno estructurado con posiciones numéricas y total.
  - Lanza `ColaDeEsperaVaciaException` para distinguir el caso de materia sin cola.

### Por qué este diseño

- Refleja reglas reales de matrícula: inscrito vs. en cola.
- Evita duplicados y permite una política clara de promoción.
- Leave invariants consistent after each mutation.

### Horarios y aulas

- `reservarHorarioConPila(...)` y `liberarHorarioConPila(...)`.
  - Guardan un `boolean[][]` antes y después de la modificación.
  - Registran `OperacionCambioHorario` para permitir undo/redo.
- `consultarDisponibilidadAula(...)`.
  - Delegación simple a la clase `Aula`.
- `registrarAula(Aula a)`.
  - Introduce aulas al sistema con llave por nombre.

### Por qué así

- Permite separar la lógica de reserva (en `Aula`) de la lógica de historial (en `SistemaAcademico`).
- Las copias completas de la matriz son necesarias porque un estado parcial no es suficiente para deshacer.

### Rutas entre edificios

- `establecerDistanciaSimetrica(int i, int j, int metros)`.
  - Inserta distancias en ambas direcciones para mantener la matriz simétrica.
  - El valor `-1` en la matriz inicial indica ausencia de conexión.
- `calcularRutaMasCorta(int origen, int destino)`.
  - Llama a `DijkstraCaminoCorto.calcular(...)`.
  - Construye una salida en texto que muestra tramo por tramo y la distancia total.

### Justificación

- Se agrega una funcionalidad complementaria que hace uso de estructuras y algoritmos clásicos.
- El grafo se maneja con matriz porque el número de edificios es pequeño y fijo.

### Reportes y notas

- `registrarNotaConPila(...)`.
  - Busca una columna vacía en el semestre dado.
  - Crea un comando `OperacionRegistrarNota` con el estado anterior y el nuevo para poder deshacer.
  - Usa copias defensivas de las matrices de notas/códigos.
- `verReporteAcademicoYApilar(String idEstudiante)`.
  - Construye el reporte completo y lo apila en `pilaReportesParaAtras`.
- `atrasUltimoReporte()`.
  - Permite regresar de manera simple al reporte anterior.

### Por qué así

- El reporte no es una operación que modifica el estado persistente, pero se guarda para navegación, lo cual es una decisión de usabilidad.
- Guardar cadenas en una pila es suficiente y evita recomputar el reporte completo si el usuario quiere ver el anterior.

### Procesamiento por lotes

- `procesarInscripcionesDesdeArchivo(Path archivoCsv)`.
  - Usa `LectorCsvInscripciones.cargarCola(archivoCsv)` para abstraer la lectura de archivos.
  - Procesa cada solicitud en orden.
  - Diferencia entre excepciones por tipo para clasificar fallas.
  - Genera un reporte de resultado con detalle por solicitud.

### Por qué así

- El procesamiento por lotes permite automatizar inscripciones y manejar errores de manera completa.
- La cola FIFO mantiene el orden del archivo, lo que es importante en escenarios reales.

### Undo/redo global

- `deshacerUltimaOperacionGlobal()` y `rehacerUltimaOperacionGlobal()`.
  - Delegan al gestor de pilas.
  - Mantienen el sistema preparado para operaciones reversibles.

### Inicialización de datos de ejemplo

- `inicializarFacultadesEjemplo()` y `inicializarCampusEjemplo()`.
  - Proveen datos que facilitan la prueba sin tener que crear manualmente todas las entidades.
  - Incluyen edificios y aulas para las funciones de rutas y horarios.

---

## 4. `Persona.java` y subclases

### `Persona.java`

#### Qué hace

Define los atributos comunes de una persona del sistema: `id`, `nombre` y `email`.

#### Decisiones

- `protected Persona(String id, String nombre, String email)`.
  - Se usa el constructor protegido para obligar a instanciar solo subclases.
- `Objects.requireNonNull(...).trim()`.
  - Valida que no se reciban campos nulos y elimina espacios en blanco al inicio/final.
- `public abstract String mostrarInformacion()`.
  - Forza a cada subclase a definir su propia representación textual.

#### Por qué así

- La abstracción reduce duplicación y permite futuros roles adicionales (por ejemplo, administrador).
- El uso de validación temprana evita errores silenciosos en el sistema.

### `Estudiante.java`

#### Qué hace

Modela un estudiante con:
- semestre actual,
- matriz de notas y códigos de materia,
- historial de materias aprobadas,
- materias actualmente inscritas.

#### Decisiones clave

- `public static final int MAX_SEMESTRES = 10;`
- `public static final int MAX_MATERIAS_POR_SEMESTRE = 20;`
  - Estas constantes definen los límites del sistema.
- `private final Double[][] notas;` y `private final String[][] codigosMateria;`
  - Un diseño matricial permite acceder directamente a semestre y columna.
  - `Double` en vez de `double` permite valores `null` para celdas vacías.
- `private final ListaEnlazada<String> historialMateriasCursadas;`
  - El historial debe ser preservado en orden, y una lista enlazada es suficiente.
- `private final Set<String> materiasAprobadasCodigo;`
  - Permite validaciones de prerrequisitos en tiempo constante.
- `private final Set<String> materiasInscritas;`
  - Evita que un estudiante se inscriba varias veces en la misma materia.

#### Métodos y propósito

- `setSemestreActual(int semestreActual)`.
  - Valida rango entre 1 y 10.
- `agregarInscripcion(String codigoMateria)` y `quitarInscripcion(String codigoMateria)`.
  - Normalizan códigos a mayúsculas y mantienen consistencia con el sistema principal.
- `tieneAprobado(String codigoMateria)`.
  - Usa el conjunto de aprobadas para responder rápido.
- `registrarNota(int semestre, String codigoMateria, double nota)`.
  - Inserta la nota en la primera celda libre del semestre.
  - Usa `asignarNotaEnCelda` para centralizar el control.
- `asignarNotaEnCelda(int semestre, int columna, Double nota, String codigo)`.
  - Soporta nulls para borrar celdas.
  - Actualiza el código de materia y recomputa aprobado/historial.
- `recomputarAprobadasEHistorial()`.
  - Reconstruye los estados derivados cada vez que cambia alguna nota, asegurando consistencia.
- `getNotasCopia()` y `getCodigosCopia()`.
  - Devuelven copias defensivas, evitando exponer el arreglo interno.
- `restaurarNotas(Double[][] n, String[][] cod)`.
  - Restaura todo el estado de la matriz y recalcula aprobaciones.
- `promedioSemestre(int semestre)` y `promedioAcumulado()`.
  - Manejan casos de filas vacías devolviendo 0.0.
- `contarReprobadas()` y `contarAprobadas()`.
  - Miden el rendimiento académico del estudiante.
- `mostrarInformacion()`.
  - Retorna texto con ID, nombre, email, semestre y promedio acumulado.

#### Por qué cada decisión

- El modelo de matriz fija responde a la especificación de 10 semestres y 20 materias.
- El uso de objetos `Double` permite distinguir ausencia de nota de una nota 0.0.
- La recomputación de aprobadas evita problemas cuando se hace `undo` sobre notas.
- Mantener un historial separado de materias aprobadas permite reconstruir prerrequisitos con exactitud.

### `Profesor.java`

#### Qué hace

Extiende `Persona` y agrega `departamento` y `salario`.

#### Por qué está incluido

- Aunque en el sistema actual no se usa de manera activa, demuestra que el modelo puede ampliarse a más actores.
- Sirve como ejemplo de herencia simple y de especialización.

### `Facultad.java`

#### Qué hace

Representa una facultad con `codigo` y `nombre`.

#### Decisión clave

- Se creó como clase simple porque el proyecto solicita facultades como parte del contexto académico.
- Se utiliza en un array fijo de 5 elementos en `SistemaAcademico`.

#### Por qué así

- El modelo no necesita más comportamiento que DTO, por eso no se añadió lógica adicional.
- `toString()` facilita su impresión en listados.

---

## 5. `Materia.java`

### Qué hace

Modela una materia con cupos, créditos, prerrequisitos, inscritos y cola de espera.

### Decisiones y su justificación

- `private final ListaEnlazada<String> prerrequisitos;`
  - Elegida para mostrar el uso de una estructura propia y porque no se necesita acceso aleatorio.
  - Permite agregar prerrequisitos en orden de declaración.
- `private final List<Estudiante> inscritos;`
  - `ArrayList` no se declara explícitamente pero se usa para mantener la lista de inscritos.
- `private final Deque<Estudiante> colaEspera;`
  - `ArrayDeque` implementa la cola FIFO de forma eficiente.
- `inscribirDirecto(Estudiante e)`.
  - Comprueba si ya está inscrito y si hay cupo.
- `agregarAColaEspera(Estudiante e)`.
  - Evita duplicados en cola o inscripción.
- `removerDeColaEspera(Estudiante e)`.
  - Permite retirar explicitamente cuando se cancela sin estar inscrito.
- `agregarAlFrenteColaEspera(Estudiante e)`.
  - Se añadió para poder revertir la promoción de la cola en operaciones `undo`.
- `promoverDesdeColaSiHayCupo()`.
  - Realiza la transición automática de la cola a inscritos cuando se libera espacio.

### Qué se buscó

- Modelar la dinámica real de materias con cupos y lista de espera.
- Separar claramente la inscripción directa de la cola de espera.
- Hacer el comportamiento predecible y reversible.

---

## 6. `Aula.java`

### Qué hace

Modela la disponibilidad de un aula en un calendario semanal de 7x24.

### Decisiones

- `boolean[][] ocupacion`.
  - Un arreglo primitivo es el más eficiente para un recurso binario ocupado/libre.
- `copiaOcupacion()`.
  - Crea una copia profunda para evitar aliasing cuando se guarda el estado.
- `restaurarOcupacion(boolean[][] matriz)`.
  - Valida la dimensión 7x24 y copia los datos.
- `consultarDisponibilidad(int dia, int hora)`.
  - Expone solo el estado de una celda.
- `reservar(int dia, int hora, int duracion)`.
  - Valida rango y lanza `HorarioConflictivoException` si alguna hora ya está tomada.
- `liberar(int dia, int hora, int duracion)`.
  - Desmarca el bloque de horas.

### Por qué cada elección

- El modelo por matriz aporta claridad y diagnósticos sencillos.
- Las validaciones mantienen el dominio libre de estados inválidos.
- Separar reserva y liberación permite revertir horarios fácilmente.

---

## 7. `DijkstraCaminoCorto.java`

### Qué hace

Implementa Dijkstra sobre una matriz de distancias con `-1` para aristas inexistentes.

### Decisiones de diseño

- `PriorityQueue<int[]>`.
  - Se usa para extraer el siguiente nodo con menor distancia acumulada.
- `int[] mejor` y `int[] previo`.
  - `mejor` guarda la distancia mínima conocida a cada nodo.
  - `previo` permite reconstruir el camino.
- Revisión de índices fuera de rango.
  - Garantiza robustez frente a entradas inválidas.
- Devolver `Resultado(List<Integer> caminoIndices, int distanciaTotalMetros)`.
  - El uso de un `record` simplifica la devolución de múltiples valores.

### Por qué así

- El algoritmo clásico es adecuado para el grafo pequeño de edificios.
- La matriz de adyacencia se ajusta al uso estático del proyecto.
- El retorno en formato estructurado facilita la construcción de la salida textual.

---

## 8. `ListaEnlazada.java`

### Qué hace

Proporciona una implementación de lista enlazada simple genérica.

### Decisiones

- `Nodo<T>` interno como clase estática.
  - Evita el acceso innecesario a la clase externa y mantiene el encapsulamiento.
- `agregarAlFinal(T dato)`.
  - Añade nodos al final para preservar orden.
- `contiene(T dato)` y `eliminarPrimeraOcurrencia(T dato)`.
  - Implementan operaciones típicas de lista enlazada.
- `iterator()`.
  - Permite iterar con `for-each`.
- `copiar()`.
  - Soporta copias de la estructura cuando se necesita duplicar datos.

### Por qué así

- La clase demuestra conocimiento de estructuras de datos básicas.
- Es útil para prerrequisitos y para historial de materias sin necesidad de colecciones complejas.

---

## 9. `LectorCsvInscripciones.java` y `SolicitudInscripcion.java`

### Qué hacen

- `LectorCsvInscripciones` lee un archivo CSV y devuelve una cola de solicitudes.
- `SolicitudInscripcion` representa cada línea como un record inmutable.

### Decisiones

- `BufferedReader` con `StandardCharsets.UTF_8`.
  - Asegura lectura con codificación estándar.
- Ignorar líneas vacías y líneas que comienzan con `#`.
  - Permite comentarios y flexibilidad en el archivo.
- Validar campos vacíos y número de columnas.
  - Evita inscripciones corruptas y produce errores claros.
- Normalizar `codigoMateria` a mayúsculas.
  - Uniformiza el formato para que las búsquedas no dependan de la entrada.

### Por qué así

- El código es robusto ante archivos mal formateados.
- La cola FIFO respeta el orden de llegada de las solicitudes.
- Usar un `record` es una elección moderna y concisa para datos inmutables.

---

## 10. Comandos y patrón Command

### `OperacionDeshacible.java`

#### Qué hace

Define la interfaz para operaciones reversibles.

#### Por qué

- Elegir este patrón permite desacoplar la ejecución de la reversión.
- Facilita agregar nuevas operaciones en el futuro sin cambiar el gestor.

### `GestorPilasDeshacer.java`

#### Qué hace

Maneja dos pilas: una de deshacer y otra de rehacer.

#### Decisiones

- `ArrayDeque` para pilas LIFO eficientes.
- `registrar(op)` limpia `pilaRehacer` para mantener la consistencia cuando se hace una nueva acción luego de un deshacer.
- Excepciones específicas cuando no hay operaciones disponibles.

#### Por qué

- Implementa correctamente la semántica clásica de undo/redo.
- Evita estados inconsistentes tras una nueva operación después de un deshacer.

### Operaciones específicas

#### `OperacionInscribir.java`

- Guarda si la inscripción fue directa o en cola.
- `deshacer()` remueve la inscripción o la cola.
- `rehacer()` vuelve a aplicar la inscripción en el mismo modo.

#### Por qué

- Necesita distinguir cola vs. inscripción porque revertir cada caso es diferente.
- Mantener el modo de inscripción asegura un comportamiento predecible.

#### `OperacionCancelarInscripcion.java`

- Guarda el estudiante cancelado y el posible estudiante promovido.
- `deshacer()` devuelve al promovido a la cola y reinscribe al estudiante original.
- `rehacer()` reaplica la cancelación.

#### Por qué

- La cancelación afecta a más de un estudiante; el comando guarda ese efecto secundario.
- Permite volver atrás sin perder el orden de la cola.

#### `OperacionRegistrarNota.java`

- Guarda celda, nota anterior y nueva, y códigos respectivos.
- `deshacer()` restaura el estado anterior completo.
- `rehacer()` vuelve a aplicar el estado nuevo.

#### Por qué

- Registrar una nota debe ser reversible sin recalcular la posición o el estado en otra parte.
- Se guarda el mínimo estado necesario para restaurar la celda.

#### `OperacionEliminarEstudiante.java`

- Guarda el objeto `Estudiante` completo.
- `deshacer()` restaura al estudiante con sus inscripciones.
- `rehacer()` elimina definitivamente sin registrar otra vez.

#### Por qué

- La eliminación es una operación destructiva que requiere conservar todo el objeto para poder revertir.
- Se mantiene la integridad de las relaciones con materias y colas.

#### `OperacionCambioHorario.java`

- Guarda el estado anterior y el posterior de la matriz de ocupación.
- `copiarMatriz(...)` realiza copias profundas.
- `deshacer()` y `rehacer()` restauran matrices completas.

#### Por qué

- Un cambio de horario no puede revertirse parcialmente sin un snapshot completo.
- Clonar matrices evita que la misma referencia sea modificada tras registrarla.

---

## 11. Excepciones personalizadas

### Qué hacen

Cada clase de excepción representa una condición específica del dominio.

### Decisiones y su justificación

- `EstudianteNoEncontradoException`.
  - Se lanza cuando no existe el estudiante solicitado.
- `PreRequisitoNoAprobadoException`.
  - Implícitamente refleja una regla académica importante.
- `HorarioConflictivoException`.
  - Delimita errores de reserva de aulas.
- `ColaDeEsperaVaciaException`.
  - Indica que no hay datos en la cola, no un fallo genérico.
- `PilaDeshacerVaciaException`.
  - Se usa para diferenciar entre ausencia de operaciones y otros fallos.
- `ArchivoInvalidoException`.
  - Señala problemas de estructura en CSV.
- `CupoLlenoException`.
  - Aunque no se usa en el código actual, está presente como excepción de dominio para escenarios futuros donde no se permita fila de espera.

### Por qué usar excepciones específicas

- Facilitan el manejo puntual en `Main`.
- Mejoran la comunicación sobre el tipo de error.
- Permiten que la lógica de negocio declare claramente sus condiciones de fallo.

---

## 12. Otras decisiones de diseño transversales

### Normalización de códigos y texto

- Se usa `toUpperCase(Locale.ROOT)` cuando se guarda o compara códigos de materia.
- Esto evita que `proG101` y `PROG101` sean tratados como distintos.

### Locales en formato numérico

- `String.format(Locale.US, "%.1f", n)` y similares.
- Se decide usar `Locale.US` para garantizar un punto decimal consistente en reportes.

### Uso de `System.lineSeparator()`

- Garantiza salidas multilineales compatibles con diferentes sistemas operativos.

### Manejo de colecciones

- `Collections.unmodifiableMap(...)` devuelve vistas de solo lectura.
- Se expone el estado sin permitir modificaciones externas.

### Separación de responsabilidades

- `Main`: interacción.
- `SistemaAcademico`: lógica y reglas.
- Entidades (`Estudiante`, `Materia`, `Aula`, `Facultad`, `Profesor`): estado.
- Comandos (`Operacion*`): reversibilidad.
- Utilidades (`DijkstraCaminoCorto`, `LectorCsvInscripciones`): algoritmos y E/S.

---

## 13. Conclusión final

Este proyecto se diseñó para ser:
- completo en funcionalidad,
- coherente en su modelo de datos,
- robusto en la validación de entradas,
- extensible para nuevos roles o operaciones,
- fácil de depurar gracias a excepciones precisas,
- reversible gracias al patrón command.

Cada decisión: tipo de colección, representación matricial, uso de estructuras propias, separación de capas, manejo del historial, lectura de CSV, normalización de datos y excepciones personalizadas, se tomó para resolver un requisito específico, mantener integridad y ofrecer un sistema académico consistente.
