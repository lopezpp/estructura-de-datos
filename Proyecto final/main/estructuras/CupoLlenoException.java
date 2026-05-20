package estructuras;


/**
 * Operación inválida cuando no hay cupos y no aplica la cola de espera solicitada.
 */
public class CupoLlenoException extends Exception {

    public CupoLlenoException(String mensaje) {
        super(mensaje);
    }
}
