import javax.swing.*;
import java.sql.Timestamp;

/**
 * @author Misha
 * Contiene toda la informacion relacionada a partidos
 */
public class Partido {
    private int IdPartido, Puntuacion, ResultadoE1, ResultadoE2;
    private Timestamp Fecha;
    int visitante = -1;
    int local = -1;


    private Equipo equipo1, equipo2;


    public Partido(int idPartido, int puntuacion, int resultadoE1, int resultadoE2, Timestamp fecha, int visitante, int local) {
        IdPartido = idPartido;
        Puntuacion = puntuacion;
        ResultadoE1 = resultadoE1;
        ResultadoE2 = resultadoE2;
        Fecha = fecha;
        this.visitante = visitante;
        this.local = local;
    }

    public Partido(int puntuacion, int resultadoE1, int resultadoE2, Timestamp fecha, int visitante, int local) {
        IdPartido = -1;
        Puntuacion = puntuacion;
        ResultadoE1 = resultadoE1;
        ResultadoE2 = resultadoE2;
        Fecha = fecha;
        this.visitante = visitante;
        this.local = local;
    }

    public Partido() {

    }

    public Partido(Equipo equipo1, Equipo equipo2) {
        this.equipo1 = equipo1;
        this.equipo2 = equipo2;
    }

    public Partido(Timestamp fecha, Equipo eq1, Equipo eq2, int resultado1, int resultado2) {
        Fecha = fecha;
        this.equipo1 = eq1;
        this.equipo2 = eq2;
        this.ResultadoE1 = resultado1;
        this.ResultadoE2 = resultado2;


    }

    public Partido(int idPartido, Timestamp fecha, Equipo eq1, Equipo eq2, int puntuacion, int resultado1, int resultado2) {
        this.IdPartido = idPartido;
        Fecha = fecha;
        this.equipo1 = eq1;
        this.equipo2 = eq2;
        this.ResultadoE1 = resultado1;
        this.ResultadoE2 = resultado2;


    }


    public int getIdPartido() {
        return IdPartido;
    }

    public void setIdPartido(int idPartido) {
        IdPartido = idPartido;
    }

    public int getPuntuacion() {
        return Puntuacion;
    }

    public void setPuntuacion(int puntuacion) {
        Puntuacion = puntuacion;
    }

    public int getResultadoE1() {
        return ResultadoE1;
    }

    public void setResultadoE1(int resultadoE1) {
        ResultadoE1 = resultadoE1;
    }

    public int getResultadoE2() {
        return ResultadoE2;
    }

    public void setResultadoE2(int resultadoE2) {
        ResultadoE2 = resultadoE2;
    }

    public Timestamp getFecha() {
        return Fecha;
    }

    public void setFecha(Timestamp fecha) {
        Fecha = fecha;
    }

    public Equipo getEquipo1() {
        return equipo1;
    }

    public void setEquipo1(Equipo equipo1) {
        this.equipo1 = equipo1;
    }

    public Equipo getEquipo2() {
        return equipo2;
    }

    public void setEquipo2(Equipo equipo2) {
        this.equipo2 = equipo2;
    }

    private Jornada jornada;

    public Jornada getJornada() {
        return jornada;
    }

    public void setJornada(Jornada jornada) {
        this.jornada = jornada;
    }

    @Override
    public String toString() {
        return
                "IdPartido: " + IdPartido +
                        ", Fecha: " + Fecha;
    }
}
