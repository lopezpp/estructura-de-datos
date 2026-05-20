package estructuras;


public final class OperacionCambioHorario implements OperacionDeshacible {

    private final Aula aula;
    private final boolean[][] estadoAnterior;
    private final boolean[][] estadoPosterior;

    public OperacionCambioHorario(Aula aula, boolean[][] estadoAnterior, boolean[][] estadoPosterior) {
        this.aula = aula;
        this.estadoAnterior = copiarMatriz(estadoAnterior);
        this.estadoPosterior = copiarMatriz(estadoPosterior);
    }

    private static boolean[][] copiarMatriz(boolean[][] m) {
        boolean[][] c = new boolean[7][24];
        for (int d = 0; d < 7; d++) {
            System.arraycopy(m[d], 0, c[d], 0, 24);
        }
        return c;
    }

    @Override
    public void deshacer() {
        aula.restaurarOcupacion(estadoAnterior);
    }

    @Override
    public void rehacer() {
        aula.restaurarOcupacion(estadoPosterior);
    }
}
