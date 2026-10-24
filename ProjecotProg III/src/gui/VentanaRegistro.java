package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import domain.Usuario;

// Formulario provisional para registrar un nuevo cliente.
public class VentanaRegistro extends JDialog {

    private static final long serialVersionUID = 1L;

    private VentanaLogin padre;

    // Campos para introducir los datos del nuevo usuario.
    private JTextField txtNombre = new JTextField(20);
    private JTextField txtApellidos = new JTextField(20);
    private JTextField txtDni = new JTextField(20);
    private JTextField txtFechaNac = new JTextField(20);
    private JTextField txtEmail = new JTextField(20);
    private JTextField txtTelefono = new JTextField(20);
    private JTextField txtDireccion = new JTextField(20);
    private JTextField txtCp = new JTextField(20);
    private JTextField txtCiudad = new JTextField(20);
    private JTextField txtUsuario = new JTextField(20);

    private JPasswordField txtContrasena = new JPasswordField(20);
    private JPasswordField txtContrasena2 = new JPasswordField(20);

    // Etiqueta para mostrar posibles errores.
    private JLabel lblError = new JLabel(" ");

    // Panel que contiene todos los campos del formulario.
    private JPanel formulario = new JPanel(new GridLayout(0, 1, 5, 5));

    public VentanaRegistro(VentanaLogin padre) { //Nombre del panel de Registro
        super(padre, "Registro de Nuevo Cliente", true);
        this.padre = padre;

        formulario.setBorder(
                BorderFactory.createEmptyBorder(10, 15, 10, 15));

        // Datos personales.
        anadirSeccion("DATOS PERSONALES");
        anadirCampo("Nombre", txtNombre);
        anadirCampo("Apellidos", txtApellidos);
        anadirCampo("DNI", txtDni);
        anadirCampo("Fecha de nacimiento (dd/mm/aaaa)", txtFechaNac);

        // Datos de contacto.
        anadirSeccion("CONTACTO");
        anadirCampo("Email", txtEmail);
        anadirCampo("Teléfono", txtTelefono);

        // Dirección del cliente.
        anadirSeccion("DIRECCIÓN");
        anadirCampo("Calle y número", txtDireccion);
        anadirCampo("Código postal", txtCp);
        anadirCampo("Ciudad", txtCiudad);

        // Datos necesarios para acceder a la aplicación.
        anadirSeccion("ACCESO A LA BANCA");
        anadirCampo("Usuario", txtUsuario);
        anadirCampo("Contraseña", txtContrasena);
        anadirCampo("Repetir contraseña", txtContrasena2);

        // Mostramos los mensajes de error en rojo.
        lblError.setForeground(Color.RED);

        // Botones para crear la cuenta o cancelar el registro.
        JButton btnCrear = new JButton("Registrarse");
        btnCrear.addActionListener(e -> crearCuenta());

        JButton btnCancelar = new JButton("Cancelar");
        btnCancelar.addActionListener(e -> dispose());

        JPanel filaBotones = new JPanel();
        filaBotones.add(btnCrear);
        filaBotones.add(btnCancelar);

        // Colocamos el mensaje de error encima de los botones.
        JPanel inferior = new JPanel(new BorderLayout());
        inferior.setBorder(
                BorderFactory.createEmptyBorder(5, 15, 5, 15));

        inferior.add(lblError, BorderLayout.NORTH);
        inferior.add(filaBotones, BorderLayout.CENTER);

        // Colocamos el formulario y la zona de botones en la ventana.
        add(formulario, BorderLayout.CENTER);
        add(inferior, BorderLayout.SOUTH);

        setSize(600, 650);
        setLocationRelativeTo(padre);
    }

    // Comprueba los datos y crea el usuario si son correctos.
    private void crearCuenta() {
    	// Recoge el texto que ha escrito el usuario en el campo y elimina los espacios del principio y del final.
        String nombre = txtNombre.getText().trim();
        String apellidos = txtApellidos.getText().trim();
        String dni = txtDni.getText().trim();
        String fechaNac = txtFechaNac.getText().trim();
        String email = txtEmail.getText().trim();
        String telefono = txtTelefono.getText().trim();
        String direccion = txtDireccion.getText().trim();
        String cp = txtCp.getText().trim();
        String ciudad = txtCiudad.getText().trim();
        String usuario = txtUsuario.getText().trim();

        String contrasena = new String(txtContrasena.getPassword());
        String contrasena2 = new String(txtContrasena2.getPassword());

        // Comprobamos que no quede ningún campo vacío.
        if (nombre.isEmpty() || apellidos.isEmpty() || dni.isEmpty()
                || fechaNac.isEmpty() || email.isEmpty()
                || telefono.isEmpty() || direccion.isEmpty()
                || cp.isEmpty() || ciudad.isEmpty()
                || usuario.isEmpty() || contrasena.isEmpty()
                || contrasena2.isEmpty()) {

            lblError.setText("Rellena todos los campos");
            return;
        }

        // Las dos contraseñas deben coincidir.
        if (!contrasena.equals(contrasena2)) {
            lblError.setText("Las contraseñas no coinciden");

            // Vacia el campo de contraseña para volver a completarlos
            txtContrasena.setText(""); 
            txtContrasena2.setText("");
            return;
        }

        // Creamos el usuario con los datos introducidos.
        Usuario nuevo = new Usuario(
                usuario,
                contrasena,
                nombre + " " + apellidos,
                dni,
                fechaNac,
                email,
                telefono,
                direccion,
                cp,
                ciudad
        );

        // Cerramos el registro y entramos con el nuevo usuario.
        // Actualmente, el usuario no se guarda de forma permanente.
        dispose();
        padre.entrarComo(nuevo);
    }

 // Añade un título para separar las secciones del formulario.
    private void anadirSeccion(String titulo) {

        // Creamos la etiqueta y ponemos el texto en negrita y tamaño 14.
        JLabel etiqueta = new JLabel(titulo);
        etiqueta.setFont(new Font("Arial", Font.BOLD, 14));

        etiqueta.setBorder(BorderFactory.createEmptyBorder(8, 5, 2, 5));

        // Incorporamos el título al formulario.
        formulario.add(etiqueta);
    }

    // Añade una fila con el nombre del campo a la izquierda y el cuadro de texto a la derecha.
    private void anadirCampo(String texto, JComponent campo) {

        // Organizamos la fila de forma que podamos colocar la etiqueta y el campo por separado.
        // Dejamos 15 píxeles de separación horizontal entre ambos.
        JPanel fila = new JPanel(new BorderLayout(15, 0));

        // Todas las etiquetas tienen el mismo ancho para que los campos queden alineados.
        JLabel etiqueta = new JLabel(texto);
        etiqueta.setPreferredSize(new Dimension(220, 25));

        // WEST coloca la etiqueta a la izquierda y CENTER ocupa el espacio restante con el campo.
        fila.add(etiqueta, BorderLayout.WEST);
        fila.add(campo, BorderLayout.CENTER);

        // Añadimos la fila completa al formulario.
        formulario.add(fila);
    }
}