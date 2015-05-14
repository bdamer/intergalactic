package com.afqa123.intergalactic.data;

import com.badlogic.gdx.Gdx;
import java.util.List;

/**
 * Simulation engine. Performs updates of sectors and ships.
 */
public class Simulation {

    private int turn;
    private final Galaxy galaxy;
    
    public Simulation(Galaxy galaxy) {
        this.galaxy = galaxy;
    }
    
    /**
     * Computes the next turn of the simulation.
     */
    public void turn() {
        turn++;
        Gdx.app.log(Simulation.class.getName(), String.format("Simulating turn %d", turn));

        // TODO: simulate combat before or after sectors?
        
        List<Sector> sectors = galaxy.getStarSystems();
        float science = 0.0f;
        for (Sector s : sectors) {
            science += s.getScientificOutput();
            s.growPopulation();
            s.produce();
            s.computerModifiers();
        }
        
        // TODO: apply science output to current research project
        Gdx.app.debug(Simulation.class.getName(), String.format("Science output: %f", science));
    }    
}
