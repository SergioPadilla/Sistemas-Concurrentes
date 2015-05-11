import monitor.* ;
import java.util.Random ;

class MonitorLE extends AbstractMonitor{ 
  private int num_lectores = 0 ;
  private Condition lectura   = makeCondition();
  private Condition escritura = makeCondition();
  private int generado = 0;
  private int buffer[] = new int[3];
  private int libre = 0;

	public void escribir(){
		enter();
		if(libre == 3)
			escritura.await();
		
		buffer[libre] = generado;
		libre++;
		generado++;
		lectura.signal();
		leave();
	}
	
	public void leer(){
		enter();
		while(libre == 0){
			lectura.await();
		}
		
		libre--;
		System.out.println(buffer[libre]);
		escritura.signal();
		leave();
	}
} // fin clase monitor "Lect\_Esc"

class aux
{
  static Random genAlea = new Random() ;
  static void dormir_max( int milisecsMax )
  { try
    { Thread.sleep( genAlea.nextInt( milisecsMax ) ) ;
    } 
    catch( InterruptedException e )
    { System.err.println("sleep interumpido en 'aux.dormir_max()'");
    }
  }
}

class Lector implements Runnable 
{
  private MonitorLE monitorLE ; // objeto monitor l.e. compartido
  private int       nveces ; // numero de veces que lee
  public Thread     thr   ; // objeto hebra encapsulado
  
  public Lector( MonitorLE p_monitorLE, int p_nveces, String nombre ) 
  { monitorLE  = p_monitorLE  ;    
    nveces     = p_nveces ;
    thr        = new Thread(this,nombre);
  }
  public void run() 
  { for( int i = 0 ; i < nveces ; i++ ) 
    { //System.out.println( thr.getName()+": solicita lectura.");
      //monitorLE.inicio_lectura();
        //System.out.println( thr.getName()+": leyendo.");
        monitorLE.leer();
      //monitorLE.fin_lectura();
    }
  }
}

class Escritor implements Runnable 
{
  private MonitorLE monitorLE ; // objeto monitor l.e. compartido
  private int       nveces ; // numero de veces que lee
  public Thread     thr   ;  // objeto hebra encapsulado
   
  public Escritor( MonitorLE p_monitorLE, int p_nveces, String nombre ) 
  { monitorLE  = p_monitorLE  ;    
    nveces     = p_nveces ;
    thr        = new Thread(this,nombre);
  }
  public void run() 
  { for( int i = 0 ; i < nveces ; i++ ) 
    { //System.out.println( thr.getName()+": solicita escritura.");
      //monitorLE.inicio_escritura();
       // System.out.println( thr.getName()+": escribiendo.");
       monitorLE.escribir();
     // monitorLE.fin_escritura ();
    }
  }
}

class EjemploLectorEscritor 
{ public static void main(String[] args) 
  { if ( args.length != 4 ) 
    { System.err.println("Uso: num_lectores num_escritores num_iters_lector num_iters_escritor");
        return ;
    }
    // leer parametros, crear vectores, crear monitor 
    Lector[]   vlec = new Lector[ Integer.parseInt(args[0]) ];
    Escritor[] vesc = new Escritor[ Integer.parseInt(args[1]) ];
    int iter_lector = Integer.parseInt(args[2]);
    int iter_escritor = Integer.parseInt(args[3]);
    MonitorLE monitor = new MonitorLE();
    // crear hebras
    for( int i = 0; i < vlec.length; i++) 
      vlec[i] = new Lector(monitor, iter_lector,"lector"+(i+1));
    for( int i = 0; i < vesc.length; i++) 
      vesc[i] = new Escritor(monitor, iter_lector,"escritor"+(i+1));
    // lanzar hebras
    for( int i = 0; i < vlec.length ; i++) vlec[i].thr.start();
    for( int i = 0; i < vesc.length ; i++) vesc[i].thr.start();
  }
}
