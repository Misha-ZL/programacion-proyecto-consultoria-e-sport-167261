/**
 * @author AMisha
 * Contiene toda la informacion realcionada con los usuarios
 */
public class Usuario {

    private int ID_usuario;
    private String Nombre, Apellido, Contraseina;
    private Tipo_usuario TipoUsuario;


    public Usuario(String nombre, String contraseina, String apellido, Tipo_usuario tipoUsuario) {
        this.ID_usuario = -1;
        Nombre = nombre;
        Apellido = apellido;
        this.equipo = equipo;
        TipoUsuario = tipoUsuario;
        Contraseina=contraseina;
    }

    public Usuario(int ID_usuario, String nombre, String contraseina, String apellido, Tipo_usuario tipoUsuario, Equipo equipo) {
        this.ID_usuario = ID_usuario;
        Nombre = nombre;
        Apellido = apellido;
        Contraseina=contraseina;
        TipoUsuario = tipoUsuario;
        this.equipo = equipo;
    }

    public String getContraseina() {
        return Contraseina;
    }

    public void setContraseina(String contraseina) {
        Contraseina = contraseina;
    }

    public int getID_usuario() {
        return ID_usuario;
    }

    public void setID_usuario(int ID_usuario) {
        this.ID_usuario = ID_usuario;
    }

    public String getNombre() {
        return Nombre;
    }

    public void setNombre(String nombre) {
        Nombre = nombre;
    }

    public String getApellido() {
        return Apellido;
    }

    public void setApellido(String apellido) {
        Apellido = apellido;
    }

    public Tipo_usuario getTipoUsuario() {
        return TipoUsuario;
    }


    public void setTipoUsuario(Tipo_usuario tipoUsuario) {
        TipoUsuario = tipoUsuario;


    }

    @Override
    public String toString() {
        return "Usuario{" +
                "ID_usuario=" + ID_usuario +
                ", Nombre='" + Nombre + '\'' +
                ", Apellido='" + Apellido + '\'' +
                ", TipoUsuario=" + TipoUsuario +
                '}';
    }

    private Equipo equipo;

    public Equipo getEquipo() {
        return equipo;
    }

    public void setEquipo(Equipo equipo) {
        this.equipo = equipo;
    }
}



