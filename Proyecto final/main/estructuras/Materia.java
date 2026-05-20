package estructuras;


import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.List;
import java.util.Objects;



/**
 * Materia con prerrequisitos en lista enlazada y cola de espera cuando el cupo está lleno.
 */
public class Materia {

    private final String codigo;
    private final String nombre;
    private final int cuposMaximos;
    private final int creditos;
    private final ListaEnlazada<String> prerrequisitos;
    private final List<Estudiante> inscritos;
    private final Deque<Estudiante> colaEspera;

    public Materia(String codigo, String nombre, int cuposMaximos, int creditos) {
        this.codigo = Objects.requireNonNull(codigo, "codigo").trim();
        this.nombre = Objects.requireNonNull(nombre, "nombre").trim();
        if (cuposMaximos < 0 || creditos < 0) {
            throw new IllegalArgumentException("cupos y creditos deben ser >= 0");
        }
        this.cuposMaximos = cuposMaximos;
        this.creditos = creditos;
        this.prerrequisitos = new ListaEnlazada<>();
        this.inscritos = new ArrayList<>();
        this.colaEspera = new ArrayDeque<>();
    }

    public String getCodigo() {
        return codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public int getCuposMaximos() {
        return cuposMaximos;
    }

    public int getCreditos() {
        return creditos;
    }

    public ListaEnlazada<String> getPrerrequisitos() {
        return prerrequisitos;
    }

    public List<Estudiante> getInscritosVista() {
        return Collections.unmodifiableList(inscritos);
    }

    public int cuposDisponibles() {
        return cuposMaximos - inscritos.size();
    }

    public boolean estaInscrito(Estudiante e) {
        return inscritos.stream().anyMatch(x -> x.getId().equals(e.getId()));
    }

    public boolean estaEnEspera(Estudiante e) {
        return colaEspera.stream().anyMatch(x -> x.getId().equals(e.getId()));
    }

    /** Inscribe directamente si hay cupo. No valida prerrequisitos. */
    public boolean inscribirDirecto(Estudiante e) {
        if (estaInscrito(e)) {
            return true;
        }
        if (cuposDisponibles() <= 0) {
            return false;
        }
        inscritos.add(e);
        return true;
    }

    public void agregarAColaEspera(Estudiante e) {
        if (!estaEnEspera(e) && !estaInscrito(e)) {
            colaEspera.addLast(e);
        }
    }

    public boolean removerDeColaEspera(Estudiante e) {
        return colaEspera.removeIf(x -> x.getId().equals(e.getId()));
    }

    /** Inserta al frente (para deshacer promocion desde cola). */
    public void agregarAlFrenteColaEspera(Estudiante e) {
        if (!estaEnEspera(e) && !estaInscrito(e)) {
            colaEspera.addFirst(e);
        }
    }

    public Estudiante quitarInscripcion(Estudiante e) {
        boolean rem = inscritos.removeIf(x -> x.getId().equals(e.getId()));
        return rem ? e : null;
    }

    /**
     * Asigna cupo al primero en cola si existe y hay cupo.
     *
     * @return estudiante promovido o null
     */
    public Estudiante promoverDesdeColaSiHayCupo() {
        if (cuposDisponibles() <= 0 || colaEspera.isEmpty()) {
            return null;
        }
        Estudiante siguiente = colaEspera.removeFirst();
        inscritos.add(siguiente);
        return siguiente;
    }

    public Deque<Estudiante> getColaEspera() {
        return colaEspera;
    }

    public List<Estudiante> getInscritosModificable() {
        return inscritos;
    }

    @Override
    public String toString() {
        return codigo + " - " + nombre;
    }
}