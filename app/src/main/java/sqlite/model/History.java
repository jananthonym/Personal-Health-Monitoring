package sqlite.model;

/**
 * written by: Jan Anthony Miranda
 * tested by: Jan Anthony Miranda
 * debugged by: Jan Anthony Miranda
 */
public class History {
    int id;
    double caloriesOut;
    int caloriesIn;
    String routine;
    int steps;
    String date;

    public History(){

    }

    public History(int caloriesIn, double caloriesOut, String routine, int steps, String date){
        this.caloriesIn=caloriesIn;
        this.caloriesOut=caloriesOut;
        this.routine = routine;
        this.steps = steps;
        this.date = date;
    }

    public void setId(int id){
        this.id =id;
    }

    public void setcaloriesIn(int caloriesIn){
        this.caloriesIn=caloriesIn;
    }

    public void setcaloriesOut(double caloriesOut){
        this.caloriesOut=caloriesOut;
    }

    public void setRoutine(String routine) {
        this.routine = routine;
    }

    public void setSteps(int steps) {
        this.steps = steps;
    }

    public void setDate(String date){
        this.date = date;
    }

    public int getId(){
        return id;
    }

    public int getCaloriesIn(){
        return caloriesIn;
    }

    public double getcaloriesOut(){
        return caloriesOut;
    }

    public int getSteps(){
        return steps;
    }

    public String getRoutine(){
        return routine;
    }

    public String getDate(){
        return date;
    }
}
