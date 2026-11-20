package gui;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.DefaultTableModel;

import domain.Movimiento;

// Modelo de la tabla del Histórico: guarda los movimientos y responde a la tabla
public class ModeloMovimientos extends DefaultTableModel {

	private static final long serialVersionUID = 1L;

	private String[] columnas = { "Fecha", "Tipo", "Concepto", "Cantidad", "Saldo" };
	private List<Movimiento> movimientos = new ArrayList<>(); // los datos que se muestran

	// Cambia los movimientos que se muestran y avisa a la tabla para que se repinte
	public void setMovimientos(List<Movimiento> movimientos) {
		this.movimientos = movimientos;
		fireTableDataChanged(); // SIN esto la tabla no se entera del cambio
	}

	// Devuelve el movimiento de una fila (lo usaremos en el doble clic)
	public Movimiento getMovimiento(int fila) {
		return movimientos.get(fila);
	}

	@Override
	public int getRowCount() {
		// El constructor de DefaultTableModel llama a este método antes de que
		// la lista exista: sin esta comprobación daría NullPointerException
		if (movimientos == null) {
			return 0;
		}
		return movimientos.size();
	}

	@Override
	public int getColumnCount() {
		return columnas.length;
	}

	@Override
	public String getColumnName(int columna) {
		return columnas[columna];
	}

	@Override
	public Object getValueAt(int fila, int columna) {
		Movimiento m = movimientos.get(fila);
		switch (columna) {
		case 0:
			return m.getFecha();
		case 1:
			return m.getTipo();
		case 2:
			return m.getConcepto();
		case 3:
			return m.getCantidad();        // número: el renderer decide el color por su signo
		case 4:
			return m.getSaldoResultante();
		default:
			return null;
		}
	}

	@Override
	public boolean isCellEditable(int fila, int columna) {
		return false; // un historial no se edita a mano
	}
}