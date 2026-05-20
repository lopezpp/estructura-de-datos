package estructuras;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Queue;


/**
 * Lee CSV de inscripciones: {@code idEstudiante,codigoMateria} por linea.
 */
public final class LectorCsvInscripciones {

    private LectorCsvInscripciones() {
    }

    public static Queue<SolicitudInscripcion> cargarCola(Path archivo) throws IOException, ArchivoInvalidoException {
        if (!Files.isRegularFile(archivo)) {
            throw new ArchivoInvalidoException("No existe el archivo: " + archivo);
        }
        List<String> lineas = new ArrayList<>();
        try (BufferedReader br = Files.newBufferedReader(archivo, StandardCharsets.UTF_8)) {
            String linea;
            while ((linea = br.readLine()) != null) {
                String t = linea.trim();
                if (t.isEmpty() || t.startsWith("#")) {
                    continue;
                }
                lineas.add(t);
            }
        }

        Queue<SolicitudInscripcion> q = new ArrayDeque<>();
        for (String t : lineas) {
            String[] partes = t.split(",");
            if (partes.length < 2) {
                throw new ArchivoInvalidoException("Linea invalida (se espera id,codigo): " + t);
            }
            String id = partes[0].trim();
            String cod = partes[1].trim().toUpperCase(Locale.ROOT);
            if (id.isEmpty() || cod.isEmpty()) {
                throw new ArchivoInvalidoException("Linea con campos vacios: " + t);
            }
            q.add(new SolicitudInscripcion(id, cod));
        }
        return q;
    }
}
