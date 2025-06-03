import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Misha
 * Esta clase contiene los objetos relacionados a la creacion de cada equipo
 */
public class Equipo {
    private int ID_Equipo, puntuacion;
    private Double presupuesto;
    private String NombreEquipo;

    public Equipo(int ID_Equipo, int puntuacion, Double presupuesto, String nombreEquipo, Partido partido, Usuario usuario) {
        this.ID_Equipo = ID_Equipo;
        this.puntuacion = puntuacion;
        this.presupuesto = presupuesto;
        NombreEquipo = nombreEquipo;
        jugadores = new ArrayList<>();
        this.partido = partido;
        this.usuario = usuario;
    }


    public Equipo(String nombreEquipo, int puntuacion) {
        this.NombreEquipo = nombreEquipo;
        this.puntuacion = puntuacion;
    }

    public Equipo(int ID_Equipo, String nombreEquipo, int puntuacion) {
        this.ID_Equipo = ID_Equipo;
        this.NombreEquipo = nombreEquipo;
        this.puntuacion = puntuacion;
    }

    public Equipo(int ID_Equipo) {
        this.ID_Equipo = ID_Equipo;
    }

    public Equipo(int puntuacion, Double presupuesto, String nombreEquipo, Partido partido, Usuario usuario) {
        this.ID_Equipo = -1;
        this.puntuacion = puntuacion;
        this.presupuesto = presupuesto;
        NombreEquipo = nombreEquipo;
        jugadores = new ArrayList<>();
        this.partido = partido;
        this.usuario = usuario;
    }

    public Equipo(String NombreEquipo) {
        this.NombreEquipo = NombreEquipo;
    }


    public Double getPresupuesto() {
        return presupuesto;
    }

    public void setPresupuesto(Double presupuesto) {
        this.presupuesto = presupuesto;
    }

    public String getNombreEquipo() {
        return NombreEquipo;
    }

    public void setNombreEquipo(String nombreEquipo) {
        NombreEquipo = nombreEquipo;
    }

    public int getPuntuacion() {
        return puntuacion;
    }

    public void setPuntuacion(int puntuacion) {
        this.puntuacion = puntuacion;
    }

    public int getID_Equipo() {
        return ID_Equipo;
    }

    public void setID_Equipo(int ID_Equipo) {
        this.ID_Equipo = ID_Equipo;
    }

    private List<Jugador> jugadores = new ArrayList<>();

    public List<Jugador> getJugadores() {
        return jugadores;
    }

    public void setJugadores(List<Jugador> jugadores) {
        this.jugadores = jugadores;


    }

    private Usuario usuario;

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    private Partido partido;

    public Partido getPartido() {
        return partido;
    }

    public void setPartido(Partido partido) {
        this.partido = partido;
    }

    @Override
    public String toString() {
        return
                "NombreEquipo: '" + NombreEquipo;
    }


}
