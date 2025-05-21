package cl.unab.inf.sistemaevaluacion.frontend;

import cl.unab.inf.sistemaevaluacion.backend.Controlador;
import cl.unab.inf.sistemaevaluacion.backend.ObservadorEvaluador;

import javax.swing.*;
import java.awt.*; // Importar para CardLayout

public class VentanaPrincipal extends JFrame implements ObservadorEvaluador {
    private final Controlador controlador;
    private final InicioPanel inicioPanel;
    private final PruebaPanel pruebaPanel;
    private final ResultadoPanel resultadoPanel;
    private final RevisarRespuestasPanel revisarPanel;

    private final JPanel contentPane; // Panel principal que usará CardLayout
    private final CardLayout cardLayout; // Layout para cambiar entre paneles

    public VentanaPrincipal(Controlador controlador) {
        this.controlador = controlador;
        // Asegúrate de que la ventana se suscriba al controlador
        this.controlador.agregarObservador(this); // Usar agregarObservador para consistencia

        setTitle("Sistema de Evaluación");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 400); // Tamaño ajustado para mejor visualización
        setLocationRelativeTo(null); // Centra la ventana en la pantalla

        // Inicializar los paneles
        inicioPanel = new InicioPanel(controlador);
        pruebaPanel = new PruebaPanel(controlador);
        resultadoPanel = new ResultadoPanel(controlador);
        revisarPanel = new RevisarRespuestasPanel(controlador);

        // Configurar el CardLayout para el contentPane
        cardLayout = new CardLayout();
        contentPane = new JPanel(cardLayout);

        // Añadir todos los paneles al contentPane con un nombre clave
        contentPane.add(inicioPanel, "Inicio");
        contentPane.add(pruebaPanel, "Prueba");
        contentPane.add(resultadoPanel, "Resultado");
        contentPane.add(revisarPanel, "RevisarRespuestas");

        // Añadir el contentPane a la ventana principal
        add(contentPane);

        // Mostrar el panel de inicio al principio
        cardLayout.show(contentPane, "Inicio");
    }

    @Override
    public void actualizar() {
        // Este método se llama cada vez que el estado del controlador cambia
        Controlador.Estado estado = controlador.getEstadoActual();

        switch (estado) {
            case INICIO:
                cardLayout.show(contentPane, "Inicio");
                // El InicioPanel ya se actualiza a sí mismo en seleccionarYEnviarArchivo
                // Puedes añadir aquí lógica si necesitas resetear el InicioPanel en algún momento
                break;
            case PRUEBA:
                cardLayout.show(contentPane, "Prueba");
                // IMPORTANTE: Asegúrate de que PruebaPanel se actualice para mostrar el ítem actual
                // Llama a un método en PruebaPanel para cargar el ítem
                pruebaPanel.mostrarItemActual();
                break;
            case RESULTADO:
                cardLayout.show(contentPane, "Resultado");
                // Llama a un método en ResultadoPanel para que muestre el resumen
                resultadoPanel.actualizarResumen();
                break;
            case REVISION:
                cardLayout.show(contentPane, "RevisarRespuestas");
                // Llama a un método en RevisarRespuestasPanel para que muestre el ítem en revisión
                revisarPanel.actualizarVista();
                break;
        }
    }

    @Override
    public void datosCargados(int cantidad, int tiempo) {
        // Este método debe ser implementado para satisfacer la interfaz ObservadorEvaluador.
        // Aunque InicioPanel maneja su propia actualización, si el Controlador llama a este método
        // en sus observadores, se puede pasar la información al InicioPanel.
        inicioPanel.mostrarDatos(cantidad, tiempo);
    }
}
