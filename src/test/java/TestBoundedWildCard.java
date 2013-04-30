import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Arindam
 * Date: 2/8/13
 * Time: 9:38 AM
 * To change this template use File | Settings | File Templates.
 */
public class TestBoundedWildCard {
	public static double getAverage(List<? extends Number> list){
		double total = 0.0;
		for (Number number : list){
			total += number.doubleValue();
		}
		return total/list.size();
	}

	public static void main(String[] args){
		List<Integer> list1 = new ArrayList<Integer>();
		list1.add(3);
		list1.add(30);
		list1.add(300);
		System.out.println(getAverage(list1));
		System.out.println("************");

		List<Double> list2 = new ArrayList<Double>();
		list2.add(3.0);
		list2.add(33.0);
		System.out.println(getAverage(list2));
		System.out.println("**************");
	}
}
