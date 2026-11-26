package db;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import domain.Cuenta;
import domain.CuentaAhorro;
import domain.CuentaCorriente;
import domain.Movimiento;
import domain.TipoMovimiento;
import domain.Usuario;

// FUNCIONALIDAD 17: Persistencia en base de datos.
// Clase GESTORA (como en las diapositivas del Tema 5A): es la ÚNICA clase del proyecto que habla con la base de datos.
// El resto del programallama a sus métodos.
// Todos los métodos son static, así que no hace falta crear ningún objeto GestorBD.
// Todos los métodos son synchronized: solo UN hilo a la vez puede usar la conexión, porque la conexión es
// una sola y compartida (importante cuando funcionen varios cajeros a la vez).
//
// Tablas (relacionadas entre sí):
//   usuario    1 ---- N  cuenta    1 ---- N  movimiento
// Un usuario tiene varias cuentas y cada cuenta tiene varios movimientos.
public class GestorBD {

	// Conexión activa con la base de datos. Vale null mientras no estemos conectados.
	// Es static porque solo existe una conexión para toda la aplicación.
	private static Connection conn;

	// CONEXIÓN Y CREACIÓN DE TABLAS , se usan una vez, al arrancar la aplicación

	// Carga el driver de SQLite y abre la conexión con el fichero de base de datos indicado.
	// Si el fichero no existe, SQLite lo crea vacío. Lanza SQLException si algo falla, para que Main avise al usuario.
	public static synchronized void conectar(String ruta) throws SQLException {
		try {
			// Carga en memoria la clase del driver de SQLite. Solo hace falta hacerlo una vez por programa.
			Class.forName("org.sqlite.JDBC");
		} catch (ClassNotFoundException e) {
			// Si salta es porque el .jar del driver no está en el Build Path de Eclipse.
			throw new SQLException("No se encuentra el driver de SQLite. Añade lib/sqlite-jdbc-3.46.1.3.jar al Build Path.", e);
		}

		File carpeta = new File(ruta).getAbsoluteFile().getParentFile(); // Carpeta donde va el fichero (resources/db)
		if (carpeta != null) {
			carpeta.mkdirs(); // Si la carpeta no existe la crea; si ya existe no hace nada. SQLite no crea carpetas.
		}

		// Abre la conexión. La URL de SQLite es "jdbc:sqlite:" seguido de la ruta del fichero.
		conn = DriverManager.getConnection("jdbc:sqlite:" + ruta);

		// SQLite no vigila las claves foráneas si no se le pide. Con esto exige que una cuenta
		// pertenezca a un usuario que existe y que un movimiento pertenezca a una cuenta que existe.
		try (Statement st = conn.createStatement()) {
			st.execute("PRAGMA foreign_keys = ON");
		}
	}

	// Cierra la conexión con la base de datos. Se llama al cerrar el programa (ver Main).
	public static synchronized void desconectar() {
		if (conn != null) { // Solo si hay una conexión abierta
			try {
				conn.close(); // Libera el fichero de la base de datos
			} catch (SQLException e) {
				System.out.println("Error al cerrar la base de datos: " + e.getMessage());
			}
			conn = null; // Dejamos constancia de que ya no hay conexión
		}
	}

	// Crea las tres tablas si todavía no existen. "IF NOT EXISTS" evita el error cuando ya estaban creadas
	// (es decir, en todos los arranques menos el primero).
	public static synchronized void crearTablas() throws SQLException {
		exigirConexion(); // Si no hay conexión, lanza SQLException

		// Tabla de usuarios. El nombre de usuario es la clave primaria: no puede repetirse.
		// admin se guarda como número (SQLite no tiene boolean): 1 = administrador, 0 = cliente.
		// Los datos personales pueden ser NULL (los usuarios de prueba no los tienen).
		String sqlUsuario = "CREATE TABLE IF NOT EXISTS usuario ("
				+ "nombre_usuario TEXT PRIMARY KEY, "
				+ "contrasena TEXT NOT NULL, "
				+ "nombre_completo TEXT NOT NULL, "
				+ "admin INTEGER NOT NULL DEFAULT 0, "
				+ "dni TEXT, fecha_nacimiento TEXT, email TEXT, telefono TEXT, "
				+ "direccion TEXT, codigo_postal TEXT, ciudad TEXT)";

		// Tabla de cuentas. El IBAN es la clave primaria. titular es una clave foránea hacia usuario.
		// tipo guarda "Corriente" o "Ahorro". limite_descubierto solo se usa en las corrientes (en las de ahorro vale 0).
		String sqlCuenta = "CREATE TABLE IF NOT EXISTS cuenta ("
				+ "iban TEXT PRIMARY KEY, "
				+ "titular TEXT NOT NULL REFERENCES usuario(nombre_usuario), "
				+ "tipo TEXT NOT NULL, "
				+ "saldo REAL NOT NULL, "
				+ "limite_descubierto REAL NOT NULL DEFAULT 0)";

		// Tabla de movimientos. El id se genera solo (AUTOINCREMENT). iban es una clave foránea hacia cuenta.
		// La fecha se guarda como texto en formato aaaa-mm-dd y el tipo con el nombre del enum (DEPOSITO, RETIRO...).
		String sqlMovimiento = "CREATE TABLE IF NOT EXISTS movimiento ("
				+ "id INTEGER PRIMARY KEY AUTOINCREMENT, "
				+ "iban TEXT NOT NULL REFERENCES cuenta(iban), "
				+ "fecha TEXT NOT NULL, "
				+ "tipo TEXT NOT NULL, "
				+ "cantidad REAL NOT NULL, "
				+ "concepto TEXT, "
				+ "saldo_resultante REAL NOT NULL)";

		// Statement sirve para sentencias fijas, sin valores que vengan de variables.
		// Al estar dentro del try(...) se cierra solo al terminar (try-with-resources).
		try (Statement st = conn.createStatement()) {
			st.execute(sqlUsuario); // Orden importante: primero usuario, luego cuenta, luego movimiento,
			st.execute(sqlCuenta); // porque cada tabla hace referencia a la anterior.
			st.execute(sqlMovimiento);
		}
	}

	// ================================================================================
	// LECTURA: de la base de datos a los objetos del programa (se usa una vez, al arrancar)
	// ================================================================================

	// Lee las tres tablas y reconstruye la lista de usuarios con sus cuentas y los movimientos de cada cuenta.
	// Devuelve una lista vacía si la base de datos no tiene ningún usuario (primer arranque).
	public static synchronized List<Usuario> cargarUsuarios() throws SQLException {
		exigirConexion();

		List<Usuario> usuarios = new ArrayList<>();          // Resultado: todos los usuarios, en el orden en que se guardaron
		Map<String, Usuario> usuarioPorNombre = new HashMap<>(); // Para encontrar rápido el dueño de cada cuenta
		Map<String, Cuenta> cuentaPorIban = new HashMap<>();     // Para encontrar rápido la cuenta de cada movimiento

		// PASO 1: usuarios. ORDER BY rowid = en el mismo orden en que se insertaron.
		String sqlUsuarios = "SELECT nombre_usuario, contrasena, nombre_completo, admin, dni, fecha_nacimiento, "
				+ "email, telefono, direccion, codigo_postal, ciudad FROM usuario ORDER BY rowid";
		try (Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sqlUsuarios)) {
			while (rs.next()) { // next() avanza a la siguiente fila; devuelve false cuando ya no quedan
				// Creamos el Usuario con el constructor completo. getString devuelve null si la columna es NULL.
				Usuario u = new Usuario(rs.getString("nombre_usuario"), rs.getString("contrasena"),
						rs.getString("nombre_completo"), rs.getString("dni"), rs.getString("fecha_nacimiento"),
						rs.getString("email"), rs.getString("telefono"), rs.getString("direccion"),
						rs.getString("codigo_postal"), rs.getString("ciudad"));
				u.setAdmin(rs.getInt("admin") == 1); // 1 en la base de datos = true en Java
				usuarios.add(u); // Lo añadimos a la lista que devolveremos
				usuarioPorNombre.put(u.getNombreUsuario(), u); // Y al mapa para localizarlo después por su nombre
			}
		}

		// PASO 2: cuentas. Cada una se añade a la lista de cuentas de su titular.
		String sqlCuentas = "SELECT iban, titular, tipo, saldo, limite_descubierto FROM cuenta ORDER BY rowid";
		try (Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sqlCuentas)) {
			while (rs.next()) {
				String iban = rs.getString("iban");
				String titular = rs.getString("titular");
				double saldo = rs.getDouble("saldo");

				Cuenta cuenta; // Cuenta es abstracta: creamos la clase hija que corresponda según el tipo guardado
				if (rs.getString("tipo").equals("Corriente")) {
					cuenta = new CuentaCorriente(iban, titular, saldo, rs.getDouble("limite_descubierto"));
				} else {
					cuenta = new CuentaAhorro(iban, titular, saldo);
				}

				Usuario dueno = usuarioPorNombre.get(titular); // Buscamos al titular en el mapa
				if (dueno != null) { // Siempre debería existir (clave foránea), pero comprobamos por seguridad
					dueno.getCuentas().add(cuenta); // La cuenta pasa a la lista de cuentas de su dueño
					cuentaPorIban.put(iban, cuenta); // Y al mapa para localizarla después por su IBAN
				}
			}
		}

		// PASO 3: movimientos. ORDER BY id = en orden cronológico (el id crece con cada movimiento nuevo).
		String sqlMovimientos = "SELECT iban, fecha, tipo, cantidad, concepto, saldo_resultante FROM movimiento ORDER BY id";
		try (Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sqlMovimientos)) {
			while (rs.next()) {
				Cuenta cuenta = cuentaPorIban.get(rs.getString("iban")); // Cuenta a la que pertenece este movimiento
				if (cuenta != null) {
					// Reconstruimos el Movimiento a partir de las columnas.
					// LocalDate.parse convierte el texto aaaa-mm-dd en fecha; valueOf convierte el texto en el enum.
					Movimiento m = new Movimiento(LocalDate.parse(rs.getString("fecha")),
							TipoMovimiento.valueOf(rs.getString("tipo")), rs.getDouble("cantidad"),
							rs.getString("concepto"), rs.getDouble("saldo_resultante"));
					// Lo añadimos directamente al historial de la cuenta. NO usamos depositar() ni retirar(),
					// porque cambiarían el saldo: aquí solo recuperamos el historial tal y como estaba guardado.
					cuenta.getMovimientos().add(m);
				}
			}
		}

		return usuarios;
	}

	// ================================================================================
	// ESCRITURA: de los objetos del programa a la base de datos
	// ================================================================================

	// Guarda de golpe TODOS los usuarios con sus cuentas y movimientos. Se usa solo en el primer arranque,
	// para guardar los datos de prueba. Se hace en una TRANSACCIÓN: o se guarda todo o no se guarda nada.
	public static synchronized void guardarUsuarios(List<Usuario> usuarios) throws SQLException {
		exigirConexion();

		conn.setAutoCommit(false); // Empieza la transacción: los cambios no son definitivos hasta hacer commit()
		try {
			for (Usuario u : usuarios) {
				insertarUsuarioSQL(u); // 1º el usuario (las cuentas dependen de él)
				for (Cuenta c : u.getCuentas()) {
					insertarCuentaSQL(c); // 2º cada una de sus cuentas
					for (Movimiento m : c.getMovimientos()) {
						insertarMovimientoSQL(c.getIban(), m); // 3º cada movimiento de la cuenta
					}
				}
			}
			conn.commit(); // Todo ha ido bien: se guardan definitivamente todos los cambios
		} catch (SQLException e) {
			conn.rollback(); // Algo ha fallado: se deshace todo lo hecho dentro de la transacción
			throw e; // Y avisamos a quien nos llamó
		} finally {
			conn.setAutoCommit(true); // Volvemos al modo normal (cada sentencia se guarda sola)
		}
	}

	// Guarda un usuario nuevo (registro). Devuelve true si se ha guardado y false si ha habido un error.
	public static synchronized boolean insertarUsuario(Usuario usuario) {
		if (!hayConexion()) {
			return false;
		}
		try {
			insertarUsuarioSQL(usuario); // Una sola sentencia: no hace falta transacción
			return true;
		} catch (SQLException e) {
			System.out.println("Error al guardar el usuario " + usuario.getNombreUsuario() + ": " + e.getMessage());
			e.printStackTrace(); // Imprimimos el error completo: nos ayuda a encontrar el fallo
			return false;
		}
	}

	// Guarda una cuenta nueva junto con sus movimientos (si los tuviera). Devuelve true si todo ha ido bien.
	public static synchronized boolean insertarCuenta(Cuenta cuenta) {
		if (!hayConexion()) {
			return false;
		}
		try {
			conn.setAutoCommit(false); // Transacción: la cuenta y sus movimientos se guardan juntos o no se guarda nada
			insertarCuentaSQL(cuenta);
			for (Movimiento m : cuenta.getMovimientos()) {
				insertarMovimientoSQL(cuenta.getIban(), m);
			}
			conn.commit();
			return true;
		} catch (SQLException e) {
			System.out.println("Error al guardar la cuenta " + cuenta.getIban() + ": " + e.getMessage());
			e.printStackTrace();
			deshacer(); // rollback
			return false;
		} finally {
			restaurarAutoCommit(); // Pase lo que pase, volvemos al modo normal
		}
	}

	// Borra una cuenta y todos sus movimientos. Devuelve true si todo ha ido bien.
	// Primero hay que borrar los movimientos y después la cuenta, porque los movimientos dependen de ella.
	public static synchronized boolean eliminarCuenta(String iban) {
		if (!hayConexion()) {
			return false;
		}
		try {
			conn.setAutoCommit(false); // Transacción: se borran las dos cosas o ninguna
			try (PreparedStatement psMov = conn.prepareStatement("DELETE FROM movimiento WHERE iban = ?");
					PreparedStatement psCuenta = conn.prepareStatement("DELETE FROM cuenta WHERE iban = ?")) {
				psMov.setString(1, iban); // El ? se sustituye por el IBAN (los huecos se numeran desde 1)
				psMov.executeUpdate(); // Borra los movimientos de esa cuenta
				psCuenta.setString(1, iban);
				psCuenta.executeUpdate(); // Borra la cuenta
			}
			conn.commit();
			return true;
		} catch (SQLException e) {
			System.out.println("Error al eliminar la cuenta " + iban + ": " + e.getMessage());
			e.printStackTrace();
			deshacer();
			return false;
		} finally {
			restaurarAutoCommit();
		}
	}

	// Borra un usuario con todas sus cuentas y los movimientos de esas cuentas (lo usa el panel de administrador).
	// Orden obligatorio por las claves foráneas: movimientos -> cuentas -> usuario.
	public static synchronized boolean eliminarUsuario(String nombreUsuario) {
		if (!hayConexion()) {
			return false; // Sin conexión no se puede borrar nada
		}
		try {
			conn.setAutoCommit(false); // Transacción: se borra todo o nada
			try (PreparedStatement psMov = conn.prepareStatement(
					"DELETE FROM movimiento WHERE iban IN (SELECT iban FROM cuenta WHERE titular = ?)");
					PreparedStatement psCuentas = conn.prepareStatement("DELETE FROM cuenta WHERE titular = ?");
					PreparedStatement psUsuario = conn.prepareStatement("DELETE FROM usuario WHERE nombre_usuario = ?")) {
				psMov.setString(1, nombreUsuario); // 1º: movimientos de todas las cuentas de ese usuario
				psMov.executeUpdate();
				psCuentas.setString(1, nombreUsuario); // 2º: sus cuentas
				psCuentas.executeUpdate();
				psUsuario.setString(1, nombreUsuario); // 3º: el propio usuario
				psUsuario.executeUpdate();
			}
			conn.commit();
			return true;
		} catch (SQLException e) {
			System.out.println("Error al eliminar el usuario " + nombreUsuario + ": " + e.getMessage());
			e.printStackTrace();
			deshacer();
			return false;
		} finally {
			restaurarAutoCommit();
		}
	}

	// Guarda el resultado de una operación (depósito, retiro o transferencia) sobre una o varias cuentas.
	// "Cuenta..." (varargs) permite pasar una cuenta (depósito/retiro) o dos (transferencia: origen y destino).
	// PRECONDICIÓN: la operación ya se ha hecho en memoria, así que el ÚLTIMO movimiento de cada cuenta es
	// el nuevo y todavía no está en la base de datos. Llamar solo después de una operación que haya salido bien.
	// Todo va en una transacción: en una transferencia se guardan los dos saldos y los dos movimientos, o ninguno.
	public static synchronized boolean guardarOperacion(Cuenta... cuentas) {
		if (!hayConexion()) {
			return false;
		}
		try {
			conn.setAutoCommit(false);
			for (Cuenta c : cuentas) {
				// Actualiza el saldo de la cuenta en la tabla cuenta
				try (PreparedStatement ps = conn.prepareStatement("UPDATE cuenta SET saldo = ? WHERE iban = ?")) {
					ps.setDouble(1, c.getSaldo());
					ps.setString(2, c.getIban());
					ps.executeUpdate();
				}
				// Inserta el último movimiento de la lista: es el que acaba de generar la operación
				List<Movimiento> movimientos = c.getMovimientos();
				insertarMovimientoSQL(c.getIban(), movimientos.get(movimientos.size() - 1));
			}
			conn.commit();
			return true;
		} catch (SQLException e) {
			System.out.println("Error al guardar la operación: " + e.getMessage());
			e.printStackTrace();
			deshacer();
			return false;
		} finally {
			restaurarAutoCommit();
		}
	}

	// MÉTODOS AUXILIARES PRIVADOS 

	// Inserta una fila en la tabla usuario. Usa PreparedStatement: los valores van en los huecos "?" y JDBC
	// se encarga de escribirlos de forma segura. NUNCA se pegan los valores a mano dentro del texto SQL,
	// porque permitiría la inyección de SQL que vimos en clase.
	private static void insertarUsuarioSQL(Usuario u) throws SQLException {
		String sql = "INSERT INTO usuario (nombre_usuario, contrasena, nombre_completo, admin, dni, fecha_nacimiento, "
				+ "email, telefono, direccion, codigo_postal, ciudad) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
		try (PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setString(1, u.getNombreUsuario());
			ps.setString(2, u.getContrasena());
			ps.setString(3, u.getNombreCompleto());
			ps.setInt(4, u.esAdmin() ? 1 : 0); // true -> 1, false -> 0
			ps.setString(5, u.getDni()); // Si el valor es null, JDBC guarda NULL
			ps.setString(6, u.getFechaNacimiento());
			ps.setString(7, u.getEmail());
			ps.setString(8, u.getTelefono());
			ps.setString(9, u.getDireccion());
			ps.setString(10, u.getCodigoPostal());
			ps.setString(11, u.getCiudad());
			ps.executeUpdate(); // Ejecuta el INSERT
		}
	}

	// Inserta una fila en la tabla cuenta (sin sus movimientos).
	private static void insertarCuentaSQL(Cuenta c) throws SQLException {
		String sql = "INSERT INTO cuenta (iban, titular, tipo, saldo, limite_descubierto) VALUES (?, ?, ?, ?, ?)";
		try (PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setString(1, c.getIban());
			ps.setString(2, c.getTitular()); // Nombre de usuario del dueño (clave foránea hacia usuario)
			ps.setString(3, c.getTipo()); // "Corriente" o "Ahorro"
			ps.setDouble(4, c.getSaldo());
			// El límite de descubierto solo existe en la cuenta corriente. instanceof pregunta de qué clase es el objeto.
			if (c instanceof CuentaCorriente) {
				ps.setDouble(5, ((CuentaCorriente) c).getLimiteDescubierto()); // (CuentaCorriente) = conversión de tipo (cast)
			} else {
				ps.setDouble(5, 0); // Las cuentas de ahorro no tienen descubierto
			}
			ps.executeUpdate();
		}
	}

	// Inserta una fila en la tabla movimiento. El id no se pasa: lo genera la base de datos (AUTOINCREMENT).
	private static void insertarMovimientoSQL(String iban, Movimiento m) throws SQLException {
		String sql = "INSERT INTO movimiento (iban, fecha, tipo, cantidad, concepto, saldo_resultante) VALUES (?, ?, ?, ?, ?, ?)";
		try (PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setString(1, iban);
			ps.setString(2, m.getFecha().toString()); // LocalDate a texto 
			ps.setString(3, m.getTipo().name()); // El enum se guarda con su nombre: "DEPOSITO"
			ps.setDouble(4, m.getCantidad());
			ps.setString(5, m.getConcepto());
			ps.setDouble(6, m.getSaldoResultante());
			ps.executeUpdate();
		}
	}

	// Comprueba que hay conexión; si no, lanza una SQLException.
	private static void exigirConexion() throws SQLException {
		if (conn == null) {
			throw new SQLException("No hay conexión con la base de datos. Llama antes a GestorBD.conectar().");
		}
	}

	// Comprueba que hay conexión; si no, avisa por consola y devuelve false.
	private static boolean hayConexion() {
		if (conn == null) {
			System.out.println("No hay conexión con la base de datos.");
			return false;
		}
		return true;
	}

	// Deshace los cambios de la transacción en curso sin lanzar excepción hacia fuera.
	private static void deshacer() {
		try {
			conn.rollback();
		} catch (SQLException e) {
			System.out.println("Error al deshacer la transacción: " + e.getMessage());
		}
	}

	// Vuelve al modo normal de SQLite sin lanzar excepción hacia fuera.
	private static void restaurarAutoCommit() {
		try {
			conn.setAutoCommit(true);
		} catch (SQLException e) {
			System.out.println("Error al restaurar el modo normal: " + e.getMessage());
		}
	}
}
