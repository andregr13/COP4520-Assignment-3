import java.util.*;
import java.util.concurrent.*;
import java.io.*;
import java.util.concurrent.atomic.*;

public class birthdayGifts
{
	public static void main(String [] args)
	{
		List<gift> bag = Collections.synchronizedList(new ArrayList<gift>());
		List<gift> chain = Collections.synchronizedList(new LinkedList<gift>());
		ThankYous total = new ThankYous();
		
		for (int i = 0; i < 500000; i++)
		{
			bag.add(new gift(i));
		}
		long start = System.nanoTime();
		
		Thread servants [] = new Thread[4];
		for (int i = 0; i < 4; i++)
		{
			servants[i] = new Thread(new Servant(bag, chain, total));
			servants[i].start();
		}
		
		for (Thread servant : servants)
		{
			try
			{
				servant.join();
			}
			catch (Exception e) {}
		}
		
		long end = System.nanoTime();
		
		System.out.println( (end-start)/Math.pow(10,9));
		System.out.println("Bag items: " + bag.size());
		System.out.println("Chain items: " + chain.size());
		System.out.println("Thank You Letters: " + total.getTotal());
	}
}

public class gift
{
	int id;
	boolean thankyou = false;
	gift(int id)
	{
		this.id = id;
	}
	
	public int getID()
	{
		return this.id;
	}
	
	public boolean getThankYou()
	{
		return thankyou;
	}
	
	public void writeThankYou()
	{
		this.thankyou = true;
	}
	
	boolean equals(gift g)
	{
		if (this.id == g.getID() && this.thankyou == g.getThankYou())
			return true;
		return false;
	}
}

class giftComparator implements Comparator<gift>
{
	public int compare(gift a, gift b)
	{
		return a.getID() - b.getID();
	}
}

public class ThankYous
{
	private AtomicInteger count = new AtomicInteger(0);
	
	public void increment()
	{
		count.incrementAndGet();
	}
	
	public int getTotal()
	{
		return count.get();
	}
}
class Servant implements Runnable
{
	List<gift> bag;
	List<gift> chain;
	ThankYous total;
	Random random = new Random();
	
	Servant(List<gift> bag, List<gift> chain, ThankYous total)
	{
		this.bag = bag;
		this.chain = chain;
		this.total = total;
	}
	
	public void run()
	{	
		while (bag.size() > 0 || chain.size() > 0)
		{
			int task = random.nextInt(3);
		
			switch (task)
			{
				case 0:
					addGift();
					break;
				case 1:
					writeThankYou();
					break;
				default:
					checkGift(random.nextInt(500000));
			}
		}
	}
	
	public void addGift()
	{
		int i;
		gift g = null;
		synchronized(bag)
		{
			if (bag.size() > 0)
			{
				i = random.nextInt(bag.size());
				g = bag.remove(i);
			}
		}
		synchronized(chain)
		{
			if (g != null)
			{
				chain.add(g);
				chain.sort(new giftComparator());
			}
		}
	}
	
	public void writeThankYou()
	{
		int i;
		gift g = new gift(0);
		boolean writeThankYou = false;
		synchronized(chain)
		{
			if (chain.size() > 0)
			{
				i = random.nextInt(chain.size());
				if (chain.get(i).getThankYou() == false)
				{
					g = chain.remove(i);
					writeThankYou = true;
					total.increment();
				}
			}
		}
	}
	
	public boolean checkGift(int id)
	{
		gift g = new gift(id);
		synchronized(chain)
		{	
			if (chain.contains(g))
				return true;
			return false;
		}
	}
}
