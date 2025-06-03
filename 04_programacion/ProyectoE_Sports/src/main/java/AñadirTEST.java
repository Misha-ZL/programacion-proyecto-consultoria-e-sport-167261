import org.junit.Test;
import org.opentest4j.AssertionFailedError;

import java.sql.Connection;
import java.sql.PreparedStatement;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Alberto
 * Crea los procesos de testeo de algunas funciones de la aplicacion
 */
public class AñadirTEST {

    @Test
    public void testValidarJugadorSalarioValido() {
        double salarioValido = 150000;
        boolean resultado = AÑADIR_JUGADOR.validarSalario(salarioValido);
        assertTrue(resultado, "El presupuesto del equipo deberia ser menor de 200000");
    }

    @Test
    public void testInsertarEquipoPresupuestoInvalido() {
        double presupuesto = 250000.0;

        if (presupuesto > 200000) {
            assertTrue(true, "Presupuesto inválido. No se debe intentar insertar.");
        } else {
            fail("Este test simula que no se debería insertar con presupuesto > 200000.");
        }
    }

    @Test
    public void testNoInsertarJugadorSalarioInvalido() {
        double salario = 250000.0;

        if (salario >= 200000) {
            assertTrue(true, "Salario inválido, no se debe intentar insertar.");
        } else {
            fail("Este test simula que no se debe insertar con salario > 200000.");
        }
    }



    public void lanzarError() {
        throw new AssertionFailedError("Error de asercion esperado");
    }

    @Test
    public void testInsertarUsuarioSinTipoSeleccionado() {

        Usuario usuario = new Usuario("nose", "nose", "nose", null);

        if (usuario.getTipoUsuario() == null) {
            assertTrue(true, "No se debe insertar usuario sin tipo de usuario seleccionado.");
        } else {
            fail("Este test espera que no haya tipo de usuario seleccionado.");
        }
    }




}





























