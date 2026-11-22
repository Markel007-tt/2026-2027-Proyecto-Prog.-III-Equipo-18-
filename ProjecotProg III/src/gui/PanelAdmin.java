//FUENTE-EXTERNA
//URL: Claude (Anthropic), herramienta de IA generativa
//SIN-CAMBIOS
package gui;

import java.awt.BorderLayout;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JPanel;

import domain.Usuario;

// Pestaña "Gestión de clientes" del administrador: muestra y gestiona todos los clientes del banco
public class PanelAdmin extends JPanel {

	private static final long serialVersionUID = 1L;

	private List<Usuario> usuarios; // TODOS los usuarios del banco (la misma lista que crea Main)

	public PanelAdmin(List<Usuario> usuarios) {
		this.usuarios = usuarios;
		setLayout(new BorderLayout());

		// Provisional: comprobamos que el panel aparece y que recibe la lista
		add(new JLabel("Clientes en el banco: " + usuarios.size(), JLabel.CENTER), BorderLayout.CENTER);
	}
}
