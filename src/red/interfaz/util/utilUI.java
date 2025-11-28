package red.interfaz.util;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import red.modelo.Equipo; // Importante para poder usar el objeto Equipo

public class utilUI {

    public utilUI(){
    }

    /**
     * Método genérico para mostrar un ComboBox buscable a partir de un mapa de Equipos.
     * @param titulo Título de la ventana
     * @param mensaje Mensaje para el usuario
     * @param equipos Mapa de equipos (Clave: ID, Valor: Objeto Equipo)
     * @return La IP seleccionada (string limpio) o null si cancela.
     */
    public static String seleccionarIP(String titulo, String mensaje, Map<String, Equipo> equipos) {

        // 1. Validar si hay datos
        if (equipos == null || equipos.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No hay equipos disponibles.");
            return null;
        }

        // 2. Transformar el Mapa de Equipos a una Lista de Strings formateada
        List<String> opciones = new ArrayList<>();
        for (Equipo e : equipos.values()) {
            // Creamos el formato "IP - ID" para que el usuario lo vea en el combo
            String etiqueta = e.getId() + " - " + e.getIpAddress();
            opciones.add(etiqueta);
        }

        // 3. Ordenar alfabéticamente (por ID) para facilitar la búsqueda
        Collections.sort(opciones);

        // 4. Configurar el ComboBox
        String[] arrayOpciones = opciones.toArray(new String[0]);
        JComboBox<String> comboBox = new JComboBox<>(arrayOpciones);
        comboBox.setEditable(true); // Permite escribir para filtrar/buscar

        // 5. Mostrar el diálogo
        int result = JOptionPane.showConfirmDialog(null, comboBox, mensaje,
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);

        // 6. Procesar la selección
        if (result == JOptionPane.OK_OPTION) {
            String seleccion = (String) comboBox.getSelectedItem();
            if (seleccion != null && !seleccion.isEmpty()) {
                // Extraer solo la IP (asumiendo formato "ID - IP")
                // Ejemplo: "Router1 - 192.168.1.1" -> split(" - ")[0] -> "192.168.1.1"
                if (seleccion.contains(" - ")) {
                    return seleccion.split(" - ")[1].trim();
                }
                return seleccion.trim(); // Si el usuario escribió solo la IP a mano
            }
        }
        return null;
    }

    public static void mostrarError(String mensaje){
        JOptionPane.showMessageDialog(null, mensaje, "Error",JOptionPane.ERROR_MESSAGE);
    }

}