package com.afqa123.intergalactic.logic;

import com.afqa123.intergalactic.model.Galaxy;
import com.afqa123.intergalactic.model.Sector;
import com.afqa123.intergalactic.model.Faction;
import com.afqa123.intergalactic.model.Unit;
import com.afqa123.intergalactic.math.HexCoordinate;
import com.afqa123.intergalactic.model.StarType;
import com.afqa123.intergalactic.model.State;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Simulation engine. Performs updates of sectors and ships.
 */
public class Simulation {

    public interface StepListener {
        
        /**
         * Called before a step is simulated.
         * 
         * @return True if the step can be simulated, otherwise false.
         */
        boolean prepareStep();
        
        /**
         * Called after a step has been simulated.
         */
        void afterStep();
    };
    
    private final State state;
    private final Galaxy galaxy;
    private final Faction player;
    private final Set<StepListener> listeners = new HashSet<>();

    public Simulation(State state) {
        this.state = state;
        this.galaxy = state.getGalaxy();
        this.player = state.getFactions().get("player");
    }
    
    public void init() {        
        // Determine player start planets

        // Select random home sector
        // List<Sector> sectors = galaxy.getStarSystems();
        // Sector home = sectors.get((int)(Math.random() * sectors.size()));
        
        // Select sector at origin
        HexCoordinate c = new HexCoordinate(0, 0);
        Sector home = galaxy.getSector(HexCoordinate.ORIGIN);
        if (home.getType() == null) {
            home = new Sector("Sol", c, StarType.YELLOW);
            galaxy.getStarSystems().add(home);
        }
        Vector2 offset = galaxy.axialToOffset(c.x, c.y);
        galaxy.getSectors()[(int)offset.x][(int)offset.y] = home;
        player.addColony(home);
    }
    
    /**
     * Computes the next turn of the simulation.
     * // TODO: this will have to be done for each player
     */
    public void turn() {
        for (StepListener listener : listeners) {
            if (!listener.prepareStep()) {
                return;
            }
        }

        state.increaseTurns();
        Gdx.app.log(Simulation.class.getName(), String.format("Simulating turn %d", state.getTurn()));
        
        List<Sector> sectors = galaxy.getStarSystems();
        float science = 0.0f;
        for (Sector s : sectors) {
            science += s.getScientificOutput();
            s.update(state);
        }
        
        // TODO: apply science output to current research project
        Gdx.app.debug(Simulation.class.getName(), String.format("Science output: %f", science));

        for (Unit u : state.getUnits()) {
            u.step();
        }
        
        for (StepListener listener : listeners) {
            listener.afterStep();
        }
    }    
    
    public void addStepListener(StepListener l) {
        listeners.add(l);
    }
    
    public void removeStepListener(StepListener l) {
        listeners.remove(l);
    }
}