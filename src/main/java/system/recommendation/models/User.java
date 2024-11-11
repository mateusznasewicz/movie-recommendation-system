package system.recommendation.models;

import java.util.HashMap;

public class User extends Entity{
    public User(int id) { super(id); }
    public User() { super(); }
    public User(int id, double avg, HashMap<Integer, Double> ratings){
        super(id,avg,ratings);
    }
}
