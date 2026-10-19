package domain;

// Cuenta corriente: puede quedar en negativo hasta un límite (descubierto)

public class CuentaCorriente extends Cuenta {

	private double limiteDescubierto; // ej: 500 → el saldo puede bajar hasta -500

	public CuentaCorriente(String iban, String titular, double saldo, double limiteDescubierto) {
		
		super(iban, titular, saldo); // rellena la parte común (debe ir la PRIMERA)
		
		this.limiteDescubierto = limiteDescubierto;
	}

	@Override
	public boolean puedeRetirar(double cantidad) {
		
		// El saldo que quedaría no puede bajar de -limiteDescubierto
		
		return getSaldo() - cantidad >= -limiteDescubierto;
	}

	// Getter
	
	@Override
	public String getTipo() {
		return "Corriente";
	}
}