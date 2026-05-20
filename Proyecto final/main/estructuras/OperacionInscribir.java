package estructuras;


public final class OperacionInscribir implements OperacionDeshacible {

    private final SistemaAcademico sistema;
    private final String idEstudiante;
    private final String codigoMateria;
    private final boolean enColaEspera;

    public OperacionInscribir(SistemaAcademico sistema, String idEstudiante, String codigoMateria,
            boolean enColaEspera) {
        this.sistema = sistema;
        this.idEstudiante = idEstudiante;
        this.codigoMateria = codigoMateria.toUpperCase();
        this.enColaEspera = enColaEspera;
    }

    @Override
    public void deshacer() {
        Estudiante e = sistema.estudianteSinChequeo(idEstudiante);
        Materia m = sistema.materiaSinChequeo(codigoMateria);
        if (enColaEspera) {
            m.removerDeColaEspera(e);
        } else {
            m.quitarInscripcion(e);
            e.quitarInscripcion(codigoMateria);
        }
    }

    @Override
    public void rehacer() {
        sistema.reaplicarInscripcion(idEstudiante, codigoMateria, enColaEspera);
    }
}
