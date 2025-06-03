import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Misha
 * Contiene la informacion relacionada a los equipos
 * para crear la cclasificacion
 */
public class ClasifiacionEquipoModel extends AbstractTableModel {
    private List<Equipo> equipos;
    private String[] columnas = {"ID_EQUIPO", "NOMBRE_EQUIPO", "PUNTUACION"};

    public ClasifiacionEquipoModel(List<Equipo> equipos) {

        this.equipos = equipos;
    }

    @Override
    public int getRowCount() {
        return equipos.size();
    }

    @Override
    public int getColumnCount() {
        return columnas.length;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Equipo e = equipos.get(rowIndex);

        switch (columnIndex) {
            case 0:
                return e.getID_Equipo();
            case 1:
                return e.getNombreEquipo();
            case 2:
                return e.getPuntuacion();
            default:
                return null;
        }
    }

    @Override
    public String getColumnName(int column) {
        return columnas[column];
    }
}
