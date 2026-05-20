package estructuras;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

/**
 * Dijkstra sobre matriz de distancias {@code int[n][n]}.
 * {@code -1} indica que no hay arista (excepto diagonal en 0).
 */
public final class DijkstraCaminoCorto {

    private DijkstraCaminoCorto() {
    }

    public record Resultado(List<Integer> caminoIndices, int distanciaTotalMetros) {
    }

    /**
     * @param distancias matriz simetrica; distancias[i][j] &gt;= 0 o -1 si no hay arista
     * @return camino de indices de edificio desde origen hasta destino inclusive
     */
    public static Resultado calcular(int[][] distancias, int origen, int destino) {
        int n = distancias.length;
        if (origen < 0 || destino < 0 || origen >= n || destino >= n) {
            throw new IllegalArgumentException("indices fuera de rango");
        }
        if (origen == destino) {
            return new Resultado(List.of(origen), 0);
        }

        int[] mejor = new int[n];
        Arrays.fill(mejor, Integer.MAX_VALUE / 4);
        int[] previo = new int[n];
        Arrays.fill(previo, -1);
        mejor[origen] = 0;

        PriorityQueue<int[]> pq = new PriorityQueue<>(Comparator.comparingInt(a -> a[1]));
        pq.add(new int[]{origen, 0});

        while (!pq.isEmpty()) {
            int[] act = pq.poll();
            int u = act[0];
            int dU = act[1];
            if (dU != mejor[u]) {
                continue;
            }
            if (u == destino) {
                break;
            }
            for (int v = 0; v < n; v++) {
                if (v == u) {
                    continue;
                }
                int w = distancias[u][v];
                if (w < 0) {
                    continue;
                }
                int nd = dU + w;
                if (nd < mejor[v]) {
                    mejor[v] = nd;
                    previo[v] = u;
                    pq.add(new int[]{v, nd});
                }
            }
        }

        if (mejor[destino] >= Integer.MAX_VALUE / 8) {
            return new Resultado(List.of(), -1);
        }

        List<Integer> camino = new ArrayList<>();
        for (int cur = destino; cur != -1; cur = previo[cur]) {
            camino.add(cur);
        }
        java.util.Collections.reverse(camino);
        return new Resultado(camino, mejor[destino]);
    }
}
