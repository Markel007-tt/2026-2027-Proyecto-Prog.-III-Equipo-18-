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
  	// Paneles de la pestaña "Resumen". Se guardan como campos para poder refrescarlos
  	// con su método actualizar() después de una transferencia.
  	private PanelResumenSaldo panelResumenSaldo;
  	private PanelListadoCuentas panelListadoCuentas;
	
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
			pestanas.addTab("Transferir", new PanelTransferencia(usuario, usuarios, this::actualizarResumen));
			pestanas.addTab("Histórico", crearPanelPendiente());
		}

		// Colocamos la cabecera en la parte superior y las pestañas en el centro.
		add(cabecera, BorderLayout.NORTH);
		add(pestanas, BorderLayout.CENTER);

		setDefaultCloseOperation(EXIT_ON_CLOSE); // Cierra la aplicación al cerrar la ventana.
		setSize(700, 450);
		setLocationRelativeTo(null); // Centra la ventana en la pantalla.
	}

	// Construye la pestaña "Resumen" del cliente. Sigue el boceto de la entrega:
	// el resumen de saldo arriba (funcionalidad 5) y la tabla de cuentas en el centro (funcionalidad 4).
	private JPanel crearPanelResumen() {
		// BorderLayout con 10 píxeles de separación vertical entre la zona de arriba y la del centro.
		JPanel panel = new JPanel(new BorderLayout(0, 10));
		panel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15)); // Márgenes para separar el contenido de los bordes de la pestaña.

		// Saldo total y número de cuentas (clase PanelResumenSaldo), arriba.
		  panelResumenSaldo = new PanelResumenSaldo(usuario);
		  panel.add(panelResumenSaldo, BorderLayout.NORTH);

		// Tabla con IBAN, tipo y saldo de cada cuenta (clase PanelListadoCuentas), ocupando el centro.
		  panelListadoCuentas = new PanelListadoCuentas(usuario);
		  panel.add(panelListadoCuentas, BorderLayout.CENTER);

		return panel; // Devolvemos el panel para añadirlo a la pestaña correspondiente.
	}

  	// Vuelve a calcular el resumen de saldo y a cargar la tabla de cuentas con los datos actuales.
  	// Lo llama PanelTransferencia después de cada transferencia correcta.
  	private void actualizarResumen() {
  		panelResumenSaldo.actualizar();   // Saldo total y número de cuentas.
  		panelListadoCuentas.actualizar(); // Tabla con el saldo de cada cuenta.
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