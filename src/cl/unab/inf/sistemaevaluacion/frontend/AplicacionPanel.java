package cl.unab.inf.sistemaevaluacion.frontend;

import cl.unab.inf.sistemaevaluacion.backend.Controlador;
import cl.unab.inf.sistemaevaluacion.backend.Item;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Enumeration;

public class AplicacionPanel extends JPanel {
    private final Controlador controlador;

    private final JLabel enunciadoLabel = new JLabel();
    private final JPanel opcionesPanel = new JPanel();
    private final JButton botonAnterior = new JButton("Atrás");
    private final JButton botonSiguiente = new JButton("Siguiente");
    private final ButtonGroup grupoOpciones = new ButtonGroup();

    public AplicacionPanel(Controlador controlador) {
        this.controlador = controlador;
        setLayout(new BorderLayout(10, 10));

        JPanel topPanel = new JPanel(new BorderLayout());
        enunciadoLabel.setFont(new Font("Arial", Font.BOLD, 16));
        topPanel.add(enunciadoLabel, BorderLayout.CENTER);

        opcionesPanel.setLayout(new BoxLayout(opcionesPanel, BoxLayout.Y_AXIS));

        JPanel botonesPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        botonesPanel.add(botonAnterior);
        botonesPanel.add(botonSiguiente);

        add(topPanel, BorderLayout.NORTH);
        add(opcionesPanel, BorderLayout.CENTER);
        add(botonesPanel, BorderLayout.SOUTH);

        botonAnterior.addActionListener(e -> {
            guardarRespuesta();
            controlador.anteriorItem();
            cargarItemActual();
        });

        botonSiguiente.addActionListener(e -> {
            guardarRespuesta();
            if (controlador.esUltimoItem()) {
                controlador.enviarRespuestas();
            } else {
                controlador.siguienteItem();
                cargarItemActual();
            }
        });

        cargarItemActual();
    }

    private void cargarItemActual() {
        Item item = controlador.obtenerItemActual();
        if (item == null) return;

        enunciadoLabel.setText("<html><body style='width: 700px'>" + item.getEnunciado() + "</body></html>");
        opcionesPanel.removeAll();
        grupoOpciones.clearSelection();

        List<String> opciones = item.getOpciones();
        for (String opcion : opciones) {
            JRadioButton botonOpcion = new JRadioButton(opcion);
            grupoOpciones.add(botonOpcion);
            opcionesPanel.add(botonOpcion);

            // Marcar si ya fue respondido
            String respuestaUsuario = controlador.obtenerRespuestaUsuarioActual();
            if (respuestaUsuario != null && respuestaUsuario.equals(opcion)) {
                botonOpcion.setSelected(true);
            }
        }

        botonAnterior.setEnabled(!controlador.estaPrimeraPregunta());

        if (controlador.esUltimoItem()) {
            botonSiguiente.setText("Enviar respuestas");
        } else {
            botonSiguiente.setText("Siguiente");
        }

        revalidate();
        repaint();
    }

    private void guardarRespuesta() {
        String respuestaSeleccionada = null;
        Enumeration<AbstractButton> botones = grupoOpciones.getElements();
        while (botones.hasMoreElements()) {
            AbstractButton boton = botones.nextElement();
            if (boton.isSelected()) {
                respuestaSeleccionada = boton.getText();
                break;
            }
        }

        controlador.registrarRespuesta(respuestaSeleccionada);
    }
}
