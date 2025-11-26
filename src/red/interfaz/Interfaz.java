package red.interfaz;

import javax.swing.JOptionPane;
import javax.swing.JTextArea;

import net.datastructures.PositionalList;
import net.datastructures.Vertex;
import red.interfaz.util.utilUI;
import red.modelo.*;
import red.logica.*;
import java.util.*;

public class Interfaz {
    
    /**
     * metodo que muestra una serie de oopciones al usuario
     * @return eleccion del usuario
     */
    public static int opcion() {
    	String[] options = { "4.salir", "3. Arbol de expansion minimo", "2. Traceroute","1. Ping"};
    	
    	int input = JOptionPane.showOptionDialog(
                null,
                "Seleccione:",
                "Funcion",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.INFORMATION_MESSAGE,
                null,
                options,
                options[0]
            );
    	
        return input;
    }

    /**
     * muestra el resultado de ping
     * @param red logica de ping
     */
    public static void ping(Logica red, Red datos){

        String ipAddress = utilUI.seleccionarIP(
                "Ping",
                "Seleccione la direccion IP del equipo a hacer ping:",
                datos.getEquipos()
        );

        boolean activo = red.ping(ipAddress);
          // Verificar si el equipo está activo;
          if (activo) {
            JOptionPane.showMessageDialog(null, "El equipo con la direccion IP" + ipAddress + "Esta activo");
        }else {
             JOptionPane.showMessageDialog(null, "El equipo con IP " + ipAddress + " no está activo o no se encuentra en la red.");
        }
    }
    
    /**
     * Muestra el resultado de traceroute
     * @param red logica de traceroute
     */
    public static void resultadoTraceroute(Logica red, Red datos) {
//    	String ipOrigen = JOptionPane.showInputDialog(
//                "Ingrese la direccion ID del equipo de salida o -1 para salir:" );
//        String ipDestino = JOptionPane.showInputDialog(
//                "Ingrese la direccion ID del equipo de objetivo o -1 para salir:" );
        String ipOrigen = utilUI.seleccionarIP("Traceroute", "Origen:", datos.getEquiposEncendidos());
        String ipDestino = utilUI.seleccionarIP("Traceroute", "Destino:", datos.getEquiposEncendidos());

        try{
            PositionalList<Vertex<Equipo>> camino = red.traceroute(ipOrigen, ipDestino);
            for (Vertex<Equipo> p : camino) {
                System.out.println(p);
            }

            String traceroute = "";
            for (Vertex<Equipo> conexion : camino) {
                traceroute += conexion.getElement().getIpAddress() + " -> ";
            }

            JOptionPane.showMessageDialog(null, traceroute);
        } catch(IllegalArgumentException e){
            JOptionPane.showMessageDialog(null, e.getMessage());
        }


    }
    
    /**
     * Muestra el arbol de expansion minimo
     * @param red logica de MST
     */
    public static void MST(Logica red) {
        List<String> lista = red.MST();

        JTextArea outputTextArea = new JTextArea();
        outputTextArea.setEditable(false);

        outputTextArea.append("Arbol de Expansión Mínimo\n");
        outputTextArea.append("----------------------------------------\n");
        outputTextArea.append(String.format("%-10s %-10s %-15s\n", "Origen", "Destino", "Latencia (ms)"));
        outputTextArea.append("----------------------------------------\n");

        for (String p : lista) {
            // Asume formato: "id1 <--> id2 [Latencia: xx ms]\n"
            String[] partes = p.split(" <--> | \\[Latencia: | ms\\]");
            if (partes.length >= 3) {
                outputTextArea.append(String.format("%-10s %-10s %-15s\n", partes[0], partes[1], partes[2]));
            }
        }

        JOptionPane.showMessageDialog(null, outputTextArea, "Árbol de expansión mínimo", JOptionPane.INFORMATION_MESSAGE);
    }


    /**
     * Mensaje de salida
     */
    public static void salir() {
    	JOptionPane.showMessageDialog(null, "Saliendo");
    	System.exit( 0 );
    }
    
    /**
     * Mensaje al ingresar una opcion incorrecta
     */
    public static void invalido() {
    	JOptionPane.showMessageDialog(null, "Opcion invalida");
    }
}
