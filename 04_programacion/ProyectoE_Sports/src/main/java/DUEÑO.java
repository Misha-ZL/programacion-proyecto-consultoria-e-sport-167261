import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;

public class DUEÑO {
    private JButton modificarEquipoButton;
    JPanel panelDuenio;
    private JButton visualizarJornadaButton;

    /**
     * @param id_equipo del equipo que pertenece al dueño que inicia en la app
     * @author Misha
     * Genera la ventana del dueño.
     * Es la ventana que ve un dueño al iniciar sesion en la app
     */
    public DUEÑO(int id_equipo) {
        int id_equipoD = id_equipo;
        modificarEquipoButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFrame frame = new JFrame("MODIFICAR_EQUIPO_DUENIOS");
                try {
                    frame.setContentPane(new MODIFICAR_EQUIPO_DUENIOS(id_equipo).panel);
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
                frame.pack();
                frame.setVisible(true);
            }
        });

        /**
         * @author Misha
         * Carga la ventana para poder ver la jornada
         */
        visualizarJornadaButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFrame frame = new JFrame("VisualizarResult");
                frame.setContentPane(new VisualizarResultDueño(id_equipo).VisualizarResult);
                frame.pack();
                frame.setVisible(true);
            }
        });

    }
}
