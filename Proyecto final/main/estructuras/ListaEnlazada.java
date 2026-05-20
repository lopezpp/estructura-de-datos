package estructuras;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;

/**
 * Lista enlazada simple (prerrequisitos e historial de materias cursadas).
 */
public class ListaEnlazada<T> implements Iterable<T> {

    private Nodo<T> cabeza;
    private int tamano;

    public void agregarAlFinal(T dato) {
        Objects.requireNonNull(dato, "dato");
        if (cabeza == null) {
            cabeza = new Nodo<>(dato, null);
        } else {
            Nodo<T> actual = cabeza;
            while (actual.siguiente != null) {
                actual = actual.siguiente;
            }
            actual.siguiente = new Nodo<>(dato, null);
        }
        tamano++;
    }

    public boolean contiene(T dato) {
        for (T t : this) {
            if (Objects.equals(t, dato)) {
                return true;
            }
        }
        return false;
    }

    public boolean eliminarPrimeraOcurrencia(T dato) {
        if (cabeza == null) {
            return false;
        }
        if (Objects.equals(cabeza.dato, dato)) {
            cabeza = cabeza.siguiente;
            tamano--;
            return true;
        }
        Nodo<T> prev = cabeza;
        while (prev.siguiente != null) {
            if (Objects.equals(prev.siguiente.dato, dato)) {
                prev.siguiente = prev.siguiente.siguiente;
                tamano--;
                return true;
            }
            prev = prev.siguiente;
        }
        return false;
    }

    public int tamano() {
        return tamano;
    }

    public boolean estaVacia() {
        return tamano == 0;
    }

    public void limpiar() {
        cabeza = null;
        tamano = 0;
    }

    public ListaEnlazada<T> copiar() {
        ListaEnlazada<T> otra = new ListaEnlazada<>();
        for (T t : this) {
            otra.agregarAlFinal(t);
        }
        return otra;
    }

    @Override
    public Iterator<T> iterator() {
        return new Iterator<>() {
            Nodo<T> cursor = cabeza;

            @Override
            public boolean hasNext() {
                return cursor != null;
            }

            @Override
            public T next() {
                if (cursor == null) {
                    throw new NoSuchElementException();
                }
                T v = cursor.dato;
                cursor = cursor.siguiente;
                return v;
            }
        };
    }

    private static final class Nodo<E> {
        private final E dato;
        private Nodo<E> siguiente;

        private Nodo(E dato, Nodo<E> siguiente) {
            this.dato = dato;
            this.siguiente = siguiente;
        }
    }
}
