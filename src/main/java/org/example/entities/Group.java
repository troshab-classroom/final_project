package org.example.entities;

import lombok.Data;
import org.json.JSONObject;

@Data
public class Group {
    private int id_group;
    private final String name;
    private final String description;

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
}
