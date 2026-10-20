package gui;

import java.awt.BorderLayout;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;

import domain.Usuario;

// Ventana principal de la aplicación.
// Los clientes tienen acceso a sus operaciones bancarias, mientras que
// los administradores disponen de una pestaña específica de gestión.
public class VentanaPrincipal extends JFrame {

	private static final long serialVersionUID = 1L;

	private Usuario usuario;        // Usuario que ha iniciado sesión y cuyos datos se mostrarán.
	private List<Usuario> usuarios; // Lista completa de usuarios, necesaria para volver al login.

	public VentanaPrincipal(Usuario usuario, List<Usuario> usuarios) {
		super("Banco Concurrente");
		this.usuario = usuario;
		this.usuarios = usuarios;

		// Creamos la cabecera con el nombre del usuario y el botón para cerrar sesión.
		JLabel lblSaludo = new JLabel("Hola, " + usuario.getNombreCompleto());
		JButton btnSalir = new JButton("Salir");
		btnSalir.addActionListener(e -> cerrarSesion()); // Ejecuta el método de cierre de sesión al pulsarlo.

		JPanel cabecera = new JPanel(new BorderLayout());
		cabecera.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15)); // Añadimos márgenes para separar los elementos de los bordes.
		cabecera.add(lblSaludo, BorderLayout.WEST);
		cabecera.add(btnSalir, BorderLayout.EAST);

		// Las pestañas se crean según el tipo de usuario que ha iniciado sesión.
		JTabbedPane pestanas = new JTabbedPane();

		if (usuario.esAdmin()) {
			// El administrador solo dispone, por ahora, de la pestaña de gestión de clientes.
			pestanas.addTab("Gestión de clientes", crearPanelPendiente());
		} else {
			// Los clientes pueden consultar su resumen y acceder a las secciones
			// de transferencias e histórico, que todavía están pendientes de implementar.
			pestanas.addTab("Resumen", crearPanelResumen());
			pestanas.addTab("Transferir", crearPanelPendiente());
			pestanas.addTab("Histórico", crearPanelPendiente());
		}

		// Colocamos la cabecera en la parte superior y las pestañas en el centro.
		add(cabecera, BorderLayout.NORTH);
		add(pestanas, BorderLayout.CENTER);

		setDefaultCloseOperation(EXIT_ON_CLOSE); // Cierra la aplicación al cerrar la ventana.
		setSize(700, 450);
		setLocationRelativeTo(null); // Centra la ventana en la pantalla.
	}

	// Construye el resumen bancario del cliente mostrando su saldo total
	// y el número de cuentas que tiene registradas.
	private JPanel crearPanelResumen() {
		// String.format permite dar formato al saldo con dos decimales
		// y separadores de miles. %d representa el número de cuentas.
		String texto = String.format("Saldo total: %,.2f €   |   Cuentas: %d", usuario.getSaldoTotal(),
				usuario.getCuentas().size());

		JPanel panel = new JPanel(new BorderLayout());
		panel.add(new JLabel(texto, SwingConstants.CENTER), BorderLayout.CENTER); // Mostramos el resumen centrado en el panel.
		return panel; // Devolvemos el panel para añadirlo a la pestaña correspondiente.
	}

	// Crea un panel provisional para las secciones que todavía no están implementadas.
	private JPanel crearPanelPendiente() {
		JPanel panel = new JPanel(new BorderLayout());
		panel.add(new JLabel("Pendiente de implementar", SwingConstants.CENTER), BorderLayout.CENTER);
		return panel;
	}

	// Cierra la sesión actual y vuelve a mostrar la ventana de inicio de sesión.
	private void cerrarSesion() {
		new VentanaLogin(usuarios).setVisible(true); // Reutilizamos la lista de usuarios para permitir otro inicio de sesión.
		dispose(); // Cerramos la ventana principal sin finalizar toda la aplicación.
	}
}