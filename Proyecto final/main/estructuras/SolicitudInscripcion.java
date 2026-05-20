package estructuras;


/** Linea de solicitud para procesamiento batch (cola). */
public record SolicitudInscripcion(String idEstudiante, String codigoMateria) {
}
