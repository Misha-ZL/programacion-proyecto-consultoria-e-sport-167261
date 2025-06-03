import javax.swing.table.AbstractTableModel;
import java.util.List;

/**
 * @author Alberto
 * Crea la estructura para la visualizacion de los datos del calendario,
 * para la tabla calendario
 */
public class tablaModeloCalendar extends AbstractTableModel {

    private String[] columnas = {"Nombre Equipo 1 ", " Resultado 1 - ", "Resultado 2 ", "Nombre Equipo 2 ", "Fecha "};
    private List<Partido> partidos;


    protected tablaModeloCalendar(List<Partido> partidos) {
        this.partidos = partidos;

    }

    @Override
    public int getRowCount() {
        return partidos.size();
    }

    @Override
    public int getColumnCount() {
        return columnas.length;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Partido partido = partidos.get(rowIndex);

        switch (columnIndex) {
            case 0:
                if (partido.getEquipo1() != null) {
                    return partido.getEquipo1().getNombreEquipo();
                } else {
                    return "Equipo no definido";
                }
            case 1:
                return partido.getResultadoE1();
            case 2:
                return partido.getResultadoE2();
            case 3:
                if (partido.getEquipo2() != null) {
                    return partido.getEquipo2().getNombreEquipo();
                } else {
                    return "Equipo no definido";
                }
            case 4:
                return partido.getFecha();
            default:
                return null;
        }
    }

    @Override
    public String getColumnName(int column) {
        return columnas[column];
    }
}