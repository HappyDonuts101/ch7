// Microsoft: They bought near the peak, so their shares lost value for years. In 2010, they were still worth less than in 2000.

//Starbucks and S&P 500: 
// Starbucks bottomed during the 2008 financial crisis, 
// and the S&P 500 bottomed in 1932, during the Great Depression. 
// The timing makes sense, because prices were at the lowest and economy was the worst.


// 1. The moving average did best on the most files. No strategy was best on every file.
//    The dip buyer won on zigzag, msft, ibm and tiny, and buy and hold won on rising and goog.
//
// 2. The dip buyer did badly on aapl .It sold too early at +10%, like in Jul 2003 at $10.54, and didn't buy again until Apr 2005 at $36.06.
//
// 3. On sp500 1925 to 1945 the moving average got $37,196 vs $16,362 for buy and hold. It sold in Nov 1929 at $20.58 as the crash started, and it avoided the Great Depression drop to $4.77.
//
// 4. My random strategy was unreliable: it beat buy and hold on some file but lost badly on others, and the results changed every run. When I changed the rules to hold longer (sell chance 2% instead of 5%, and it did better on average, but it did worse on some files like amzn. That showed luck mattered mor.
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.Scanner;

import java.util.Random;
public class StockTrader {
    public static final double START_CASH = 10000.0;
    public static final double FEE = 10.0;
    public static void main(String[] args) throws FileNotFoundException {
        Scanner in = new Scanner(new File("goog_2004_2010.txt"));
        String symbol = in.next();
        int n = in.nextInt();
        
        int [] years = new int [n];
        int [] months = new int [n];
        double[] prices = new double[n];   
        
        loadData(in, years, months, prices);
        printData(years, months, prices);

        int low = indexOfLowest(prices);
    int high = indexOfHighest(prices);

System.out.printf("%s lowest:  $%.2f in %s %d%n", symbol, prices[low], monthName(months[low]), years[low]);
System.out.printf("%s highest: $%.2f in %s %d%n", symbol, prices[high], monthName(months[high]), years[high]);
        int drop = indexOfBiggestDrop(prices);
System.out.printf("%s biggest drop: %s %d%n", symbol, monthName(months[drop]), years[drop]);
        double result = simulate(years, months, prices);
        System.out.printf("Final value: $%,.2f%n", result);        
     }
     
     
     public static void loadData(Scanner in, int[] years, int[] months, double[] prices) {
		 
		 for(int i =0; i<years.length; i++) {
			 
			 years[i]= in.nextInt();
			 months[i]= in.nextInt();
			 prices[i] = in.nextDouble();
		 }
		 
		 
		 
	 }
	 
	 
	 public static String monthName(int month) {
		 
		 String[] months = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
		 
		 return months[month-1];
		 
	 }
	 
	 public static void printData(int[] years, int[] months, double[] prices) {
		 
		 for(int i =0; i<prices.length; i++) {
			 
		System.out.print(monthName(months[i]) + " " + years[i] + ": $");
  System.out.printf("%.2f", prices[i]);
  System.out.println();
			 
			 
			 
		}

    }
		
		 public static int indexOfLowest(double[] prices) {
        int lowestIndex = 0;
        for (int i = 1; i < prices.length; i++) {
            if (prices[i] < prices[lowestIndex]) {
            lowestIndex = i;
            }
        }
          return lowestIndex;
    }
		
		
		
		public static int indexOfHighest(double[] prices) {
			
			int highestIndex = 0;
			
			
			for(int i =0 ; i<prices.length; i++) {
				
				if(prices[i]>prices[highestIndex])  {
					
					highestIndex = i;
					
				}
				
				
			}
				   return highestIndex;

        }

         

            public static int indexOfBiggestDrop(double[] prices) {
    int worstIndex = 1;
 double worstChange = (prices[1] - prices[0]) / prices[0];

    for (int i = 2; i < prices.length; i++) {
double change = (prices[i] - prices[i - 1]) / prices[i - 1];
if (change < worstChange) {
        worstChange = change;
            worstIndex = i;
        }
    }
    return worstIndex;
}
	 


    public static double simulate(int[] years, int[] months, double[] prices) {
        double cash = START_CASH;
        int shares = 0;
        double boughtAt = 0.0;

        for (int i = 0; i < prices.length; i++) {
            double[] history = Arrays.copyOf(prices, i + 1);
            String action = decide(history, shares > 0, boughtAt);

            if (action.equals("BUY") && shares == 0) {
                int canBuy = (int) ((cash - FEE) / prices[i]);
                if (canBuy > 0) {
                    shares = canBuy;
                    cash = cash - shares * prices[i] - FEE;
                    boughtAt = prices[i];
                    System.out.printf("%s %d: BUY %d shares at $%.2f%n",
                            monthName(months[i]), years[i], shares, prices[i]);
                }
            } else if (action.equals("SELL") && shares > 0) {
                cash = cash + shares * prices[i] - FEE;
                System.out.printf("%s %d: SELL %d shares at $%.2f%n",
                        monthName(months[i]), years[i], shares, prices[i]);
                shares = 0;
                boughtAt = 0.0;
            }
        }
        return cash + shares * prices[prices.length - 1];
    }

    public static String decide(double[] history, boolean holding, double boughtAt) {
    return myStrategy(history, holding, boughtAt);
}
    public static String buyAndHold(double[] history, boolean holding, double boughtAt) {
        return "BUY";
    }

    public static String dipBuyer(double[] history, boolean holding, double boughtAt) {
        int n = history.length;

        if (n < 3) {
            return "HOLD";
        }

        double today = history[n - 1];
        double lastMonth = history[n - 2];
        double twoMonthsAgo = history[n - 3];

        if (!holding && today < lastMonth && lastMonth < twoMonthsAgo) {
            return "BUY";
        }
        if (holding && today >= boughtAt * 1.10) {
            return "SELL";
        }
        return "HOLD";
    }

    public static String movingAverage(double[] history, boolean holding, double boughtAt) {
        int n = history.length;

        if (n < 12) {
            return "HOLD";
        }

        double today = history[n - 1];
        double average = getAverage(history);

        if (!holding && today > average) {
            return "BUY";
        }
        if (holding && today < average) {
            return "SELL";
        }
        return "HOLD";
    }

    public static double getAverage(double[] history) {
        double total = 0;
        for (int i = history.length - 12; i < history.length; i++) {
            total += history[i];
        }
        return total / 12;
    }
    public static Random rng = new Random();


	// My strategy uses random chances, but the chances change based on the price. If I'm not holding: 15% chance to buy each month, or 40% if the price went down since last month, so I'm more likely to buy after a dip. If I'm holding: only a 10% chance to sell, so I usually hold for a long time.
// If I'm up 50% or more, the chance goes up to 50% to lock in the profit. If I'm down 15% or more, it goes up to 35% to stop losing more.
// My stock assumes that after a dip, it will go back up and rise over time. 
    public static String myStrategy(double[] history, boolean holding, double boughtAt) {
        int n = history.length;
        double today = history[n - 1];
        double roll = rng.nextDouble();

        if (!holding) {
            double buyChance = 0.15;
            if (n >= 2 && today < history[n - 2]) {
                buyChance = 0.40;
            }
            if (roll < buyChance) {
                return "BUY";
            }
        } else {
            double change = (today - boughtAt) / boughtAt;
            double sellChance = 0.1;
            if (change >= 0.50) {
                sellChance = 0.50;
            } else if (change <= -0.15) {
                sellChance = 0.35;
            }
            if (roll < sellChance) {
                return "SELL";
            }
        }
        return "HOLD";
    }
    
	 
 }
 