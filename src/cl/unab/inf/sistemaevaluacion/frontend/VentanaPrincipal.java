package cl.unab.inf.sistemaevaluacion.frontend;

import cl.unab.inf.sistemaevaluacion.backend.Controlador;
import cl.unab.inf.sistemaevaluacion.backend.ObservadorEvaluador;

import javax.swing.*;

public class VentanaPrincipal extends JFrame implements ObservadorEvaluador {
    private final Controlador controlador;
    private final InicioPanel inicioPanel;
    private final PruebaPanel pruebaPanel;
    private final ResultadoPanel resultadoPanel;
    private final RevisarRespuestasPanel revisarPanel;

    public VentanaPrincipal(Controlador controlador) {
        this.controlador = controlador;
        this.controlador.suscribir(this);

        setTitle("Sistema de Evaluación");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 500);
        setLocationRelativeTo(null);

        inicioPanel = new InicioPanel(controlador);
        pruebaPanel = new PruebaPanel(controlador);
        resultadoPanel = new ResultadoPanel(controlador);
        revisarPanel = new RevisarRespuestasPanel(controlador);

        mostrarInicio();
    }

    public void mostrarInicio() {
        setContentPane(inicioPanel);
        revalidate();
        repaint();
    }

    public void mostrarPrueba() {
        pruebaPanel.actualizarVista();
        setContentPane(pruebaPanel);
        revalidate();
        repaint();
    }

    public void mostrarResultados() {
        resultadoPanel.actualizarResumen();
        setContentPane(resultadoPanel);
        revalidate();
        repaint();
    }

    public void mostrarRevisarRespuestas() {
        revisarPanel.actualizarVista();
        setContentPane(revisarPanel);
        revalidate();
        repaint();
    }

    @Override
    public void actualizar() {
        if (controlador.estaEnModoRevisar()) {
            mostrarRevisarRespuestas();
        } else if (controlador.estaEnModoResultado()) {
            mostrarResultados();
        } else if (controlador.estaEnModoPrueba()) {
            mostrarPrueba();
        } else {
            mostrarInicio();
        }
    }

    @Override
    public void datosCargados(int cantidad, int tiempo) {
        inicioPanel.mostrarDatos(cantidad, tiempo);
    }
}