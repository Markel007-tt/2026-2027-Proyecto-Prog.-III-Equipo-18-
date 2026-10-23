package gui;

import java.awt.BorderLayout;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import domain.Cuenta;
import domain.Usuario;

// FUNCIONALIDAD 4: Listado de cuentas.
// Panel con una tabla que muestra el IBAN, el tipo y el saldo de cada cuenta del usuario.
// Es un JPanel normal, así que se puede meter dentro de cualquier ventana o pestaña.
public class PanelListadoCuentas extends JPanel {

	private static final long serialVersionUID = 1L;

	private Usuario usuario;          
	private DefaultTableModel modelo; // Modelo los DATOS de la tabla. La tabla solo los dibuja
	private JTable tabla;             // Tabla visual que se ve en pantalla y que lee los datos del modelo.

	// Constructor: recibe el usuario que ha iniciado sesión y construye el panel entero.
	public PanelListadoCuentas(Usuario usuario) {
		this.usuario = usuario; // Guardamos el usuario para poder leer sus cuentas .

		setLayout(new BorderLayout()); // BorderLayout: así el scroll con la tabla ocupa todo el panel.
		setBorder(BorderFactory.createTitledBorder("Mis cuentas")); // Borde con el título "Mis cuentas".

		// Nombres de las tres columnas, tal y como pide el enunciado: IBAN, tipo y saldo.
		String[] columnas = { "IBAN", "Tipo", "Saldo" };

		// Creamos el modelo con esas columnas y 0 filas (las filas se añaden en actualizar()).
		// Las llaves { } crean una subclase anónima: es una clase sin nombre que hereda de DefaultTableModel
		// y nos deja cambiar un método. Aquí cambiamos isCellEditable.
		modelo = new DefaultTableModel(columnas, 0) {
			private static final long serialVersionUID = 1L;

			// Devolvemos false para que el usuario NO pueda cambiar IBAN, tipo ni saldo a mano.
			@Override
			public boolean isCellEditable(int fila, int columna) {
				return false;
			}
		};

		tabla = new JTable(modelo); // Creamos la tabla visual y la conectamos con el modelo de datos.
		tabla.setRowHeight(24); // Altura de cada fila en píxeles, para que se lea mejor.
		tabla.getTableHeader().setReorderingAllowed(false); // Impide arrastrar las cabeceras para cambiar el orden de las columnas.
		tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); // Solo se puede seleccionar UNA fila a la vez.

		// Los números se leen mejor alineados a la derecha, así que creamos un renderer (el que "pinta" las celdas)
		// y le decimos que alinee el texto a la derecha.
		DefaultTableCellRenderer alineadoDerecha = new DefaultTableCellRenderer();
		alineadoDerecha.setHorizontalAlignment(SwingConstants.RIGHT);

		// Aplicamos ese renderer solo a la columna del saldo , la 3ª, que es el índice 2 porque se empieza en 0.
		tabla.getColumnModel().getColumn(2).setCellRenderer(alineadoDerecha);

		// Metemos la tabla en un JScrollPane: sin él no se verían las cabeceras y no habría barra de desplazamiento.
		add(new JScrollPane(tabla), BorderLayout.CENTER);

		actualizar(); // Rellenamos la tabla por primera vez con las cuentas del usuario.
	}

	// Vuelve a cargar la tabla con las cuentas actuales del usuario.
	// Se llamará cuando cambien los datos. Crear cuenta, depositar, retirar...
	// IMPORTANTE para el tema de hilos: si se llama desde otro hilo (por ejemplo un cajero),
	// hay que hacerlo con SwingUtilities.invokeLater(() -> panel.actualizar()),
	public void actualizar() {
		modelo.setRowCount(0); // Borramos todas las filas actuales para no duplicar cuentas al volver a cargar.

		// Recorremos las cuentas del usuario; "c" es cada una de ellas en cada vuelta del bucle.
		for (Cuenta c : usuario.getCuentas()) {
			// Cada fila es un array de objetos: uno por columna, en el mismo orden que "columnas".
			Object[] fila = {
					c.getIban(), // Columna 1: IBAN de la cuenta.
					c.getTipo(), // Columna 2: "Corriente" o "Ahorro" (lo decide cada clase hija de Cuenta).
					String.format("%,.2f €", c.getSaldo()) // Columna 3: saldo con 2 decimales, separador de miles y símbolo €.
			};
			modelo.addRow(fila); // Añadimos la fila al modelo y la tabla se redibuja sola.
		}
	}
}
