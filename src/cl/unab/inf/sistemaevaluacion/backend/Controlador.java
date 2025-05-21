package cl.unab.inf.sistemaevaluacion.backend;

import java.util.*;
import java.io.*;

public class Controlador {

    public enum Estado {
        INICIO, PRUEBA, RESULTADO, REVISION
    }

    private Estado estadoActual;
    private final List<Item> items = new ArrayList<>();
    private final List<String> respuestasUsuario = new ArrayList<>();
    private int indiceActual = 0;
    private final List<ObservadorEvaluador> observadores = new ArrayList<>();
    private Evaluador evaluador; // Variable para almacenar la instancia de Evaluador

    // Clase auxiliar para encapsular los detalles de cada respuesta para la revisión
    public static class DetalleRespuesta {
        public Item item;
        public String respuestaUsuario;
        public boolean esCorrecta;

        public DetalleRespuesta(Item item, String respuestaUsuario, boolean esCorrecta) {
            this.item = item;
            this.respuestaUsuario = respuestaUsuario;
            this.esCorrecta = esCorrecta;
        }
    }

    // Constructor por defecto
    public Controlador() {
        this.estadoActual = Estado.INICIO;
    }

    // Constructor con observador
    public Controlador(ObservadorEvaluador observador) {
        this();
        this.suscribir(observador);
    }

    // Nuevo constructor para aceptar una instancia de Evaluador
    public Controlador(Evaluador evaluador) {
        this();
        this.evaluador = evaluador;
    }

    public void suscribir(ObservadorEvaluador o) {
        observadores.add(o);
    }

    public void agregarObservador(ObservadorEvaluador o) {
        suscribir(o);
    }

    private void notificarObservadores() {
        for (ObservadorEvaluador o : observadores) {
            o.actualizar();
        }
    }

    public void cargarItemsDesdeArchivo(File archivo) throws IOException {
        items.clear();
        respuestasUsuario.clear();

        try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                String[] partes = linea.split(";");
                if (partes.length < 6) {
                    throw new IOException("Formato de línea incorrecto: " + linea);
                }

                Item.Tipo tipo = Item.Tipo.valueOf(partes[0].trim().toUpperCase());
                String nivel = partes[1].trim();
                String enunciado = partes[2].trim();
                List<String> opciones = Arrays.asList(partes[3].split("\\|"));
                String respuestaCorrecta = partes[4].trim();
                int tiempo = Integer.parseInt(partes[5].trim());

                Item item = new Item(tipo, nivel, enunciado, opciones, respuestaCorrecta, tiempo);
                items.add(item);
                respuestasUsuario.add(null);
            }
        }

        estadoActual = Estado.INICIO;
        notificarObservadores();
    }

    public int obtenerCantidadItems() {
        return items.size();
    }

    public int getCantidadItems() {
        return obtenerCantidadItems();
    }

    public int obtenerTiempoTotal() {
        return items.stream().mapToInt(Item::getTiempoEstimado).sum();
    }

    public int getTiempoTotal() {
        return obtenerTiempoTotal();
    }

    public void iniciarPrueba() {
        estadoActual = Estado.PRUEBA;
        indiceActual = 0;
        notificarObservadores();
    }

    public boolean estaEnModoPrueba() {
        return estadoActual == Estado.PRUEBA;
    }

    public boolean estaEnModoResultado() {
        return estadoActual == Estado.RESULTADO;
    }

    public boolean estaEnModoRevisar() {
        return estadoActual == Estado.REVISION;
    }

    public Item getItemActual() {
        if (items.isEmpty() || indiceActual < 0 || indiceActual >= items.size()) {
            return null;
        }
        return items.get(indiceActual);
    }

    public Item obtenerItemActual() {
        return getItemActual();
    }

    public String getRespuestaUsuarioActual() {
        if (indiceActual < 0 || indiceActual >= respuestasUsuario.size()) {
            return null;
        }
        return respuestasUsuario.get(indiceActual);
    }

    public String obtenerRespuestaUsuarioActual() {
        return getRespuestaUsuarioActual();
    }

    public void responderActual(String respuesta) {
        if (indiceActual >= 0 && indiceActual < respuestasUsuario.size()) {
            respuestasUsuario.set(indiceActual, respuesta);
        }
    }

    public void registrarRespuesta(String respuesta) {
        responderActual(respuesta);
    }

    public void avanzar() {
        if (indiceActual < items.size() - 1) {
            indiceActual++;
            notificarObservadores();
        } else {
            // Si ya no hay más ítems, la prueba ha terminado
            estadoActual = Estado.RESULTADO;
            notificarObservadores();
        }
    }

    public void siguienteItem() {
        avanzar();
    }

    public void retroceder() {
        if (indiceActual > 0) {
            indiceActual--;
            notificarObservadores();
        }
    }

    public void anteriorItem() {
        retroceder();
    }

    public void siguiente() {
        avanzar();
    }

    public void anterior() {
        retroceder();
    }

    public boolean estaEnPrimero() {
        return indiceActual == 0;
    }

    public boolean estaPrimeraPregunta() {
        return estaEnPrimero();
    }

    public boolean estaUltimaPregunta() {
        return estaEnUltimo();
    }

    public boolean esUltimoItem() {
        return estaEnUltimo();
    }

    public boolean estaEnUltimo() {
        return !items.isEmpty() && indiceActual == items.size() - 1;
    }

    // Métodos para obtener el conteo de respuestas correctas e incorrectas
    public int getRespuestasCorrectasCount() {
        int correctCount = 0;
        for (int i = 0; i < items.size(); i++) {
            Item item = items.get(i);
            String respuestaUsuario = respuestasUsuario.get(i);
            if (item.esCorrecta(respuestaUsuario)) { // Usar el método esCorrecta del Item
                correctCount++;
            }
        }
        return correctCount;
    }

    public int getRespuestasIncorrectasCount() {
        return items.size() - getRespuestasCorrectasCount();
    }

    // Método para obtener una lista detallada de todas las respuestas para revisión
    public List<DetalleRespuesta> getDetalleRespuestas() {
        List<DetalleRespuesta> detalles = new ArrayList<>();
        for (int i = 0; i < items.size(); i++) {
            Item item = items.get(i);
            String respuestaUsuario = respuestasUsuario.get(i);
            boolean esCorrecta = item.esCorrecta(respuestaUsuario);
            detalles.add(new DetalleRespuesta(item, respuestaUsuario, esCorrecta));
        }
        return detalles;
    }

    // Métodos de resumen (ya existentes, solo se asegura que usen el método esCorrecta del Item)
    public Map<String, Double> getResumenPorNivel() {
        Map<String, Integer> conteoCorrectas = new HashMap<>();
        Map<String, Integer> totalPorNivel = new HashMap<>();

        for (int i = 0; i < items.size(); i++) {
            Item item = items.get(i);
            String nivel = item.getNivel();

            totalPorNivel.put(nivel, totalPorNivel.getOrDefault(nivel, 0) + 1);

            String respuestaUsuario = respuestasUsuario.get(i);
            if (item.esCorrecta(respuestaUsuario)) {
                conteoCorrectas.put(nivel, conteoCorrectas.getOrDefault(nivel, 0) + 1);
            }
        }

        Map<String, Double> porcentaje = new HashMap<>();
        for (String nivel : totalPorNivel.keySet()) {
            int correctas = conteoCorrectas.getOrDefault(nivel, 0);
            int total = totalPorNivel.get(nivel);
            double resultado = total > 0 ? (100.0 * correctas / total) : 0.0;
            porcentaje.put(nivel, resultado);
        }
        return porcentaje;
    }

    public Map<Item.Tipo, Double> getResumenPorTipo() {
        Map<Item.Tipo, Integer> conteoCorrectas = new HashMap<>();
        Map<Item.Tipo, Integer> totalPorTipo = new HashMap<>();

        for (int i = 0; i < items.size(); i++) {
            Item item = items.get(i);
            Item.Tipo tipo = item.getTipo();

            totalPorTipo.put(tipo, totalPorTipo.getOrDefault(tipo, 0) + 1);

            String respuestaUsuario = respuestasUsuario.get(i);
            if (item.esCorrecta(respuestaUsuario)) {
                conteoCorrectas.put(tipo, conteoCorrectas.getOrDefault(tipo, 0) + 1);
            }
        }

        Map<Item.Tipo, Double> porcentaje = new HashMap<>();
        for (Item.Tipo tipo : totalPorTipo.keySet()) {
            int correctas = conteoCorrectas.getOrDefault(tipo, 0);
            int total = totalPorTipo.get(tipo);
            double resultado = total > 0 ? (100.0 * correctas / total) : 0.0;
            porcentaje.put(tipo, resultado);
        }
        return porcentaje;
    }

    // Método para iniciar el modo de revisión
    public void revisarRespuestas() {
        estadoActual = Estado.REVISION;
        indiceActual = 0; // Reiniciar el índice para empezar la revisión desde el primer ítem
        notificarObservadores();
    }

    // Método para volver al resumen desde el modo de revisión
    public void volverAlResumen() {
        estadoActual = Estado.RESULTADO;
        notificarObservadores();
    }

    public boolean respuestaEsCorrecta() {
        // Asegúrate de que haya un ítem actual y una respuesta de usuario
        if (getItemActual() == null) {
            return false;
        }
        String respuestaUsuario = getRespuestaUsuarioActual();
        return getItemActual().esCorrecta(respuestaUsuario);
    }

    public void enviarRespuestas() {
        notificarFinalizado();
    }

    public void notificarFinalizado() {
        estadoActual = Estado.RESULTADO;
        notificarObservadores();
    }

    public int getIndiceActual() {
        return indiceActual;
    }

    public List<String> getRespuestasUsuario() {
        return respuestasUsuario;
    }

    public Estado getEstadoActual() {
        return estadoActual;
    }

    public List<Item> getItems() {
        return items;
    }
}