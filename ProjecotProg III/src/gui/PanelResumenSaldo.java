package gui;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import domain.Usuario;

// FUNCIONALIDAD 5: Resumen de saldo.
// Panel con dos recuadros: el saldo total del usuario y el número de cuentas que tiene.
public class PanelResumenSaldo extends JPanel {

	private static final long serialVersionUID = 1L;

	private Usuario usuario;                         // Usuario del que se calcula el resumen.
	private JLabel lblSaldoTotal = new JLabel();     // Etiqueta donde se escribe la suma de los saldos de todas sus cuentas.
	private JLabel lblNumCuentas = new JLabel();     // Etiqueta donde se escribe cuántas cuentas tiene.

	// Constructor: recibe el usuario que ha iniciado sesión y construye el panel.
	public PanelResumenSaldo(Usuario usuario) {
		this.usuario = usuario; // Guardamos el usuario para poder recalcular el resumen cuando haga falta.

		setLayout(new GridLayout(1, 2, 20, 0)); // 1 fila y 2 columnas (una para cada recuadro), con 20 px de hueco entre ellas.

		add(crearRecuadro("Saldo total", lblSaldoTotal));       // Recuadro de la izquierda: saldo total.
		add(crearRecuadro("Número de cuentas", lblNumCuentas)); // Recuadro de la derecha: número de cuentas.

		actualizar(); // Escribimos los valores por primera vez (hasta ahora las etiquetas están vacías).
	}

	// Método auxiliar: construye un recuadro con un título y un valor grande debajo.
	// Lo usamos dos veces, así no repetimos el mismo código para cada recuadro.
	private JPanel crearRecuadro(String titulo, JLabel valor) {
		JPanel recuadro = new JPanel(new BorderLayout()); // Panel con BorderLayout para centrar la etiqueta del valor.
		recuadro.setBorder(BorderFactory.createTitledBorder(titulo)); // Hecho por IA desde linea 40 a 48. Borde con el título arriba a la izquierda.

		// deriveFont crea una copia de la fuente actual con otro estilo y tamaño: negrita y 22 puntos.
		// Se escribe 22f (con f) porque el tamaño tiene que ser un float.
		valor.setFont(valor.getFont().deriveFont(Font.BOLD, 22f));
		valor.setHorizontalAlignment(SwingConstants.CENTER); // Centramos el texto horizontalmente dentro del recuadro.

		recuadro.add(valor, BorderLayout.CENTER); // Colocamos el valor en el centro del recuadro.
		return recuadro; // Devolvemos el recuadro ya montado para añadirlo al panel.
	}

	// Recalcula y vuelve a escribir los dos valores del resumen.
	// Se llamará cuando cambien los datos (depósito, retiro, crear o eliminar cuenta...).
	// IMPORTANTE para el tema de hilos: si se llama desde otro hilo (por ejemplo un cajero),
	// hay que hacerlo con SwingUtilities.invokeLater(() -> panel.actualizar()), porque Swing solo es seguro en su propio hilo.
	public void actualizar() {
		// getSaldoTotal() ya existe en Usuario: suma el saldo de todas sus cuentas.
		// Con el formato "%,.2f €" se muestra con separador de miles, 2 decimales y el símbolo €.
		lblSaldoTotal.setText(String.format("%,.2f €", usuario.getSaldoTotal()));

		// getCuentas() devuelve la lista de cuentas, y size() dice cuántos elementos tiene.
		// String.valueOf convierte ese número entero en texto, que es lo que necesita setText.
		lblNumCuentas.setText(String.valueOf(usuario.getCuentas().size()));
	}
}