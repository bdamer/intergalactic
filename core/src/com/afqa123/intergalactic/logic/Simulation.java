package com.afqa123.intergalactic.logic;

import com.afqa123.intergalactic.model.Galaxy;
import com.afqa123.intergalactic.model.Sector;
import com.afqa123.intergalactic.model.Faction;
import com.afqa123.intergalactic.model.Ship;
import com.afqa123.intergalactic.model.ShipType;
import com.afqa123.intergalactic.model.Unit;
import com.afqa123.intergalactic.model.Session;
import com.badlogic.gdx.Gdx;
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
    
    private final Session state;
    private final Galaxy galaxy;
    private final Set<StepListener> listeners = new HashSet<>();

    public Simulation(Session state) {
        this.state = state;
        this.galaxy = state.getGalaxy();
    }
    
    public void init() {
        ShipType scoutType = state.getDatabase().getShip("scout");
        // Determine faction start planets
        List<Sector> sectors = galaxy.getStarSystems();
        for (Faction faction : state.getFactions().values()) {
            Sector home;
            do {
                home = sectors.get((int)(Math.random() * sectors.size()));
            } while (home.getOwner() != null);            
            faction.addColony(home);
            Ship scout = new Ship(scoutType, faction);
            scout.setCoordinates(home.getCoordinates());
            state.addUnit(scout);
        }
    }
    
    /**
     * Computes the next turn of the simulation.
     */
    public void turn() {
        for (StepListener listener : listeners) {
            if (!listener.prepareStep()) {
                return;
            }
        }

        // AI turns
        Gdx.app.log(Simulation.class.getName(), "Simulating other factions.");        
        for (Faction faction : state.getFactions().values()) {
            if (!faction.isPlayer()) {
                faction.getStrategy().nextTurn(state);
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