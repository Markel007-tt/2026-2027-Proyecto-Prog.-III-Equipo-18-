package domain;

import java.util.ArrayList;
import java.util.List;

// Usuario del banco: datos de acceso y sus cuentas

public class Usuario {

	private String nombreUsuario;  	// Con el que inicia sesión, no se repite
	private String contrasena;		// Contrasena de acceso
	private String nombreCompleto;	// El que se muestra ("Hola, Ana")
	private List<Cuenta> cuentas;  	// Admite corrientes y de ahorro mezcladas

	public Usuario(String nombreUsuario, String contrasena, String nombreCompleto) {
		this.nombreUsuario = nombreUsuario;
		this.contrasena = contrasena;
		this.nombreCompleto = nombreCompleto;
		this.cuentas = new ArrayList<>(); // Si no se crea: NullPointerException al añadir
	}

	// Devuelve true si la contraseña escrita coincide con la del usuario
	
	public boolean comprobarContrasena(String contrasena) {
		return this.contrasena.equals(contrasena); // Con Strings se usa equals, nunca ==
	}

	// Suma el saldo de todas sus cuentas
	public double getSaldoTotal() {
		double total = 0;
		for (Cuenta c : cuentas) {
			total = total + c.getSaldo();
		}
		return total;
	}

	// Getters

	public String getNombreUsuario() {
		return nombreUsuario;
	}

	public String getNombreCompleto() {
		return nombreCompleto;
	}

	public List<Cuenta> getCuentas() {
		return cuentas;
	}

	// ToString
	
	@Override
	public String toString() {
		return nombreCompleto + " (" + nombreUsuario + ")";
	}
}