import javax.swing.*;
import java.awt.event.*;
import java.sql.*;
import java.util.ArrayList;

/**
 *  En esta clase se gestionara la introducion de resultados y la jornada.
 *  Coge los datos de la BD y actualizamos los resultados y la puntuacion
 *  de equipos y jornadas en la interfaz grafica
 */
public class IntroducirResultadosJornadas {

    public JPanel panelnew;
    private JComboBox<Jornada> Jornadas;
    private JComboBox<Partido> PartidosJornada;
    private JComboBox<String> Resultado1;
    private JComboBox<String> Resultado2;

    private JButton introducirResultadosButton;
    private JPanel panel;
    private JTextField Equipo1;
    private JTextField Equipo2;
    private JTextField ResultadoE1;
    private JTextField ResultadoE2;
    private JButton GenerarJornada;

    private ArrayList<Jornada> jornadas = new ArrayList<>();
    private ArrayList<Partido> partidos = new ArrayList<>();

    private boolean updatingResultado1 = false;
    private boolean updatingResultado2 = false;

    /**
     * @author Misha
     * Obtiene y carga la informacion de cada jornada
     * desde la base de datos a la aplicacion
     */
    public IntroducirResultadosJornadas() {

        /**
         * Limpiamos toda la lista antes de visualizarlo
         */
        Resultado1.removeAllItems();
        Resultado1.addItem("Victoria");
        Resultado1.addItem("Empate");
        Resultado1.addItem("Derrota");

        Resultado2.removeAllItems();
        Resultado2.addItem("Victoria");
        Resultado2.addItem("Empate");
        Resultado2.addItem("Derrota");


        Equipo1.setEditable(false);
        Equipo2.setEditable(false);

        /**
         * Maneja selecion de una jornada.
         * Carga los partidos desde la BD.
         */
        try {
            Connection conn = GestorBD.conectar();
            String query = "SELECT * FROM JORNADA";
            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();

            Jornadas.removeAllItems();
            jornadas.clear();

            while (rs.next()) {
                int id = rs.getInt("ID_JORNADA");
                int puntos = rs.getInt("PUNTOSTOT");
                Timestamp fecha = rs.getTimestamp("FECHA");

                Jornada jornada = new Jornada(id, puntos, fecha);
                jornadas.add(jornada);
                Jornadas.addItem(jornada);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }


        Jornadas.addActionListener(e -> {
            Jornada jornadaSeleccionada = (Jornada) Jornadas.getSelectedItem();

            /**
             * Maneja la seleccion de una jornada.
             * Carga los partidos desde la BD.
             */
            if (jornadaSeleccionada != null) {
                try {
                    Connection conn = GestorBD.conectar();
                    String query = "SELECT * FROM PARTIDO WHERE ID_JORNADA = ?";
                    PreparedStatement stmt = conn.prepareStatement(query);
                    stmt.setInt(1, jornadaSeleccionada.getId_Jornada());
                    ResultSet rs = stmt.executeQuery();

                    PartidosJornada.removeAllItems();
                    partidos.clear();

                    while (rs.next()) {
                        int idPartido = rs.getInt("ID_PARTIDO");
                        Timestamp fecha = rs.getTimestamp("FECHA");
                        int puntuacion = rs.getInt("PUNTUACION");
                        int resultadoE1 = rs.getInt("RESULTADO_EQUIPO1");
                        int resultadoE2 = rs.getInt("RESULTADO_EQUIPO2");
                        int idEquipo1 = rs.getInt("ID_EQUIPO1");
                        int idEquipo2 = rs.getInt("ID_EQUIPO2");

                        Equipo equipo1 = new Equipo(idEquipo1);
                        Equipo equipo2 = new Equipo(idEquipo2);

                        Partido partido = new Partido(idPartido, fecha, equipo1, equipo2, puntuacion, resultadoE1, resultadoE2);
                        partido.setJornada(jornadaSeleccionada);
                        partidos.add(partido);
                        PartidosJornada.addItem(partido);
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }


        });

        /**
         * @author Misha
         * Permite seleccionar que partidos se jugaron en esa jornada
         */
        PartidosJornada.addActionListener(e -> {
            Partido partidoSeleccionado = (Partido) PartidosJornada.getSelectedItem();

            if (partidoSeleccionado != null) {
                int idEquipo1 = partidoSeleccionado.getEquipo1().getID_Equipo();
                int idEquipo2 = partidoSeleccionado.getEquipo2().getID_Equipo();

                try {
                    Connection conn = GestorBD.conectar();


                    String query1 = "SELECT * FROM EQUIPO WHERE ID_EQUIPO = ?";
                    PreparedStatement stmt1 = conn.prepareStatement(query1);
                    stmt1.setInt(1, idEquipo1);
                    ResultSet rs1 = stmt1.executeQuery();
                    if (rs1.next()) {
                        Equipo1.setText(rs1.getString("NOMBRE_EQUIPO"));
                    }


                    String query2 = "SELECT * FROM EQUIPO WHERE ID_EQUIPO = ?";
                    PreparedStatement stmt2 = conn.prepareStatement(query2);
                    stmt2.setInt(1, idEquipo2);
                    ResultSet rs2 = stmt2.executeQuery();
                    if (rs2.next()) {
                        Equipo2.setText(rs2.getString("NOMBRE_EQUIPO"));
                    }

                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        });

        /**
         * @auhtor Misha
         * Asigna el resultado del equipo1 o local
         * en el partido creado anteriormente
         */
        Resultado1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                // Prevenimos de bucles
                if (updatingResultado1) return;

                // Optiene los tres posibles resultados
                String seleccionado = (String) Resultado1.getSelectedItem();

                // verificamos que se este actualizando verdaderamente El resultado 2 de forma programada
                updatingResultado2 = true;
                Resultado2.removeAllItems();

                // Con las condiciones nos aseguramos que solo se cumpla una de las acciones, si el equipo 1 gana el 2 no puede ganar
                if ("Victoria".equals(seleccionado)) {
                    Resultado2.addItem("Derrota");
                } else if ("Derrota".equals(seleccionado)) {
                    Resultado2.addItem("Victoria");
                } else if ("Empate".equals(seleccionado)) {
                    Resultado2.addItem("Empate");
                    Resultado2.setSelectedItem("Empate");

                }

                // Finalmente desactivamos la verificacion de la actualizacion
                updatingResultado2 = false;
            }
        });

        /**
         * @auhtor Misha
         * Asigna el resultado del equipo2 o visitante
         * en el partido creado anteriormente
         */
        Resultado2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (updatingResultado2) return;

                String seleccionado = (String) Resultado2.getSelectedItem();

                updatingResultado1 = true;
                Resultado1.removeAllItems();

                if ("Victoria".equals(seleccionado)) {
                    Resultado1.addItem("Derrota");
                } else if ("Derrota".equals(seleccionado)) {
                    Resultado1.addItem("Victoria");
                } else if ("Empate".equals(seleccionado)) {
                    Resultado1.addItem("Empate");
                    Resultado1.setSelectedItem("Empate");
                }
                updatingResultado1 = false; // Fin de actualización
            }
        });

        /**
         * @author Misha
         * Guarda el resultado de ambos equipos en la aplicacion
         * y lo carga y actualiza en la base de datos
         * 1. Valida que se haya seleccionado un partido y los resultados ingresados sean validos.
         * 2. Consulta resultados anteriores de los partidos para restarselos a los partidos y jornadas anteriores.
         * 3. Actualizamos los resultados nuevos en la BD.
         * 4. Calcula los puntos a asignar segun el resultado.
         * 5. Suma los puntos correspondientes a los equipos y jornada.
         */
        introducirResultadosButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                Partido partidoSeleccionado = (Partido) PartidosJornada.getSelectedItem();
                if (partidoSeleccionado == null) {
                    JOptionPane.showMessageDialog(null, "Por favor, selecciona un partido.");
                    return;
                }

                int resultadoE1Int, resultadoE2Int;
                try {
                    resultadoE1Int = Integer.parseInt(ResultadoE1.getText());
                    resultadoE2Int = Integer.parseInt(ResultadoE2.getText());
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(null, "Por favor, introduce números válidos en los resultados.");
                    return;
                }


                try {
                    Connection conn = GestorBD.conectar();
                    conn.setAutoCommit(false);

                    // Consultamos los resultados anteriores que hubiese y poder cambiarlos.
                    String queryPuntosPrevios = "SELECT RESULTADO_EQUIPO1, RESULTADO_EQUIPO2 FROM PARTIDO WHERE ID_PARTIDO = ?";
                    PreparedStatement stmtPrevios = conn.prepareStatement(queryPuntosPrevios);
                    stmtPrevios.setInt(1, partidoSeleccionado.getIdPartido());
                    ResultSet rsPrevios = stmtPrevios.executeQuery();

                    int prevResultadoE1 = 0;
                    int prevResultadoE2 = 0;
                    if (rsPrevios.next()) {
                        prevResultadoE1 = rsPrevios.getInt("RESULTADO_EQUIPO1");
                        prevResultadoE2 = rsPrevios.getInt("RESULTADO_EQUIPO2");
                    }


                    // Se hace el calculo de cada equipo teniendo en cuenta los resultados previos.
                    int prevPuntosE1 = 0;
                    int prevPuntosE2 = 0;

                    // Dependiendo del resultado se asigna 3 puntos a uno de los dos equipo y no a los dos, a menos que sea empate
                    if (prevResultadoE1 > prevResultadoE2) {
                        prevPuntosE1 = 3;
                    } else if (prevResultadoE1 < prevResultadoE2) {
                        prevPuntosE2 = 3;
                    } else {
                        prevPuntosE1 = 1;
                        prevPuntosE2 = 1;
                    }


                    // Al modficar el resultado a un equipo que se le asigno un resultado incorrecto, este coja el valor anterior y le reste en caso de haberle
                    // puesto como supuesto ganador
                    String sqlRestarPuntos = "UPDATE EQUIPO SET PUNTUACION = PUNTUACION - ? WHERE ID_EQUIPO = ?";

                    if (prevPuntosE1 >= 0) {
                        PreparedStatement stmtRestarE1 = conn.prepareStatement(sqlRestarPuntos);
                        stmtRestarE1.setInt(1, prevPuntosE1);
                        stmtRestarE1.setInt(2, partidoSeleccionado.getEquipo1().getID_Equipo());
                        stmtRestarE1.executeUpdate();
                        stmtRestarE1.close();
                    }

                    if (prevPuntosE2 >= 0) {
                        PreparedStatement stmtRestarE2 = conn.prepareStatement(sqlRestarPuntos);
                        stmtRestarE2.setInt(1, prevPuntosE2);
                        stmtRestarE2.setInt(2, partidoSeleccionado.getEquipo2().getID_Equipo());
                        stmtRestarE2.executeUpdate();
                        stmtRestarE2.close();
                    }


                    int prevPuntosJornada = (prevResultadoE1 == prevResultadoE2) ? 2 : 3;

                    String sqlRestarPuntosJornada = "UPDATE JORNADA SET PUNTOSTOT = PUNTOSTOT - ? WHERE ID_JORNADA = ?";
                    PreparedStatement stmtRestarJornada = conn.prepareStatement(sqlRestarPuntosJornada);
                    stmtRestarJornada.setInt(1, prevPuntosJornada);
                    stmtRestarJornada.setInt(2, partidoSeleccionado.getJornada().getId_Jornada());
                    stmtRestarJornada.executeUpdate();

                    // Actualizamos el resultado de partido con los nuevos valores
                    String sqlPartido = "UPDATE PARTIDO SET RESULTADO_EQUIPO1 = ?, RESULTADO_EQUIPO2 = ? WHERE ID_PARTIDO = ?";
                    PreparedStatement stmtPartido = conn.prepareStatement(sqlPartido);
                    stmtPartido.setInt(1, resultadoE1Int);
                    stmtPartido.setInt(2, resultadoE2Int);
                    stmtPartido.setInt(3, partidoSeleccionado.getIdPartido());
                    int filasPartido = stmtPartido.executeUpdate();

                    if (filasPartido == 0) {
                        JOptionPane.showMessageDialog(null, "No se pudo actualizar el partido.");
                        conn.rollback();
                        return;
                    }


                    // Calculo de como se asignaran los puntos a los equipos segun el nuevo resultado
                    int puntosE1 = 0;
                    int puntosE2 = 0;

                    if (resultadoE1Int > resultadoE2Int) {
                        puntosE1 = 3;
                    } else if (resultadoE1Int < resultadoE2Int) {
                        puntosE2 = 3;
                    } else {
                        puntosE1 = 1;
                        puntosE2 = 1;
                    }


                    // Va sumando los puntos tanto al equipo 1 como al 2 y actualiza la puntuacion total a la jornada
                    String sqlSumarPuntos = "UPDATE EQUIPO SET PUNTUACION = PUNTUACION + ? WHERE ID_EQUIPO = ?";

                    PreparedStatement stmtSumarE1 = conn.prepareStatement(sqlSumarPuntos);
                    stmtSumarE1.setInt(1, puntosE1);
                    stmtSumarE1.setInt(2, partidoSeleccionado.getEquipo1().getID_Equipo());
                    stmtSumarE1.executeUpdate();

                    PreparedStatement stmtSumarE2 = conn.prepareStatement(sqlSumarPuntos);
                    stmtSumarE2.setInt(1, puntosE2);
                    stmtSumarE2.setInt(2, partidoSeleccionado.getEquipo2().getID_Equipo());
                    stmtSumarE2.executeUpdate();

                    int puntosJornada = (resultadoE1Int == resultadoE2Int) ? 2 : 3;

                    String sqlSumarPuntosJornada = "UPDATE JORNADA SET PUNTOSTOT = PUNTOSTOT + ? WHERE ID_JORNADA = ?";
                    PreparedStatement stmtSumarJornada = conn.prepareStatement(sqlSumarPuntosJornada);
                    stmtSumarJornada.setInt(1, puntosJornada);
                    stmtSumarJornada.setInt(2, partidoSeleccionado.getJornada().getId_Jornada());
                    stmtSumarJornada.executeUpdate();


                    conn.commit();

                    partidoSeleccionado.setResultadoE1(resultadoE1Int);
                    partidoSeleccionado.setResultadoE2(resultadoE2Int);

                    JOptionPane.showMessageDialog(null, "Resultados y puntos actualizados correctamente.");


                    stmtPrevios.close();
                    stmtRestarJornada.close();
                    stmtPartido.close();
                    stmtSumarE1.close();
                    stmtSumarE2.close();
                    stmtSumarJornada.close();
                    conn.close();

                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Error al actualizar la base de datos: " + ex.getMessage());
                }
            }
        });


        /**
         * @author Misha
         * Genera la ventana para visualizar la ventana de generar jornada
         */
        GenerarJornada.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFrame frame = new JFrame("GenerarJornada");
                frame.setContentPane(new GenerarJornada().GenerarJornada);
                frame.pack();
                frame.setVisible(true);
            }
        });
    }



}
