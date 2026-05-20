package estructuras;


public final class OperacionCancelarInscripcion implements OperacionDeshacible {

    private final SistemaAcademico sistema;
    private final String idEstudiante;
    private final String codigoMateria;
    /** Estudiante promovido desde cola al aplicar la cancelacion (puede ser null). */
    private final String idPromovido;

    public OperacionCancelarInscripcion(SistemaAcademico sistema, String idEstudiante, String codigoMateria,
            String idPromovido) {
        this.sistema = sistema;
        this.idEstudiante = idEstudiante;
        this.codigoMateria = codigoMateria.toUpperCase();
        this.idPromovido = idPromovido;
    }

    @Override
    public void deshacer() {
        Estudiante cancelado = sistema.estudianteSinChequeo(idEstudiante);
        Materia m = sistema.materiaSinChequeo(codigoMateria);

        if (idPromovido != null) {
            Estudiante p = sistema.estudianteSinChequeo(idPromovido);
            m.quitarInscripcion(p);
            p.quitarInscripcion(codigoMateria);
            m.agregarAlFrenteColaEspera(p);
        }

        m.inscribirDirecto(cancelado);
        cancelado.agregarInscripcion(codigoMateria);
    }

    @Override
    public void rehacer() {
        sistema.reaplicarCancelacion(idEstudiante, codigoMateria);
    }
}
