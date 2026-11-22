package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.TableCellRenderer;

import domain.Usuario;

// Pestaña "Gestión de clientes" del administrador: muestra y gestiona todos los clientes del banco
public class PanelAdmin extends JPanel {

	private static final long serialVersionUID = 1L;

	private List<Usuario> usuarios;                       // TODOS los usuarios del banco (la misma lista que crea Main)
	private ModeloClientes modelo = new ModeloClientes(); // UN solo modelo para siempre
	private JTable tabla = new JTable(modelo);            // la tabla pregunta a ese modelo

	public PanelAdmin(List<Usuario> usuarios) {
		this.usuarios = usuarios;
		setLayout(new BorderLayout());

		// ---------- CENTER: tabla de clientes ----------
		tabla.getTableHeader().setReorderingAllowed(false);           // no se pueden arrastrar las columnas
		tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); // solo se puede elegir un cliente a la vez

		// Renderer: el "pintor" de cada celda
		tabla.setDefaultRenderer(Object.class, new TableCellRenderer() {
			@Override
			public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
					boolean hasFocus, int row, int column) {

				// Evitamos NullPointerException si algún dato viene vacío (por ejemplo, el nombre)
				String texto = "";
				if (value != null) {
					texto = value.toString();
				}

				// Saldo total con formato de dinero: 2 decimales, miles y €
				if (column == 3) {
					texto = String.format("%,.2f €", (Double) value);
				}

				JLabel etiqueta = new JLabel(texto);
				etiqueta.setOpaque(true); // SIN esto el fondo no se pinta nunca

				// Colores por defecto: los de la tabla
				etiqueta.setBackground(table.getBackground());
				etiqueta.setForeground(table.getForeground());

				// Cliente sin cuentas: toda su fila en gris (miramos el cliente entero, no solo la celda)
				Usuario cliente = modelo.getCliente(row);
				if (cliente.getCuentas().isEmpty()) {
					etiqueta.setForeground(Color.GRAY);
				}

				// Nº de cuentas: centrado
				if (column == 2) {
					etiqueta.setHorizontalAlignment(JLabel.CENTER);
				}

				// Saldo total: negativo en rosa, bajo (< 100 €) en amarillo
				if (column == 3) {
					etiqueta.setHorizontalAlignment(JLabel.RIGHT);
					double saldo = (Double) value;
					if (saldo < 0) {
						etiqueta.setBackground(new Color(255, 205, 210));
					} else if (saldo < 100) {
						etiqueta.setBackground(new Color(255, 245, 190));
					}
				}

				// Fila seleccionada: color de selección (va al final para que gane)
				if (isSelected) {
					etiqueta.setBackground(table.getSelectionBackground());
				}

				return etiqueta;
			}
		});

		add(new JScrollPane(tabla), BorderLayout.CENTER); // sin JScrollPane no se ven las cabeceras

		mostrarClientes(); // primera carga de la tabla
	}

	// Muestra en la tabla todos los usuarios del banco excepto el administrador
	private void mostrarClientes() {
		List<Usuario> clientes = new ArrayList<>(); // lista NUEVA: la del banco no se toca
		for (Usuario u : usuarios) {
			if (!u.esAdmin()) {
				clientes.add(u);
			}
		}
		modelo.setClientes(clientes);
	}
}