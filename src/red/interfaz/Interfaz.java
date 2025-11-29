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

    public static String leerIP(TreeMap<String, Equipo> datos) {
        String ip = utilUI.seleccionarIP("Seleccione equipo al que hacerle ping", "Seleccione la direccion IP del equipo:", datos);
        return ip;
    }

    /**
     * Muestra el resultado del ping
     * @param equipo
     * @param estado
     */
    public static void ping(String equipo, boolean estado){


          if (estado) {
            JOptionPane.showMessageDialog(null, "El equipo con la direccion IP" + equipo + " Esta activo");
        }else {
             JOptionPane.showMessageDialog(null, "El equipo con IP " + equipo + " no está activo o no se encuentra en la red.");
        }
    }

    /**
     * Muestra el resultado del traceroute
     * @param ipOrigen
     * @param ipDestino
     * @param camino
     */
    public static void resultadoTraceroute(String ipOrigen, String ipDestino, PositionalList<Vertex<Equipo>> camino) {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("Traceroute %s -> %s\n\n", ipOrigen, ipDestino));
        sb.append(String.format("%-4s %-18s\n", "Hop", "IP"));
        sb.append("--------------------------------\n");

        int hop = 0;
        for (Vertex<Equipo> conexion : camino) {
            hop++;
            String ip = "(" + conexion.getElement().getId()+ ") " + conexion.getElement().getIpAddress();
            sb.append(String.format("%-4d %-18s\n", hop, ip));
        }

        if (hop == 0) {
            JOptionPane.showMessageDialog(null, "No se encontró ruta entre " + ipOrigen + " y " + ipDestino + ".");
            return;
        }

        sb.append("\nTotal de saltos: ").append(hop);

        JTextArea outputTextArea = new JTextArea(sb.toString());
        outputTextArea.setEditable(false);
        outputTextArea.setFont(new java.awt.Font("Monospaced", java.awt.Font.PLAIN, 12));
        outputTextArea.setCaretPosition(0);

        JOptionPane.showMessageDialog(null, outputTextArea, "Traceroute", JOptionPane.INFORMATION_MESSAGE);
    }


    /**
     * Muestra el arbol de expansion minimo
     * @param mst Lista de conexiones del arbol de expansion minimo
     */
    public static void MST(List<String> mst) {
        JTextArea outputTextArea = new JTextArea();
        outputTextArea.setEditable(false);

        outputTextArea.append("Arbol de Expansión Mínimo\n");
        outputTextArea.append("----------------------------------------\n");
        outputTextArea.append(String.format("%-10s %-10s %-15s\n", "Origen", "Destino", "Latencia (ms)"));
        outputTextArea.append("----------------------------------------\n");

        for (String p : mst) {
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

    public static void mostrarError(String mensaje){
        utilUI.mostrarError(mensaje);
    }
}
