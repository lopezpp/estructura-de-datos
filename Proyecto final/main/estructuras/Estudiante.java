package estructuras;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;


/**
 * Estudiante con notas en {@code Double[10][20]} y paralelo {@code String[10][20]} para codigos.
 */
public class Estudiante extends Persona {

    public static final int MAX_SEMESTRES = 10;
    public static final int MAX_MATERIAS_POR_SEMESTRE = 20;
    private static final double NOTA_MINIMA_APROBACION = 3.0;

    private int semestreActual;
    private final Double[][] notas;
    private final String[][] codigosMateria;
    private final ListaEnlazada<String> historialMateriasCursadas;
    /** Materias con nota &gt;= 3.0 (atributo auxiliar para validar prerrequisitos rapido). */
    private final Set<String> materiasAprobadasCodigo;
    /** Inscripciones vigentes (codigo materia). */
    private final Set<String> materiasInscritas;

    public Estudiante(String id, String nombre, String email, int semestreActual) {
        super(id, nombre, email);
        setSemestreActual(semestreActual);
        this.notas = new Double[MAX_SEMESTRES][MAX_MATERIAS_POR_SEMESTRE];
        this.codigosMateria = new String[MAX_SEMESTRES][MAX_MATERIAS_POR_SEMESTRE];
        this.historialMateriasCursadas = new ListaEnlazada<>();
        this.materiasAprobadasCodigo = new HashSet<>();
        this.materiasInscritas = new HashSet<>();
    }

    public int getSemestreActual() {
        return semestreActual;
    }

    public void setSemestreActual(int semestreActual) {
        if (semestreActual < 1 || semestreActual > MAX_SEMESTRES) {
            throw new IllegalArgumentException("semestre entre 1 y " + MAX_SEMESTRES);
        }
        this.semestreActual = semestreActual;
    }

    public ListaEnlazada<String> getHistorialMateriasCursadas() {
        return historialMateriasCursadas;
    }

    public Set<String> getMateriasInscritas() {
        return materiasInscritas;
    }

    public boolean agregarInscripcion(String codigoMateria) {
        return materiasInscritas.add(codigoMateria.toUpperCase(Locale.ROOT));
    }

    public boolean quitarInscripcion(String codigoMateria) {
        return materiasInscritas.remove(codigoMateria.toUpperCase(Locale.ROOT));
    }

    public boolean tieneAprobado(String codigoMateria) {
        return materiasAprobadasCodigo.contains(codigoMateria.toUpperCase(Locale.ROOT));
    }

    /**
     * Registra nota en el primer hueco libre del semestre (indice 0-based: semestre 1 -> fila 0).
     *
     * @return posicion columna usada
     */
    public int registrarNota(int semestre, String codigoMateria, double nota) {
        if (semestre < 1 || semestre > MAX_SEMESTRES) {
            throw new IllegalArgumentException("semestre invalido");
        }
        if (nota < 0 || nota > 5) {
            throw new IllegalArgumentException("nota entre 0 y 5");
        }
        int fila = semestre - 1;
        String cod = codigoMateria.toUpperCase(Locale.ROOT);
        for (int c = 0; c < MAX_MATERIAS_POR_SEMESTRE; c++) {
            if (notas[fila][c] == null) {
                asignarNotaEnCelda(semestre, c, nota, cod);
                return c;
            }
        }
        throw new IllegalStateException("semestre lleno (max " + MAX_MATERIAS_POR_SEMESTRE + " materias)");
    }

    /** Para deshacer: poner celda en null y actualizar aprobacion/historial si aplica. */
    public void asignarNotaEnCelda(int semestre, int columna, Double nota, String codigo) {
        if (semestre < 1 || semestre > MAX_SEMESTRES) {
            throw new IllegalArgumentException("semestre invalido");
        }
        if (columna < 0 || columna >= MAX_MATERIAS_POR_SEMESTRE) {
            throw new IllegalArgumentException("columna invalida");
        }
        int fila = semestre - 1;
        notas[fila][columna] = nota;
        if (codigo == null) {
            codigosMateria[fila][columna] = null;
        } else {
            codigosMateria[fila][columna] = codigo.toUpperCase(Locale.ROOT);
        }
        recomputarAprobadasEHistorial();
    }

    public void recomputarAprobadasEHistorial() {
        materiasAprobadasCodigo.clear();
        historialMateriasCursadas.limpiar();
        for (int s = 0; s < MAX_SEMESTRES; s++) {
            for (int m = 0; m < MAX_MATERIAS_POR_SEMESTRE; m++) {
                Double n = notas[s][m];
                String cod = codigosMateria[s][m];
                if (n != null && cod != null && n >= NOTA_MINIMA_APROBACION) {
                    materiasAprobadasCodigo.add(cod);
                    if (!historialMateriasCursadas.contiene(cod)) {
                        historialMateriasCursadas.agregarAlFinal(cod);
                    }
                }
            }
        }
    }

    public Double[][] getNotasCopia() {
        Double[][] c = new Double[MAX_SEMESTRES][MAX_MATERIAS_POR_SEMESTRE];
        for (int i = 0; i < MAX_SEMESTRES; i++) {
            System.arraycopy(notas[i], 0, c[i], 0, MAX_MATERIAS_POR_SEMESTRE);
        }
        return c;
    }

    public String[][] getCodigosCopia() {
        String[][] c = new String[MAX_SEMESTRES][MAX_MATERIAS_POR_SEMESTRE];
        for (int i = 0; i < MAX_SEMESTRES; i++) {
            System.arraycopy(codigosMateria[i], 0, c[i], 0, MAX_MATERIAS_POR_SEMESTRE);
        }
        return c;
    }

    public void restaurarNotas(Double[][] n, String[][] cod) {
        for (int i = 0; i < MAX_SEMESTRES; i++) {
            System.arraycopy(n[i], 0, notas[i], 0, MAX_MATERIAS_POR_SEMESTRE);
            System.arraycopy(cod[i], 0, codigosMateria[i], 0, MAX_MATERIAS_POR_SEMESTRE);
        }
        materiasAprobadasCodigo.clear();
        historialMateriasCursadas.limpiar();
        recomputarAprobadasEHistorial();
    }

    public double promedioSemestre(int semestre) {
        if (semestre < 1 || semestre > MAX_SEMESTRES) {
            throw new IllegalArgumentException("semestre invalido");
        }
        int fila = semestre - 1;
        double suma = 0;
        int cnt = 0;
        for (int c = 0; c < MAX_MATERIAS_POR_SEMESTRE; c++) {
            Double v = notas[fila][c];
            if (v != null) {
                suma += v;
                cnt++;
            }
        }
        return cnt == 0 ? 0.0 : suma / cnt;
    }

    public double promedioAcumulado() {
        double suma = 0;
        int cnt = 0;
        for (int s = 0; s < MAX_SEMESTRES; s++) {
            for (int m = 0; m < MAX_MATERIAS_POR_SEMESTRE; m++) {
                if (notas[s][m] != null) {
                    suma += notas[s][m];
                    cnt++;
                }
            }
        }
        return cnt == 0 ? 0.0 : suma / cnt;
    }

    public int contarReprobadas() {
        int r = 0;
        for (int s = 0; s < MAX_SEMESTRES; s++) {
            for (int m = 0; m < MAX_MATERIAS_POR_SEMESTRE; m++) {
                Double v = notas[s][m];
                if (v != null && v < NOTA_MINIMA_APROBACION) {
                    r++;
                }
            }
        }
        return r;
    }

    public int contarAprobadas() {
        int r = 0;
        for (int s = 0; s < MAX_SEMESTRES; s++) {
            for (int m = 0; m < MAX_MATERIAS_POR_SEMESTRE; m++) {
                Double v = notas[s][m];
                if (v != null && v >= NOTA_MINIMA_APROBACION) {
                    r++;
                }
            }
        }
        return r;
    }

    @Override
    public String mostrarInformacion() {
        return "ID: " + getId() + System.lineSeparator()
                + "Nombre: " + getNombre() + System.lineSeparator()
                + "Email: " + getEmail() + System.lineSeparator()
                + "Semestre: " + semestreActual + System.lineSeparator()
                + "Promedio acumulado: " + String.format(Locale.US, "%.2f", promedioAcumulado());
    }
}
