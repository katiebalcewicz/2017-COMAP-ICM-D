
public class Passenger 
{

	public int pass_type; //1=express, 2= specNeeds, 3=precheck, 4=regular //new for modelB
	public int passenger_id;
	public boolean tsa_precheck;
	public double id_check_time, mm_scan_time, xray_scan_time, property_collect_time; //length of time in secs
	public double idQueue_arrive_time, mmQueue_arrive_time, xRayQueue_arrive_time, collectPropertyQueue_arrive_time; //num secs since midnight
	public double idQueue_exit_time, mmQueue_exit_time, xRayQueue_exit_time, collectPropertyQueue_exit_time; //num secs since midnight
	public double idQueue_total_time, mmQueue_total_time, xRayQueue_total_time, collectPropertyQueue_total_time; //length of time in secs
	public double exit_time; //num secs since midnight
	public double total_time; //length of time in secs

	
	
    public Passenger(int id)
	{
		passenger_id = id;
		setTsa_precheck();
		setId_check_time();
		setMm_scan_time();
		setXray_scan_time();
		setProperty_collect_time();	
		total_time = 0;
		idQueue_total_time = 0;
		mmQueue_total_time = 0;
		xRayQueue_total_time = 0;
		collectPropertyQueue_total_time = 0;
		
	}
	
	public void finish()
	{
		total_time = exit_time-idQueue_arrive_time;
		idQueue_total_time = idQueue_exit_time - idQueue_arrive_time;
		mmQueue_total_time = mmQueue_exit_time - mmQueue_arrive_time;
		xRayQueue_total_time = xRayQueue_exit_time - xRayQueue_arrive_time;
		collectPropertyQueue_total_time = collectPropertyQueue_exit_time - collectPropertyQueue_arrive_time;
	}
    
    
	public int getPass_type() {
		return pass_type;
	}

	public void setPass_type(int pass_type) {
		this.pass_type = pass_type;
	}

	public double getId_check_time() 
	{
		return id_check_time;
	}
	
	public double getMm_scan_time() 
	{
		return mm_scan_time;
	}
	
	public double getXray_scan_time() 
	{
		return xray_scan_time;
	}

	public double getProperty_collect_time() 
	{
		return property_collect_time;
	}

	public boolean isTsa_precheck() 
	{
		return tsa_precheck;
	}




	public void setId_check_time() 
	{
		id_check_time = DriverB.Distributions.generateIDCheckTime();
	}

	public void setMm_scan_time() 
	{
        mm_scan_time = DriverB.Distributions.generateMmScanTime();
	}


	public void setXray_scan_time() 
	{
		xray_scan_time = DriverB.Distributions.generateXrayScanTime();
	}

	public void setProperty_collect_time() 
	{
		property_collect_time = DriverB.Distributions.generatePropertyCollectTime();
	}

	public void setTsa_precheck() 
	{
		tsa_precheck = DriverB.Distributions.generatePrecheckValue();
	}



	public double getidQueue_arrive_time() 
	{
		return idQueue_arrive_time;
	}


	public void setidQueue_arrive_time(double t) 
	{
		idQueue_arrive_time = t;
	}


	public double getmmQueue_arrive_time() 
	{
		return mmQueue_arrive_time;
	}


	public void setmmQueue_arrive_time(double t) 
	{
		mmQueue_arrive_time = t;
	}


	public double getxRayQueue_arrive_time() 
	{
		return xRayQueue_arrive_time;
	}


	public void setxRayQueue_arrive_time(double t) 
	{
		xRayQueue_arrive_time = t;
	}


	public double getcollectPropertyQueue_arrive_time() 
	{
		return collectPropertyQueue_arrive_time;
	}


	public void setcollectPropertyQueue_arrive_time(double t) 
	{
		collectPropertyQueue_arrive_time = t;
	}

	
	
	
	
	
	
	
	

	public double getidQueue_total_time() 
	{
		return idQueue_total_time;
	}

	public void setidQueue_total_time(double t) 
	{
		idQueue_total_time = t;
	}
	
	public void addidQueue_total_time(double t) 
	{
		idQueue_total_time += t;
	}

	public double getmmQueue_total_time() 
	{
		return mmQueue_total_time;
	}

	public void setmmQueue_total_time(double t) 
	{
		mmQueue_total_time = t;
	}
	
	public void addmmQueue_total_time(double t) 
	{
		mmQueue_total_time += t;
	}

	public double getxRayQueue_total_time() 
	{
		return xRayQueue_total_time;
	}

	public void setxRayQueue_total_time(double t) 
	{
		xRayQueue_total_time = t;
	}

	public void addxRayQueue_total_time(double t) 
	{
		xRayQueue_total_time += t;
	}

	public double getcollectPropertyQueue_total_time() 
	{
		return collectPropertyQueue_total_time;
	}

	public void setcollectPropertyQueue_total_time(double t) 
	{
		collectPropertyQueue_total_time = t;
	}

	public void addcollectPropertyQueue_total_time(double t) 
	{
		collectPropertyQueue_total_time += t;
	}
	
	
	


	public double getIdQueue_exit_time() {
		return idQueue_exit_time;
	}


	public void setIdQueue_exit_time(double idQueue_exit_time) {
		this.idQueue_exit_time = idQueue_exit_time;
	}


	public double getMmQueue_exit_time() {
		return mmQueue_exit_time;
	}


	public void setMmQueue_exit_time(double mmQueue_exit_time) {
		this.mmQueue_exit_time = mmQueue_exit_time;
	}


	public double getxRayQueue_exit_time() {
		return xRayQueue_exit_time;
	}


	public void setxRayQueue_exit_time(double xRayQueue_exit_time) {
		this.xRayQueue_exit_time = xRayQueue_exit_time;
	}


	public double getCollectPropertyQueue_exit_time() {
		return collectPropertyQueue_exit_time;
	}


	public void setCollectPropertyQueue_exit_time(double collectPropertyQueue_exit_time) {
		this.collectPropertyQueue_exit_time = collectPropertyQueue_exit_time;
	}


	
	
	public double getExit_time() 
	{
		return exit_time;
	}


	public void setExit_time(double d) 
	{
		exit_time = d;
	}


	public double getTotal_time() {
		return total_time;
	}


	public void setTotal_time(double total_time) {
		this.total_time = total_time;
	}


	public String toString()
	{
		StringBuilder output = new StringBuilder();	
		output.append("Passenger ID: " + passenger_id + "\n");
		output.append("Precheck?: " + tsa_precheck + "\n");
		output.append("Arrival Time: "+ idQueue_arrive_time + "\n");
		output.append("ID exit Time: "+ idQueue_exit_time + "\n");
		output.append("ID Check time length: " + id_check_time + "\n");
		output.append("MM Arrival Time: "+ mmQueue_arrive_time + "\n");
		output.append("MM exit Time: "+ mmQueue_exit_time + "\n");
		output.append("MM Scan time length: " + mm_scan_time + "\n");
		output.append("Xray Arrival Time: "+ xRayQueue_arrive_time + "\n");
		output.append("Xray exit Time: "+ xRayQueue_exit_time + "\n");
		output.append("X-Ray scan time: " + xray_scan_time + "\n");
		output.append("Collect Property Arrival Time: "+ collectPropertyQueue_arrive_time + "\n");
		output.append("Collect Property exit Time: "+ collectPropertyQueue_exit_time + "\n");
		output.append("Property collect time length: " + property_collect_time + "\n");
		output.append("Exit Time: " + exit_time + "\n\n");
	


		return output.toString();
	}
	
	
	public static void main(String[] args) 
	{
		// TODO Auto-generated method stub

		
		
	}
	
	
}
