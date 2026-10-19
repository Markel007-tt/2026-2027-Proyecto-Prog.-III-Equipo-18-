package domain;

// Cuenta de ahorro: nunca puede quedar en negativo

public class CuentaAhorro extends Cuenta {

	public CuentaAhorro(String iban, String titular, double saldo) {
		super(iban, titular, saldo);
	}

	@Override
	public boolean puedeRetirar(double cantidad) {
		
		// El saldo que quedaría tiene que ser 0 o más
		
		return getSaldo() - cantidad >= 0;
	}
	
	// Getter

	@Override
	public String getTipo() {
		return "Ahorro";
	}
}