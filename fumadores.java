import monitor.* ;
import java.util.Random ;

class Estanco extends AbstractMonitor{ 
   private int ingrediente = 0;
	private Condition fuma1 = makeCondition();
	private Condition fuma2 = makeCondition();
	private Condition fuma3 = makeCondition();
	private Condition estanquero = makeCondition();

	public void obtenerIngrediente(int miIngrediente){
		enter();
		if(miIngrediente == 1 && ingrediente != miIngrediente)
			fuma1.await();
		else if(miIngrediente == 2 && ingrediente != miIngrediente)
			fuma2.await();
		else if(miIngrediente == 3 && ingrediente != miIngrediente)
			fuma2.await();

		System.out.println("Fumador "+ingrediente+" ha obtenido su ingrediente");
		ingrediente = 0;
		estanquero.signal();
		leave();
	}

	public void ponerIngrediente(int p_ingrediente){
		enter();
		ingrediente = p_ingrediente;

		if(ingrediente == 1){
			System.out.println("Suministro ingrediente 1");
			fuma1.signal();
		}
		if(ingrediente == 2){
			System.out.println("Suministro ingrediente 2");
			fuma2.signal();
		}
		if(ingrediente == 3){
			System.out.println("Suministro ingrediente 3");
			fuma3.signal();
		}
		leave();
	}

	public void esperarRecogidaIngrediente(){
		enter();
		if(ingrediente != 0){
			System.out.println("Estanquero esperando recogida del ingrediente");
			estanquero.await();
		}
		leave();
	}
}

class Fumador implements Runnable{
	int miIngrediente;
	public Thread thr;
	Estanco estanco;

	public Fumador(int p_miIngrediente,Estanco p_estanco, String nombre){
		miIngrediente = p_miIngrediente;
		estanco = p_estanco;
		thr = new Thread(this,nombre);
	}

	public void run(){
		while(true){
			estanco.obtenerIngrediente(miIngrediente);
			aux.dormir_max(2000);
		}
	}
}

class Estanquero implements Runnable{
	public Thread thr;
	Estanco estanco;

	public Estanquero (Estanco p_estanco, String nombre){
		estanco = p_estanco;
		thr = new Thread(this,nombre);
	}

	public void run(){
		int ingrediente;
		while(true){
			ingrediente = (int)(Math.random()*3.0);
			estanco.ponerIngrediente(ingrediente);
			estanco.esperarRecogidaIngrediente();
		}
	}
}

class EjemploFumadores { 
	public static void main(String[] args){ 
    // crear fumadores y estanco y estanquero
   Estanco estanco = new Estanco();
	Fumador fumador1 = new Fumador(1, estanco,"fumador 1");
	Fumador fumador2 = new Fumador(2, estanco,"fumador 2");
	Fumador fumador3 = new Fumador(3, estanco,"fumador 3");
	Estanquero estanquero = new Estanquero(estanco,"estanquero");

   fumador1.thr.start();
	fumador2.thr.start();
	fumador3.thr.start();
	estanquero.thr.start();
  }
}
