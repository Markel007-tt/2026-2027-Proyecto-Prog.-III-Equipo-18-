package domain;

// Posibles resultados de intentar hacer una transferencia.
// Es un enum igual que TipoMovimiento: la ventana lee este valor y decide qué mensaje mostrar.
public enum ResultadoTransferencia {

	OK,                   // La transferencia se ha realizado correctamente
	CANTIDAD_INVALIDA,    // La cantidad es 0 o negativa
	IBAN_NO_EXISTE,       // No hay ninguna cuenta del banco con ese IBAN
	MISMA_CUENTA,         // El IBAN de destino es el mismo que el de origen
	SALDO_INSUFICIENTE    // La cuenta de origen no puede permitirse enviar esa cantidad
}