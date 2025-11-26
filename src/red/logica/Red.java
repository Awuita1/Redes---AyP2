package red.logica;

import red.modelo.Conexion;
import red.modelo.Equipo;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Clase que almacena los datos para operaciones de la UI
 */
public class Red
{
    TreeMap<String, Equipo> equipos;
    List<Conexion> conexiones;

    public Red(TreeMap<String, Equipo> equipos, List<Conexion> conexiones) {
        this.equipos = equipos;
        this.conexiones = conexiones;
    }

    public List<Conexion> getConexiones() {
        return conexiones;
    }

    public void setConexiones(List<Conexion> conexiones) {
        this.conexiones = conexiones;
    }

    public TreeMap<String, Equipo> getEquipos() {
        return equipos;
    }

    public void setEquipos(TreeMap<String, Equipo> equipos) {
        this.equipos = equipos;
    }

    /**
     * Obtiene un mapa de los equipos que están encendidos (status = true).
     * @return
     */
    public TreeMap<String, Equipo> getEquiposEncendidos()
    {
        TreeMap<String, Equipo> equiposEncendidos = new TreeMap<>();
        for(Map.Entry<String, Equipo> e: equipos.entrySet())
        {
            if(e.getValue().isStatus())
            {
                equiposEncendidos.put(e.getKey(), e.getValue());
            }
        }

        return equiposEncendidos;
    }
}
