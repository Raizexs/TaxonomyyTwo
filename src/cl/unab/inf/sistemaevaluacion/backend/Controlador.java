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
    private Evaluador evaluador; // Nueva variable para almacenar la instancia de Evaluador

    // Constructor por defecto
    public Controlador() {
        this.estadoActual = Estado.INICIO;
        // Inicializa la lista de observadores aquí también, si no se hace en otro constructor
        // o si este es el único que se usa para inicializar
        // this.observadores = new ArrayList<>(); // Ya se inicializa en la declaración
    }

    // Constructor con observador (existente)
    public Controlador(ObservadorEvaluador observador) {
        this(); // Llama al constructor por defecto para inicializar estadoActual
        this.suscribir(observador);
    }

    // Nuevo constructor para aceptar una instancia de Evaluador
    // Este es el constructor que tu Main.java necesita
    public Controlador(Evaluador evaluador) {
        this(); // Llama al constructor por defecto para inicializar estadoActual
        this.evaluador = evaluador; // Asigna la instancia de Evaluador
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
                // Inicializa la respuesta del usuario para este ítem como null
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
        // Asegúrate de que haya ítems antes de intentar acceder a uno
        if (items.isEmpty()) {
            return null; // O lanzar una excepción, dependiendo de tu lógica de negocio
        }
        return items.get(indiceActual);
    }

    public Item obtenerItemActual() {
        return getItemActual();
    }

    public String getRespuestaUsuarioActual() {
        // Asegúrate de que el índice sea válido
        if (indiceActual >= 0 && indiceActual < respuestasUsuario.size()) {
            return respuestasUsuario.get(indiceActual);
        }
        return null; // O lanzar una excepción
    }

    public String obtenerRespuestaUsuarioActual() {
        return getRespuestaUsuarioActual();
    }

    public void responderActual(String respuesta) {
        // Asegúrate de que el índice sea válido antes de establecer la respuesta
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
            // Aquí podrías llamar a evaluador.evaluarResultados() si es necesario
            // y si el método existe en tu clase Evaluador.
            // Por ejemplo: evaluador.evaluarResultados(items, respuestasUsuario);
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

    public Map<String, Integer> resumenPorBloom() {
        // Este método y getResumenPorNivel hacen lo mismo, considera consolidarlos.
        Map<String, Integer> conteoCorrectas = new HashMap<>();
        Map<String, Integer> totalPorNivel = new HashMap<>();

        for (int i = 0; i < items.size(); i++) {
            Item item = items.get(i);
            String nivel = item.getNivel();

            totalPorNivel.put(nivel, totalPorNivel.getOrDefault(nivel, 0) + 1);

            String respuestaUsuario = respuestasUsuario.get(i);
            if (respuestaUsuario != null && item.getRespuestaCorrecta().equalsIgnoreCase(respuestaUsuario)) {
                conteoCorrectas.put(nivel, conteoCorrectas.getOrDefault(nivel, 0) + 1);
            }
        }

        Map<String, Integer> porcentaje = new HashMap<>();
        for (String nivel : totalPorNivel.keySet()) {
            int correctas = conteoCorrectas.getOrDefault(nivel, 0);
            int total = totalPorNivel.get(nivel);
            // Evitar división por cero
            porcentaje.put(nivel, total > 0 ? (int) ((correctas * 100.0) / total) : 0);
        }
        return porcentaje;
    }

    // Este método es idéntico a resumenPorBloom pero devuelve Double.
    // Considera usar solo uno o renombrarlos para mayor claridad.
    public Map<String, Double> getResumenPorNivel() {
        Map<String, Integer> conteoCorrectas = new HashMap<>();
        Map<String, Integer> totalPorNivel = new HashMap<>();

        for (int i = 0; i < items.size(); i++) {
            Item item = items.get(i);
            String nivel = item.getNivel();

            totalPorNivel.put(nivel, totalPorNivel.getOrDefault(nivel, 0) + 1);

            String respuestaUsuario = respuestasUsuario.get(i);
            if (respuestaUsuario != null && item.getRespuestaCorrecta().equalsIgnoreCase(respuestaUsuario)) {
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

    public Map<Item.Tipo, Integer> resumenPorTipo() {
        // Este método y getResumenPorTipo hacen lo mismo, considera consolidarlos.
        Map<Item.Tipo, Integer> conteoCorrectas = new HashMap<>();
        Map<Item.Tipo, Integer> totalPorTipo = new HashMap<>();

        for (int i = 0; i < items.size(); i++) {
            Item item = items.get(i);
            Item.Tipo tipo = item.getTipo();

            totalPorTipo.put(tipo, totalPorTipo.getOrDefault(tipo, 0) + 1);

            String respuestaUsuario = respuestasUsuario.get(i);
            if (respuestaUsuario != null && item.getRespuestaCorrecta().equalsIgnoreCase(respuestaUsuario)) {
                conteoCorrectas.put(tipo, conteoCorrectas.getOrDefault(tipo, 0) + 1);
            }
        }

        Map<Item.Tipo, Integer> porcentaje = new HashMap<>();
        for (Item.Tipo tipo : totalPorTipo.keySet()) {
            int correctas = conteoCorrectas.getOrDefault(tipo, 0);
            int total = totalPorTipo.get(tipo);
            // Evitar división por cero
            porcentaje.put(tipo, total > 0 ? (int) ((correctas * 100.0) / total) : 0);
        }
        return porcentaje;
    }

    // Este método es idéntico a resumenPorTipo pero devuelve Double.
    // Considera usar solo uno o renombrarlos para mayor claridad.
    public Map<Item.Tipo, Double> getResumenPorTipo() {
        Map<Item.Tipo, Integer> conteoCorrectas = new HashMap<>();
        Map<Item.Tipo, Integer> totalPorTipo = new HashMap<>();

        for (int i = 0; i < items.size(); i++) {
            Item item = items.get(i);
            Item.Tipo tipo = item.getTipo();

            totalPorTipo.put(tipo, totalPorTipo.getOrDefault(tipo, 0) + 1);

            String respuestaUsuario = respuestasUsuario.get(i);
            if (respuestaUsuario != null && item.getRespuestaCorrecta().equalsIgnoreCase(respuestaUsuario)) {
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

    public void revisarRespuestas() {
        estadoActual = Estado.REVISION;
        indiceActual = 0;
        notificarObservadores();
    }

    public boolean respuestaEsCorrecta() {
        // Asegúrate de que haya un ítem actual y una respuesta de usuario
        if (getItemActual() == null || getRespuestaUsuarioActual() == null) {
            return false;
        }
        String respuestaUsuario = getRespuestaUsuarioActual();
        return getItemActual().getRespuestaCorrecta().equalsIgnoreCase(respuestaUsuario);
    }

    public void enviarRespuestas() {
        // Aquí podrías usar la instancia de 'evaluador' para procesar las respuestas
        // Por ejemplo: evaluador.procesarRespuestas(items, respuestasUsuario);
        notificarFinalizado();
    }

    public void notificarFinalizado() {
        estadoActual = Estado.RESULTADO;
        notificarObservadores();
    }

    public void volverAlResumen() {
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