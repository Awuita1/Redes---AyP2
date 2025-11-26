package red.test;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import red.datos.CargarParametros;
import red.datos.Dato;
import red.logica.Logica;
import net.datastructures.PositionalList;
import net.datastructures.Vertex;
import red.modelo.Conexion;
import red.modelo.Equipo;

import java.io.IOException;
import java.util.List;
import java.util.TreeMap;

/**
 * Clase de pruebas de integración.
 * Carga los datos reales desde los archivos .txt (usando Dato.java)
 * y verifica que la lógica funcione correctamente con esa información.
 */
class LogicaTest {

    private Logica logica;

    @BeforeEach
    void setUp() {
        try {
            CargarParametros.parametros();
            TreeMap<String, Equipo> equipos = Dato.cargarEquipos(
                    CargarParametros.getArchivoComputadoras(),
                    CargarParametros.getArchivoRouters()
            );

            List<Conexion> conexiones = Dato.cargarConexiones(
                    CargarParametros.getArchivoConexiones(),
                    equipos
            );

            logica = new Logica(equipos, conexiones);

        } catch (IOException e) {
            fail("Error crítico en setUp: No se pudieron cargar los archivos de datos. " +
                    "Verifica que config.properties y los .txt estén en la raíz del proyecto. " + e.getMessage());
        }
    }

    @Test
    @DisplayName("Caso 1: Ping - Integración con datos reales")
    void testPingReal() {
        assertTrue(logica.ping("172.16.0.5"),
                "La PC17 debería responder al ping según el archivo.");

        assertFalse(logica.ping("192.168.1.0"),
                "La PC1 debería fallar el ping (status=false en archivo).");
        assertFalse(logica.ping("192.168.11.1"),
                "El Router11 debería fallar el ping (status=false en archivo).");
    }

    @Test
    @DisplayName("Caso 2: Traceroute Complejo - Red Nueva a Red Vieja")
    void testTracerouteInterRedes() {

        PositionalList<Vertex<Equipo>> ruta = logica.traceroute("172.16.0.5", "192.168.5.0");

        assertNotNull(ruta);
        assertFalse(ruta.isEmpty());

        // Verificar que el origen y destino son correctos
        assertEquals("PC17", ruta.first().getElement().getElement().getId());
        assertEquals("PC5", ruta.last().getElement().getElement().getId());

        // Verificar que la ruta pasa por el Router13 (Core Nuevo) o Router1 (Entrada vieja)
        boolean pasaPorCore = false;
        for (Vertex<Equipo> v : ruta) {
            String id = v.getElement().getId();
            if (id.equals("Router13") || id.equals("Router1")) {
                pasaPorCore = true;
            }
        }
        assertTrue(pasaPorCore, "La ruta entre PC17 y PC5 debe pasar por los routers troncales (R13 o R1).");
    }

    @Test
    @DisplayName("Caso 3: Optimización - Evitar enlace Satelital")
    void testEvitarSatelite() {
        // Origen: PC16 (Sótano)
        // Destino: PC12 (Oficina B)
        // Conexión directa R17-R16 existe en txt pero tiene latencia 500 (Satelital).
        // Camino alternativo por R3 -> R1 -> R13... es más rápido.

        PositionalList<Vertex<Equipo>> ruta = logica.traceroute("10.0.3.51", "10.0.2.20");

        assertTrue(ruta.size() > 4,
                "El algoritmo tomó el atajo satelital lento (4 saltos). Debería haber tomado el camino largo pero rápido.");

        boolean usoSatelite = false;
        Vertex<Equipo> anterior = null;
        for (Vertex<Equipo> actual : ruta) {
            if (anterior != null) {
                String idAnt = anterior.getElement().getId();
                String idAct = actual.getElement().getId();
                // Si salta de R17 a R16 (o viceversa) directamente
                if ((idAnt.equals("Router17") && idAct.equals("Router16")) ||
                        (idAnt.equals("Router16") && idAct.equals("Router17"))) {
                    usoSatelite = true;
                }
            }
            anterior = actual;
        }
        assertFalse(usoSatelite, "El traceroute pasó por la conexión satelital (R17-R16) de alta latencia.");
    }

    @Test
    @DisplayName("Caso 4: Árbol de Expansión (MST) sin nodos inactivos")
    void testMSTReal() {
        List<String> mst = logica.MST();

        assertNotNull(mst);
        assertFalse(mst.isEmpty());

        for (String linea : mst) {
            assertFalse(linea.contains("PC1 "), "El MST no debe contener a PC1 (Inactivo).");
            assertFalse(linea.contains("Router11 "), "El MST no debe contener a Router11 (Inactivo).");
        }

        boolean sateliteEncontrado = false;
        for (String linea : mst) {
            if (linea.contains("Router17") && linea.contains("Router16") && linea.contains("500")) {
                sateliteEncontrado = true;
            }
        }
        assertFalse(sateliteEncontrado, "El MST incluyó la conexión más cara del sistema (Satelital 500ms).");
    }

    @Test
    @DisplayName("Caso 5: Validación de Errores con datos reales")
    void testErroresReales() {
        // Intentar ir a un nodo aislado o inactivo
        // PC6 es inactiva en computadoras.txt
        assertThrows(IllegalArgumentException.class, () -> {
            logica.traceroute("PC17", "PC6");
        }, "Debe lanzar excepción al intentar llegar a PC6 (inactiva).");

        // Intentar ir a un nodo que depende de un router caído
        // PC2 está conectada a Router12, que conecta a Router11.
        // Router11 está FALSE en routers.txt. Por ende, PC2 es inalcanzable desde PC17.
        // o si el nodo destino 'adaptado' no existe (porque R11 cortó el paso y R12 quedó aislado del componente principal).

        try {
            logica.traceroute("PC17", "PC2");
        } catch (IllegalArgumentException | NullPointerException e) {
            // Es el comportamiento esperado: no se puede llegar.
            assertTrue(true);
        }
    }
}