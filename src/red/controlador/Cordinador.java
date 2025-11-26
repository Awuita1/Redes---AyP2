package red.controlador;

import red.datos.CargarParametros;
import red.datos.Dato;
import red.interfaz.Interfaz;
import red.logica.Logica;
import red.logica.Red;
import red.modelo.Conexion;
import red.modelo.Equipo;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.TreeMap;

/**
 * Clase que representa el coordinador principal del sistema de red.
 * Se encarga de gestionar la lógica central y coordinar los diferentes módulos.
 */
public class Cordinador {
    Logica red;
    TreeMap<String, Equipo> equipos;
    List<Conexion> conexiones;
    Red datosRed;

    public void inicio()
    {
        inicioDatos();

        inicioRed();

        inicioUI();
    }

    /**
     * Inicia la carga de datos del coordinador
     */
    private void inicioDatos()
    {
        // Cargar parámetros
        try {
            CargarParametros.parametros();
        } catch (IOException e) {
            System.err.print("Error al cargar parámetros");
            System.exit(-1);
        }



        try {
            equipos = Dato.cargarEquipos(CargarParametros.getArchivoComputadoras(),CargarParametros.getArchivoRouters());
            conexiones = Dato.cargarConexiones(CargarParametros.getArchivoConexiones(), equipos);
        } catch (FileNotFoundException e) {
            System.err.print("Error al cargar archivos de datos");
            System.exit(-1);
        }
        for (String id : equipos.keySet()) {
            System.out.println("Nodo ID: " + id);
        }
        for (Conexion conexion : conexiones) {
            System.out.println("Conexion de " + conexion.getSource().getId() + " a " + conexion.getTarget().getId());

        }


        try {
            red = new Logica(equipos, conexiones);
            System.out.println("-----------Grafo cargado exitosamente.-----------");
        } catch (Exception e) {
            System.err.println("Error al cargar el grafo: " + e.getMessage());
            e.printStackTrace();
        }


    }

    private void inicioRed()
    {
        datosRed = new Red(equipos, conexiones);
    }

    /**
     * Inicia la ui
     */
    private void inicioUI()
    {
        /* Interfaz */
        boolean on = true;
        while (on) {
            int opcion = Interfaz.opcion();
            switch (opcion) {
                case 3:
                    Interfaz.ping(red);
                    break;

                case 2:
                    Interfaz.resultadoTraceroute(red);
                    break;

                case 1:
                    Interfaz.MST(red);
                    break;

                case 0:
                    Interfaz.salir();
                    on = false;
                    break;

                case -1:
                    Interfaz.salir();
                    on = false;

                default:
                    Interfaz.invalido();
                    break;
            }
        }
    }
}
