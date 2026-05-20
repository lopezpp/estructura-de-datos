package estructuras;

import java.util.Objects;

/**
 * Clase abstracta base de la jerarquia Persona / Estudiante.
 */
public abstract class Persona {

    private final String id;
    private String nombre;
    private String email;

    protected Persona(String id, String nombre, String email) {
        this.id = Objects.requireNonNull(id, "id").trim();
        this.nombre = Objects.requireNonNull(nombre, "nombre").trim();
        this.email = Objects.requireNonNull(email, "email").trim();
    }

    public String getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = Objects.requireNonNull(nombre, "nombre").trim();
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = Objects.requireNonNull(email, "email").trim();
    }

    public abstract String mostrarInformacion();
}