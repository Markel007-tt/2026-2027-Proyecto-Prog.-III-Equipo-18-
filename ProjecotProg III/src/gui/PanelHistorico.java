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

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.TableCellRenderer;

import domain.Cuenta;
import domain.Movimiento;
import domain.TipoMovimiento;
import domain.Usuario;

// Pestaña "Histórico": muestra los movimientos de las cuentas del usuario
public class PanelHistorico extends JPanel {

	private static final long serialVersionUID = 1L;

	private Usuario usuario;                                    // usuario que ha iniciado sesión
	private JComboBox<Cuenta> cmbCuentas = new JComboBox<>();   // desplegable con sus cuentas
	private JTextField txtConcepto = new JTextField(12);        // filtro por concepto
	private JComboBox<String> cmbTipo = new JComboBox<>();      // filtro por tipo
	private ModeloMovimientos modelo = new ModeloMovimientos(); // UN solo modelo para siempre
	private JTable tabla = new JTable(modelo);                  // la tabla pregunta a ese modelo

	public PanelHistorico(Usuario usuario) {
		this.usuario = usuario;
		setLayout(new BorderLayout());

		// ---------- NORTH: elegir cuenta y filtros ----------
		cmbTipo.addItem("Todos");
		for (TipoMovimiento t : TipoMovimiento.values()) { // values() devuelve todos los valores del enum
			cmbTipo.addItem(t.toString());
		}

		JPanel panelSuperior = new JPanel(); // FlowLayout por defecto: componentes en fila
		panelSuperior.add(new JLabel("Cuenta:"));
		panelSuperior.add(cmbCuentas);
		panelSuperior.add(new JLabel("Concepto:"));
		panelSuperior.add(txtConcepto);
		panelSuperior.add(new JLabel("Tipo:"));
		panelSuperior.add(cmbTipo);
		add(panelSuperior, BorderLayout.NORTH);

		// ---------- CENTER: tabla de movimientos ----------
		tabla.getTableHeader().setReorderingAllowed(false); // no se pueden arrastrar las columnas

		// Renderer: el "pintor" de cada celda
		tabla.setDefaultRenderer(Object.class, new TableCellRenderer() {
			@Override
			public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
					boolean hasFocus, int row, int column) {

				// Algunos valores (un concepto vacío) pueden ser null: evitamos NullPointerException
				String texto = "";
				if (value != null) {
					texto = value.toString();
				}

				// Cantidad y saldo con formato de dinero: 2 decimales, miles y €
				if (column == 3 || column == 4) {
					double numero = (Double) value;
					if (column == 3 && numero > 0) {
						texto = String.format("+%,.2f €", numero); // el + deja claro que entra dinero
					} else {
						texto = String.format("%,.2f €", numero);
					}
				}

				JLabel etiqueta = new JLabel(texto);
				etiqueta.setOpaque(true); // SIN esto el fondo no se pinta nunca

				// Colores por defecto: los de la tabla
				etiqueta.setBackground(table.getBackground());
				etiqueta.setForeground(table.getForeground());

				// Columna Cantidad: verde si entra dinero, rojo si sale
				if (column == 3) {
					etiqueta.setHorizontalAlignment(JLabel.RIGHT);
					double cantidad = (Double) value;
					if (cantidad > 0) {
						etiqueta.setForeground(new Color(0, 128, 0));
					} else {
						etiqueta.setForeground(Color.RED);
					}
				}

				// Columna Saldo: negativo en rosa, bajo (< 100 €) en amarillo
				if (column == 4) {
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

		// ---------- Eventos ----------

		// Al cambiar de cuenta, cambiar la tabla
		cmbCuentas.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				mostrarMovimientos();
			}
		});

		// Filtro por tipo
		cmbTipo.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				mostrarMovimientos();
			}
		});

		// Filtro por concepto: se avisa cada vez que cambia el texto
		txtConcepto.getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void insertUpdate(DocumentEvent e) { // se ha escrito algo
				mostrarMovimientos();
			}

			@Override
			public void removeUpdate(DocumentEvent e) { // se ha borrado algo
				mostrarMovimientos();
			}

			@Override
			public void changedUpdate(DocumentEvent e) { // cambios de estilo: no nos afectan
			}
		});

		// Doble clic en una fila: muestra el detalle del movimiento
		tabla.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {                // solo doble clic
					int fila = tabla.rowAtPoint(e.getPoint()); // fila donde se ha hecho clic
					if (fila != -1) {                          // -1 = clic en zona vacía
						mostrarDetalle(modelo.getMovimiento(fila));
					}
				}
			}
		});

		cargarCuentas();
		mostrarMovimientos(); // primera carga de la tabla
	}

	// Rellena el desplegable con las cuentas actuales del usuario
	private void cargarCuentas() {
		cmbCuentas.removeAllItems(); // vaciamos primero para no duplicar
		for (Cuenta c : usuario.getCuentas()) {
			cmbCuentas.addItem(c);
		}
	}

	// Muestra en la tabla los movimientos de la cuenta elegida que cumplen los filtros
	private void mostrarMovimientos() {
		Cuenta cuenta = (Cuenta) cmbCuentas.getSelectedItem();
		if (cuenta == null) {
			// Sin cuentas (o desplegable vaciándose): tabla vacía
			modelo.setMovimientos(new ArrayList<>());
			return;
		}

		String textoFiltro = txtConcepto.getText().trim().toLowerCase();
		String tipoFiltro = (String) cmbTipo.getSelectedItem();

		// Lista NUEVA con los que pasan los filtros: la de la cuenta no se toca
		List<Movimiento> filtrados = new ArrayList<>();
		for (Movimiento m : cuenta.getMovimientos()) {
			// Filtro 1: el concepto contiene el texto (sin distinguir mayúsculas)
			String concepto = "";
			if (m.getConcepto() != null) {
				concepto = m.getConcepto().toLowerCase();
			}
			boolean pasaConcepto = concepto.contains(textoFiltro); // "" lo contiene todo

			// Filtro 2: el tipo coincide, o se ha elegido "Todos"
			boolean pasaTipo = tipoFiltro == null || tipoFiltro.equals("Todos")
					|| m.getTipo().toString().equals(tipoFiltro);

			if (pasaConcepto && pasaTipo) {
				filtrados.add(m);
			}
		}
		modelo.setMovimientos(filtrados);
	}

	// Muestra todos los datos de un movimiento en un cuadro de diálogo
	private void mostrarDetalle(Movimiento m) {
		String texto = "Movimiento nº " + m.getId()
				+ "\nFecha: " + m.getFecha()
				+ "\nTipo: " + m.getTipo()
				+ "\nConcepto: " + m.getConcepto()
				+ "\nCantidad: " + String.format("%,.2f €", m.getCantidad())
				+ "\nSaldo tras la operación: " + String.format("%,.2f €", m.getSaldoResultante());
		JOptionPane.showMessageDialog(this, texto, "Detalle del movimiento", JOptionPane.INFORMATION_MESSAGE);
	}

	// Recarga cuentas y movimientos. Lo llama VentanaPrincipal cuando cambian los datos
	// (transferencia, crear o eliminar cuenta).
	public void actualizar() {
		Cuenta elegida = (Cuenta) cmbCuentas.getSelectedItem(); // recordamos la cuenta que se estaba viendo
		cargarCuentas();
		if (elegida != null && usuario.getCuentas().contains(elegida)) {
			cmbCuentas.setSelectedItem(elegida); // si sigue existiendo, la volvemos a mostrar
		}
		mostrarMovimientos();
	}
}