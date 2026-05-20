package estructuras;


/**
 * Cuando el estudiante no cumple todos los prerrequisitos aprobados de una materia.
 */
public class PreRequisitoNoAprobadoException extends Exception {

    public PreRequisitoNoAprobadoException(String mensaje) {
        super(mensaje);
    }
}

