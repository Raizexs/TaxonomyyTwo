package cl.unab.inf.sistemaevaluacion.backend;

import java.util.List;

public class Item {
    public enum Tipo { SELECCION, VF }

    private Tipo tipo;
    private String nivel;
    private String enunciado;
    private List<String> opciones;
    private String respuestaCorrecta;
    private int tiempoEstimado;

    public Item(Tipo tipo, String nivel, String enunciado, List<String> opciones, String respuestaCorrecta, int tiempoEstimado) {
        this.tipo = tipo;
        this.nivel = nivel;
        this.enunciado = enunciado;
        this.opciones = opciones;
        this.respuestaCorrecta = respuestaCorrecta;
        this.tiempoEstimado = tiempoEstimado;
    }

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

    // ✅ Método nuevo: evalúa si la respuesta del usuario es correcta
    public boolean esCorrecta(String respuestaUsuario) {
        return respuestaCorrecta.trim().equalsIgnoreCase(respuestaUsuario.trim());
    }

    // ✅ Método nuevo: devuelve tipo como texto útil para análisis
    public String getTipoComoTexto() {
        return tipo == Tipo.SELECCION ? "SeleccionMultiple" : "VerdaderoFalso";
    }
}