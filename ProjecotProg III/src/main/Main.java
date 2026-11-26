package main;

import java.sql.SQLException;
import java.util.List;

import javax.swing.JOptionPane;

import db.GestorBD;
import domain.DatosPrueba;
import domain.Usuario;
import gui.VentanaLogin;

public class Main {

	// Ruta del fichero de la base de datos (relativa a la carpeta del proyecto).
	// Si el fichero no existe, se crea solo en el primer arranque.
	private static final String RUTA_BD = "resources/db/banco.db";

	public static void main(String[] args) {
		List<Usuario> usuarios; // Aquí estarán los usuarios con los que trabaja el programa

		try {
			GestorBD.conectar(RUTA_BD); // Carga el driver y abre la conexión con el fichero de la base de datos
			GestorBD.crearTablas();     // Crea las tablas si todavía no existen (primer arranque)
			usuarios = GestorBD.cargarUsuarios(); // Lee de la base de datos los usuarios, sus cuentas y sus movimientos

			if (usuarios.isEmpty()) {
				// La base de datos está vacía: es el primer arranque. Creamos los datos de prueba
				// y los guardamos para que la próxima vez ya estén en la base de datos.
				usuarios = DatosPrueba.crearUsuarios();
				GestorBD.guardarUsuarios(usuarios);
			}
		} catch (SQLException e) {
			// Sin base de datos no podemos arrancar: mostramos el error en una ventana y terminamos.
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "No se ha podido abrir la base de datos:\n" + e.getMessage(),
					"Error de base de datos", JOptionPane.ERROR_MESSAGE);
			return;
		}

		// Al cerrar la aplicación (la X de cualquier ventana, System.exit...) Java ejecuta este hilo,
		// que cierra la conexión con la base de datos de forma ordenada.
		Runtime.getRuntime().addShutdownHook(new Thread(() -> GestorBD.desconectar()));

		// Abrimos la ventana de login con los usuarios que hemos leído de la base de datos
		// (o con los de prueba si era el primer arranque) y la hacemos visible para iniciar la aplicación.
		new VentanaLogin(usuarios).setVisible(true);
	}
}