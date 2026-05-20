package estructuras;


import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Queue;
import java.util.TreeMap;


/**
 * Fachada del sistema: mapas, matrices de campus/horarios, lotes y pilas de acciones.
 */
public class SistemaAcademico {

    private final Map<String, Estudiante> estudiantesPorId = new HashMap<>();
    private final Map<String, Materia> materiasPorCodigo = new HashMap<>();
    private final NavigableMap<String, Aula> aulasPorNombre = new TreeMap<>();
    private final Facultad[] facultades = new Facultad[5];
    private String[] nombresEdificios;
    private int[][] distanciasEdificiosMetros;
    private final GestorPilasDeshacer historialAcciones = new GestorPilasDeshacer();
    private final Deque<String> pilaReportesParaAtras = new ArrayDeque<>();

    public SistemaAcademico() {
        inicializarFacultadesEjemplo();
        inicializarCampusEjemplo();
    }

    // ---- Acceso interno para operaciones de deshacer/rehacer ----

    Estudiante estudianteSinChequeo(String id) {
        return estudiantesPorId.get(id);
    }

    Materia materiaSinChequeo(String cod) {
        return materiasPorCodigo.get(cod.toUpperCase(Locale.ROOT));
    }

    void restaurarEstudiante(Estudiante e) {
        estudiantesPorId.put(e.getId(), e);
        for (String cod : e.getMateriasInscritas()) {
            Materia m = materiaSinChequeo(cod);
            if (m != null && !m.estaInscrito(e)) {
                m.inscribirDirecto(e);
            }
        }
    }

    void eliminarEstudianteDefinitivoSinPila(String id) {
        Estudiante e = estudiantesPorId.get(id);
        if (e == null) {
            return;
        }
        desvincularEstudianteDeMaterias(e);
        estudiantesPorId.remove(id);
    }

    void reaplicarInscripcion(String idEstudiante, String codigoMateria, boolean enColaEspera) {
        Estudiante e = estudiantesPorId.get(idEstudiante);
        Materia m = materiasPorCodigo.get(codigoMateria.toUpperCase(Locale.ROOT));
        if (e == null || m == null) {
            return;
        }
        if (enColaEspera) {
            m.agregarAColaEspera(e);
            return;
        }
        m.inscribirDirecto(e);
        e.agregarInscripcion(codigoMateria);
    }

    void reaplicarCancelacion(String idEstudiante, String codigoMateria) {
        try {
            cancelarInscripcionInterno(idEstudiante, codigoMateria, false);
        } catch (EstudianteNoEncontradoException ex) {
            // ignorar en rehacer si el estado ya no aplica
        }
    }

    // ---- Getters consulta ----

    public Map<String, Estudiante> getEstudiantes() {
        return Collections.unmodifiableMap(estudiantesPorId);
    }

    public Map<String, Materia> getMaterias() {
        return Collections.unmodifiableMap(materiasPorCodigo);
    }

    public NavigableMap<String, Aula> getAulasPorNombre() {
        return Collections.unmodifiableNavigableMap(aulasPorNombre);
    }

    public Facultad[] getFacultades() {
        return facultades;
    }

    public String[] getNombresEdificios() {
        return nombresEdificios;
    }

    public int[][] getDistanciasEdificiosMetros() {
        return distanciasEdificiosMetros;
    }

    // ---- Estudiantes ----

    public void registrarEstudiante(Estudiante e) {
        estudiantesPorId.put(e.getId(), e);
    }

    public Estudiante buscarEstudiante(String id) throws EstudianteNoEncontradoException {
        Estudiante e = estudiantesPorId.get(id);
        if (e == null) {
            throw new EstudianteNoEncontradoException("No existe estudiante con ID: " + id);
        }
        return e;
    }

    public List<Estudiante> listarEstudiantesOrdenados() {
        List<Estudiante> lista = new ArrayList<>(estudiantesPorId.values());
        lista.sort(java.util.Comparator.comparing(Persona::getId));
        return lista;
    }

    public void eliminarEstudianteConPila(String id) throws EstudianteNoEncontradoException {
        Estudiante e = buscarEstudiante(id);
        desvincularEstudianteDeMaterias(e);
        estudiantesPorId.remove(id);
        historialAcciones.registrar(new OperacionEliminarEstudiante(this, e));
    }

    private void desvincularEstudianteDeMaterias(Estudiante e) {
        for (Materia m : materiasPorCodigo.values()) {
            if (m.estaInscrito(e)) {
                m.quitarInscripcion(e);
                e.quitarInscripcion(m.getCodigo());
                Estudiante prom = m.promoverDesdeColaSiHayCupo();
                if (prom != null) {
                    prom.agregarInscripcion(m.getCodigo());
                }
            }
            m.removerDeColaEspera(e);
        }
    }

    // ---- Materias ----

    public void registrarMateria(Materia m) {
        materiasPorCodigo.put(m.getCodigo().toUpperCase(Locale.ROOT), m);
    }

    public void agregarPrerrequisito(String codigoMateria, String codigoPrerrequisito) {
        Materia m = materiasPorCodigo.get(codigoMateria.toUpperCase(Locale.ROOT));
        if (m == null) {
            throw new IllegalArgumentException("Materia inexistente: " + codigoMateria);
        }
        m.getPrerrequisitos().agregarAlFinal(codigoPrerrequisito.toUpperCase(Locale.ROOT));
    }

    public String mostrarPrerrequisitos(String codigoMateria) {
        Materia m = materiasPorCodigo.get(codigoMateria.toUpperCase(Locale.ROOT));
        if (m == null) {
            return "Materia inexistente.";
        }
        if (m.getPrerrequisitos().estaVacia()) {
            return "Sin prerrequisitos.";
        }
        StringBuilder sb = new StringBuilder();
        for (String p : m.getPrerrequisitos()) {
            sb.append("- ").append(p).append(System.lineSeparator());
        }
        return sb.toString().trim();
    }

    public String inscribirEstudianteEnMateria(String idEstudiante, String codigoMateria)
            throws EstudianteNoEncontradoException, PreRequisitoNoAprobadoException {
        Estudiante e = buscarEstudiante(idEstudiante);
        Materia m = materiasPorCodigo.get(codigoMateria.toUpperCase(Locale.ROOT));
        if (m == null) {
            return "Materia inexistente: " + codigoMateria;
        }
        validarPrerrequisitos(e, m);

        if (m.estaInscrito(e) || m.estaEnEspera(e)) {
            return "El estudiante ya esta inscrito o en cola de espera.";
        }

        boolean enEspera;
        if (m.cuposDisponibles() > 0) {
            m.inscribirDirecto(e);
            e.agregarInscripcion(m.getCodigo());
            enEspera = false;
        } else {
            m.agregarAColaEspera(e);
            enEspera = true;
        }

        historialAcciones.registrar(new OperacionInscribir(this, idEstudiante, m.getCodigo(), enEspera));
        return enEspera ? "Materia llena. Agregada a COLA DE ESPERA" : "Inscripcion exitosa";
    }

    private static void validarPrerrequisitos(Estudiante e, Materia m) throws PreRequisitoNoAprobadoException {
        for (String codPre : m.getPrerrequisitos()) {
            if (!e.tieneAprobado(codPre)) {
                throw new PreRequisitoNoAprobadoException(
                        "Falta aprobar prerrequisito " + codPre + " para " + m.getCodigo());
            }
        }
    }

    public String cancelarInscripcionConPila(String idEstudiante, String codigoMateria)
            throws EstudianteNoEncontradoException {
        return cancelarInscripcionInterno(idEstudiante, codigoMateria, true);
    }

    private String cancelarInscripcionInterno(String idEstudiante, String codigoMateria, boolean apilar)
            throws EstudianteNoEncontradoException {
        Estudiante e = buscarEstudiante(idEstudiante);
        Materia m = materiasPorCodigo.get(codigoMateria.toUpperCase(Locale.ROOT));
        if (m == null) {
            return "Materia inexistente.";
        }
        if (!m.estaInscrito(e)) {
            if (m.removerDeColaEspera(e)) {
                return "Estudiante retirado de la cola de espera.";
            }
            return "El estudiante no estaba inscrito en esta materia.";
        }

        m.quitarInscripcion(e);
        e.quitarInscripcion(m.getCodigo());

        Estudiante promovido = m.promoverDesdeColaSiHayCupo();
        String idProm = null;
        if (promovido != null) {
            promovido.agregarInscripcion(m.getCodigo());
            idProm = promovido.getId();
        }

        if (apilar) {
            historialAcciones.registrar(new OperacionCancelarInscripcion(this, idEstudiante, m.getCodigo(), idProm));
        }
        return "Cancelacion exitosa.";
    }

    public String mostrarColaEspera(String codigoMateria) throws ColaDeEsperaVaciaException {
        Materia m = materiasPorCodigo.get(codigoMateria.toUpperCase(Locale.ROOT));
        if (m == null) {
            return "Materia inexistente.";
        }
        if (m.getColaEspera().isEmpty()) {
            throw new ColaDeEsperaVaciaException("La cola de espera esta vacia para " + codigoMateria);
        }
        int i = 1;
        StringBuilder sb = new StringBuilder();
        for (Estudiante e : m.getColaEspera()) {
            sb.append("Posicion ").append(i++).append(": ").append(e.getNombre()).append(System.lineSeparator());
        }
        sb.append("Total en espera: ").append(m.getColaEspera().size());
        return sb.toString().trim();
    }

    // ---- Horarios ----

    public void reservarHorarioConPila(String nombreAula, int dia, int hora, int duracion)
            throws HorarioConflictivoException {
        Aula a = requireAula(nombreAula);
        boolean[][] antes = a.copiaOcupacion();
        a.reservar(dia, hora, duracion);
        boolean[][] despues = a.copiaOcupacion();
        historialAcciones.registrar(new OperacionCambioHorario(a, antes, despues));
    }

    public void liberarHorarioConPila(String nombreAula, int dia, int hora, int duracion) {
        Aula a = requireAula(nombreAula);
        boolean[][] antes = a.copiaOcupacion();
        a.liberar(dia, hora, duracion);
        boolean[][] despues = a.copiaOcupacion();
        historialAcciones.registrar(new OperacionCambioHorario(a, antes, despues));
    }

    public boolean consultarDisponibilidadAula(String nombreAula, int dia, int hora) {
        return requireAula(nombreAula).consultarDisponibilidad(dia, hora);
    }

    private Aula requireAula(String nombre) {
        Aula a = aulasPorNombre.get(nombre);
        if (a == null) {
            throw new IllegalArgumentException("Aula inexistente: " + nombre);
        }
        return a;
    }

    public void registrarAula(Aula a) {
        aulasPorNombre.put(a.getNombre(), a);
    }

    // ---- Rutas (Dijkstra) ----

    public void establecerDistanciaSimetrica(int i, int j, int metros) {
        if (nombresEdificios == null || distanciasEdificiosMetros == null) {
            throw new IllegalStateException("Campus no inicializado");
        }
        if (i < 0 || j < 0 || i >= nombresEdificios.length || j >= nombresEdificios.length) {
            throw new IllegalArgumentException("Indices invalidos");
        }
        if (i == j) {
            distanciasEdificiosMetros[i][j] = 0;
            return;
        }
        distanciasEdificiosMetros[i][j] = metros;
        distanciasEdificiosMetros[j][i] = metros;
    }

    public String calcularRutaMasCorta(int origen, int destino) {
        if (nombresEdificios == null) {
            return "Campus no inicializado.";
        }
        DijkstraCaminoCorto.Resultado r = DijkstraCaminoCorto.calcular(distanciasEdificiosMetros, origen, destino);
        if (r.distanciaTotalMetros() < 0) {
            return "No hay ruta entre los edificios indicados.";
        }
        StringBuilder sb = new StringBuilder();
        List<Integer> camino = r.caminoIndices();
        for (int k = 0; k < camino.size(); k++) {
            int idx = camino.get(k);
            if (k == 0) {
                sb.append(nombresEdificios[idx]);
            } else {
                int prev = camino.get(k - 1);
                int tramo = distanciasEdificiosMetros[prev][idx];
                sb.append(" -> ").append(nombresEdificios[idx])
                        .append(" (").append(tramo).append("m)");
            }
        }
        sb.append(System.lineSeparator())
                .append("Distancia TOTAL: ")
                .append(r.distanciaTotalMetros())
                .append(" metros");
        return sb.toString();
    }

    // ---- Reportes ----

    public String registrarNotaConPila(String idEstudiante, int semestre, String codigoMateria, double nota)
            throws EstudianteNoEncontradoException {
        Estudiante e = buscarEstudiante(idEstudiante);

        int fila = semestre - 1;
        Double[][] antesNotas = e.getNotasCopia();
        String[][] antesCod = e.getCodigosCopia();
        int colLibre = -1;
        for (int c = 0; c < Estudiante.MAX_MATERIAS_POR_SEMESTRE; c++) {
            if (antesNotas[fila][c] == null) {
                colLibre = c;
                break;
            }
        }
        if (colLibre < 0) {
            return "Sin cupo de materias en ese semestre (max 20).";
        }

        Double notaAnt = antesNotas[fila][colLibre];
        String codAnt = antesCod[fila][colLibre];

        e.asignarNotaEnCelda(semestre, colLibre, nota, codigoMateria);

        historialAcciones.registrar(new OperacionRegistrarNota(e, semestre, colLibre,
                notaAnt, codAnt, nota, codigoMateria.toUpperCase(Locale.ROOT)));

        return "Nota registrada en semestre " + semestre + ", columna " + colLibre + ".";
    }

    public String verReporteAcademicoYApilar(String idEstudiante) throws EstudianteNoEncontradoException {
        Estudiante e = buscarEstudiante(idEstudiante);
        String rep = construirReporteAcademico(e);
        pilaReportesParaAtras.push(rep);
        return rep;
    }

    public String atrasUltimoReporte() throws PilaDeshacerVaciaException {
        if (pilaReportesParaAtras.isEmpty()) {
            throw new PilaDeshacerVaciaException("No hay reportes previos.");
        }
        return pilaReportesParaAtras.pop();
    }

    private static String construirReporteAcademico(Estudiante e) {
        StringBuilder sb = new StringBuilder();
        sb.append("Estudiante: ").append(e.getNombre()).append(" (ID: ").append(e.getId()).append(")")
                .append(System.lineSeparator());

        for (int sem = 1; sem <= Estudiante.MAX_SEMESTRES; sem++) {
            int fila = sem - 1;
            boolean vacio = true;
            for (int c = 0; c < Estudiante.MAX_MATERIAS_POR_SEMESTRE; c++) {
                if (e.getNotasCopia()[fila][c] != null) {
                    vacio = false;
                    break;
                }
            }
            if (vacio) {
                continue;
            }
            sb.append("Semestre ").append(sem).append(":").append(System.lineSeparator());
            Double[][] n = e.getNotasCopia();
            String[][] cd = e.getCodigosCopia();
            for (int c = 0; c < Estudiante.MAX_MATERIAS_POR_SEMESTRE; c++) {
                if (n[fila][c] != null) {
                    sb.append(cd[fila][c]).append(": ").append(String.format(Locale.US, "%.1f", n[fila][c]))
                            .append(System.lineSeparator());
                }
            }
            sb.append("Promedio: ").append(String.format(Locale.US, "%.2f", e.promedioSemestre(sem)))
                    .append(System.lineSeparator());
        }

        sb.append("=== RESUMEN ===").append(System.lineSeparator());
        sb.append("Promedio acumulado: ").append(String.format(Locale.US, "%.2f", e.promedioAcumulado()))
                .append(System.lineSeparator());
        sb.append("Materias aprobadas: ").append(e.contarAprobadas()).append(System.lineSeparator());
        sb.append("Materias reprobadas: ").append(e.contarReprobadas());
        return sb.toString();
    }

    // ---- Lote CSV ----

    public String procesarInscripcionesDesdeArchivo(Path archivoCsv) throws IOException, ArchivoInvalidoException {
        Queue<SolicitudInscripcion> cola = LectorCsvInscripciones.cargarCola(archivoCsv);
        int total = cola.size();
        int ok = 0;
        int fail = 0;
        StringBuilder detalle = new StringBuilder();
        int i = 1;
        while (!cola.isEmpty()) {
            SolicitudInscripcion s = cola.poll();
            String motivo = "";
            try {
                String msg = inscribirEstudianteEnMateria(s.idEstudiante(), s.codigoMateria());
                boolean mala = msg.contains("inexistente")
                        || msg.contains("ya esta")
                        || msg.contains("Sin cupo");
                if (mala) {
                    fail++;
                    motivo = msg;
                } else {
                    ok++;
                    detalle.append("[").append(i).append("/").append(total).append("] ")
                            .append(s.idEstudiante()).append(" -> ").append(s.codigoMateria())
                            .append(" -> Exitosa").append(System.lineSeparator());
                }
            } catch (PreRequisitoNoAprobadoException | EstudianteNoEncontradoException ex) {
                fail++;
                motivo = ex.getClass().getSimpleName() + " - " + ex.getMessage();
            }
            if (!motivo.isEmpty()) {
                detalle.append("[").append(i).append("/").append(total).append("] ")
                        .append(s.idEstudiante()).append(" -> ").append(s.codigoMateria())
                        .append(" -> Fallida: ").append(motivo).append(System.lineSeparator());
            }
            i++;
        }
        return "Se encolaron " + total + " solicitudes." + System.lineSeparator()
                + detalle + "=== RESUMEN ===" + System.lineSeparator()
                + "Exitosas: " + ok + System.lineSeparator()
                + "Fallidas: " + fail;
    }

    // ---- Pilas globales acciones ----

    public void deshacerUltimaOperacionGlobal() throws PilaDeshacerVaciaException {
        historialAcciones.deshacer();
    }

    public void rehacerUltimaOperacionGlobal() throws PilaDeshacerVaciaException {
        historialAcciones.rehacer();
    }

    // ---- Datos ejemplo ----

    private void inicializarFacultadesEjemplo() {
        facultades[0] = new Facultad("ING", "Ingenierias");
        facultades[1] = new Facultad("ADM", "Administracion");
        facultades[2] = new Facultad("HUM", "Humanidades");
        facultades[3] = new Facultad("SAL", "Salud");
        facultades[4] = new Facultad("CIE", "Ciencias Basicas");
    }

    private void inicializarCampusEjemplo() {
        nombresEdificios = new String[] {
                "Ingenieria", "Biblioteca", "Cafeteria", "Rectoria", "Laboratorios"
        };
        int n = nombresEdificios.length;
        distanciasEdificiosMetros = new int[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                distanciasEdificiosMetros[i][j] = (i == j) ? 0 : -1;
            }
        }
        establecerDistanciaSimetrica(0, 1, 120);
        establecerDistanciaSimetrica(0, 2, 150);
        establecerDistanciaSimetrica(1, 3, 90);
        establecerDistanciaSimetrica(2, 3, 180);
        establecerDistanciaSimetrica(2, 4, 110);
        establecerDistanciaSimetrica(3, 4, 95);

        registrarAula(new Aula("101", 40));
        registrarAula(new Aula("102", 35));
        registrarAula(new Aula("103", 30));
        registrarAula(new Aula("AuditorioCentral", 120));
        registrarAula(new Aula("Laboratorio1", 20));
    }
}
