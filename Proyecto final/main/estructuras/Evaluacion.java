package estructuras;


/** Jerarquia sugerida para evaluaciones (opcional en el UML). */
public abstract class Evaluacion {

    private final String titulo;
    private final double ponderacion;

    protected Evaluacion(String titulo, double ponderacion) {
        this.titulo = titulo;
        this.ponderacion = ponderacion;
    }

    public String getTitulo() {
        return titulo;
    }

    public double getPonderacion() {
        return ponderacion;
    }

    public abstract String tipoEvaluacion();
}
