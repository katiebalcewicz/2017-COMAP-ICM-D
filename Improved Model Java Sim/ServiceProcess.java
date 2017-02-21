import java.util.*;

public abstract class ServiceProcess implements Process
{
	//IdCheckService, MmScanService, XrayScanService, CollectPropertyService

	LinkedList<Passenger> passenger;
	double removeTime;
	
	public ServiceProcess()
	{
		passenger = new LinkedList<Passenger>();
	}
	
	public void addPassenger(Passenger p)
	{
		passenger.add(p);
	}
	
	public Passenger removePassenger()
	{
		return passenger.remove(0);
	}
	
	public double getRemoveTime() 
	{
		return removeTime;
	}

	public void setRemoveTime(double d) 
	{
		removeTime = d;
	}

	public boolean isAvailable()
	{
		return (passenger.size() == 0);
	}
	
	
}
