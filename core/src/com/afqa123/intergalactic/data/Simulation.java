package com.afqa123.intergalactic.data;

import com.afqa123.intergalactic.math.HexCoordinate;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import java.util.List;

/**
 * Simulation engine. Performs updates of sectors and ships.
 */
public class Simulation {

    private int turn;
    private final Galaxy galaxy;
    private final Faction player;
    
    public Simulation(Galaxy galaxy, Faction player) {
        this.galaxy = galaxy;
        this.player = player;
    }
    
    public void init() {
        galaxy.randomizeSectorsSpiral();
        
        // Determine player start planets

        // Select random home sector
        // List<Sector> sectors = galaxy.getStarSystems();
        // Sector home = sectors.get((int)(Math.random() * sectors.size()));
        
        // Select sector at origin
        HexCoordinate c = new HexCoordinate(0, 0);
        Sector home = galaxy.getSector(HexCoordinate.ORIGIN);
        if (home.getCategory() == null) {
            home = new Sector("Sol", c, Sector.StarCategory.YELLOW);
            galaxy.getStarSystems().add(home);
        }
        Vector2 offset = galaxy.axialToOffset(c.x, c.y);
        galaxy.getSectors()[(int)offset.x][(int)offset.y] = home;
        
        home.setOwner(player);
        home.setPopulation(2.0f);
        home.setFoodProducers(2);
        // TODO: compute automatically based on number of terraformed planets
        home.setMaxPopulation(10);
        home.computerModifiers();
        player.getSectors().add(home);
        player.getMap().addHomeColony(home);
        player.getMap().update();
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
