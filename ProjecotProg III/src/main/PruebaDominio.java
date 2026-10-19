package main;

import java.util.List;

import domain.Cuenta;
import domain.DatosPrueba;
import domain.Movimiento;
import domain.Usuario;

// Prueba por consola para comprobar que el dominio y los datos funcionan.

// La consola solo se usa para depurar, como permite el enunciado.

public class PruebaDominio {

	public static void main(String[] args) {
		List<Usuario> usuarios = DatosPrueba.crearUsuarios();

		// 1. ¿Cuántos usuarios hay? Esperado: 30
		
		System.out.println("Usuarios: " + usuarios.size());

		// 2. Cada usuario con su saldo total y sus cuentas
		
		for (Usuario u : usuarios) {
			System.out.println(u + " -> saldo total: " + u.getSaldoTotal());
			for (Cuenta c : u.getCuentas()) {
				System.out.println("    " + c + " | movimientos: " + c.getMovimientos().size());
			}
		}

		// 3. Movimientos de la primera cuenta de Ana. Esperado: +2000 y -200
		
		System.out.println("\nMovimientos de la primera cuenta de Ana:");
		for (Movimiento m : usuarios.get(0).getCuentas().get(0).getMovimientos()) {
			System.out.println("    " + m);
		}

		// 4. Reglas de retiro con 150 € sobre las cuentas de Oier (ahorro, saldo 0) y Ana (corriente). Esperado: Oier false, Ana true
		
		Cuenta oierAhorro = usuarios.get(25).getCuentas().get(0);
		Cuenta anaCorriente = usuarios.get(0).getCuentas().get(0);
		System.out.println("\nOier (ahorro, saldo 0) puede retirar 150: " + oierAhorro.puedeRetirar(150));
		System.out.println("Ana (corriente) puede retirar 150: " + anaCorriente.puedeRetirar(150));

		// 5. Comprobar el login. Esperado: true y false
		
		Usuario ana = usuarios.get(0);
		System.out.println("\nContraseña correcta: " + ana.comprobarContrasena("1234"));
		System.out.println("Contraseña incorrecta: " + ana.comprobarContrasena("0000"));
	}
}
