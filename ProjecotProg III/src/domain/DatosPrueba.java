package domain;

import java.util.ArrayList;
import java.util.List;

public class DatosPrueba {

	public static List<Usuario> crearUsuarios() {
		List<Usuario> usuarios = new ArrayList<>();
		
		// Uso de IA generativa para generar muchas cuentas 

		// ---------- 1: Ana (caso normal, dos cuentas) ----------
		Usuario ana = new Usuario("ana", "1234", "Ana López");
		Cuenta anaCorriente = new CuentaCorriente("ES01-4521", ana.getNombreUsuario(), 0, 500);
		anaCorriente.depositar(2000, "Nómina");
		anaCorriente.retirar(200, "Cajero");
		Cuenta anaAhorro = new CuentaAhorro("ES01-7733", ana.getNombreUsuario(), 0);
		anaAhorro.depositar(2500, "Ahorro inicial");
		anaAhorro.retirar(50, "Comisión");
		ana.getCuentas().add(anaCorriente);
		ana.getCuentas().add(anaAhorro);
		usuarios.add(ana);

		// ---------- 2: Jon (saldo bajo) ----------
		Usuario jon = new Usuario("jon", "1234", "Jon Etxeberria");
		Cuenta jonCorriente = new CuentaCorriente("ES01-1188", jon.getNombreUsuario(), 0, 300);
		jonCorriente.depositar(900, "Nómina");
		jonCorriente.retirar(850, "Alquiler");
		jonCorriente.retirar(30, "Supermercado");
		jon.getCuentas().add(jonCorriente);
		usuarios.add(jon);

		// ---------- 3: Lucía (corriente en negativo) ----------
		Usuario lucia = new Usuario("lucia", "1234", "Lucía García");
		Cuenta luciaAhorro = new CuentaAhorro("ES01-3090", lucia.getNombreUsuario(), 0);
		luciaAhorro.depositar(5000, "Herencia");
		Cuenta luciaCorriente = new CuentaCorriente("ES01-6654", lucia.getNombreUsuario(), 0, 1000);
		luciaCorriente.depositar(1200, "Nómina");
		luciaCorriente.retirar(1500, "Coche");
		lucia.getCuentas().add(luciaAhorro);
		lucia.getCuentas().add(luciaCorriente);
		usuarios.add(lucia);

		// ---------- 4: Markel ----------
		Usuario markel = new Usuario("markel", "1234", "Markel Torres");
		Cuenta markelCorriente = new CuentaCorriente("ES01-2001", markel.getNombreUsuario(), 0, 500);
		markelCorriente.depositar(1500, "Nómina");
		markelCorriente.retirar(300, "Gimnasio");
		markel.getCuentas().add(markelCorriente);
		usuarios.add(markel);

		// ---------- 5: Iker (corriente en negativo) ----------
		Usuario iker = new Usuario("iker", "1234", "Iker Pérez");
		Cuenta ikerAhorro = new CuentaAhorro("ES01-2002", iker.getNombreUsuario(), 0);
		ikerAhorro.depositar(3000, "Ahorro inicial");
		Cuenta ikerCorriente = new CuentaCorriente("ES01-2003", iker.getNombreUsuario(), 0, 400);
		ikerCorriente.depositar(800, "Nómina");
		ikerCorriente.retirar(1000, "Viaje");
		iker.getCuentas().add(ikerAhorro);
		iker.getCuentas().add(ikerCorriente);
		usuarios.add(iker);

		// ---------- 6: Ivan ----------
		Usuario ivan = new Usuario("ivan", "1234", "Ivan Calleja");
		Cuenta ivanCorriente = new CuentaCorriente("ES01-2004", ivan.getNombreUsuario(), 0, 300);
		ivanCorriente.depositar(1100, "Nómina");
		ivanCorriente.retirar(60, "Gasolina");
		ivan.getCuentas().add(ivanCorriente);
		usuarios.add(ivan);

		// ---------- 7: Maite ----------
		Usuario maite = new Usuario("maite", "1234", "Maite Ruiz");
		Cuenta maiteAhorro = new CuentaAhorro("ES01-2005", maite.getNombreUsuario(), 0);
		maiteAhorro.depositar(7200, "Ahorro inicial");
		maiteAhorro.retirar(200, "Seguro");
		maite.getCuentas().add(maiteAhorro);
		usuarios.add(maite);

		// ---------- 8: Unai (saldo bajo) ----------
		Usuario unai = new Usuario("unai", "1234", "Unai Arrieta");
		Cuenta unaiCorriente = new CuentaCorriente("ES01-2006", unai.getNombreUsuario(), 0, 500);
		unaiCorriente.depositar(650, "Beca");
		unaiCorriente.retirar(620, "Matrícula");
		unai.getCuentas().add(unaiCorriente);
		usuarios.add(unai);

		// ---------- 9: Laura ----------
		Usuario laura = new Usuario("laura", "1234", "Laura Martín");
		Cuenta lauraCorriente = new CuentaCorriente("ES01-2007", laura.getNombreUsuario(), 0, 1000);
		lauraCorriente.depositar(2400, "Nómina");
		lauraCorriente.retirar(900, "Alquiler");
		Cuenta lauraAhorro = new CuentaAhorro("ES01-2008", laura.getNombreUsuario(), 0);
		lauraAhorro.depositar(1500, "Ahorro inicial");
		laura.getCuentas().add(lauraCorriente);
		laura.getCuentas().add(lauraAhorro);
		usuarios.add(laura);

		// ---------- 10: Gorka (corriente en negativo) ----------
		Usuario gorka = new Usuario("gorka", "1234", "Gorka Zubiri");
		Cuenta gorkaCorriente = new CuentaCorriente("ES01-2009", gorka.getNombreUsuario(), 0, 200);
		gorkaCorriente.depositar(400, "Paga");
		gorkaCorriente.retirar(550, "Taller");
		gorka.getCuentas().add(gorkaCorriente);
		usuarios.add(gorka);

		// ---------- 11: Nerea ----------
		Usuario nerea = new Usuario("nerea", "1234", "Nerea Alonso");
		Cuenta nereaAhorro = new CuentaAhorro("ES01-2010", nerea.getNombreUsuario(), 0);
		nereaAhorro.depositar(12000, "Venta piso");
		nereaAhorro.retirar(3000, "Reforma");
		nerea.getCuentas().add(nereaAhorro);
		usuarios.add(nerea);

		// ---------- 12: Pablo ----------
		Usuario pablo = new Usuario("pablo", "1234", "Pablo Sanz");
		Cuenta pabloCorriente = new CuentaCorriente("ES01-2011", pablo.getNombreUsuario(), 0, 500);
		pabloCorriente.depositar(1750, "Nómina");
		pabloCorriente.retirar(45, "Restaurante");
		pablo.getCuentas().add(pabloCorriente);
		usuarios.add(pablo);

		// ---------- 13: Ainhoa (ahorro con saldo bajo) ----------
		Usuario ainhoa = new Usuario("ainhoa", "1234", "Ainhoa Larrea");
		Cuenta ainhoaCorriente = new CuentaCorriente("ES01-2012", ainhoa.getNombreUsuario(), 0, 500);
		ainhoaCorriente.depositar(2100, "Nómina");
		ainhoaCorriente.retirar(700, "Alquiler");
		Cuenta ainhoaAhorro = new CuentaAhorro("ES01-2013", ainhoa.getNombreUsuario(), 0);
		ainhoaAhorro.depositar(600, "Ahorro inicial");
		ainhoaAhorro.retirar(550, "Regalo");
		ainhoa.getCuentas().add(ainhoaCorriente);
		ainhoa.getCuentas().add(ainhoaAhorro);
		usuarios.add(ainhoa);

		// ---------- 14: Mikel ----------
		Usuario mikel = new Usuario("mikel", "1234", "Mikel Ibarra");
		Cuenta mikelCorriente = new CuentaCorriente("ES01-2014", mikel.getNombreUsuario(), 0, 300);
		mikelCorriente.depositar(950, "Nómina");
		mikelCorriente.retirar(80, "Supermercado");
		mikel.getCuentas().add(mikelCorriente);
		usuarios.add(mikel);

		// ---------- 15: Sara ----------
		Usuario sara = new Usuario("sara", "1234", "Sara Gómez");
		Cuenta saraAhorro = new CuentaAhorro("ES01-2015", sara.getNombreUsuario(), 0);
		saraAhorro.depositar(4300, "Ahorro inicial");
		sara.getCuentas().add(saraAhorro);
		usuarios.add(sara);

		// ---------- 16: David (corriente en negativo) ----------
		Usuario david = new Usuario("david", "1234", "David Ortiz");
		Cuenta davidCorriente = new CuentaCorriente("ES01-2016", david.getNombreUsuario(), 0, 800);
		davidCorriente.depositar(1300, "Nómina");
		davidCorriente.retirar(1900, "Ordenador");
		david.getCuentas().add(davidCorriente);
		usuarios.add(david);

		// ---------- 17: June ----------
		Usuario june = new Usuario("june", "1234", "June Agirre");
		Cuenta juneCorriente = new CuentaCorriente("ES01-2017", june.getNombreUsuario(), 0, 500);
		juneCorriente.depositar(700, "Beca");
		juneCorriente.retirar(25, "Libros");
		june.getCuentas().add(juneCorriente);
		usuarios.add(june);

		// ---------- 18: Carlos ----------
		Usuario carlos = new Usuario("carlos", "1234", "Carlos Vega");
		Cuenta carlosCorriente = new CuentaCorriente("ES01-2018", carlos.getNombreUsuario(), 0, 1000);
		carlosCorriente.depositar(3200, "Nómina");
		carlosCorriente.retirar(1200, "Hipoteca");
		Cuenta carlosAhorro = new CuentaAhorro("ES01-2019", carlos.getNombreUsuario(), 0);
		carlosAhorro.depositar(8000, "Ahorro inicial");
		carlos.getCuentas().add(carlosCorriente);
		carlos.getCuentas().add(carlosAhorro);
		usuarios.add(carlos);

		// ---------- 19: Irati (saldo bajo) ----------
		Usuario irati = new Usuario("irati", "1234", "Irati Uriarte");
		Cuenta iratiAhorro = new CuentaAhorro("ES01-2020", irati.getNombreUsuario(), 0);
		iratiAhorro.depositar(250, "Paga");
		iratiAhorro.retirar(180, "Concierto");
		irati.getCuentas().add(iratiAhorro);
		usuarios.add(irati);

		// ---------- 20: Javier ----------
		Usuario javier = new Usuario("javier", "1234", "Javier Moreno");
		Cuenta javierCorriente = new CuentaCorriente("ES01-2021", javier.getNombreUsuario(), 0, 500);
		javierCorriente.depositar(1600, "Nómina");
		javierCorriente.retirar(350, "Luz");
		javier.getCuentas().add(javierCorriente);
		usuarios.add(javier);

		// ---------- 21: Leire ----------
		Usuario leire = new Usuario("leire", "1234", "Leire Bilbao");
		Cuenta leireCorriente = new CuentaCorriente("ES01-2022", leire.getNombreUsuario(), 0, 400);
		leireCorriente.depositar(1400, "Nómina");
		leireCorriente.retirar(90, "Farmacia");
		Cuenta leireAhorro = new CuentaAhorro("ES01-2023", leire.getNombreUsuario(), 0);
		leireAhorro.depositar(2000, "Ahorro inicial");
		leire.getCuentas().add(leireCorriente);
		leire.getCuentas().add(leireAhorro);
		usuarios.add(leire);

		// ---------- 22: Asier (corriente en negativo) ----------
		Usuario asier = new Usuario("asier", "1234", "Asier Goikoetxea");
		Cuenta asierCorriente = new CuentaCorriente("ES01-2024", asier.getNombreUsuario(), 0, 600);
		asierCorriente.depositar(500, "Paga");
		asierCorriente.retirar(900, "Multa");
		asier.getCuentas().add(asierCorriente);
		usuarios.add(asier);

		// ---------- 23: Paula ----------
		Usuario paula = new Usuario("paula", "1234", "Paula Navarro");
		Cuenta paulaAhorro = new CuentaAhorro("ES01-2025", paula.getNombreUsuario(), 0);
		paulaAhorro.depositar(6000, "Ahorro inicial");
		paulaAhorro.retirar(1000, "Viaje");
		paula.getCuentas().add(paulaAhorro);
		usuarios.add(paula);

		// ---------- 24: Aitor ----------
		Usuario aitor = new Usuario("aitor", "1234", "Aitor Lasa");
		Cuenta aitorCorriente = new CuentaCorriente("ES01-2026", aitor.getNombreUsuario(), 0, 500);
		aitorCorriente.depositar(2050, "Nómina");
		aitorCorriente.retirar(600, "Alquiler");
		aitor.getCuentas().add(aitorCorriente);
		usuarios.add(aitor);

		// ---------- 25: Elena ----------
		Usuario elena = new Usuario("elena", "1234", "Elena Castro");
		Cuenta elenaCorriente = new CuentaCorriente("ES01-2027", elena.getNombreUsuario(), 0, 300);
		elenaCorriente.depositar(1150, "Nómina");
		elenaCorriente.retirar(40, "Cine");
		elena.getCuentas().add(elenaCorriente);
		usuarios.add(elena);

		// ---------- 26: Oier (saldo 0: se puede probar "Eliminar cuenta") ----------
		Usuario oier = new Usuario("oier", "1234", "Oier Arana");
		Cuenta oierAhorro = new CuentaAhorro("ES01-2028", oier.getNombreUsuario(), 0);
		oierAhorro.depositar(900, "Paga");
		oierAhorro.retirar(900, "Traspaso");
		oier.getCuentas().add(oierAhorro);
		usuarios.add(oier);

		// ---------- 27: Marta ----------
		Usuario marta = new Usuario("marta", "1234", "Marta Herrero");
		Cuenta martaCorriente = new CuentaCorriente("ES01-2029", marta.getNombreUsuario(), 0, 1000);
		martaCorriente.depositar(2800, "Nómina");
		martaCorriente.retirar(150, "Gimnasio");
		Cuenta martaAhorro = new CuentaAhorro("ES01-2030", marta.getNombreUsuario(), 0);
		martaAhorro.depositar(3500, "Ahorro inicial");
		marta.getCuentas().add(martaCorriente);
		marta.getCuentas().add(martaAhorro);
		usuarios.add(marta);

		// ---------- 28: Xabier ----------
		Usuario xabier = new Usuario("xabier", "1234", "Xabier Olano");
		Cuenta xabierCorriente = new CuentaCorriente("ES01-2031", xabier.getNombreUsuario(), 0, 500);
		xabierCorriente.depositar(1000, "Nómina");
		xabierCorriente.retirar(75, "Gasolina");
		xabier.getCuentas().add(xabierCorriente);
		usuarios.add(xabier);

		// ---------- 29: Nora ----------
		Usuario nora = new Usuario("nora", "1234", "Nora Iturbe");
		Cuenta noraAhorro = new CuentaAhorro("ES01-2032", nora.getNombreUsuario(), 0);
		noraAhorro.depositar(1800, "Ahorro inicial");
		noraAhorro.retirar(300, "Dentista");
		nora.getCuentas().add(noraAhorro);
		usuarios.add(nora);

		// ---------- 30: Hugo (saldo bajo) ----------
		Usuario hugo = new Usuario("hugo", "1234", "Hugo Delgado");
		Cuenta hugoCorriente = new CuentaCorriente("ES01-2033", hugo.getNombreUsuario(), 0, 500);
		hugoCorriente.depositar(600, "Nómina");
		hugoCorriente.retirar(590, "Alquiler");
		hugo.getCuentas().add(hugoCorriente);
		usuarios.add(hugo);

		return usuarios;
	}
}
