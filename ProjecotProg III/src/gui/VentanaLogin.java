package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import domain.Usuario;

// Ventana de inicio de sesión con opción para crear una cuenta nueva.
public class VentanaLogin extends JFrame {

	private static final long serialVersionUID = 1L;

	private List<Usuario> usuarios; // Lista con los usuarios registrados que se pueden utilizar para iniciar sesión.

	private JTextField txtUsuario = new JTextField(); // Campo donde se introduce el nombre de usuario.
	private JPasswordField txtContrasena = new JPasswordField(); // Campo de contraseña que oculta los caracteres introducidos.
	private JLabel lblError = new JLabel(" "); // Etiqueta para mostrar errores; empieza con un espacio para reservar sitio.

	public VentanaLogin(List<Usuario> usuarios) {
		super("Banco Concurrente");
		this.usuarios = usuarios; // Guardamos la lista recibida para comprobar las credenciales al iniciar sesión.

		// Creamos el formulario y colocamos sus elementos en una columna, uno debajo de otro.
		JPanel formulario = new JPanel(new GridLayout(0, 1, 5, 5));
		formulario.setBorder(BorderFactory.createEmptyBorder(20, 25, 10, 25)); // Añadimos márgenes interiores para separar los elementos de los bordes.
		formulario.add(new JLabel("Usuario"));
		formulario.add(txtUsuario);
		formulario.add(new JLabel("Contraseña"));
		formulario.add(txtContrasena);
		lblError.setForeground(Color.RED); // Los mensajes de error aparecerán en rojo.
		formulario.add(lblError);

		// Creamos los botones y definimos qué debe ocurrir al pulsarlos.
		JButton btnEntrar = new JButton("Entrar");
		btnEntrar.addActionListener(e -> intentarLogin()); // Llama al método que comprueba el usuario y la contraseña.

		JButton btnCrearCuenta = new JButton("Registrarse");
		btnCrearCuenta.addActionListener(e -> new VentanaRegistro(this).setVisible(true)); // Abre la ventana de registro y le pasa esta ventana como referencia.

		// Agrupamos los dos botones en un panel para colocarlos juntos.
		JPanel filaBotones = new JPanel();
		filaBotones.add(btnEntrar);
		filaBotones.add(btnCrearCuenta);

		// Distribuimos el formulario en el centro y los botones en la parte inferior.
		add(formulario, BorderLayout.CENTER);
		add(filaBotones, BorderLayout.SOUTH);

		setDefaultCloseOperation(EXIT_ON_CLOSE); // Cierra la aplicación al cerrar esta ventana.
		setSize(350, 280); // Establece el tamaño de la ventana.
		setResizable(false); // Impide cambiar el tamaño manualmente.
		setLocationRelativeTo(null); // Centra la ventana en la pantalla.
	}

	// Comprueba si las credenciales introducidas corresponden a un usuario registrado.
	private void intentarLogin() {
		String nombre = txtUsuario.getText().trim(); // Obtenemos el nombre y quitamos los espacios del principio y del final.
		String contrasena = new String(txtContrasena.getPassword()); // Recuperamos la contraseña introducida en el campo protegido.

		// Recorremos la lista de usuarios buscando una coincidencia.
		for (Usuario u : usuarios) {
			// El nombre se compara sin distinguir entre mayúsculas y minúsculas.
			// Además, comprobarContrasena verifica que la contraseña sea correcta.
			// Las dos condiciones deben cumplirse para permitir el acceso.
			if (u.getNombreUsuario().equalsIgnoreCase(nombre) && u.comprobarContrasena(contrasena)) {
				entrarComo(u); // Abrimos la aplicación con los datos del usuario identificado.
				return; // Terminamos el método para no seguir comprobando otros usuarios.
			}
		}

		// Si termina el bucle sin encontrar coincidencias, los datos son incorrectos.
		// Mostramos el mismo mensaje en ambos casos para no revelar qué dato ha fallado.
		lblError.setText("Usuario o contraseña incorrectos");
		txtContrasena.setText(""); // Borramos la contraseña para que se pueda volver a introducir.
	}

	// Abre la ventana principal con el usuario que ha iniciado sesión y cierra el login.
	// Este método también puede utilizarse desde la ventana de registro.
	void entrarComo(Usuario usuario) {
		new VentanaPrincipal(usuario, usuarios).setVisible(true); // Pasamos el usuario actual y la lista completa a la ventana principal.
		dispose(); // Cerramos esta ventana sin finalizar toda la aplicación.
	}
}