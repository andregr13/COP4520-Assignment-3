import java.util.*;
import java.util.concurrent.*;
import java.io.*;
import java.util.concurrent.TimeUnit.*;


class TempReader
{
	static ScheduledExecutorService executor = Executors.newScheduledThreadPool(9);
	static ScheduledFuture<?> [] t = new ScheduledFuture<?>[9];
		
	public static void main(String [] args)
	{
		Temps temps = new Temps();
		int minTime = 50;
		int hoursRun = 4;
		for (int i = 0; i < 8; i++)
		{
			t[i] = executor.scheduleAtFixedRate(new Reader(i, temps), 0, minTime, TimeUnit.MILLISECONDS);
		}
		t[8] = executor.scheduleAtFixedRate(new Compiler(temps, hoursRun), 60*minTime, 60*minTime, TimeUnit.MILLISECONDS);
	}
	
	static class Compiler implements Runnable
	{
		Temps temps;
		private int hours = 0;
		int limit;
		public Compiler(Temps t, int h)
		{
			this.temps = t;
			this.limit = h;
		}
		public void run()
		{
			ArrayList<ArrayList<Integer>> data = temps.getData();
			
			int[] largest = new int[5];
			int[] smallest = new int[5];
			int max10 = Integer.MIN_VALUE;
			int min10 = Integer.MAX_VALUE;
			Queue<Integer> last10max = new LinkedList<Integer>();
			Queue<Integer> last10min = new LinkedList<Integer>();
			int minInt = Integer.MAX_VALUE;
			int maxInt = Integer.MIN_VALUE;
			int diffInt = 0;
			int largestDiff = Integer.MIN_VALUE;
			int intervals = Integer.MAX_VALUE;
			int temp = 0;
			
			for (int i = 0; i < 8; i++)
				intervals = (data.get(i).size() < intervals) ? data.get(i).size() : intervals;
			
			
			for (int i = intervals-1; i > intervals-60 && i >= 0; i--)
			{
				minInt = Integer.MAX_VALUE;
				maxInt = Integer.MIN_VALUE;
			
				for (int j = 0; j < 8; j++)
				{
					temp = data.get(j).get(i);
					if (intervals - i - 1 < 5)
					{
						largest[intervals - i - 1]  = temp;
						smallest[intervals - i - 1] = temp;
					}
					else
					{
						Arrays.sort(largest);
						Arrays.sort(smallest);
						largest[0] = (temp > largest[0]) ? temp : largest[0];
						smallest[4] = (temp < smallest[4]) ? temp : smallest[4];
					}
					maxInt = (temp > maxInt) ? temp : maxInt;
					minInt = (temp < minInt) ? temp : minInt;
				}
				
				last10max.add(maxInt);
				last10min.add(minInt);
				if (last10max.size() > 10)
				{
					last10max.poll();
					last10min.poll();
				}
				int tempmax = Integer.MIN_VALUE;
				int tempmin = Integer.MAX_VALUE;
				
				for (int k : last10max)
					tempmax = (k > tempmax) ? k : tempmax;
					
				for (int k : last10min)
					tempmin = (k < tempmin) ? k : tempmin;
				
				if (tempmax - tempmin > largestDiff)
				{
					largestDiff = tempmax - tempmin;
					diffInt = i;
				}
			}
			
			if (intervals-11 < diffInt)
				diffInt = intervals-11;
			System.out.println("Hourly Report " + (intervals-61) + "-" + (intervals-1) + " mins");
			System.out.println("5 largest temps: " + Arrays.toString(largest));
			System.out.println("5 smallest temps: " + Arrays.toString(smallest));
			System.out.println("Largest 10 minute difference: " + largestDiff + "F from " + diffInt + "-" + (diffInt+10) + " mins");
			System.out.println();
			
			if (++hours >= limit)
			{
				for (int i = 0; i < 9; i++)
					t[i].cancel(false);
				System.exit(0);
			}
		}
	}
}

class Temps 
{
	private ArrayList<ArrayList<Integer>> readers; 
	
	public Temps()
	{
		readers = new ArrayList<ArrayList<Integer>>(8);
		for (int i = 0; i < 8; i++)
			readers.add(new ArrayList<Integer>());
	}
	
	public synchronized void add(int reader, int temp)
	{
		readers.get(reader).add(temp);
	}
	
	public synchronized ArrayList<Integer> getReader(int r)
	{
		return readers.get(r);
	}
	
	public synchronized ArrayList<Integer> getInterval(int i)
	{
		ArrayList<Integer> ret = new ArrayList<Integer>();
		for (int j = 0; j < 8; j++)
		{
			if (readers.get(j).get(i) != null)
				ret.add(readers.get(j).get(i));
		}
		return ret;
	}
	
	public synchronized ArrayList<ArrayList<Integer>> getData()
	{
		return new ArrayList<ArrayList<Integer>>(readers);
	}
}
class Reader implements Runnable
{
	Random random = new Random();
	int index;
	Temps temps;
	
	public Reader(int i, Temps t)
	{
		this.index = i;
		this.temps = t;
		
	}
	public void run()
	{
		int temp = random.nextInt(171) - 100;
		
		temps.add(index, temp);
	}
}



























