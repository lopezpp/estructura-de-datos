package estructuras;


/**
 * Patron comando para deshacer/rehacer.
 */
public interface OperacionDeshacible {

    void deshacer();

    void rehacer();
}
