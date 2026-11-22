package gui;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;

import domain.Usuario;

// Pestaña "Gestión de clientes" del administrador: muestra y gestiona todos los clientes del banco
public class PanelAdmin extends JPanel {

	private static final long serialVersionUID = 1L;

	private List<Usuario> usuarios;                       // TODOS los usuarios del banco (la misma lista que crea Main)
	private ModeloClientes modelo = new ModeloClientes(); // UN solo modelo para siempre
	private JTable tabla = new JTable(modelo);            // la tabla pregunta a ese modelo

	public PanelAdmin(List<Usuario> usuarios) {
		this.usuarios = usuarios;
		setLayout(new BorderLayout());

		// ---------- CENTER: tabla de clientes ----------
		tabla.getTableHeader().setReorderingAllowed(false);           // no se pueden arrastrar las columnas
		tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); // solo se puede elegir un cliente a la vez
		add(new JScrollPane(tabla), BorderLayout.CENTER);            // sin JScrollPane no se ven las cabeceras

		mostrarClientes(); // primera carga de la tabla
	}

	// Muestra en la tabla todos los usuarios del banco excepto el administrador
	private void mostrarClientes() {
		List<Usuario> clientes = new ArrayList<>(); // lista NUEVA: la del banco no se toca
		for (Usuario u : usuarios) {
			if (!u.esAdmin()) {
				clientes.add(u);
			}
		}
		modelo.setClientes(clientes);
	}
}