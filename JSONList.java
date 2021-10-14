import java.util.*;

public class JSONList implements Iterable<Node>
{
    ArrayList<Node> list;

    public JSONList()
    {
        list = new ArrayList<>();

    }

    public void add(Node obj)
    {
        list.add(obj);
    }

    public void add(int index, Node obj)
    {
        list.add(index,obj);
    }

    public Node get(int index)
    {
        return list.get(index);
    }

    public Iterator<Node> iterator()
    {
        return list.iterator();
    }

    public String toString()
    {
        StringBuilder str = new StringBuilder();
        str.append("[");
        for(var value : list)
        {
            str.append(value.toString());
            str.append(",");
        }
        str.deleteCharAt(str.lastIndexOf(","));
        str.append("]");

        return str.toString();
    }

}
