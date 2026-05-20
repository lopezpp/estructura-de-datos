package estructuras;


import java.util.ArrayDeque;
import java.util.Deque;

/**
 * Dos pilas: deshacer y rehacer.
 */
public class GestorPilasDeshacer {

    private final Deque<OperacionDeshacible> pilaDeshacer = new ArrayDeque<>();
    private final Deque<OperacionDeshacible> pilaRehacer = new ArrayDeque<>();

    public void registrar(OperacionDeshacible op) {
        pilaDeshacer.push(op);
        pilaRehacer.clear();
    }

    public void deshacer() throws PilaDeshacerVaciaException {
        if (pilaDeshacer.isEmpty()) {
            throw new PilaDeshacerVaciaException("No hay operaciones para deshacer");
        }
        OperacionDeshacible op = pilaDeshacer.pop();
        op.deshacer();
        pilaRehacer.push(op);
    }

    public void rehacer() throws PilaDeshacerVaciaException {
        if (pilaRehacer.isEmpty()) {
            throw new PilaDeshacerVaciaException("No hay operaciones para rehacer");
        }
        OperacionDeshacible op = pilaRehacer.pop();
        op.rehacer();
        pilaDeshacer.push(op);
    }
}
