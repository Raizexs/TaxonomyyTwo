package cl.unab.inf.sistemaevaluacion.frontend;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Map;
import cl.unab.inf.sistemaevaluacion.backend.Controlador;
import cl.unab.inf.sistemaevaluacion.backend.Item;

public class ResultadoPanel extends JPanel {
    private final Controlador controlador;

    private JLabel correctasLabel;
    private JLabel incorrectasLabel;
    private JLabel resumenBloomLabel;
    private JLabel resumenTipoLabel;
    private JTextArea detalleRespuestasArea; // Para mostrar las preguntas falladas
    private JButton revisarRespuestasButton;

    public ResultadoPanel(Controlador controlador) {
        this.controlador = controlador;
        setLayout(new GroupLayout(this));
        GroupLayout layout = (GroupLayout) getLayout();
        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);

        // Inicializar componentes
        correctasLabel = new JLabel("Respuestas Correctas: -");
        incorrectasLabel = new JLabel("Respuestas Incorrectas: -");
        resumenBloomLabel = new JLabel("Resumen por Nivel de Bloom:");
        resumenTipoLabel = new JLabel("Resumen por Tipo de Ítem:");

        detalleRespuestasArea = new JTextArea(10, 30); // 10 filas, 30 columnas
        detalleRespuestasArea.setEditable(false); // No editable por el usuario
        JScrollPane scrollPane = new JScrollPane(detalleRespuestasArea); // Añadir scroll si el contenido es largo

        revisarRespuestasButton = new JButton("Revisar Respuestas");
        revisarRespuestasButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                controlador.revisarRespuestas(); // Cambia el estado del controlador a REVISION
            }
        });

        // Definir el layout usando GroupLayout
        // Horizontal Group (alineado a la izquierda)
        layout.setHorizontalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addComponent(correctasLabel)
                        .addComponent(incorrectasLabel)
                        .addComponent(resumenBloomLabel)
                        .addComponent(resumenTipoLabel)
                        .addComponent(scrollPane) // El scrollPane se expandirá
                        .addGroup(layout.createSequentialGroup() // Agrupar el botón para centrarlo si es necesario
                                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE) // Espacio flexible a la izquierda
                                .addComponent(revisarRespuestasButton)
                                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)) // Espacio flexible a la derecha
        );

        // Vertical Group (secuencial)
        layout.setVerticalGroup(
                layout.createSequentialGroup()
                        .addComponent(correctasLabel)
                        .addComponent(incorrectasLabel)
                        .addComponent(resumenBloomLabel)
                        .addComponent(resumenTipoLabel)
                        .addComponent(scrollPane)
                        .addComponent(revisarRespuestasButton)
        );
    }

    /**
     * Este método se llama desde VentanaPrincipal para actualizar el resumen
     * cuando el estado del controlador cambia a RESULTADO.
     */
    public void actualizarResumen() {
        int correctas = controlador.getRespuestasCorrectasCount();
        int incorrectas = controlador.getRespuestasIncorrectasCount();

        correctasLabel.setText("Respuestas Correctas: " + correctas);
        incorrectasLabel.setText("Respuestas Incorrectas: " + incorrectas);

        // Generar y mostrar el resumen por Taxonomía de Bloom
        Map<String, Double> resumenNivel = controlador.getResumenPorNivel();
        StringBuilder bloomSummary = new StringBuilder("<html><b>Resumen por Nivel de Bloom:</b><br>");
        for (Map.Entry<String, Double> entry : resumenNivel.entrySet()) {
            bloomSummary.append("&nbsp;&nbsp;").append(entry.getKey()).append(": ").append(String.format("%.2f", entry.getValue())).append("%<br>");
        }
        bloomSummary.append("</html>");
        resumenBloomLabel.setText(bloomSummary.toString());

        // Generar y mostrar el resumen por Tipo de Ítem
        Map<Item.Tipo, Double> resumenTipo = controlador.getResumenPorTipo();
        StringBuilder tipoSummary = new StringBuilder("<html><b>Resumen por Tipo de Ítem:</b><br>");
        for (Map.Entry<Item.Tipo, Double> entry : resumenTipo.entrySet()) {
            tipoSummary.append("&nbsp;&nbsp;").append(entry.getKey().toString()).append(": ").append(String.format("%.2f", entry.getValue())).append("%<br>");
        }
        tipoSummary.append("</html>");
        resumenTipoLabel.setText(tipoSummary.toString());

        // Generar y mostrar el detalle de respuestas incorrectas
        StringBuilder detalle = new StringBuilder("Respuestas Incorrectas:\n\n");
        int incorrectaIndex = 1;
        for (Controlador.DetalleRespuesta dr : controlador.getDetalleRespuestas()) {
            if (!dr.esCorrecta) {
                detalle.append(incorrectaIndex++).append(". ").append(dr.item.getEnunciado()).append("\n");
                detalle.append("   Tu respuesta: ").append(dr.respuestaUsuario != null ? dr.respuestaUsuario : "[No respondida]").append("\n");
                detalle.append("   Correcta: ").append(dr.item.getRespuestaCorrecta()).append("\n\n");
            }
        }
        detalleRespuestasArea.setText(detalle.toString());
        detalleRespuestasArea.setCaretPosition(0); // Volver al inicio del texto
    }
}