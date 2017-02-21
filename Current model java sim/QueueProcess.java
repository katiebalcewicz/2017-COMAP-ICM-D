import java.util.*;

public abstract class QueueProcess implements Process
{
	//IdQueue, MmQueue, XrayQueue, CollectPropertyQueue
	
	ArrayList<Passenger> passengers;
	
	public QueueProcess()
	{
		passengers = new ArrayList<Passenger>();
	}
	
	public void addPassenger(Passenger p)
	{
		passengers.add(p);
	}
	
	public Passenger removePassenger()
	{
		return passengers.remove(0);
	}
	
	public int size()
	{
		return passengers.size();
	}

}
