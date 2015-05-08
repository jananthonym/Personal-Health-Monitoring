package com.trainer.g14.g_trainer;

/**
 * written by: Jan Anthony Miranda
 * tested by: Jan Anthony Miranda
 * debugged by: Jan Anthony Miranda
 */

//Exercise object to hold name, reps, and sets
public class exercise {

    String name;
    int sets;
    int reps;

    public exercise(String name, int sets, int reps){
        this.name=name;
        this.sets=sets;
        this.reps=reps;
    }

    public String getName(){
        return name;
    }

    public int getSets(){
        return sets;
    }

    public int getReps(){
        return reps;
    }
}
