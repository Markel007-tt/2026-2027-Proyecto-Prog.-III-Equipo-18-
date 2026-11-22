package gui;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.DefaultTableModel;

import domain.Usuario;

// Modelo de la tabla de clientes del administrador: cada fila es un usuario del banco
public class ModeloClientes extends DefaultTableModel {

	private static final long serialVersionUID = 1L;

	private String[] columnas = { "Usuario", "Nombre", "Nº cuentas", "Saldo total" };
	private List<Usuario> clientes = new ArrayList<>(); // los clientes que se muestran

	// Cambia los clientes que se muestran y avisa a la tabla para que se repinte
	public void setClientes(List<Usuario> clientes) {
		this.clientes = clientes;
		fireTableDataChanged(); // SIN esto la tabla no se entera del cambio
	}

	// Devuelve el cliente de una fila (lo usaremos en el doble clic y al eliminar)
	public Usuario getCliente(int fila) {
		return clientes.get(fila);
	}

	@Override
	public int getRowCount() {
		// El constructor de DefaultTableModel llama a este método antes de que
		// la lista exista: sin esta comprobación daría NullPointerException
		if (clientes == null) {
			return 0;
		}
		return clientes.size();
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
		Usuario u = clientes.get(fila);
		switch (columna) {
		case 0:
			return u.getNombreUsuario();
		case 1:
			return u.getNombreCompleto();
		case 2:
			return u.getCuentas().size();  // número entero
		case 3:
			return u.getSaldoTotal();      // número: el renderer decide el color
		default:
			return null;
		}
	}

	@Override
	public boolean isCellEditable(int fila, int columna) {
		return false; // los datos de los clientes no se editan desde la tabla
	}
}