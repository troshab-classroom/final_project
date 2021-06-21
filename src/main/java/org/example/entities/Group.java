package org.example.entities;

import lombok.Data;
import org.json.JSONObject;

import java.util.List;

@Data
public class Group {
    private int id_group;
    private final String name;
    private final String description;
    public Group(JSONObject group){
        this.id_group = group.getInt("id");
        this.name = group.getString("name");
        this.description = group.getString("description");
    }
    public Group( final String name, final String description) {
        this.name = name;
        this.description = description;
    }
    public Group(int id, final String name, final String description) {
        this.id_group=id;
        this.name = name;
        this.description = description;
    }
    public JSONObject toJSON(){
        JSONObject json = new JSONObject("{"+"\"id\":"+id_group+", \"name\":\""+name+"\", \"description\":\""+description+"\"}");
        return json;

    }
    public static JSONObject toJSONObject(List<Group> groups) {
        StringBuffer stringBuffer = new StringBuffer();

        stringBuffer.append("{\"list\":[");

        for (Group g : groups) {
            stringBuffer.append(g.toJSON().toString() + ", ");
        }
        stringBuffer.delete(stringBuffer.length() - 1, stringBuffer.length() - 1);
        stringBuffer.append("]}");

        return new JSONObject(stringBuffer.toString());
    }
}
