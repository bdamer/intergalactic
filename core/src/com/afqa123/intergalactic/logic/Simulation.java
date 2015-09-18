package com.afqa123.intergalactic.logic;

import com.afqa123.intergalactic.model.Galaxy;
import com.afqa123.intergalactic.model.Sector;
import com.afqa123.intergalactic.model.Faction;
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
    
    private final Session session;
    private final Galaxy galaxy;
    private final Set<StepListener> listeners = new HashSet<>();

    public Simulation(Session session) {
        this.session = session;
        this.galaxy = session.getGalaxy();
    }
    
    public void init() {
        ShipType scoutType = session.getDatabase().getShip("scout");
        // Determine faction start planets
        List<Sector> sectors = galaxy.getStarSystems();
        for (Faction faction : session.getFactions().values()) {
            if (faction.isPirates()) {
                continue;
            }
            
            Sector home;
            do {
                home = sectors.get((int)(Math.random() * sectors.size()));
            } while (home.getOwner() != null);            
            home.colonize(session, faction.getName());
            // explore player map around new colony
            faction.getMap().addRange(home.getCoordinates());
            session.createShip(scoutType, home.getCoordinates(), faction);
        }
        
        // seed pirates
        for (Sector sector : galaxy.getStarSystems()) {
            sector.spawnPirate(session);
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

        Gdx.app.log(Simulation.class.getName(), String.format("Simulating turn %d", session.getTurn()));

        // AI turns
        Gdx.app.log(Simulation.class.getName(), "Simulating other factions.");        
        for (Faction faction : session.getFactions().values()) {
            if (!faction.isPlayer()) {
                faction.getStrategy().nextTurn(session);
            }
        }
                
        List<Sector> sectors = galaxy.getStarSystems();
        for (Sector s : sectors) {
            s.update(session);
        }

        for (Unit u : session.getUnits()) {
            u.update(session);
        }

        session.increaseTurns();
        
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