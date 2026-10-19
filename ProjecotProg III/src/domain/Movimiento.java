package domain;

import java.time.LocalDate;

public class Movimiento {
	
	private static int contador = 0; 	// Compartido por TODOS los movimientos
	private int id;						// Id unico de cada movimiento
	private LocalDate fecha;			// Fecha en la que se realizó la operacion
	private TipoMovimiento tipo;		// Tipo de movimiento
	private double cantidad; 			// Positivo si entra dinero, negativo si sale
	private String concepto; 			// Mensaje que se quiera poner
	private double saldoResultante; 	// Como queda el saldo tras la transaccion
	
	// Contructor con parametros (todos los datos del movimiento
	
	public Movimiento(LocalDate fecha, TipoMovimiento tipo, double cantidad, String concepto, double saldoResultante) {

		contador++;
		this.id = contador;
		this.fecha = fecha;
		this.tipo = tipo;
		this.cantidad = cantidad;
		this.concepto = concepto;
		this.saldoResultante = saldoResultante;
	}

	// Getters y setters
	
	public int getId() {
		return id;
	}

	public LocalDate getFecha() {
		return fecha;
	}

	public void setFecha(LocalDate fecha) {
		this.fecha = fecha;
	}

	public TipoMovimiento getTipo() {
		return tipo;
	}

	public void setTipo(TipoMovimiento tipo) {
		this.tipo = tipo;
	}

	public double getCantidad() {
		return cantidad;
	}

	public void setCantidad(double cantidad) {
		this.cantidad = cantidad;
	}

	public String getConcepto() {
		return concepto;
	}

	public void setConcepto(String concepto) {
		this.concepto = concepto;
	}

	public double getSaldoResultante() {
		return saldoResultante;
	}

	public void setSaldoResultante(double saldoResultante) {
		this.saldoResultante = saldoResultante;
	}
	
	// ToString

	@Override
	public String toString() {
		return "Movimiento [id=" + id + ", fecha=" + fecha + ", tipo=" + tipo + ", cantidad=" + cantidad + ", concepto="
				+ concepto + ", saldoResultante=" + saldoResultante + "]";
	}
	
}
