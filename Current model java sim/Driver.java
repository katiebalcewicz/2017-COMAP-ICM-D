import org.apache.commons.math3.distribution.*;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import java.util.*;
import java.io.*;

public class Driver {

	public static class Distributions
	{
		public static AbstractRealDistribution tsa_precheck_intervals_dist,  reg_pax_intervals_dist,  id_check_dist, 
		mm_scan_dist, xray_scan_dist, property_collect_dist;
		
		public static final double TSA_PRECHECK_INTERVALS_MEDIAN = 3.205467;
		public static final double TSA_PRECHECK_INTERVALS_SCALE = 3.521331;
		public static final double REG_PAX_INTERVALS_MEDIAN = 7.237164;
		public static final double REG_PAX_INTERVALS_SCALE = 5.919411;
		public static final double ID_CHECK_DIST_MEAN = 11.2125;
		public static final double MM_SCAN_DIST_MEAN = 11.6359;
		public static final double XRAY_SCAN_DIST_DF = 0.5728939;
		public static final double PROPERTY_COLLECT_DIST_MEAN = 28.62069;
		public static final double TSA_PRECHECK_PROPORTION = .45;
		
		
		public Distributions()
		{ 
		tsa_precheck_intervals_dist = new CauchyDistribution(TSA_PRECHECK_INTERVALS_MEDIAN, TSA_PRECHECK_INTERVALS_SCALE);
    	reg_pax_intervals_dist = new CauchyDistribution(REG_PAX_INTERVALS_MEDIAN,REG_PAX_INTERVALS_SCALE);
    	id_check_dist = new ExponentialDistribution(ID_CHECK_DIST_MEAN);
        mm_scan_dist = new ExponentialDistribution(MM_SCAN_DIST_MEAN);
    	xray_scan_dist = new TDistribution(XRAY_SCAN_DIST_DF);
        property_collect_dist = new ExponentialDistribution(PROPERTY_COLLECT_DIST_MEAN);
        }
		
		public static double generateTSAInterval()
		{
			double x;
			do 
			{ 
				x = tsa_precheck_intervals_dist.sample();
			} while (x<=0);
			return x;
		}
		
		public static double generateRegPaxInterval()
		{
			double x;
			do 
			{ 
				x = reg_pax_intervals_dist.sample();
			} while (x<=0);
			return x;
		}
		
		public static double generateIDCheckTime()
		{
			return id_check_dist.sample();
		}
		
		public static double generateMmScanTime()
		{
			return mm_scan_dist.sample();
		}
		
		public static double generateXrayScanTime()
		{
			double x;
			do 
			{ 
				x = xray_scan_dist.sample();
			} while (x<=0);
			return x;
		}
		
		public static double generatePropertyCollectTime()
		{
			return property_collect_dist.sample();
		}
		
		public static boolean generatePrecheckValue()
		{
			double i = Math.random();
			return i <= TSA_PRECHECK_PROPORTION;
		}
		
	}
	
	
	public int passenger_count;
	public static final int NUM_PASSENGERS = 500, N_TRIALS = 10000; //TODO
	public static final double delta = 1.0;
	public Distributions dist;
	public ArrayList<Passenger> passengers, passengers_in_checkpoint, passengers_thru_checkpoint;
	public LinkedList<Process> precheck_path, regular_path;
	
	public Driver()
	{ 
		dist = new Distributions();
		passengers = new ArrayList<Passenger>(NUM_PASSENGERS);
		passengers_in_checkpoint = new ArrayList<Passenger>(NUM_PASSENGERS);
		passengers_thru_checkpoint = new ArrayList<Passenger>(NUM_PASSENGERS);
		generatePassList();
		definePathsModelA();
	}
	

	public void generatePassList()
	{
		passenger_count = 0;
		double sum_secs_precheck = 0;
		double sum_secs_regular = 0;
		
		while(passenger_count < NUM_PASSENGERS)
		{
			passengers.add(new Passenger(passenger_count));
			if(passengers.get(passenger_count).isTsa_precheck())
			{
				sum_secs_precheck += Distributions.generateTSAInterval();
				passengers.get(passenger_count).setidQueue_arrive_time( sum_secs_precheck );
			}
			else
			{
				sum_secs_regular += Distributions.generateRegPaxInterval();
				passengers.get(passenger_count).setidQueue_arrive_time( sum_secs_regular );
			}
			
			passenger_count++;
		}
	}
	
	
	
	//This is the main part where the flow of passengers through the security checkpoint is modeled
	public void definePathsModelA()
	{
		//precheck path is a branching queue with a single queue for id check, then 3 queues for the rest of the services
	    precheck_path = new LinkedList<Process>();
		precheck_path.add(new IdQueue()); //0
		precheck_path.add(new IdService()); //1
		
		//path1
		precheck_path.add(new MmQueue()); //2
		precheck_path.add(new MmService()); //3
		precheck_path.add(new XrayQueue()); //4
		precheck_path.add(new XrayService()); //5
		precheck_path.add(new CollectPropertyQueue()); //6
		precheck_path.add(new CollectPropertyService()); //7
		
		//path2
		precheck_path.add(new MmQueue()); //8
		precheck_path.add(new MmService()); //9
		precheck_path.add(new XrayQueue()); //10
		precheck_path.add(new XrayService()); //11
		precheck_path.add(new CollectPropertyQueue()); //12
		precheck_path.add(new CollectPropertyService()); //13
		
		//path2
		precheck_path.add(new MmQueue()); //14
		precheck_path.add(new MmService()); //15
		precheck_path.add(new XrayQueue()); //16
		precheck_path.add(new XrayService()); //17
		precheck_path.add(new CollectPropertyQueue()); //18
		precheck_path.add(new CollectPropertyService()); //19
		
		//regular path is a branching queue
		//single queue for id check, then 5 queues for the rest of the services
		regular_path = new LinkedList<Process>();
		regular_path.add(new IdQueue()); //0
		regular_path.add(new IdService()); //1
		
		//path1
		regular_path.add(new MmQueue()); //2
		regular_path.add(new MmService()); //3
		regular_path.add(new XrayQueue()); //4
		regular_path.add(new XrayService()); //5
		regular_path.add(new CollectPropertyQueue()); //6
		regular_path.add(new CollectPropertyService()); //7
		
		//path2
		regular_path.add(new MmQueue()); //8
		regular_path.add(new MmService()); //9
		regular_path.add(new XrayQueue()); //10
		regular_path.add(new XrayService()); //11
		regular_path.add(new CollectPropertyQueue()); //12
		regular_path.add(new CollectPropertyService()); //13
		
		//path3
		regular_path.add(new MmQueue()); //14
		regular_path.add(new MmService()); //15
		regular_path.add(new XrayQueue()); //16
		regular_path.add(new XrayService()); //17
		regular_path.add(new CollectPropertyQueue()); //18
		regular_path.add(new CollectPropertyService()); //19
		
		//path4
		regular_path.add(new MmQueue()); //20
		regular_path.add(new MmService()); //21
		regular_path.add(new XrayQueue()); //22
		regular_path.add(new XrayService()); //23
		regular_path.add(new CollectPropertyQueue()); //24
		regular_path.add(new CollectPropertyService()); //25
		
		//path4
		regular_path.add(new MmQueue()); //26
		regular_path.add(new MmService()); //27
		regular_path.add(new XrayQueue()); //28
		regular_path.add(new XrayService()); //29
		regular_path.add(new CollectPropertyQueue()); //30
		regular_path.add(new CollectPropertyService()); //31
		
		
		
	}
	
		
	public void enqueueId(int i, QueueProcess que)
	{
		Passenger p = passengers.remove(i);
		que.addPassenger( p );
		passengers_in_checkpoint.add(p);
	}
		
	public void dequeueIdEnqueueMm(double s, QueueProcess que, ServiceProcess serv, QueueProcess nextQueue)
	{
		
		if(serv.isAvailable() && que.size() > 0)
		{
			Passenger p = que.removePassenger();
			p.setIdQueue_exit_time(s);
			serv.addPassenger(p);
			serv.setRemoveTime(s + p.getId_check_time() );
		}
		if(!serv.isAvailable() && s >= serv.getRemoveTime())
		{
			Passenger p = serv.removePassenger();
			nextQueue.addPassenger(p);
			p.setmmQueue_arrive_time(s);		
		}
	}
		
	public void dequeueMmEnqueueXray(double s, QueueProcess que, ServiceProcess serv, QueueProcess nextQueue)
	{
		if(serv.isAvailable() && que.size() > 0)
		{
			Passenger p = que.removePassenger();
			p.setMmQueue_exit_time(s);
			serv.addPassenger(p);
			serv.setRemoveTime(s + p.getMm_scan_time() );
		}
		if(!serv.isAvailable() && s >= serv.getRemoveTime())
		{
			Passenger p = serv.removePassenger();
			nextQueue.addPassenger(p);
			p.setxRayQueue_arrive_time(s);
		}
	}
		
	public void dequeueXrayEnqueueCollectProp(double s, QueueProcess que, ServiceProcess serv, QueueProcess nextQueue)
	{
		if(serv.isAvailable() && que.size() > 0)
		{
			Passenger p = que.removePassenger();
			p.setxRayQueue_exit_time(s);
			serv.addPassenger(p);
			serv.setRemoveTime(s + p.getXray_scan_time() );
		}
		if(!serv.isAvailable() && s >= serv.getRemoveTime())
		{
			Passenger p = serv.removePassenger();
			nextQueue.addPassenger(p);
			//System.out.println(d.passengers_in_checkpoint);

			p.setcollectPropertyQueue_arrive_time(s);
		}
	}
	
	public void dequeueCollectPropExitCheckpoint(double s, QueueProcess que, ServiceProcess serv)
	{
		if(serv.isAvailable() && que.size() > 0)
		{
			Passenger p = que.removePassenger();
			p.setCollectPropertyQueue_exit_time(s);
			serv.addPassenger(p);
			serv.setRemoveTime(s + p.getProperty_collect_time() );
		}
		if(!serv.isAvailable() && s >= serv.getRemoveTime())
		{
			Passenger p = serv.removePassenger();
			p.setExit_time(s);
			p.finish(); //calculate times in each queue
			passengers_in_checkpoint.remove(p);
			passengers_thru_checkpoint.add(p);		
			
		}
	}
		
	
	public QueueProcess shortestQueue(LinkedList<QueueProcess> list)
	{
		QueueProcess q = list.get(0);
		int minval = q.size();
		int i=1;
		while(i<list.size())
		{
			if( list.get(i).size() < minval)
			{
				q= list.get(i);
				minval = list.get(i).size();
			}
			i++;
		}

		return q;
	}
	
	
	public static boolean writeCSV(String[] labels, double[][] results, String fileName)
	{
		try
		{
			FileWriter writer = new FileWriter(fileName);
			
			for(int i=0; i<labels.length; i++)
			{
				writer.append(labels[i]);
				if(i<labels.length-1)
					writer.append(",");			
			}
			writer.append("\n");
			
			for(int i=0; i<N_TRIALS; i++)
			{
				for(int j=0; j<labels.length; j++)
				{
					writer.append(String.format("%.2f", results[j][i]));
					if(j<labels.length-1)
						writer.append(",");
				}
				writer.append("\n");
			}
			
			writer.flush();
			writer.close();
			return true;
		}
		catch(IOException e)
		{
			return false;
		}
		
		
	}

	
	
	
	int num_pass, num_thru, num_rem;

	public static void main(String[] args) 
	{
		Driver d;
		String[] labels = {"total_time_avg_all", "total_time_sd_all",
				"id_queue_total_time_avg_all", "id_queue_total_time_sd_all",
				"mm_queue_total_time_avg_all", "mm_queue_total_time_sd_all",
				"xray_queue_total_time_avg_all", "xray_queue_total_time_sd_all",
				"collect_prop_queue_total_time_avg_all", "collect_prop_queue_total_time_sd_all",
				"total_time_avg_pre", "total_time_sd_pre",
				"id_queue_total_time_avg_pre", "id_queue_total_time_sd_pre",
				"mm_queue_total_time_avg_pre", "mm_queue_total_time_sd_pre",
				"xray_queue_total_time_avg_pre", "xray_queue_total_time_sd_pre",
				"collect_prop_queue_total_time_avg_pre", "collect_prop_queue_total_time_sd_pre",
				"total_time_avg_reg", "total_time_sd_reg",
				"id_queue_total_time_avg_reg", "id_queue_total_time_sd_reg",
				"mm_queue_total_time_avg_reg", "mm_queue_total_time_sd_reg",
				"xray_queue_total_time_avg_reg", "xray_queue_total_time_sd_reg",
				"collect_prop_queue_total_time_avg_reg", "collect_prop_queue_total_time_sd_reg"};
		double[][] results = new double[30][10000];
		
		
		
		
		
		for(int n=0; n<N_TRIALS; n++)
		{
		System.out.println("Running trial " + n + " of " + N_TRIALS);	
			
		d = new Driver();		
		
		LinkedList<QueueProcess> mmQueuesPre = new LinkedList<QueueProcess>();
		mmQueuesPre.add((QueueProcess)d.precheck_path.get(2));
		mmQueuesPre.add((QueueProcess)d.precheck_path.get(8));
		mmQueuesPre.add((QueueProcess)d.precheck_path.get(14));

		
		LinkedList<QueueProcess> mmQueuesReg = new LinkedList<QueueProcess>();
		mmQueuesReg.add((QueueProcess)d.regular_path.get(2));
		mmQueuesReg.add((QueueProcess)d.regular_path.get(8));
		mmQueuesReg.add((QueueProcess)d.regular_path.get(14));
		mmQueuesReg.add((QueueProcess)d.regular_path.get(20));
		mmQueuesReg.add((QueueProcess)d.regular_path.get(26));


		
		d.num_pass = d.passengers.size();

		double s=0;
		while(d.passengers_thru_checkpoint.size()<NUM_PASSENGERS)
		{
			//Enqueue ID Queue, arrivals
			boolean bprecheck = true, bregular = true;
			int i=0;
			while(bprecheck && bregular && i<d.passengers.size())
			{
				if(d.passengers.get(i).getidQueue_arrive_time() < s)
				{
					if(d.passengers.get(i).isTsa_precheck())
						d.enqueueId(i, (QueueProcess)d.precheck_path.get(0) );
					else
						d.enqueueId(i, (QueueProcess)d.regular_path.get(0) );	
				}
				else
				{
					if(d.passengers.get(i).isTsa_precheck())
						bprecheck = false;
					else
						bregular = false;
				}
				i++;
			}
			
			//Dequeue ID Queue, Enque MM Queue Precheck
			d.dequeueIdEnqueueMm(s, (QueueProcess)d.precheck_path.get(0), (ServiceProcess)d.precheck_path.get(1), d.shortestQueue(mmQueuesPre));
			
			//Dequeue MM Queue Precheck
			d.dequeueMmEnqueueXray(s, (QueueProcess)d.precheck_path.get(2), (ServiceProcess)d.precheck_path.get(3), (QueueProcess)d.precheck_path.get(4));
			d.dequeueMmEnqueueXray(s, (QueueProcess)d.precheck_path.get(8), (ServiceProcess)d.precheck_path.get(9), (QueueProcess)d.precheck_path.get(10));
			d.dequeueMmEnqueueXray(s, (QueueProcess)d.precheck_path.get(14), (ServiceProcess)d.precheck_path.get(15), (QueueProcess)d.precheck_path.get(16));

			
			//Dequeue Xray Queue Precheck
			d.dequeueXrayEnqueueCollectProp(s, (QueueProcess)d.precheck_path.get(4), (ServiceProcess)d.precheck_path.get(5), (QueueProcess)d.precheck_path.get(6));
			d.dequeueXrayEnqueueCollectProp(s, (QueueProcess)d.precheck_path.get(10), (ServiceProcess)d.precheck_path.get(11), (QueueProcess)d.precheck_path.get(12));
			d.dequeueXrayEnqueueCollectProp(s, (QueueProcess)d.precheck_path.get(16), (ServiceProcess)d.precheck_path.get(17), (QueueProcess)d.precheck_path.get(18));
			
			//Dequeue CollectProperty Queue Precheck
			d.dequeueCollectPropExitCheckpoint(s, (QueueProcess)d.precheck_path.get(6), (ServiceProcess)d.precheck_path.get(7));
			d.dequeueCollectPropExitCheckpoint(s, (QueueProcess)d.precheck_path.get(12), (ServiceProcess)d.precheck_path.get(13));
			d.dequeueCollectPropExitCheckpoint(s, (QueueProcess)d.precheck_path.get(18), (ServiceProcess)d.precheck_path.get(19));

			
			
			//Dequeue ID Queue Regular
			d.dequeueIdEnqueueMm(s, (QueueProcess)d.regular_path.get(0), (ServiceProcess)d.regular_path.get(1), d.shortestQueue(mmQueuesReg));
			
			//Dequeue MM Queues Regular
			d.dequeueMmEnqueueXray(s, (QueueProcess)d.regular_path.get(2), (ServiceProcess)d.regular_path.get(3), (QueueProcess)d.regular_path.get(4));
			d.dequeueMmEnqueueXray(s, (QueueProcess)d.regular_path.get(8), (ServiceProcess)d.regular_path.get(9), (QueueProcess)d.regular_path.get(10));
			d.dequeueMmEnqueueXray(s, (QueueProcess)d.regular_path.get(14), (ServiceProcess)d.regular_path.get(15), (QueueProcess)d.regular_path.get(16));
			d.dequeueMmEnqueueXray(s, (QueueProcess)d.regular_path.get(20), (ServiceProcess)d.regular_path.get(21), (QueueProcess)d.regular_path.get(22));
			d.dequeueMmEnqueueXray(s, (QueueProcess)d.regular_path.get(26), (ServiceProcess)d.regular_path.get(27), (QueueProcess)d.regular_path.get(28));
			
			//Dequeue Xray Queue Regular
			d.dequeueXrayEnqueueCollectProp(s, (QueueProcess)d.regular_path.get(4), (ServiceProcess)d.regular_path.get(5), (QueueProcess)d.regular_path.get(6));
			d.dequeueXrayEnqueueCollectProp(s, (QueueProcess)d.regular_path.get(10), (ServiceProcess)d.regular_path.get(11), (QueueProcess)d.regular_path.get(12));
			d.dequeueXrayEnqueueCollectProp(s, (QueueProcess)d.regular_path.get(16), (ServiceProcess)d.regular_path.get(17), (QueueProcess)d.regular_path.get(18));
			d.dequeueXrayEnqueueCollectProp(s, (QueueProcess)d.regular_path.get(22), (ServiceProcess)d.regular_path.get(23), (QueueProcess)d.regular_path.get(24));
			d.dequeueXrayEnqueueCollectProp(s, (QueueProcess)d.regular_path.get(28), (ServiceProcess)d.regular_path.get(29), (QueueProcess)d.regular_path.get(30));

			
			//Dequeue CollectProperty Queue Regular
			d.dequeueCollectPropExitCheckpoint(s, (QueueProcess)d.regular_path.get(6), (ServiceProcess)d.regular_path.get(7));
			d.dequeueCollectPropExitCheckpoint(s, (QueueProcess)d.regular_path.get(12), (ServiceProcess)d.regular_path.get(13));
			d.dequeueCollectPropExitCheckpoint(s, (QueueProcess)d.regular_path.get(18), (ServiceProcess)d.regular_path.get(19));
			d.dequeueCollectPropExitCheckpoint(s, (QueueProcess)d.regular_path.get(24), (ServiceProcess)d.regular_path.get(25));
			d.dequeueCollectPropExitCheckpoint(s, (QueueProcess)d.regular_path.get(30), (ServiceProcess)d.regular_path.get(31));


			
			s+=delta; //delta = 1 second for processing speed //1 second also represents travel time between stations
			
			if(s>172800) //hack to fix problem where a single simulation trial would take 5 minutes, also if this happened IRL someone would intervene
			{
				s=0;
				d = new Driver();
				mmQueuesPre = new LinkedList<QueueProcess>();
				mmQueuesPre.add((QueueProcess)d.precheck_path.get(2));
				mmQueuesPre.add((QueueProcess)d.precheck_path.get(8));
				
				mmQueuesReg = new LinkedList<QueueProcess>();
				mmQueuesReg.add((QueueProcess)d.regular_path.get(2));
				mmQueuesReg.add((QueueProcess)d.regular_path.get(8));
				mmQueuesReg.add((QueueProcess)d.regular_path.get(14));
				mmQueuesReg.add((QueueProcess)d.regular_path.get(20));

				
				d.num_pass = d.passengers.size();
			}
		}	
			
		
		d.num_thru = d.passengers_thru_checkpoint.size();
		d.num_rem = d.passengers_in_checkpoint.size();
	
		
		double[] total_times = new double[d.num_thru];
		double[] id_queue_total_times = new double[d.num_thru];
		double[] mm_queue_total_times = new double[d.num_thru];
		double[] xray_queue_total_times = new double[d.num_thru];
		double[] collect_prop_queue_total_times = new double[d.num_thru];

		
		for(int i=0; i<d.num_thru; i++)
		{
			total_times[i] = d.passengers_thru_checkpoint.get(i).getTotal_time();
			id_queue_total_times[i] = d.passengers_thru_checkpoint.get(i).getidQueue_total_time();
			mm_queue_total_times[i] = d.passengers_thru_checkpoint.get(i).getmmQueue_total_time();
			xray_queue_total_times[i] = d.passengers_thru_checkpoint.get(i).getxRayQueue_total_time();
			collect_prop_queue_total_times[i] = d.passengers_thru_checkpoint.get(i).getcollectPropertyQueue_total_time();
		}
		

		DescriptiveStatistics totalStats = new DescriptiveStatistics(total_times);
		results[0][n] = totalStats.getMean();
		results[1][n] = totalStats.getStandardDeviation();
		
		DescriptiveStatistics idStats = new DescriptiveStatistics(id_queue_total_times);
		results[2][n] = idStats.getMean();
		results[3][n] = idStats.getStandardDeviation();
		
		DescriptiveStatistics mmStats = new DescriptiveStatistics(mm_queue_total_times);
		results[4][n] = mmStats.getMean();
		results[5][n] = mmStats.getStandardDeviation();
		
		DescriptiveStatistics xrayStats = new DescriptiveStatistics(xray_queue_total_times);
		results[6][n] = xrayStats.getMean();
		results[7][n] = xrayStats.getStandardDeviation();
		
		DescriptiveStatistics collectPropStats = new DescriptiveStatistics(collect_prop_queue_total_times);
		results[8][n] = collectPropStats.getMean();
		results[9][n] = collectPropStats.getStandardDeviation();
		
		
		ArrayList<Passenger> precheck = new ArrayList<Passenger>();
		ArrayList<Passenger> regular = new ArrayList<Passenger>();
		for(int i=0; i<d.num_thru; i++)
		{
			if(d.passengers_thru_checkpoint.get(i).isTsa_precheck())
				precheck.add(d.passengers_thru_checkpoint.get(i));
			else
				regular.add(d.passengers_thru_checkpoint.get(i));
		}
		
		double[] total_times_pre = new double[precheck.size()];
		double[] id_queue_total_times_pre = new double[precheck.size()];
		double[] mm_queue_total_times_pre = new double[precheck.size()];
		double[] xray_queue_total_times_pre = new double[precheck.size()];
		double[] collect_prop_queue_total_times_pre = new double[precheck.size()];
		
		for(int i=0; i<precheck.size(); i++)
		{
			total_times_pre[i] = precheck.get(i).getTotal_time();
			id_queue_total_times_pre[i] = precheck.get(i).getidQueue_total_time();
			mm_queue_total_times_pre[i] = precheck.get(i).getmmQueue_total_time();
			xray_queue_total_times_pre[i] = precheck.get(i).getxRayQueue_total_time();
			collect_prop_queue_total_times_pre[i] = precheck.get(i).getcollectPropertyQueue_total_time();
		}
		
		DescriptiveStatistics totalStatsPre = new DescriptiveStatistics(total_times_pre);
		results[10][n] = totalStatsPre.getMean();
		results[11][n] = totalStatsPre.getStandardDeviation();
		
		DescriptiveStatistics idStatsPre = new DescriptiveStatistics(id_queue_total_times_pre);
		results[12][n] = idStatsPre.getMean();
		results[13][n] = idStatsPre.getStandardDeviation();
		
		DescriptiveStatistics mmStatsPre = new DescriptiveStatistics(mm_queue_total_times_pre);
		results[14][n] = mmStatsPre.getMean();
		results[15][n] = mmStatsPre.getStandardDeviation();
		
		DescriptiveStatistics xrayStatsPre = new DescriptiveStatistics(xray_queue_total_times_pre);
		results[16][n] = xrayStatsPre.getMean();
		results[17][n] = xrayStatsPre.getStandardDeviation();
		
		DescriptiveStatistics collectPropStatsPre = new DescriptiveStatistics(collect_prop_queue_total_times_pre);
		results[18][n] = collectPropStatsPre.getMean();
		results[19][n] = collectPropStatsPre.getStandardDeviation();
		
		
		

		
		double[] total_times_reg = new double[regular.size()];
		double[] id_queue_total_times_reg = new double[regular.size()];
		double[] mm_queue_total_times_reg = new double[regular.size()];
		double[] xray_queue_total_times_reg = new double[regular.size()];
		double[] collect_prop_queue_total_times_reg = new double[regular.size()];
		
		for(int i=0; i<regular.size(); i++)
		{
			total_times_reg[i] = regular.get(i).getTotal_time();
			id_queue_total_times_reg[i] = regular.get(i).getidQueue_total_time();
			mm_queue_total_times_reg[i] = regular.get(i).getmmQueue_total_time();
			xray_queue_total_times_reg[i] = regular.get(i).getxRayQueue_total_time();
			collect_prop_queue_total_times_reg[i] = regular.get(i).getcollectPropertyQueue_total_time();
		}
		
		DescriptiveStatistics totalStatsReg = new DescriptiveStatistics(total_times_reg);
		results[20][n] = totalStatsReg.getMean();
		results[21][n] = totalStatsReg.getStandardDeviation();
		
		DescriptiveStatistics idStatsReg = new DescriptiveStatistics(id_queue_total_times_reg);
		results[22][n] = idStatsReg.getMean();
		results[23][n] = idStatsReg.getStandardDeviation();
		
		DescriptiveStatistics mmStatsReg = new DescriptiveStatistics(mm_queue_total_times_reg);
		results[24][n] = mmStatsReg.getMean();
		results[25][n] = mmStatsReg.getStandardDeviation();
		
		DescriptiveStatistics xrayStatsReg = new DescriptiveStatistics(xray_queue_total_times_reg);
		results[26][n] = xrayStatsReg.getMean();
		results[27][n] = xrayStatsReg.getStandardDeviation();
		
		DescriptiveStatistics collectPropStatsReg = new DescriptiveStatistics(collect_prop_queue_total_times_reg);
		results[28][n] = collectPropStatsReg.getMean();
		results[29][n] = collectPropStatsReg.getStandardDeviation();
		
		
		}
		
		
		boolean b = writeCSV(labels, results, "Model_A_Times.csv");
		System.out.println("Average queue times exported success? " + b);
			
	}
}
