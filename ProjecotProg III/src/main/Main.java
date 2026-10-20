package main;

import domain.DatosPrueba;
import gui.VentanaLogin;

public class Main {

	public static void main(String[] args) {
		// Creamos la ventana de login pasando los usuarios de prueba
		// y la hacemos visible para iniciar la aplicación.
		new VentanaLogin(DatosPrueba.crearUsuarios()).setVisible(true);
	}
}