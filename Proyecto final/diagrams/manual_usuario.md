# Manual de Usuario

## 1. Introducción
Este manual describe el uso del sistema académico desarrollado en Java dentro del proyecto `Proyecto final/main/estructuras`.

El sistema permite gestionar:
- estudiantes,
- materias,
- inscripciones,
- horarios de aulas,
- rutas entre edificios,
- registro de notas,
- reportes académicos,
- operaciones de deshacer/rehacer,
- procesamiento por lotes desde archivos CSV.

## 2. Requisitos
- Java 17 o posterior instalado.
- Acceso a la carpeta del proyecto.
- Terminal o consola de comandos.

## 3. Compilación y ejecución
### 3.1 Compilar
Desde la raíz del proyecto:

```bash
javac "Proyecto final/main/estructuras/*.java"
```

### 3.2 Ejecutar
Desde la raíz del proyecto:

```bash
java -cp "Proyecto final/main" estructuras.Main
```

## 4. Estructura del menú principal
Al iniciar el sistema se muestra un menú con opciones numeradas. Estas son las principales funciones:

1. Registrar estudiante
2. Buscar estudiante por ID
3. Listar todos los estudiantes
4. Eliminar estudiante
5. Crear materia
6. Mostrar todas las materias
7. Crear 10 materias de ejemplo
8. Agregar prerrequisito
9. Mostrar prerrequisitos
10. Inscribir estudiante
11. Cancelar inscripción
12. Mostrar cola de espera
13. Mostrar aulas
14. Mostrar aulas disponibles
15. Reservar horario en aula
16. Liberar horario
17. Consultar disponibilidad
18. Agregar conexión entre edificios
19. Calcular ruta más corta
20. Registrar nota
21. Ver reporte académico
22. Navegador de reportes (atrás)
23. Deshacer última operación global
24. Rehacer última operación global
25. Procesar archivo CSV
26. Listar datos de ejemplo
0. Salir

## 5. Uso detallado de cada opción
### 5.1 Registrar estudiante
El sistema solicita:
- ID
- Nombre
- Email
- Semestre actual (1-10)

La operación guarda el estudiante para su posterior uso.

### 5.2 Buscar estudiante por ID
Ingrese el ID del estudiante para mostrar su información básica.

### 5.3 Listar todos los estudiantes
Muestra la lista de estudiantes ordenada por ID.

### 5.4 Eliminar estudiante
Ingrese el ID del estudiante a eliminar. Esta acción se puede deshacer con la opción de deshacer global.

### 5.5 Crear materia
El sistema solicita:
- Código de materia
- Nombre
- Cupos máximos
- Créditos

Esta materia queda disponible para inscripciones.

### 5.6 Mostrar todas las materias
Lista todas las materias registradas con su código, nombre, cupos y créditos.

### 5.7 Crear 10 materias de ejemplo
Agrega un conjunto predefinido de 10 materias al sistema, evitando duplicados.

### 5.8 Agregar prerrequisito
Permite asociar un prerrequisito a una materia existente. Se solicita:
- Código de materia
- Código de prerrequisito

### 5.9 Mostrar prerrequisitos
Muestra la lista de prerrequisitos de una materia.

### 5.10 Inscribir estudiante
Solicita:
- ID del estudiante
- Código de materia

El sistema valida:
- existencia del estudiante,
- existencia de la materia,
- prerrequisitos aprobados,
- disponibilidad de cupo.

Si la materia está llena, el estudiante se agrega a la cola de espera.

### 5.11 Cancelar inscripción
Solicita:
- ID del estudiante
- Código de materia

Si hay estudiantes en cola, el primero en la cola se promueve a cupo disponible.

### 5.12 Mostrar cola de espera
Solicita el código de materia y muestra los estudiantes en espera en orden.

### 5.13 Mostrar aulas
Muestra el listado de aulas existentes en el sistema.

### 5.14 Mostrar aulas disponibles
Permite consultar qué aulas están disponibles y en qué horarios.

### 5.15 Reservar horario en aula
Solicita:
- Nombre de aula
- Día (0-6)
- Hora (0-23)
- Duración (horas)

Verifica conflictos y guarda la reserva.

### 5.16 Liberar horario
Permite liberar un rango horario previamente reservado para un aula.

### 5.17 Consultar disponibilidad
Consulta si un aula está libre en un día y hora específicos.

### 5.18 Agregar conexión entre edificios
Permite construir el grafo de distancias entre edificios.
Se ingresan índices de edificios y distancia en metros.

### 5.19 Calcular ruta más corta
Calcula el camino de menor distancia entre dos edificios usando Dijkstra.
Muestra el recorrido y la distancia total.

### 5.20 Registrar nota
Solicita:
- ID del estudiante
- Semestre
- Código de materia
- Nota

Registra la nota en la primera posición libre del semestre.

### 5.21 Ver reporte académico
Muestra el reporte académico del estudiante, incluyendo materias y promedio.

### 5.22 Navegador de reportes (atrás)
Permite regresar al reporte académico anterior mostrado en la sesión.

### 5.23 Deshacer última operación global
Revierte la última acción registrada en la pila de operaciones.

### 5.24 Rehacer última operación global
Reaplica la última operación deshecha.

### 5.25 Procesar archivo CSV
Permite cargar un archivo CSV con inscripciones masivas.
Formato por línea:

```text
idEstudiante,codigoMateria
```

Se ignoran líneas vacías y líneas que inician con `#`.

### 5.26 Listar datos de ejemplo
Muestra datos iniciales de ejemplo: edificios, aulas y facultades.

## 6. Formato del archivo CSV
El archivo debe contener una solicitud por línea:

```text
idEstudiante,codigoMateria
```

- No usar campos vacíos.
- Si la línea tiene formato incorrecto, se rechaza.
- Se puede usar `#` al inicio para comentarios.

## 7. Mensajes y errores comunes
- `EstudianteNoEncontradoException`: ID de estudiante no encontrado.
- `PreRequisitoNoAprobadoException`: prerrequisitos no cumplidos.
- `HorarioConflictivoException`: horario ocupado.
- `ColaDeEsperaVaciaException`: cola de espera vacía.
- `PilaDeshacerVaciaException`: no hay operación para deshacer/rehacer.
- `ArchivoInvalidoException`: CSV inválido.

## 8. Recomendaciones de uso
- Registra estudiantes antes de inscribirlos.
- Crea materias y prerrequisitos antes de inscribirte.
- Usa la función de deshacer para corregir errores recientes.
- Para pruebas rápidas, crea 10 materias de ejemplo.
- Usa la gestión de aula para evitar conflictos de horario.

## 9. Estructura del proyecto
El código principal se encuentra en `Proyecto final/main/estructuras`.
Clases más importantes:
- `Main.java`
- `SistemaAcademico.java`
- `Estudiante.java`
- `Materia.java`
- `Aula.java`
- `Facultad.java`
- `DijkstraCaminoCorto.java`
- `GestorPilasDeshacer.java`
- `OperacionDeshacible.java` y sus implementaciones
- `LectorCsvInscripciones.java`

## 10. Ejemplo de uso rápido
1. Ejecuta `java -cp "Proyecto final/main" estructuras.Main`.
2. Selecciona opción `7` para crear materias de ejemplo.
3. Selecciona opción `1` para registrar un estudiante.
4. Selecciona opción `10` para inscribir al estudiante.
5. Selecciona opción `20` para registrar una nota.
6. Selecciona opción `21` para ver el reporte académico.

## 11. Anexos
- Guarda el archivo CSV con codificación UTF-8.
- Para sumar distancias de edificios, usa la opción 18 antes de calcular rutas.
- El sistema admite deshacer/rehacer de inscripciones, notas, horarios y eliminación de estudiantes.
