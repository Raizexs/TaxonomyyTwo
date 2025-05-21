package cl.unab.inf.sistemaevaluacion.frontend;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Enumeration;
import java.util.List;

import cl.unab.inf.sistemaevaluacion.backend.Controlador;
import cl.unab.inf.sistemaevaluacion.backend.Item; // Importar la clase Item

public class PruebaPanel extends JPanel {
    private final Controlador controlador;

    private JLabel enunciadoLabel;
    private JPanel opcionesPanel;
    private ButtonGroup opcionesGroup; // Para agrupar los JRadioButtons
    private JButton anteriorButton;
    private JButton siguienteButton;

    public PruebaPanel(Controlador controlador) {
        this.controlador = controlador;
        // Configurar el layout del panel
        setLayout(new GroupLayout(this));
        GroupLayout layout = (GroupLayout) getLayout();
        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);

        // Inicializar componentes
        enunciadoLabel = new JLabel("Enunciado de la pregunta");
        opcionesPanel = new JPanel(); // Panel para las opciones
        opcionesPanel.setLayout(new BoxLayout(opcionesPanel, BoxLayout.Y_AXIS)); // Opciones apiladas verticalmente
        opcionesGroup = new ButtonGroup();

        anteriorButton = new JButton("Anterior");
        siguienteButton = new JButton("Siguiente");

        // Configurar listeners de los botones
        anteriorButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                controlador.retroceder(); // Llama al método retroceder del controlador
            }
        });

        siguienteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Antes de avanzar, guardar la respuesta actual del usuario
                String respuestaSeleccionada = getRespuestaSeleccionada();
                controlador.responderActual(respuestaSeleccionada);
                controlador.avanzar(); // Llama al método avanzar del controlador
            }
        });

        // Definir el layout usando GroupLayout
        layout.setHorizontalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING) // CAMBIO AQUÍ: Alineado a la izquierda
                        .addComponent(enunciadoLabel)
                        .addComponent(opcionesPanel)
                        .addGroup(layout.createSequentialGroup()
                                .addComponent(anteriorButton)
                                .addComponent(siguienteButton))
        );

        layout.setVerticalGroup(
                layout.createSequentialGroup()
                        .addComponent(enunciadoLabel)
                        .addComponent(opcionesPanel)
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addComponent(anteriorButton)
                                .addComponent(siguienteButton))
        );
    }

    /**
     * Este método se llama desde VentanaPrincipal para actualizar la vista
     * del panel de la prueba con el ítem actual del controlador.
     */
    public void mostrarItemActual() {
        Item itemActual = controlador.getItemActual();
        if (itemActual != null) {
            enunciadoLabel.setText("<html>" + itemActual.getEnunciado() + "</html>"); // Usar HTML para posibles saltos de línea

            // Limpiar opciones anteriores
            opcionesPanel.removeAll();
            opcionesGroup = new ButtonGroup(); // Reiniciar el ButtonGroup

            // Añadir nuevas opciones
            for (String opcion : itemActual.getOpciones()) {
                JRadioButton radioButton = new JRadioButton(opcion);
                opcionesGroup.add(radioButton);
                opcionesPanel.add(radioButton);

                // Seleccionar la respuesta del usuario si ya existe
                String respuestaUsuario = controlador.getRespuestaUsuarioActual();
                if (respuestaUsuario != null && respuestaUsuario.equals(opcion)) {
                    radioButton.setSelected(true);
                }
            }

            // Actualizar el panel de opciones
            opcionesPanel.revalidate();
            opcionesPanel.repaint();

            // Actualizar el estado de los botones de navegación
            actualizarBotonesNavegacion();
        } else {
            // Si no hay ítem actual (ej. al final de la prueba), limpiar la vista
            enunciadoLabel.setText("No hay preguntas disponibles.");
            opcionesPanel.removeAll();
            opcionesPanel.revalidate();
            opcionesPanel.repaint();
            anteriorButton.setEnabled(false);
            siguienteButton.setEnabled(false);
        }
    }

    /**
     * Obtiene la respuesta seleccionada por el usuario en los JRadioButtons.
     * @return La cadena de la respuesta seleccionada o null si ninguna está seleccionada.
     */
    private String getRespuestaSeleccionada() {
        for (Enumeration<AbstractButton> buttons = opcionesGroup.getElements(); buttons.hasMoreElements();) {
            AbstractButton button = buttons.nextElement();
            if (button.isSelected()) {
                return button.getText();
            }
        }
        return null;
    }

    /**
     * Actualiza el estado de los botones "Anterior" y "Siguiente"
     * y el texto del botón "Siguiente" si es el último ítem.
     */
    private void actualizarBotonesNavegacion() {
        anteriorButton.setEnabled(!controlador.estaEnPrimero()); // Deshabilitar si es la primera pregunta

        if (controlador.estaEnUltimo()) {
            siguienteButton.setText("Enviar respuestas");
        } else {
            siguienteButton.setText("Siguiente");
        }
    }

    // El método actualizarVista() original, ahora puede ser usado para una actualización más general
    // o simplemente ser llamado por mostrarItemActual si se desea.
    public void actualizarVista() {
        mostrarItemActual(); // Por ahora, simplemente llama a mostrarItemActual
    }
}