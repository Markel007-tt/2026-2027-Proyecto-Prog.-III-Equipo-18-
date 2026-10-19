package domain;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

// Clase ABSTRACTA: no se pueden crear objetos Cuenta directamente,

// Solo CuentaCorriente o CuentaAhorro.
public abstract class Cuenta {

	private String iban;                  // Identificador de la cuenta, no cambia nunca
	private String titular;               // Nombre de usuario del dueño
	private double saldo;
	private List<Movimiento> movimientos; // Historial de la cuenta

	public Cuenta(String iban, String titular, double saldo) {
		this.iban = iban;
		this.titular = titular;
		this.saldo = saldo;
		this.movimientos = new ArrayList<>(); // Si no se crea, el primer add da NullPointerException
	}

	// Métodos abstractos: cada hija los rellena 

	// ¿Se puede retirar esta cantidad? Cada tipo de cuenta tiene su regla
	
	public abstract boolean puedeRetirar(double cantidad);

	// Nombre del tipo para mostrar en las tablas ("Corriente" o "Ahorro")
	
	public abstract String getTipo();

	// Operaciones

	// Ingresa dinero y lo apunta en el historial
	
	public void depositar(double cantidad, String concepto) {
		
		if (cantidad <= 0) {
			return; // No se permiten depósitos de 0 o negativos
		}
		
		saldo = saldo + cantidad;
		movimientos.add(new Movimiento(LocalDate.now(), TipoMovimiento.DEPOSITO, cantidad, concepto, saldo));
	}

	// Saca dinero si la regla de la cuenta lo permite.
	
	// Devuelve true si se ha hecho y false si se ha rechazado.
	
	public boolean retirar(double cantidad, String concepto) {
		
		if (cantidad <= 0) {
			return false;
		}
		if (!puedeRetirar(cantidad)) { 
			return false;
		}
		saldo = saldo - cantidad;
		
		// La cantidad se guarda en NEGATIVO porque sale dinero
		
		movimientos.add(new Movimiento(LocalDate.now(), TipoMovimiento.RETIRO, -cantidad, concepto, saldo));
		return true;
	}

	// Getters y setters 

	public String getIban() {
		return iban;
	}

	public String getTitular() {
		return titular;
	}

	public void setTitular(String titular) {
		this.titular = titular;
	}

	public double getSaldo() {
		return saldo;
	}

	public List<Movimiento> getMovimientos() {
		return movimientos;
	}

	// ToString
	
	@Override
	public String toString() {
		return iban + " (" + getTipo() + ") - " + saldo + " €";
	}
}
