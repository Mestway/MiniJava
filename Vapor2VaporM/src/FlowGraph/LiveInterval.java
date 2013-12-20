package FlowGraph;

import java.util.Comparator;

public class LiveInterval {
	String variableName;
	private int left;
	private int right;
	
	LiveInterval(LiveInterval li)
	{
		variableName = li.variableName;
		left = li.get_left();
		right = li.get_right();
	}
	
	LiveInterval(String name,int l, int r)
	{
		set(name,l,r);
	}
	void set(String name,int l, int r)
	{
		variableName = name;
		left = l;
		right = r;
	}
	void set_left(int l)
	{
		left = l;
	}
	void set_right(int r)
	{
		right = r;
	}
	int get_left()
	{
		return left;
	}
	int get_right()
	{
		return right;
	}
	void print()
	{
		System.out.println(variableName + "["+ left + "," + right + "]");
	}

    public static Comparator<LiveInterval> RangeComparator = new Comparator<LiveInterval>() {
        public int compare(LiveInterval r1, LiveInterval r2)
        {
                int ret = Integer.compare(r1.left, r2.left);
                if (ret == 0)
                        ret = Integer.compare(r1.right, r2.right);
                
                return ret;
        }
    };
    public static Comparator<LiveInterval> ActiveComparator = new Comparator<LiveInterval>() {
        public int compare(LiveInterval r1, LiveInterval r2)
        {
                int ret = Integer.compare(r1.right, r2.right);
                if (ret == 0)
                        ret = Integer.compare(r1.left, r2.left);
                
                return ret;
        }
    };
	
}
