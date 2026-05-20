package estructuras;


/**
 * Opcional en el enunciado: docente vinculado a la universidad.
 */
public class Profesor extends Persona {

    private String departamento;
    private double salario;

    public Profesor(String id, String nombre, String email, String departamento, double salario) {
        super(id, nombre, email);
        this.departamento = departamento;
        this.salario = salario;
    }

    public String getDepartamento() {
        return departamento;
    }

    public void setDepartamento(String departamento) {
        this.departamento = departamento;
    }

    public double getSalario() {
        return salario;
    }

    public void setSalario(double salario) {
        this.salario = salario;
    }

    @Override
    public String mostrarInformacion() {
        return "ID: " + getId() + System.lineSeparator()
                + "Nombre: " + getNombre() + System.lineSeparator()
                + "Email: " + getEmail() + System.lineSeparator()
                + "Departamento: " + departamento + System.lineSeparator()
                + "Salario: " + salario;
    }
}
