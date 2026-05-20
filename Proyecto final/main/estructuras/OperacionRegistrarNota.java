package estructuras;


public final class OperacionRegistrarNota implements OperacionDeshacible {

    private final Estudiante estudiante;
    private final int semestre;
    private final int columna;
    private final Double notaAnterior;
    private final String codigoAnterior;
    private final Double notaNueva;
    private final String codigoNuevo;

    public OperacionRegistrarNota(Estudiante estudiante, int semestre, int columna,
            Double notaAnterior, String codigoAnterior, Double notaNueva, String codigoNuevo) {
        this.estudiante = estudiante;
        this.semestre = semestre;
        this.columna = columna;
        this.notaAnterior = notaAnterior;
        this.codigoAnterior = codigoAnterior;
        this.notaNueva = notaNueva;
        this.codigoNuevo = codigoNuevo;
    }

    @Override
    public void deshacer() {
        estudiante.asignarNotaEnCelda(semestre, columna, notaAnterior, codigoAnterior);
    }

    @Override
    public void rehacer() {
        estudiante.asignarNotaEnCelda(semestre, columna, notaNueva, codigoNuevo);
    }
}
