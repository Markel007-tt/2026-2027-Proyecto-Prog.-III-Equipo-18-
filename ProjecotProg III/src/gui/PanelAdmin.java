package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.TableCellRenderer;

import domain.Cuenta;
import domain.Usuario;

// Pestaña "Gestión de clientes" del administrador: muestra y gestiona todos los clientes del banco
public class PanelAdmin extends JPanel {

	private static final long serialVersionUID = 1L;

	private List<Usuario> usuarios;                       // TODOS los usuarios del banco (la misma lista que crea Main)
	private ModeloClientes modelo = new ModeloClientes(); // UN solo modelo para siempre
	private JTable tabla = new JTable(modelo);            // la tabla pregunta a ese modelo
	private JTextField txtBuscar = new JTextField(15);    // buscador por usuario o nombre
	private JLabel lblTotales = new JLabel();             // nº de clientes y dinero total mostrados

	public PanelAdmin(List<Usuario> usuarios) {
		this.usuarios = usuarios;
		setLayout(new BorderLayout());

		// ---------- NORTH: buscador ----------
		JPanel panelSuperior = new JPanel(); // FlowLayout por defecto: componentes en fila
		panelSuperior.add(new JLabel("Buscar (usuario o nombre):"));
		panelSuperior.add(txtBuscar);
		add(panelSuperior, BorderLayout.NORTH);

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

		// ---------- SOUTH: totales a la izquierda y botón a la derecha ----------
		JButton btnEliminar = new JButton("Eliminar cliente");

		JPanel panelInferior = new JPanel(new BorderLayout());
		panelInferior.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10)); // margen interior
		panelInferior.add(lblTotales, BorderLayout.WEST);
		panelInferior.add(btnEliminar, BorderLayout.EAST);
		add(panelInferior, BorderLayout.SOUTH);

		// ---------- Eventos ----------

		// Buscador: se filtra cada vez que cambia el texto
		txtBuscar.getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void insertUpdate(DocumentEvent e) { // se ha escrito algo
				mostrarClientes();
			}

			@Override
			public void removeUpdate(DocumentEvent e) { // se ha borrado algo
				mostrarClientes();
			}

			@Override
			public void changedUpdate(DocumentEvent e) { // cambios de estilo: no nos afectan
			}
		});

		// Doble clic en un cliente: muestra sus cuentas
		tabla.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {                  // solo doble clic
					int fila = tabla.rowAtPoint(e.getPoint());   // fila donde se ha hecho clic
					if (fila != -1) {                            // -1 = clic en zona vacía
						mostrarCuentas(modelo.getCliente(fila)); // el cliente se pide al MODELO (por el buscador)
					}
				}
			}
		});

		// Botón Eliminar cliente
		btnEliminar.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				eliminarCliente();
			}
		});

		mostrarClientes(); // primera carga de la tabla
	}

	// Muestra en la tabla los clientes (sin el administrador) que cumplen la búsqueda
	// y actualiza los totales de abajo
	private void mostrarClientes() {
		String busqueda = txtBuscar.getText().trim().toLowerCase();

		List<Usuario> clientes = new ArrayList<>(); // lista NUEVA: la del banco no se toca
		double dineroTotal = 0;

		for (Usuario u : usuarios) {
			if (u.esAdmin()) {
				continue; // el administrador nunca aparece en la tabla
			}

			// Busca en el usuario y en el nombre completo, sin distinguir mayúsculas
			String usuario = u.getNombreUsuario().toLowerCase();
			String nombre = "";
			if (u.getNombreCompleto() != null) {
				nombre = u.getNombreCompleto().toLowerCase();
			}

			if (usuario.contains(busqueda) || nombre.contains(busqueda)) { // "" lo contiene todo
				clientes.add(u);
				dineroTotal = dineroTotal + u.getSaldoTotal();
			}
		}

		modelo.setClientes(clientes);
		lblTotales.setText(String.format("Clientes: %d   ·   Dinero total: %,.2f €", clientes.size(), dineroTotal));
	}

	// Muestra en un cuadro de diálogo las cuentas de un cliente
	private void mostrarCuentas(Usuario cliente) {
		String texto = "Cliente: " + cliente.getNombreCompleto() + " (" + cliente.getNombreUsuario() + ")\n\n";

		if (cliente.getCuentas().isEmpty()) {
			texto = texto + "No tiene ninguna cuenta.";
		} else {
			for (Cuenta c : cliente.getCuentas()) {
				texto = texto + String.format("%-12s %-10s %,12.2f €%n", c.getIban(), c.getTipo(), c.getSaldo());
			}
			texto = texto + String.format("%nSaldo total: %,.2f €", cliente.getSaldoTotal());
		}

		JOptionPane.showMessageDialog(this, texto, "Cuentas del cliente", JOptionPane.INFORMATION_MESSAGE);
	}

	// Elimina el cliente seleccionado si todas sus cuentas tienen saldo 0
	private void eliminarCliente() {
		int fila = tabla.getSelectedRow();
		if (fila == -1) {
			JOptionPane.showMessageDialog(this, "Selecciona un cliente de la tabla primero.", "Aviso",
					JOptionPane.WARNING_MESSAGE);
			return;
		}

		Usuario cliente = modelo.getCliente(fila); // al MODELO, por el buscador

		// No se puede eliminar si alguna cuenta tiene dinero o deuda.
		// Se compara con 0,01 € y no con == 0 porque los double pueden guardar
		// restos como 0.0000000001 tras varias operaciones.
		for (Cuenta c : cliente.getCuentas()) {
			if (Math.abs(c.getSaldo()) >= 0.01) {
				JOptionPane.showMessageDialog(this,
						"No se puede eliminar a " + cliente.getNombreCompleto() + ":\n"
								+ "la cuenta " + c.getIban() + " tiene un saldo de "
								+ String.format("%,.2f €", c.getSaldo()) + ".",
						"No se puede eliminar", JOptionPane.ERROR_MESSAGE);
				return;
			}
		}

		int respuesta = JOptionPane.showConfirmDialog(this,
				"¿Seguro que quieres eliminar a " + cliente.getNombreCompleto() + " (" + cliente.getNombreUsuario()
						+ ") y sus " + cliente.getCuentas().size() + " cuenta(s)?",
				"Confirmar eliminación", JOptionPane.YES_NO_OPTION);

		if (respuesta == JOptionPane.YES_OPTION) {
			usuarios.remove(cliente); // se borra de la lista REAL del banco
			mostrarClientes();        // tabla y totales se reconstruyen sin él
			JOptionPane.showMessageDialog(this, "Cliente eliminado.", "Hecho", JOptionPane.INFORMATION_MESSAGE);
		}
	}
}