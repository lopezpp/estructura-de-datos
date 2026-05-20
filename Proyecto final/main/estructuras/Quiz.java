package estructuras;


public class Quiz extends Evaluacion {

    private final int numeroPreguntas;

    public Quiz(String titulo, double ponderacion, int numeroPreguntas) {
        super(titulo, ponderacion);
        this.numeroPreguntas = numeroPreguntas;
    }

    public int getNumeroPreguntas() {
        return numeroPreguntas;
    }

    @Override
    public String tipoEvaluacion() {
        return "Quiz (" + numeroPreguntas + " preguntas)";
    }
}