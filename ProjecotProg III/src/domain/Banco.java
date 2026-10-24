package domain;

import java.util.ArrayList;
import java.util.List;

// Operaciones que afectan a VARIAS cuentas o a todo el banco.
// Una transferencia toca dos cuentas, por eso no puede ser un método de Cuenta:
// vive aquí. Todos los métodos son static, así que no hace falta crear un objeto Banco.
public class Banco {

	// Busca en TODOS los usuarios la cuenta que tenga ese IBAN.
	// Devuelve la cuenta, o null si no existe.
	public static Cuenta buscarCuenta(List<Usuario> usuarios, String iban) {

		// 1. Juntamos las cuentas de todos los usuarios en una sola lista
		List<Cuenta> todas = new ArrayList<>();
		for (Usuario u : usuarios) {
			todas.addAll(u.getCuentas());
		}

		// 2. La búsqueda binaria solo funciona con la lista ORDENADA por IBAN
		todas.sort((a, b) -> a.getIban().compareToIgnoreCase(b.getIban()));

		// 3. Buscamos en toda la lista (de la posición 0 a la última)
		return busquedaBinaria(todas, iban, 0, todas.size() - 1);
	}

	// Búsqueda binaria RECURSIVA: mira la cuenta del medio y, según sea el IBAN
	// mayor o menor, repite la búsqueda solo en la mitad que corresponde.
	private static Cuenta busquedaBinaria(List<Cuenta> cuentas, String iban, int inicio, int fin) {

		// Caso base 1: no quedan cuentas donde buscar, el IBAN no existe
		if (inicio > fin) {
			return null;
		}

		int medio = (inicio + fin) / 2;
		int comparacion = cuentas.get(medio).getIban().compareToIgnoreCase(iban);

		if (comparacion == 0) {
			return cuentas.get(medio); // Caso base 2: encontrada
		} else if (comparacion > 0) {
			// El IBAN del medio es mayor que el buscado: seguimos por la mitad izquierda
			return busquedaBinaria(cuentas, iban, inicio, medio - 1);
		} else {
			// El IBAN del medio es menor que el buscado: seguimos por la mitad derecha
			return busquedaBinaria(cuentas, iban, medio + 1, fin);
		}
	}

	// Transfiere dinero de la cuenta origen a la cuenta con el IBAN indicado.
	// Devuelve un ResultadoTransferencia para que la ventana sepa qué mensaje enseñar.
	// "synchronized": solo un hilo a la vez puede hacer una transferencia, así dos cajeros
	// no pueden descontar y sumar saldos a la vez. (Más adelante se puede afinar bloqueando solo las dos cuentas.)
	public static synchronized ResultadoTransferencia transferir(List<Usuario> usuarios, Cuenta origen,
			String ibanDestino, double cantidad, String concepto) {

		// Pasamos la cantidad a 2 decimales (céntimos) para evitar importes como 0.001
		cantidad = Math.round(cantidad * 100) / 100.0;

		if (cantidad <= 0) {
			return ResultadoTransferencia.CANTIDAD_INVALIDA;
		}

		// ¿Existe el IBAN de destino?
		Cuenta destino = buscarCuenta(usuarios, ibanDestino);
		if (destino == null) {
			return ResultadoTransferencia.IBAN_NO_EXISTE;
		}

		// No tiene sentido enviarse dinero a uno mismo
		if (destino == origen) {
			return ResultadoTransferencia.MISMA_CUENTA;
		}

		// Si no se escribe concepto, ponemos uno automático en cada cuenta
		String conceptoEnviada = concepto.isEmpty() ? "Transferencia a " + destino.getIban() : concepto;
		String conceptoRecibida = concepto.isEmpty() ? "Transferencia de " + origen.getIban() : concepto;

		// Primero se intenta sacar el dinero del origen (aquí se comprueba el saldo).
		// Si no se puede, no se toca la cuenta de destino.
		if (!origen.enviarTransferencia(cantidad, conceptoEnviada)) {
			return ResultadoTransferencia.SALDO_INSUFICIENTE;
		}

		// Si se ha podido sacar, se ingresa en el destino
		destino.recibirTransferencia(cantidad, conceptoRecibida);
		return ResultadoTransferencia.OK;
	}
	// Genera un IBAN aleatorio con el formato "ES01-XXXX" comprobando que no exista ya.
		public static String generarIbanNuevo(List<Usuario> usuarios) {
			String iban;
			do {
				// Genera un número aleatorio entre 1000 y 9999
				int numero = (int) (Math.random() * 9000) + 1000;
				iban = "ES01-" + numero;
				
			// Reutilizamos el método buscarCuenta: si devuelve algo distinto de null, el IBAN ya existe y repetimos.
			} while (buscarCuenta(usuarios, iban) != null); 
			
			return iban;
		}
}