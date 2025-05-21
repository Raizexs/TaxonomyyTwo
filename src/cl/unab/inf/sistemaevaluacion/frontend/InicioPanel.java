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
            boolean cargado = false; // Variable para controlar si la carga fue exitosa

            try {
                // Intentar cargar los ítems desde el archivo
                controlador.cargarItemsDesdeArchivo(archivo);
                cargado = true; // Si no hay excepción, la carga fue exitosa
            } catch (IOException ex) {
                // Capturar la excepción IOException si ocurre
                estadoCargaLabel.setText("Error de lectura del archivo: " + ex.getMessage());
                cantidadItemsLabel.setText("Cantidad de ítems: -");
                tiempoTotalLabel.setText("Tiempo estimado total: -");
                iniciarPruebaButton.setEnabled(false);
                ex.printStackTrace(); // Imprimir el stack trace para depuración
                return; // Salir del método si hay un error de E/S
            } catch (Exception ex) {
                // Capturar cualquier otra excepción inesperada que pueda lanzar el controlador
                estadoCargaLabel.setText("Error inesperado al cargar el archivo: " + ex.getMessage());
                cantidadItemsLabel.setText("Cantidad de ítems: -");
                tiempoTotalLabel.setText("Tiempo estimado total: -");
                iniciarPruebaButton.setEnabled(false);
                ex.printStackTrace(); // Imprimir el stack trace para depuración
                return;
            }

            if (cargado) {
                int cantidad = controlador.getCantidadItems();
                int tiempo = controlador.getTiempoTotal(); // Tiempo en segundos

                estadoCargaLabel.setText("Archivo cargado exitosamente.");
                cantidadItemsLabel.setText("Cantidad de ítems: " + cantidad);

                // Lógica para mostrar el tiempo en minutos o segundos
                if (tiempo >= 60) {
                    int minutos = tiempo / 60;
                    int segundosRestantes = tiempo % 60;
                    if (segundosRestantes > 0) {
                        tiempoTotalLabel.setText("Tiempo estimado total: " + minutos + " minutos y " + segundosRestantes + " segundos");
                    } else {
                        tiempoTotalLabel.setText("Tiempo estimado total: " + minutos + " minutos");
                    }
                } else {
                    tiempoTotalLabel.setText("Tiempo estimado total: " + tiempo + " segundos");
                }

                iniciarPruebaButton.setEnabled(true);
            } else {
                // Este else se ejecutará si cargarItemsDesdeArchivo devolvió false
                // (aunque ahora asume éxito si no hay excepción),
                // o si la lógica de carga interna del controlador falló sin lanzar excepción.
                estadoCargaLabel.setText("Error al cargar el archivo. Revise el formato.");
                cantidadItemsLabel.setText("Cantidad de ítems: -");
                tiempoTotalLabel.setText("Tiempo estimado total: -");
                iniciarPruebaButton.setEnabled(false);
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

    // Este método ya no es llamado por VentanaPrincipal directamente,
    // ya que la lógica de mostrar datos se maneja en seleccionarYEnviarArchivo.
    // Sin embargo, se mantiene por si alguna otra parte del código lo usa.
    public void mostrarDatos(int cantidad, int tiempo) {
        cantidadItemsLabel.setText("Cantidad de ítems: " + cantidad);
        if (tiempo >= 60) {
            int minutos = tiempo / 60;
            int segundosRestantes = tiempo % 60;
            if (segundosRestantes > 0) {
                tiempoTotalLabel.setText("Tiempo estimado total: " + minutos + " minutos y " + segundosRestantes + " segundos");
            } else {
                tiempoTotalLabel.setText("Tiempo estimado total: " + minutos + " minutos");
            }
        } else {
            tiempoTotalLabel.setText("Tiempo estimado total: " + tiempo + " segundos");
        }
        iniciarPruebaButton.setEnabled(true);
    }
}