package domain;

import java.util.ArrayList;
import java.util.List;

// Usuario del banco: datos de acceso, datos personales y sus cuentas.
// Hay dos tipos: clientes normales y UN administrador (esAdmin = true)

public class Usuario {

	// Acceso
	private String nombreUsuario;  	// Con el que inicia sesión, no se repite
	private String contrasena;		// Contrasena de acceso
	private String nombreCompleto;	// El que se muestra ("Hola, Ana")
	private boolean admin;			// true solo para el administrador

	// Datos personales del cliente (vacíos en los usuarios de prueba antiguos)
	private String dni;
	private String fechaNacimiento;
	private String email;
	private String telefono;
	private String direccion;		// Calle y número
	private String codigoPostal;
	private String ciudad;

	private List<Cuenta> cuentas;  	// Admite corrientes y de ahorro mezcladas

	// Constructor corto: lo usan los datos de prueba y el administrador
	public Usuario(String nombreUsuario, String contrasena, String nombreCompleto) {
		this.nombreUsuario = nombreUsuario;
		this.contrasena = contrasena;
		this.nombreCompleto = nombreCompleto;
		this.cuentas = new ArrayList<>(); // Si no se crea: NullPointerException al añadir
	}

	// Constructor completo: lo usa el formulario de registro
	public Usuario(String nombreUsuario, String contrasena, String nombreCompleto, String dni,
			String fechaNacimiento, String email, String telefono, String direccion, String codigoPostal,
			String ciudad) {
		this(nombreUsuario, contrasena, nombreCompleto);
		this.dni = dni;
		this.fechaNacimiento = fechaNacimiento;
		this.email = email;
		this.telefono = telefono;
		this.direccion = direccion;
		this.codigoPostal = codigoPostal;
		this.ciudad = ciudad;
	}

	// Devuelve true si la contraseña escrita coincide con la del usuario
	
	public boolean comprobarContrasena(String contrasena) {
		return this.contrasena.equals(contrasena); // Con Strings se usa equals
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

	public boolean esAdmin() {
		return admin;
	}

	public String getDni() {
		return dni;
	}

	public String getFechaNacimiento() {
		return fechaNacimiento;
	}

	public String getEmail() {
		return email;
	}

	public String getTelefono() {
		return telefono;
	}

	public String getDireccion() {
		return direccion;
	}

	public String getCodigoPostal() {
		return codigoPostal;
	}

	public String getCiudad() {
		return ciudad;
	}

	public List<Cuenta> getCuentas() {
		return cuentas;
	}

	// Setter

	public void setAdmin(boolean admin) {
		this.admin = admin;
	}

	// ToString
	
	@Override
	public String toString() {
		return nombreCompleto + " (" + nombreUsuario + ")";
	}
}