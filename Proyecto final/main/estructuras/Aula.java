package estructuras;


/**
 * Aula con disponibilidad en matriz boolean[7][24].
 */
public class Aula {

    private final String nombre;
    private final int capacidad;
    private final boolean[][] ocupacion;

    public Aula(String nombre, int capacidad) {
        if (capacidad < 0) {
            throw new IllegalArgumentException("capacidad negativa");
        }
        this.nombre = nombre;
        this.capacidad = capacidad;
        this.ocupacion = new boolean[7][24];
    }

    public String getNombre() {
        return nombre;
    }

    public int getCapacidad() {
        return capacidad;
    }

    public boolean[][] copiaOcupacion() {
        boolean[][] copia = new boolean[7][24];
        for (int d = 0; d < 7; d++) {
            System.arraycopy(ocupacion[d], 0, copia[d], 0, 24);
        }
        return copia;
    }

    public void restaurarOcupacion(boolean[][] matriz) {
        if (matriz == null || matriz.length != 7) {
            throw new IllegalArgumentException("matriz 7x24 requerida");
        }
        for (int d = 0; d < 7; d++) {
            if (matriz[d] == null || matriz[d].length != 24) {
                throw new IllegalArgumentException("matriz 7x24 requerida");
            }
            System.arraycopy(matriz[d], 0, ocupacion[d], 0, 24);
        }
    }

    public boolean consultarDisponibilidad(int dia, int hora) {
        validarCelda(dia, hora);
        return !ocupacion[dia][hora];
    }

    public void reservar(int dia, int hora, int duracion) throws HorarioConflictivoException {
        validarRango(dia, hora, duracion);
        for (int h = hora; h < hora + duracion; h++) {
            if (ocupacion[dia][h]) {
                throw new HorarioConflictivoException(
                        nombreDia(dia) + " " + h + ":00 ya esta reservado en el aula " + nombre
                );
            }
        }
        for (int h = hora; h < hora + duracion; h++) {
            ocupacion[dia][h] = true;
        }
    }

    public void liberar(int dia, int hora, int duracion) {
        validarRango(dia, hora, duracion);
        for (int h = hora; h < hora + duracion; h++) {
            ocupacion[dia][h] = false;
        }
    }

    private static void validarCelda(int dia, int hora) {
        if (dia < 0 || dia > 6 || hora < 0 || hora > 23) {
            throw new IllegalArgumentException("dia en 0..6 y hora en 0..23");
        }
    }

    private static void validarRango(int dia, int hora, int duracion) {
        if (duracion <= 0) {
            throw new IllegalArgumentException("duracion > 0");
        }
        if (dia < 0 || dia > 6) {
            throw new IllegalArgumentException("dia 0..6");
        }
        if (hora < 0 || hora > 23 || hora + duracion > 24) {
            throw new IllegalArgumentException("rango horario invalido");
        }
    }

    private static String nombreDia(int dia) {
        switch (dia) {
            case 0:
                return "Domingo";
            case 1:
                return "Lunes";
            case 2:
                return "Martes";
            case 3:
                return "Miercoles";
            case 4:
                return "Jueves";
            case 5:
                return "Viernes";
            case 6:
                return "Sabado";
            default:
                return "Dia " + dia;
        }
    }
}
