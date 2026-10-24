package gui;

import java.awt.BorderLayout;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import domain.Banco;
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
	private List<Usuario> usuarios;    // Para verificar si el nuevo IBAN está libre
	private Runnable alCambiarCuentas; // Para avisar a la ventana de que actualice saldos
	// Constructor: recibe el usuario que ha iniciado sesión y construye el panel entero.
	public PanelListadoCuentas(Usuario usuario, List<Usuario> usuarios, Runnable alCambiarCuentas) {
		this.usuario = usuario; // Guardamos el usuario para poder leer sus cuentas .
		this.usuarios = usuarios;
		this.alCambiarCuentas = alCambiarCuentas;
		
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
		DefaultTableCellRenderer alineadoDerecha = new DefaultTableCellRenderer(); //Hecho con IA y he preguntar al chatbot 
		alineadoDerecha.setHorizontalAlignment(SwingConstants.RIGHT);				// sobre el DefaultTableCellRenderer

		// Aplicamos ese renderer solo a la columna del saldo , la 3ª, que es el índice 2 porque se empieza en 0.
		tabla.getColumnModel().getColumn(2).setCellRenderer(alineadoDerecha);

		// Metemos la tabla en un JScrollPane: sin él no se verían las cabeceras y no habría barra de desplazamiento.
		add(new JScrollPane(tabla), BorderLayout.CENTER);
		
		// Panel inferior con botón alineado a la derecha
		JPanel panelBotones = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT));
		JButton btnCrearCuenta = new JButton("Nueva Cuenta");
				
		// Al hacer clic, lanza el menú de creación
		btnCrearCuenta.addActionListener(e -> mostrarDialogoCrearCuenta());
				
		panelBotones.add(btnCrearCuenta);
		add(panelBotones, BorderLayout.SOUTH);

		actualizar(); // Rellenamos la tabla por primera vez con las cuentas del usuario.
	}
	
	// Muestra un menú, pide un IBAN nuevo al banco y crea la cuenta elegida
	private void mostrarDialogoCrearCuenta() {
		String[] opciones = {"Corriente", "Ahorro", "Cancelar"};
		int eleccion = javax.swing.JOptionPane.showOptionDialog(this,
				"¿Qué tipo de cuenta deseas abrir?", "Abrir nueva cuenta",
				javax.swing.JOptionPane.DEFAULT_OPTION, javax.swing.JOptionPane.QUESTION_MESSAGE,
				null, opciones, opciones[0]);
			
		// Cancela si el usuario cierra la ventana o elige "Cancelar"
		if (eleccion == 2 || eleccion == javax.swing.JOptionPane.CLOSED_OPTION) return; 
			
		String nuevoIban = Banco.generarIbanNuevo(usuarios);
		Cuenta nuevaCuenta;
			
		// 0 es Corriente (con 500€ de descubierto), 1 es Ahorro
		if (eleccion == 0) {
			nuevaCuenta = new domain.CuentaCorriente(nuevoIban, usuario.getNombreUsuario(), 0, 500);
		} else {
			nuevaCuenta = new domain.CuentaAhorro(nuevoIban, usuario.getNombreUsuario(), 0);
		}
			
		// Añade la cuenta al usuario y pide a la ventana principal que redibuje los datos
		usuario.getCuentas().add(nuevaCuenta);
		alCambiarCuentas.run();
			
		javax.swing.JOptionPane.showMessageDialog(this, 
				"Cuenta creada con éxito.\nTu nuevo IBAN es: " + nuevoIban, 
				"Cuenta creada", javax.swing.JOptionPane.INFORMATION_MESSAGE);
	}
	// Vuelve a cargar la tabla con las cuentas actuales del usuario.
	// Se llamará cuando cambien los datos. Crear cuenta, depositar, retirar...
	// IMPORTANTE para el tema de hilos: si se llama desde otro hilo (por ejemplo un cajero),
	// hay que hacerlo con SwingUtilities.invokeLater(() -> panel.actualizar()),
	public void actualizar() {
		modelo.setRowCount(0); // Borramos todas las filas actuales para no duplicar cuentas al volver a cargar.

		// Recorremos las cuentas del usuario; "c" es cada una de ellas en cada vuelta del bucle.
		for (Cuenta c : usuario.getCuentas()) { //Hecho con IA desde 78 a 87.
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
