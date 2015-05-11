import monitor.* ;
import java.util.Random ;

class Barberia extends AbstractMonitor{
	private boolean cortando = false;
	private Condition salaEspera = makeCondition();
	private Condition barbero = makeCondition();
	private Condition corte = makeCondition();
	
	//invocado por los clientes para cortarse el pelo
	public void cortarPelo(){
		enter();

		if(!corte.isEmpty())
			salaEspera.await();
		else if(!barbero.isEmpty())
			barbero.signal();
		
		System.out.println("voy a cortarme el pelo");
		corte.await();
		System.out.println("Ya me han cortado el pelo, Adios SEKE!");

		leave();
	}

	//invocado por el barbeto para espear a un cliente y sentarlo para el corte
	public void siguienteCliente(){
		enter();

		if(salaEspera.isEmpty()){
			System.out.println("No hay clientes esperando, voy a dormir");
			barbero.await();
		}
		
		System.out.println("Llamo al siguiente cliente");
		salaEspera.signal();
		
		leave();
	}

	//invocado por el barbero cuando acaba de cortar el pelo
	public void finCliente(){
		enter();

		System.out.println("He terminado de cortarle el pelo");
		corte.signal();
		
		leave();
	}
}

class Cliente implements Runnable{
	public Thread thr;
	Barberia barberia = new Barberia();

	public Cliente(Barberia p_barberia, String nombre){
		barberia = p_barberia;
		thr = new Thread(this,nombre);
	}

	public void run(){
		while(true){
			barberia.cortarPelo();
			aux.dormir_max(2000);
		}
	}
}

class Barbero implements Runnable{
	public Thread thr;
	Barberia barberia = new Barberia();

	public Barbero(Barberia p_barberia, String nombre){
		barberia = p_barberia;
		thr = new Thread(this,nombre);
	}

	public void run(){
		while(true){
			barberia.siguienteCliente();
			aux.dormir_max(2500);
			barberia.finCliente();
		}
	}
}

class EjemploBarbero{
	public static void main(String[] args){
		if ( args.length != 1 ){
			System.err.println("Uso: numero de clientes");
			return ;
    	}
    	// leer parametros, crear vectores, crear monitor 
    	Cliente[]   clientes = new Cliente[ Integer.parseInt(args[0]) ];

    	Barberia monitor = new Barberia();
    	// crear hebras
    	for( int i = 0; i < clientes.length; i++) 
     		clientes[i] = new Cliente(monitor, "cliente"+(i+1));
		Barbero barbero = new Barbero(monitor,"barbero");

    	// lanzar hebras
		barbero.thr.start();
    	for( int i = 0; i < clientes.length ; i++) clientes[i].thr.start();
	}
}
