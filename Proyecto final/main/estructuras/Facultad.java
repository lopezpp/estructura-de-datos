package estructuras;

/** Facultad para arreglo fijo {@code Facultad[5]}. */
public class Facultad {

    private final String nombre;
    private final String codigo;

    public Facultad(String codigo, String nombre) {
        this.codigo = codigo;
        this.nombre = nombre;
    }

    public String getCodigo() {
        return codigo;
    }

    public String getNombre() {
        return nombre;
    }

    @Override
    public String toString() {
        return codigo + " - " + nombre;
    }
}
