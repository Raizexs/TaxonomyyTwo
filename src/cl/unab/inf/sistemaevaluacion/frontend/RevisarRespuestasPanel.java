package cl.unab.inf.sistemaevaluacion.frontend;

import cl.unab.inf.sistemaevaluacion.backend.Controlador;
import cl.unab.inf.sistemaevaluacion.backend.Item;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.List;

public class RevisarRespuestasPanel extends JPanel {
    private final Controlador controlador;
    private final JLabel lblEnunciado;
    private final ButtonGroup grupoOpciones;
    private final JPanel panelOpciones;
    private final JLabel lblResultado;
    private final JButton btnAnterior;
    private final JButton btnSiguiente;
    private final JButton btnVolverResumen;

    public RevisarRespuestasPanel(Controlador controlador) {
        this.controlador = controlador;

        lblEnunciado = new JLabel();
        lblResultado = new JLabel();
        grupoOpciones = new ButtonGroup();
        panelOpciones = new JPanel();
        btnAnterior = new JButton("Anterior");
        btnSiguiente = new JButton("Siguiente");
        btnVolverResumen = new JButton("Volver al resumen");

        configurarLayout();
        configurarEventos();
    }

    private void configurarLayout() {
        GroupLayout layout = new GroupLayout(this);
        this.setLayout(layout);

        JScrollPane scrollOpciones = new JScrollPane(panelOpciones);
        panelOpciones.setLayout(new BoxLayout(panelOpciones, BoxLayout.Y_AXIS));

        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);

        layout.setHorizontalGroup(
                layout.createParallelGroup()
                        .addComponent(lblEnunciado)
                        .addComponent(scrollOpciones)
                        .addComponent(lblResultado)
                        .addGroup(layout.createSequentialGroup()
                                .addComponent(btnAnterior)
                                .addComponent(btnSiguiente)
                                .addComponent(btnVolverResumen))
        );

        layout.setVerticalGroup(
                layout.createSequentialGroup()
                        .addComponent(lblEnunciado)
                        .addComponent(scrollOpciones, 100, 200, Short.MAX_VALUE)
                        .addComponent(lblResultado)
                        .addGroup(layout.createParallelGroup()
                                .addComponent(btnAnterior)
                                .addComponent(btnSiguiente)
                                .addComponent(btnVolverResumen))
        );
    }

    private void configurarEventos() {
        btnAnterior.addActionListener((ActionEvent e) -> controlador.anterior());
        btnSiguiente.addActionListener((ActionEvent e) -> {
            if (controlador.estaUltimaPregunta()) {
                controlador.notificarFinalizado(); // vuelve al resumen
            } else {
                controlador.siguiente();
            }
        });

        btnVolverResumen.addActionListener((ActionEvent e) -> controlador.notificarFinalizado());
    }

    public void actualizarVista() {
        Item item = controlador.getItemActual();
        List<String> opciones = item.getOpciones();
        String respuestaCorrecta = item.getRespuestaCorrecta();
        String respuestaUsuario = controlador.getRespuestaUsuarioActual();

        lblEnunciado.setText("<html><b>" + item.getEnunciado() + "</b></html>");

        panelOpciones.removeAll();
        grupoOpciones.clearSelection();

        for (String opcion : opciones) {
            JRadioButton radio = new JRadioButton(opcion);
            radio.setEnabled(false);
            if (opcion.equals(respuestaUsuario)) {
                radio.setSelected(true);
            }
            panelOpciones.add(radio);
        }

        boolean esCorrecta = respuestaUsuario != null && respuestaUsuario.equals(respuestaCorrecta);
        lblResultado.setText(esCorrecta ? "✅ Correcta" : "❌ Incorrecta");

        btnAnterior.setEnabled(!controlador.estaPrimeraPregunta());
        btnSiguiente.setText(controlador.estaUltimaPregunta() ? "Volver al resumen" : "Siguiente");

        revalidate();
        repaint();
    }
}