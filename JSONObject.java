import java.util.*;

/*
Type.NUMBER == Number
Type.STRING == string
Type.BOOLEAN == boolean
Type.JSONOBJECT == JSONObject
Type.NULL == null
Type.JSONLIST == JSONList
*/

public class JSONObject
{
    HashMap<String, Node> data;

    public JSONObject()
    {
        data = new HashMap<>();
    }

    void insert(String key, int value)
    {
        Node temp = new Node(Type.NUMBER, Integer.toString(value));
        data.put(key,temp);
    }

    void insert(String key, String value)
    {
        Node temp = new Node(Type.STRING, value);
        data.put(key, temp);
    }

    void insert(String key, JSONObject value)
    {
        Node temp = new Node(Type.JSONOBJECT, value.toString());
        data.put(key, temp);
    }

    void insert(String key, boolean value)
    {
        Node temp = new Node(Type.BOOLEAN, Boolean.toString(value));
        data.put(key, temp);
    }

    void insert(String key, Object value)
    {
        Node temp;
        if(value == null) {
            temp = new Node(Type.NULL, "null");
            data.put(key, temp);
        }
    }

    void insert(String key, JSONList value)
    {
        Node temp = new Node(Type.JSONLIST, value.toString());
        data.put(key, temp);
    }

    void insert(String key, Node value)
    {
        data.put(key,value);
    }

    Node get(String key)
    {
        return data.get(key);
    }

    public String toString()
    {
        StringBuilder str = new StringBuilder();
        str.append("{");
        for(var entry : data.entrySet())
        {
            String key = entry.getKey();
            Node value = entry.getValue();
            str.append("\"");
            str.append(key);
            str.append("\"");
            str.append(":");
            str.append(value.toString());
            str.append(",");

        }
        if(str.indexOf(",") != -1)
            str.deleteCharAt(str.lastIndexOf(","));
        str.append("}");

        return str.toString();
    }

}
