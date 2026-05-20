package estructuras;


/**
 * No hay operaciones para deshacer o para rehacer (mensaje según el caso).
 */
public class PilaDeshacerVaciaException extends Exception {

    public PilaDeshacerVaciaException(String mensaje) {
        super(mensaje);
    }
}
