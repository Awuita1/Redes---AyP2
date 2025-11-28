package red.controlador;

import net.datastructures.PositionalList;
import net.datastructures.Vertex;
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
    private Logica red = null;
    private TreeMap<String, Equipo> equipos;
    private List<Conexion> conexiones;
    private Red datosRed;

    public void inicio()
    {
        inicioDatos();

        inicioLogica(equipos, conexiones);

        inicioRed();

        inicioUI();
    }

    private void inicioLogica(TreeMap<String, Equipo> equipos, List<Conexion> conexiones)
    {
        try {
            red = new Logica(equipos, conexiones);
            System.out.println("-----------Grafo cargado exitosamente.-----------");
        } catch (Exception e) {
            System.err.println("Error al cargar el grafo: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Inicia la carga de datos del coordinador
     */
    private void inicioDatos()
    {
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
                    ejecutarPing();
                    break;

                case 2:
                    ejecutarTraceroute();
                    break;

                case 1:
                    ejecutarMST();
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

    private void ejecutarPing(){
        String equipo = Interfaz.leerIP(datosRed.getEquipos());
        boolean estado = red.ping(equipo);
        Interfaz.ping(equipo, estado);
    }

    private void ejecutarTraceroute(){
        String destino = Interfaz.leerIP(datosRed.getEquiposEncendidos());
        String origen = Interfaz.leerIP(datosRed.getEquiposEncendidos());

        try{
            PositionalList<Vertex<Equipo>> traceroute = red.traceroute(destino, origen);
            Interfaz.resultadoTraceroute(origen, destino, traceroute);
        }
        catch(IllegalArgumentException e){
            Interfaz.mostrarError(e.getMessage());
        }
    }

    private void ejecutarMST(){
        List<String> mst = red.MST();
        Interfaz.MST(mst);
    }
}
