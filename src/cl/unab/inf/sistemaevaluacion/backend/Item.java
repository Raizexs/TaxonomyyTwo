package cl.unab.inf.sistemaevaluacion.backend;

import java.util.List;

public class Item {
    // Corrección: Los nombres de las constantes del enum deben coincidir
    // exactamente con las cadenas que se esperan del archivo de texto,
    // después de ser convertidas a mayúsculas.
    public enum Tipo {
        SELECCION_MULTIPLE, // Ahora coincide con "SELECCION_MULTIPLE" del archivo
        VERDADERO_FALSO     // Ahora coincide con "VERDADERO_FALSO" del archivo
    }

    private final Tipo tipo; // Se hizo 'final' para que sea inmutable después de la inicialización
    private final String nivel; // Se hizo 'final'
    private final String enunciado; // Se hizo 'final'
    private final List<String> opciones; // Se hizo 'final'
    private final String respuestaCorrecta; // Se hizo 'final'
    private final int tiempoEstimado; // Se hizo 'final'

    public Item(Tipo tipo, String nivel, String enunciado, List<String> opciones, String respuestaCorrecta, int tiempoEstimado) {
        this.tipo = tipo;
        this.nivel = nivel;
        this.enunciado = enunciado;
        this.opciones = opciones;
        this.respuestaCorrecta = respuestaCorrecta;
        this.tiempoEstimado = tiempoEstimado;
    }

    // Getters
    public Tipo getTipo() {
        return tipo;
    }

    public String getNivel() {
        return nivel;
    }

    public String getEnunciado() {
        return enunciado;
    }

    public List<String> getOpciones() {
        return opciones;
    }

    public String getRespuestaCorrecta() {
        return respuestaCorrecta;
    }

    public int getTiempoEstimado() {
        return tiempoEstimado;
    }

    // Método nuevo: evalúa si la respuesta del usuario es correcta
    public boolean esCorrecta(String respuestaUsuario) {
        // Se añade .trim() para asegurar que no haya espacios extra que impidan la comparación
        if (respuestaUsuario == null) { // Manejar el caso de respuesta nula
            return false;
        }
        return respuestaCorrecta.trim().equalsIgnoreCase(respuestaUsuario.trim());
    }

    // Método nuevo: devuelve tipo como texto útil para análisis
    public String getTipoComoTexto() {
        // Se usa un switch para mayor claridad si se añaden más tipos en el futuro
        switch (tipo) {
            case SELECCION_MULTIPLE:
                return "SeleccionMultiple";
            case VERDADERO_FALSO:
                return "VerdaderoFalso";
            default:
                return "Desconocido"; // En caso de añadir un nuevo tipo y olvidarlo aquí
        }
    }
}