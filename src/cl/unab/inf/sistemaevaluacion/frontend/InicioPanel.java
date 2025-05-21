package cl.unab.inf.sistemaevaluacion.frontend;

import cl.unab.inf.sistemaevaluacion.backend.Controlador;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException; // Importar IOException

public class InicioPanel extends JPanel {
    private final Controlador controlador;
    private final JLabel estadoCargaLabel;
    private final JLabel cantidadItemsLabel;
    private final JLabel tiempoTotalLabel;
    private final JButton cargarArchivoButton;
    private final JButton iniciarPruebaButton;

    public InicioPanel(Controlador controlador) {
        this.controlador = controlador;

        estadoCargaLabel = new JLabel("Seleccione un archivo para comenzar.");
        cantidadItemsLabel = new JLabel("Cantidad de ítems: -");
        tiempoTotalLabel = new JLabel("Tiempo estimado total: -");
        cargarArchivoButton = new JButton("Cargar archivo de ítems");
        iniciarPruebaButton = new JButton("Iniciar prueba");
        iniciarPruebaButton.setEnabled(false);

        cargarArchivoButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                seleccionarYEnviarArchivo();
            }
        });

        iniciarPruebaButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                controlador.iniciarPrueba();
            }
        });

        construirLayout();
    }

    private void seleccionarYEnviarArchivo() {
        JFileChooser fileChooser = new JFileChooser();
        int resultado = fileChooser.showOpenDialog(this);
        if (resultado == JFileChooser.APPROVE_OPTION) {
            File archivo = fileChooser.getSelectedFile();

            try {
                // Intentar cargar los ítems desde el archivo.
                // Si el método cargarItemsDesdeArchivo ahora es 'void',
                // simplemente lo llamamos. Asumimos que si no lanza una excepción,
                // la carga fue exitosa.
                controlador.cargarItemsDesdeArchivo(archivo);

                // Si llegamos a este punto, la carga fue exitosa (no se lanzó ninguna excepción)
                int cantidad = controlador.getCantidadItems();
                int tiempo = controlador.getTiempoTotal();
                estadoCargaLabel.setText("Archivo cargado exitosamente.");
                cantidadItemsLabel.setText("Cantidad de ítems: " + cantidad);
                tiempoTotalLabel.setText("Tiempo estimado total: " + tiempo + " segundos");
                iniciarPruebaButton.setEnabled(true);

            } catch (IOException ex) {
                // Capturar la excepción IOException si ocurre durante la lectura del archivo
                estadoCargaLabel.setText("Error de lectura del archivo: " + ex.getMessage());
                cantidadItemsLabel.setText("Cantidad de ítems: -");
                tiempoTotalLabel.setText("Tiempo estimado total: -");
                iniciarPruebaButton.setEnabled(false);
                ex.printStackTrace(); // Imprimir el stack trace para depuración
            } catch (Exception ex) {
                // Capturar cualquier otra excepción inesperada que pueda lanzar el controlador
                estadoCargaLabel.setText("Error inesperado al cargar el archivo: " + ex.getMessage());
                cantidadItemsLabel.setText("Cantidad de ítems: -");
                tiempoTotalLabel.setText("Tiempo estimado total: -");
                iniciarPruebaButton.setEnabled(false);
                ex.printStackTrace(); // Imprimir el stack trace para depuración
            }
        }
    }

    private void construirLayout() {
        GroupLayout layout = new GroupLayout(this);
        this.setLayout(layout);

        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);

        layout.setHorizontalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.CENTER)
                        .addComponent(estadoCargaLabel)
                        .addComponent(cantidadItemsLabel)
                        .addComponent(tiempoTotalLabel)
                        .addComponent(cargarArchivoButton)
                        .addComponent(iniciarPruebaButton)
        );

        layout.setVerticalGroup(
                layout.createSequentialGroup()
                        .addComponent(estadoCargaLabel)
                        .addComponent(cantidadItemsLabel)
                        .addComponent(tiempoTotalLabel)
                        .addComponent(cargarArchivoButton)
                        .addComponent(iniciarPruebaButton)
        );
    }

    public void mostrarDatos(int cantidad, int tiempo) {
        cantidadItemsLabel.setText("Cantidad de ítems: " + cantidad);
        tiempoTotalLabel.setText("Tiempo estimado total: " + tiempo + " min");
        iniciarPruebaButton.setEnabled(true);
    }
}