package com.afqa123.intergalactic.logic.strategy;

import com.afqa123.intergalactic.logic.strategy.Goal.Type;
import com.afqa123.intergalactic.math.HexCoordinate;
import com.afqa123.intergalactic.model.Faction;
import com.afqa123.intergalactic.model.Session;
import com.afqa123.intergalactic.model.Ship;
import com.afqa123.intergalactic.model.Unit;
import com.badlogic.gdx.utils.Json;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class PirateStrategy extends BaseStrategy implements Json.Serializable {

    PirateStrategy() {
        
    }
    
    public PirateStrategy(String factionName) {
        this.factionName = factionName;        
    }
    
    @Override
    protected void updateGoals(Session session) {
        final Faction faction = session.getFactions().get(factionName);

        // Find all sectors within range of pirate ships
        Set<HexCoordinate> sectorCandidates = new HashSet<>();
        for (Ship ship : faction.getShips()) {
            HexCoordinate c = ship.getCoordinates();
            for (int i = 1; i <= ship.getScanRange(); i++) {
                sectorCandidates.addAll(Arrays.asList(c.getRing(i)));
            }
        }
        // Find all enemy ships within these sectors
        for (Unit u : session.getUnits()) {
            if (u.getOwner() != faction && sectorCandidates.contains(u.getCoordinates())) {
                addGoal(new Goal(Type.DESTROY_UNIT, u.getId()));
            }
        }
    }
    
    @Override
    public void write(Json json) {
        json.writeValue("class", PirateStrategy.class.getName());
        super.write(json);
    }
}