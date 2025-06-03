import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Misha
 * Esta clase contiene lo relacionado a la creacion de la jornada
 */
public class Jornada {
    private int Id_Jornada, PuntosTotal;
    private Timestamp Fecha;


    public Jornada(int id_Jornada, int puntosTotal, Timestamp fecha) {
        Id_Jornada = id_Jornada;
        PuntosTotal = puntosTotal;
        Fecha = fecha;
        partidos = new ArrayList<>();
    }

    public Jornada(int puntosTotal, Timestamp fecha, List<Partido> partidos) {
        Id_Jornada = -1;
        PuntosTotal = puntosTotal;
        Fecha = fecha;
        this.partidos = partidos;
    }

    public Jornada(int idJornada, int puntosTotal, Timestamp fecha, List<Partido> partidos) {
        this.Id_Jornada = idJornada;
        this.PuntosTotal = puntosTotal;
        this.Fecha = fecha;
        this.partidos = partidos;
    }




    public int getPuntosTotal() {
        return PuntosTotal;
    }

    public void setPuntosTotal(int puntosTotal) {
        PuntosTotal = puntosTotal;
    }

    public int getId_Jornada() {
        return Id_Jornada;
    }

    public void setId_Jornada(int id_Jornada) {
        Id_Jornada = id_Jornada;
    }

    public Timestamp getFecha() {
        return Fecha;
    }

    public void setFecha(Timestamp fecha) {
        Fecha = fecha;
    }

    private List<Partido> partidos;

    public List<Partido> getPartidos() {
        return partidos;
    }

    public void setPartidos(List<Partido> partidos) {
        this.partidos = partidos;
    }



    @Override
    public String toString() {
        return
                "Fecha: " + Fecha +
                        ", Id_Jornada: " + Id_Jornada;
    }



}



