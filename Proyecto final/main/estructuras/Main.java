package estructuras;

import java.nio.file.Path;
import java.util.Comparator;
import java.util.Locale;
import java.util.Scanner;


/**
 * Menu interactivo principal.
 */
public final class Main {

    private Main() {
    }

    public static void main(String[] args) {
        SistemaAcademico sistema = new SistemaAcademico();
        try (Scanner sc = new Scanner(System.in).useDelimiter("\n")) {
            boolean salir = false;
            while (!salir) {
                mostrarMenuPrincipal();
                String op = sc.nextLine().trim();
                try {
                    switch (op) {
                        case "1" -> registrarEstudiante(sc, sistema);
                        case "2" -> buscarEstudiante(sc, sistema);
                        case "3" -> listarEstudiantes(sistema);
                        case "4" -> eliminarEstudiante(sc, sistema);
                        case "5" -> crearMateria(sc, sistema);
                        case "6" -> mostrarTodasMaterias(sistema);
                        case "7" -> crearDiezMaterias(sistema);
                        case "8" -> agregarPrerrequisito(sc, sistema);
                        case "9" -> mostrarPrerrequisitos(sc, sistema);
                        case "10" -> inscribir(sc, sistema);
                        case "11" -> cancelarInscripcion(sc, sistema);
                        case "12" -> mostrarCola(sc, sistema);
                        case "13" -> mostrarAulas(sistema);
                        case "14" -> mostrarAulasDisponibles(sc, sistema);
                        case "15" -> reservarHorario(sc, sistema);
                        case "16" -> liberarHorario(sc, sistema);
                        case "17" -> consultarHorario(sc, sistema);
                        case "18" -> agregarConexionEdificios(sc, sistema);
                        case "19" -> calcularRuta(sc, sistema);
                        case "20" -> registrarNota(sc, sistema);
                        case "21" -> verReporte(sc, sistema);
                        case "22" -> atrasReporte(sistema);
                        case "23" -> sistema.deshacerUltimaOperacionGlobal();
                        case "24" -> sistema.rehacerUltimaOperacionGlobal();
                        case "25" -> procesarCsv(sc, sistema);
                        case "26" -> listarDatosEjemplo(sistema);
                        case "0" -> salir = true;
                        default -> System.out.println("Opcion no valida.");
                    }
                } catch (PilaDeshacerVaciaException ex) {
                    System.out.println("Error: PilaDeshacerVaciaException - " + ex.getMessage());
                } catch (EstudianteNoEncontradoException ex) {
                    System.out.println("Error: EstudianteNoEncontradoException - " + ex.getMessage());
                } catch (PreRequisitoNoAprobadoException ex) {
                    System.out.println("Error: PreRequisitoNoAprobadoException - " + ex.getMessage());
                } catch (HorarioConflictivoException ex) {
                    System.out.println("Error: HorarioConflictivoException - " + ex.getMessage());
                } catch (ColaDeEsperaVaciaException ex) {
                    System.out.println("Error: ColaDeEsperaVaciaException - " + ex.getMessage());
                } catch (ArchivoInvalidoException ex) {
                    System.out.println("Error: ArchivoInvalidoException - " + ex.getMessage());
                } catch (Exception ex) {
                    System.out.println("Error: " + ex.getClass().getSimpleName() + " - " + ex.getMessage());
                }
            }
        }
        System.out.println("Fin del programa.");
    }

    private static void mostrarMenuPrincipal() {
        System.out.println();
        System.out.println("============================================================");
        System.out.println("PLANIFICACION ACADEMICA - SISTEMA UNIVERSITARIO");
        System.out.println("============================================================");
        System.out.println("=== GESTION DE ESTUDIANTES ===");
        System.out.println("1. Registrar estudiante");
        System.out.println("2. Buscar estudiante por ID");
        System.out.println("3. Listar todos los estudiantes");
        System.out.println("4. Eliminar estudiante");
        System.out.println("=== GESTION DE MATERIAS ===");
        System.out.println("5. Crear materia");
        System.out.println("6. Mostrar todas las materias");
        System.out.println("7. Crear 10 materias de ejemplo");
        System.out.println("8. Agregar pre-requisito");
        System.out.println("9. Mostrar pre-requisitos");
        System.out.println("10. Inscribir estudiante");
        System.out.println("11. Cancelar inscripcion");
        System.out.println("12. Mostrar cola de espera");
        System.out.println("=== GESTION DE HORARIOS ===");
        System.out.println("13. Mostrar aulas");
        System.out.println("14. Mostrar aulas disponibles");
        System.out.println("15. Reservar horario en aula");
        System.out.println("16. Liberar horario");
        System.out.println("17. Consultar disponibilidad");
        System.out.println("=== RUTAS ENTRE EDIFICIOS ===");
        System.out.println("18. Agregar conexion entre edificios");
        System.out.println("19. Calcular ruta mas corta");
        System.out.println("=== REPORTES ACADEMICOS ===");
        System.out.println("20. Registrar nota");
        System.out.println("21. Ver reporte academico");
        System.out.println("22. Navegador de reportes (atras)");
        System.out.println("=== SISTEMA DESHACER/REHACER ===");
        System.out.println("21. Deshacer ultima operacion global");
        System.out.println("22. Rehacer ultima operacion global");
        System.out.println("=== PROCESAMIENTO POR LOTES ===");
        System.out.println("25. Procesar archivo CSV");
        System.out.println("=== UTIL ===");
        System.out.println("26. Listar edificios, aulas y facultades de ejemplo");
        System.out.println("0. Salir");
        System.out.print("Seleccione una opcion: ");
    }

    private static void registrarEstudiante(Scanner sc, SistemaAcademico sistema) {
        System.out.print("ID: ");
        String id = sc.nextLine().trim();
        System.out.print("Nombre: ");
        String nombre = sc.nextLine().trim();
        System.out.print("Email: ");
        String email = sc.nextLine().trim();
        System.out.print("Semestre actual (1-10): ");
        int sem = Integer.parseInt(sc.nextLine().trim());
        Estudiante e = new Estudiante(id, nombre, email, sem);
        sistema.registrarEstudiante(e);
        System.out.println("Estudiante registrado exitosamente.");
    }

    private static void buscarEstudiante(Scanner sc, SistemaAcademico sistema) throws EstudianteNoEncontradoException {
        System.out.print("ID: ");
        String id = sc.nextLine().trim();
        Estudiante e = sistema.buscarEstudiante(id);
        System.out.println("Resultado encontrado:");
        System.out.println(e.mostrarInformacion());
    }

    private static void listarEstudiantes(SistemaAcademico sistema) {
        sistema.listarEstudiantesOrdenados().forEach(e -> System.out.println(e.getId() + " - " + e.getNombre()));
    }

    private static void eliminarEstudiante(Scanner sc, SistemaAcademico sistema) throws EstudianteNoEncontradoException {
        System.out.print("ID: ");
        String id = sc.nextLine().trim();
        sistema.eliminarEstudianteConPila(id);
        System.out.println("Estudiante eliminado (se puede deshacer con opcion 19).");
    }

    private static void crearMateria(Scanner sc, SistemaAcademico sistema) {
        System.out.print("Codigo: ");
        String cod = sc.nextLine().trim();
        System.out.print("Nombre: ");
        String nom = sc.nextLine().trim();
        System.out.print("Cupos maximos: ");
        int cupos = Integer.parseInt(sc.nextLine().trim());
        System.out.print("Creditos: ");
        int cred = Integer.parseInt(sc.nextLine().trim());
        sistema.registrarMateria(new Materia(cod, nom, cupos, cred));
        System.out.println("Materia creada.");
    }

    private static void mostrarTodasMaterias(SistemaAcademico sistema) {
        if (sistema.getMaterias().isEmpty()) {
            System.out.println("No hay materias registradas.");
            return;
        }
        sistema.getMaterias().values().stream()
                .sorted(Comparator.comparing(Materia::getCodigo))
                .forEach(m -> System.out.println(m.getCodigo() + " - " + m.getNombre()
                        + " | Cupos: " + m.getCuposMaximos()
                        + " | Creditos: " + m.getCreditos()));
    }

    private static void crearDiezMaterias(SistemaAcademico sistema) {
        Materia[] materias = new Materia[] {
                new Materia("MAT101", "Matematica I", 50, 4),
                new Materia("MAT102", "Matematica II", 50, 4),
                new Materia("PROG101", "Programacion I", 45, 5),
                new Materia("PROG102", "Programacion II", 45, 5),
                new Materia("FIS101", "Fisica I", 40, 4),
                new Materia("FIS102", "Fisica II", 40, 4),
                new Materia("QUI101", "Quimica General", 40, 4),
                new Materia("ECO101", "Economia", 60, 3),
                new Materia("ING101", "Ingles Tecnico", 60, 2),
                new Materia("FAS101", "Fundamentos de Arquitectura de Sistemas", 35, 3)
        };
        int creadas = 0;
        for (Materia m : materias) {
            if (!sistema.getMaterias().containsKey(m.getCodigo().toUpperCase(Locale.ROOT))) {
                sistema.registrarMateria(m);
                creadas++;
            }
        }
        System.out.println("Se agregaron " + creadas + " materias de ejemplo.");
        if (creadas == 0) {
            System.out.println("Ya existian todas las materias de ejemplo.");
        }
    }

    private static void agregarPrerrequisito(Scanner sc, SistemaAcademico sistema) {
        System.out.print("Codigo materia: ");
        String cod = sc.nextLine().trim();
        System.out.print("Codigo prerrequisito: ");
        String pre = sc.nextLine().trim();
        sistema.agregarPrerrequisito(cod, pre);
        System.out.println("Prerrequisito agregado.");
    }

    private static void mostrarPrerrequisitos(Scanner sc, SistemaAcademico sistema) {
        System.out.print("Codigo materia: ");
        String cod = sc.nextLine().trim();
        System.out.println(sistema.mostrarPrerrequisitos(cod));
    }

    private static void inscribir(Scanner sc, SistemaAcademico sistema)
            throws EstudianteNoEncontradoException, PreRequisitoNoAprobadoException {
        System.out.print("ID estudiante: ");
        String id = sc.nextLine().trim();
        System.out.print("Codigo materia: ");
        String cod = sc.nextLine().trim();
        String msg = sistema.inscribirEstudianteEnMateria(id, cod);
        System.out.println(msg);
    }

    private static void cancelarInscripcion(Scanner sc, SistemaAcademico sistema)
            throws EstudianteNoEncontradoException {
        System.out.print("ID estudiante: ");
        String id = sc.nextLine().trim();
        System.out.print("Codigo materia: ");
        String cod = sc.nextLine().trim();
        System.out.println(sistema.cancelarInscripcionConPila(id, cod));
    }

    private static void mostrarCola(Scanner sc, SistemaAcademico sistema) throws ColaDeEsperaVaciaException {
        System.out.print("Codigo materia: ");
        String cod = sc.nextLine().trim();
        System.out.println(sistema.mostrarColaEspera(cod));
    }

    private static void mostrarAulas(SistemaAcademico sistema) {
        System.out.println("Aulas registradas:");
        sistema.getAulasPorNombre().values().forEach(a -> System.out.println(
                a.getNombre() + " | Capacidad: " + a.getCapacidad()));
    }

    private static void mostrarAulasDisponibles(Scanner sc, SistemaAcademico sistema) {
        System.out.print("Dia (0=Domingo .. 6=Sabado): ");
        int dia = Integer.parseInt(sc.nextLine().trim());
        System.out.print("Hora (0-23): ");
        int hora = Integer.parseInt(sc.nextLine().trim());
        System.out.println("Aulas disponibles en dia " + dia + " hora " + hora + ":");
        sistema.getAulasPorNombre().values().stream()
                .filter(a -> a.consultarDisponibilidad(dia, hora))
                .forEach(a -> System.out.println(a.getNombre() + " | Capacidad: " + a.getCapacidad()));
    }

    private static void reservarHorario(Scanner sc, SistemaAcademico sistema) throws HorarioConflictivoException {
        System.out.print("Nombre aula: ");
        String aula = sc.nextLine().trim();
        System.out.print("Dia (0=Domingo .. 6=Sabado): ");
        int dia = Integer.parseInt(sc.nextLine().trim());
        System.out.print("Hora (0-23): ");
        int hora = Integer.parseInt(sc.nextLine().trim());
        System.out.print("Duracion (horas): ");
        int d = Integer.parseInt(sc.nextLine().trim());
        sistema.reservarHorarioConPila(aula, dia, hora, d);
        System.out.println("Reserva exitosa.");
    }

    private static void liberarHorario(Scanner sc, SistemaAcademico sistema) {
        System.out.print("Nombre aula: ");
        String aula = sc.nextLine().trim();
        System.out.print("Dia: ");
        int dia = Integer.parseInt(sc.nextLine().trim());
        System.out.print("Hora: ");
        int hora = Integer.parseInt(sc.nextLine().trim());
        System.out.print("Duracion: ");
        int d = Integer.parseInt(sc.nextLine().trim());
        sistema.liberarHorarioConPila(aula, dia, hora, d);
        System.out.println("Horario liberado.");
    }

    private static void consultarHorario(Scanner sc, SistemaAcademico sistema) {
        System.out.print("Nombre aula: ");
        String aula = sc.nextLine().trim();
        System.out.print("Dia: ");
        int dia = Integer.parseInt(sc.nextLine().trim());
        System.out.print("Hora: ");
        int hora = Integer.parseInt(sc.nextLine().trim());
        boolean libre = sistema.consultarDisponibilidadAula(aula, dia, hora);
        System.out.println(libre ? "LIBRE" : "OCUPADO");
    }

    private static void agregarConexionEdificios(Scanner sc, SistemaAcademico sistema) {
        listarEdificios(sistema);
        System.out.print("Indice edificio A: ");
        int i = Integer.parseInt(sc.nextLine().trim());
        System.out.print("Indice edificio B: ");
        int j = Integer.parseInt(sc.nextLine().trim());
        System.out.print("Distancia (metros): ");
        int m = Integer.parseInt(sc.nextLine().trim());
        sistema.establecerDistanciaSimetrica(i, j, m);
        System.out.println("Conexion guardada.");
    }

    private static void calcularRuta(Scanner sc, SistemaAcademico sistema) {
        listarEdificios(sistema);
        System.out.print("Origen (indice): ");
        int o = Integer.parseInt(sc.nextLine().trim());
        System.out.print("Destino (indice): ");
        int d = Integer.parseInt(sc.nextLine().trim());
        System.out.println("--- RESULTADO ---");
        System.out.println(sistema.calcularRutaMasCorta(o, d));
    }

    private static void registrarNota(Scanner sc, SistemaAcademico sistema) throws EstudianteNoEncontradoException {
        System.out.print("ID estudiante: ");
        String id = sc.nextLine().trim();
        System.out.print("Semestre (1-10): ");
        int sem = Integer.parseInt(sc.nextLine().trim());
        System.out.print("Codigo materia: ");
        String cod = sc.nextLine().trim();
        System.out.print("Nota (0-5): ");
        double nota = Double.parseDouble(sc.nextLine().trim());
        System.out.println(sistema.registrarNotaConPila(id, sem, cod, nota));
    }

    private static void verReporte(Scanner sc, SistemaAcademico sistema) throws EstudianteNoEncontradoException {
        System.out.print("ID estudiante: ");
        String id = sc.nextLine().trim();
        System.out.println("--- REPORTE ACADEMICO ---");
        System.out.println(sistema.verReporteAcademicoYApilar(id));
    }

    private static void atrasReporte(SistemaAcademico sistema) throws PilaDeshacerVaciaException {
        String prev = sistema.atrasUltimoReporte();
        System.out.println("--- REPORTE ANTERIOR ---");
        System.out.println(prev);
    }

    private static void procesarCsv(Scanner sc, SistemaAcademico sistema) throws Exception {
        System.out.print("Ruta del archivo CSV: ");
        String ruta = sc.nextLine().trim();
        System.out.println(sistema.procesarInscripcionesDesdeArchivo(Path.of(ruta)));
    }

    private static void listarDatosEjemplo(SistemaAcademico sistema) {
        listarEdificios(sistema);
        System.out.println("--- AULAS (TreeMap por nombre) ---");
        sistema.getAulasPorNombre().values().forEach(a -> System.out.println(
                a.getNombre() + " cap " + a.getCapacidad()));
        System.out.println("--- FACULTADES [5] ---");
        for (int i = 0; i < sistema.getFacultades().length; i++) {
            if (sistema.getFacultades()[i] != null) {
                System.out.println(i + ": " + sistema.getFacultades()[i]);
            }
        }
    }

    private static void listarEdificios(SistemaAcademico sistema) {
        System.out.println("Edificios registrados:");
        String[] n = sistema.getNombresEdificios();
        for (int i = 0; i < n.length; i++) {
            System.out.println(i + ": " + n[i]);
        }
    }
}
