package estructuras;


public final class OperacionEliminarEstudiante implements OperacionDeshacible {

    private final SistemaAcademico sistema;
    private final Estudiante estudiante;

    public OperacionEliminarEstudiante(SistemaAcademico sistema, Estudiante estudiante) {
        this.sistema = sistema;
        this.estudiante = estudiante;
    }

    @Override
    public void deshacer() {
        sistema.restaurarEstudiante(estudiante);
    }

    @Override
    public void rehacer() {
        sistema.eliminarEstudianteDefinitivoSinPila(estudiante.getId());
    }
}

