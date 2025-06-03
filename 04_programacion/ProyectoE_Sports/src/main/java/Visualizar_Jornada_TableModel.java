import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Arnau
 * rea la estructura para la visualizacion de los datos de la jornada,
 * para la tabla jornada
 */
public class Visualizar_Jornada_TableModel extends AbstractTableModel {
    private String[] columnas = {"Equipo", "Puntos Totales", "Victorias", "Derrortas", "Fecha"};

    private List<Jornada> jornada;
    private List<Equipo> equipo;
    public Visualizar_Jornada_TableModel(List<Jornada> jornada, List<Equipo> equipo) {
        this.jornada = jornada;
        this.equipo = equipo;
    }
    @Override
    public int getRowCount() {
        return equipo.size();
    }

    @Override
    public int getColumnCount() {
        return columnas.length;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Equipo e = equipo.get(rowIndex);
        Jornada j = jornada.get(rowIndex);

        switch (columnIndex) {
            case 0:
                return e.getID_Equipo();
            case 1:
                return j.getPuntosTotal();
            case 2:
                return j.getPuntosTotal()/3;
            case 3:
                int victorias = j.getPuntosTotal() / 3;
                return j.getPartidos().size() - victorias;
            case 4:
                return j.getFecha();
        }


        return null;
    }

    @Override
    public String getColumnName(int column) {
        return columnas[column];
    }
}
