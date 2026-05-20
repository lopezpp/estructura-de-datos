package estructuras;


/**
 * Intento de reservar un bloque de tiempo ya ocupado en el aula.
 */
public class HorarioConflictivoException extends Exception {

    public HorarioConflictivoException(String mensaje) {
        super(mensaje);
    }
}
