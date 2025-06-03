import javax.swing.*;

/**
 * @author Misha
 * Esta clase contiene lo relacionado a la creacion de cada jugador
 */
public class Jugador {
    private Double Salario;
    private String Nickname, Apellido, Nombre;
    private int ID_Jugador;


    public Jugador(Double salario, String nickname, String apellido, String nombre, int ID_Jugador,Equipo equipo) {
        Salario = salario;
        Nickname = nickname;
        Apellido = apellido;
        Nombre = nombre;
        this.ID_Jugador = ID_Jugador;
        this.equipo=equipo;
    }

    public Jugador(Double salario, String nickname, String apellido, String nombre,Equipo equipo) {
        Salario = salario;
        Nickname = nickname;
        Apellido = apellido;
        Nombre = nombre;
        this.ID_Jugador = -1;
        this.equipo=equipo;

    }


    public Double getSalario() {
        return Salario;
    }

    public void setSalario(Double salario) {
        Salario = salario;
    }

    public String getNickname() {
        return Nickname;
    }

    public void setNickname(String nickname) {
        Nickname = nickname;
    }

    public String getApellido() {
        return Apellido;
    }

    public void setApellido(String apellido) {
        Apellido = apellido;
    }

    public String getNombre() {
        return Nombre;
    }

    public void setNombre(String nombre) {
        Nombre = nombre;
    }

    public int getID_Jugador() {
        return ID_Jugador;
    }

    public void setID_Jugador(int ID_Jugador) {
        this.ID_Jugador = ID_Jugador;
    }

    private Equipo equipo;

    public Equipo getEquipo() {
        return equipo;
    }

    public void setEquipo(Equipo equipo) {
        this.equipo = equipo;
    }



}
