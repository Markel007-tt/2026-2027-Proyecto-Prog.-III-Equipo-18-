package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import domain.Banco;
import domain.Cuenta;
import domain.ResultadoTransferencia;
import domain.Usuario;

// Contenido de la pestaña "Transferir" de la ventana principal.
// El usuario elige una de sus cuentas, escribe el IBAN de destino y la cantidad, y pulsa Transferir.
public class PanelTransferencia extends JPanel {

	private static final long serialVersionUID = 1L;

	private static final Color VERDE = new Color(0, 128, 0); // Color de los mensajes de éxito

	private Usuario usuario;         // Usuario que ha iniciado sesión (el que envía el dinero)
	private List<Usuario> usuarios;  // Todos los usuarios del banco, para poder buscar el IBAN de destino
	private Runnable alTransferir;   // Código que se ejecuta tras una transferencia correcta (refresca el resumen)

	private JComboBox<Cuenta> cmbOrigen = new JComboBox<>(); // Desplegable con las cuentas del usuario
	private JTextField txtIbanDestino = new JTextField(20);  // IBAN de la cuenta que recibe el dinero
	private JTextField txtCantidad = new JTextField(20);     // Cantidad a transferir
	private JTextField txtConcepto = new JTextField(20);     // Concepto (opcional)
	private JButton btnTransferir = new JButton("Transferir");
	private JLabel lblMensaje = new JLabel(" ");             // Mensaje de error (rojo) o de éxito (verde)

	public PanelTransferencia(Usuario usuario, List<Usuario> usuarios, Runnable alTransferir) {
		this.usuario = usuario;
		this.usuarios = usuarios;
		this.alTransferir = alTransferir;

		// En el desplegable se muestra el IBAN, el tipo y el saldo de cada cuenta
		cmbOrigen.setRenderer(new DefaultListCellRenderer() {
			private static final long serialVersionUID = 1L;

			@Override
			public Component getListCellRendererComponent(JList<?> lista, Object valor, int indice,
					boolean seleccionado, boolean foco) {
				super.getListCellRendererComponent(lista, valor, indice, seleccionado, foco);
				if (valor instanceof Cuenta) {
					Cuenta c = (Cuenta) valor;
					setText(String.format("%s (%s) - %,.2f €", c.getIban(), c.getTipo(), c.getSaldo()));
				}
				return this;
			}
		});
		recargarCuentas();

		// Formulario: etiquetas a la izquierda y campos a la derecha
		JPanel formulario = new JPanel(new GridBagLayout());
		anadirFila(formulario, 0, "Cuenta de origen:", cmbOrigen);
		anadirFila(formulario, 1, "IBAN de destino:", txtIbanDestino);
		anadirFila(formulario, 2, "Cantidad (€):", txtCantidad);
		anadirFila(formulario, 3, "Concepto (opcional):", txtConcepto);

		// El botón ocupa la segunda columna, debajo de los campos
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 1;
		gbc.gridy = 4;
		gbc.anchor = GridBagConstraints.WEST;
		gbc.insets = new Insets(10, 5, 5, 5);
		formulario.add(btnTransferir, gbc);

		btnTransferir.addActionListener(e -> hacerTransferencia()); // Al pulsar, se valida y se hace la transferencia

		// Mensaje debajo del formulario, centrado
		lblMensaje.setHorizontalAlignment(SwingConstants.CENTER);
		lblMensaje.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

		// El formulario va en un panel auxiliar para que quede centrado en la pestaña
		JPanel centro = new JPanel(new GridBagLayout());
		centro.add(formulario);

		setLayout(new BorderLayout());
		add(centro, BorderLayout.CENTER);
		add(lblMensaje, BorderLayout.SOUTH);

		// Si el usuario aún no tiene cuentas no puede transferir nada
		if (usuario.getCuentas().isEmpty()) {
			btnTransferir.setEnabled(false);
			txtIbanDestino.setEnabled(false);
			txtCantidad.setEnabled(false);
			txtConcepto.setEnabled(false);
			mostrarError("No tienes ninguna cuenta desde la que transferir");
		}
	}

	// Añade una fila (etiqueta + componente) al formulario en la posición "fila".
	private void anadirFila(JPanel formulario, int fila, String texto, Component campo) {
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridy = fila;
		gbc.insets = new Insets(5, 5, 5, 5);

		gbc.gridx = 0;
		gbc.anchor = GridBagConstraints.EAST;
		formulario.add(new JLabel(texto), gbc);

		gbc.gridx = 1;
		gbc.anchor = GridBagConstraints.WEST;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		formulario.add(campo, gbc);
	}

	// Vuelve a cargar las cuentas del usuario en el desplegable (para que se vean los saldos actualizados)
	// y mantiene seleccionada la misma cuenta que estaba elegida.
	private void recargarCuentas() {
		int seleccion = cmbOrigen.getSelectedIndex();
		cmbOrigen.removeAllItems();
		for (Cuenta c : usuario.getCuentas()) {
			cmbOrigen.addItem(c);
		}
		if (seleccion >= 0 && seleccion < cmbOrigen.getItemCount()) {
			cmbOrigen.setSelectedIndex(seleccion);
		}
	}

	// Lee el formulario, comprueba los datos y pide al banco que haga la transferencia.
	private void hacerTransferencia() {
		Cuenta origen = (Cuenta) cmbOrigen.getSelectedItem();
		String ibanDestino = txtIbanDestino.getText().trim().toUpperCase(); // Mayúsculas: "es01-2033" también vale
		String textoCantidad = txtCantidad.getText().trim().replace(',', '.'); // Acepta coma decimal: 12,50
		String concepto = txtConcepto.getText().trim();

		// Comprobaciones de formulario (antes de llamar al banco)
		if (origen == null) {
			mostrarError("Selecciona una cuenta de origen");
			return;
		}
		if (ibanDestino.isEmpty() || textoCantidad.isEmpty()) {
			mostrarError("Rellena el IBAN de destino y la cantidad");
			return;
		}

		double cantidad;
		try {
			cantidad = Double.parseDouble(textoCantidad);
		} catch (NumberFormatException ex) {
			mostrarError("La cantidad no es un número válido");
			return;
		}
		// parseDouble acepta textos como "NaN" o "Infinity"; los rechazamos
		if (Double.isNaN(cantidad) || Double.isInfinite(cantidad)) {
			mostrarError("La cantidad no es un número válido");
			return;
		}

		// El banco comprueba si el IBAN existe y si hay saldo suficiente, y hace la transferencia
		ResultadoTransferencia resultado = Banco.transferir(usuarios, origen, ibanDestino, cantidad, concepto);

		// Según el resultado mostramos un mensaje u otro
		switch (resultado) {
		case OK:
			// Limpiamos el formulario, refrescamos los saldos y avisamos al resto de la ventana
			txtIbanDestino.setText("");
			txtCantidad.setText("");
			txtConcepto.setText("");
			recargarCuentas();
			alTransferir.run();
			mostrarExito("Transferencia realizada correctamente");
			break;
		case IBAN_NO_EXISTE:
			mostrarError("El IBAN " + ibanDestino + " no existe");
			break;
		case SALDO_INSUFICIENTE:
			mostrarError("No tienes dinero suficiente para hacer la transferencia");
			break;
		case MISMA_CUENTA:
			mostrarError("No puedes transferir a la misma cuenta de origen");
			break;
		case CANTIDAD_INVALIDA:
			mostrarError("La cantidad debe ser mayor que 0");
			break;
		}
	}

	// Muestra un mensaje en rojo debajo del formulario
	private void mostrarError(String texto) {
		lblMensaje.setForeground(Color.RED);
		lblMensaje.setText(texto);
	}

	// Muestra un mensaje en verde debajo del formulario
	private void mostrarExito(String texto) {
		lblMensaje.setForeground(VERDE);
		lblMensaje.setText(texto);
	}
}