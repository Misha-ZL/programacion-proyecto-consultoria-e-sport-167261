import javax.swing.*;
import javax.swing.table.TableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Visualizar_Jornada {
    private final TableModel Visualizar_Jornada_TableModel;
    private JTable tablaJornada;
    JPanel panelJornada;
    private JButton buttonDERECHA;
    private JButton buttonIZQUIERDA;
    private JLabel num_Pagina;
    private List<Jornada> jornada = new ArrayList<>();
    private List<Equipo> equipo = new ArrayList<>();
    int contador_pagina = 0;
    int totalPaginas = 5;
   private Partido partido;
   Connection conn = GestorBD.conectar();
    Calendario c = new Calendario();

    /**
     * @author Sagar
     * Recibe los datos desde el TableModel asociado a Visualizar_Jornada
     * @param visualizarJornadaTableModel el tablemodel que generara las columnas
     */
    public Visualizar_Jornada(TableModel visualizarJornadaTableModel) {
      Visualizar_Jornada_TableModel = visualizarJornadaTableModel;
        llamarProcedimientoCalendar();


         c.cargarEquiposJornada();
        c.cargarJornadas();

        c.almacenarJornada(jornada);

        c.almacenaResultado(partido);
        c.almacenarPuntuacion(equipo);




tablaJornada.setModel(Visualizar_Jornada_TableModel);

        num_Pagina.setText(String.valueOf(contador_pagina));
        paginacion(); // ya hace setModel internamente

        /**
         * @author Arnau R.
         * Cambia a una "pagina" de jornada anterior a la que se visualiza,
         * si es la primera cambiara a la ultima
         */
        buttonIZQUIERDA.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                contador_pagina--;
                if (contador_pagina < 0) {
                    contador_pagina = totalPaginas - 1;
                }
                num_Pagina.setText(String.valueOf(contador_pagina));
                paginacion();
            }
        });

        /**
         @author Arnau R.
          * Cambia a una "pagina" de jornada posterior a la que se visualiza,
          * si es la ultima cambiara a la primera
         */
        buttonDERECHA.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                contador_pagina++;
                if (contador_pagina >= totalPaginas) {
                    contador_pagina = 0;
                }
                num_Pagina.setText(String.valueOf(contador_pagina));
                paginacion();
            }
        });
    }

    /**
     * @author Arnau R.
     * Permite el cambio de paginas de visualizacion
     */
    public void paginacion() {
        int elementosPorPagina = 10;
        int inicio = contador_pagina * elementosPorPagina;

        // Asegura que las listas no sean vacías antes de paginar
        if (equipo.isEmpty() || jornada.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No hay datos para mostrar.");
            return;
        }

        // Evita que el índice de inicio supere el tamaño de la lista
        if (inicio >= equipo.size() || inicio >= jornada.size()) {
            inicio = 0;
            contador_pagina = 0;
        }

        int finEquipo = Math.min(inicio + elementosPorPagina, equipo.size());
        int finJornada = Math.min(inicio + elementosPorPagina, jornada.size());

        List<Equipo> paginaEquipos = equipo.subList(inicio, finEquipo);
        List<Jornada> paginaJornada = jornada.subList(inicio, finJornada);

       // tablaJornada.setModel(new Visualizar_Jornada_TableModel(paginaJornada));
    }


    /**
     * @author Sagar
     * Obtiene la informacion para generar el calendario cargandolo de los datos de jornada
     * @return false si no s epudo cargar el calendario por cualquier error inesperado
     */
    public boolean llamarProcedimientoCalendar() {
        try (CallableStatement cstmt = conn.prepareCall("{CALL GENERAR_CALENDARIO}")) {

            cstmt.execute();

            JOptionPane.showMessageDialog(null, "Calendario generado correctamente.");
            return true;

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al generar calendario: " + e.getMessage());
            return false;
        }
    }






}


