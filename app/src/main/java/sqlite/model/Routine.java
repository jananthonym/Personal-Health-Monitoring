package sqlite.model;

/**
 * written by: Jan Anthony Miranda
 * tested by: Jan Anthony Miranda
 * debugged by: Jan Anthony Miranda
 */
public class Routine {

    int id;
    String name;

    public Routine(){

    }

    public Routine(String name){
        this.name=name;
    }

    public Routine(int id, String name){
        this.id=id;
        this.name=name;
    }

    public void setId(int id){
        this.id=id;
    }

    public void setName(String name){
        this.name=name;
    }

    public long getId(){
        return id;
    }

    public String getName(){
        return name;
    }
}
