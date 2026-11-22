package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.TableCellRenderer;

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

		// ---------- SOUTH: totales ----------
		lblTotales.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10)); // margen interior
		add(lblTotales, BorderLayout.SOUTH);

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
}