package com.afqa123.intergalactic.logic.strategy;

import com.afqa123.intergalactic.logic.strategy.Goal.Type;
import com.afqa123.intergalactic.math.HexCoordinate;
import com.afqa123.intergalactic.model.Faction;
import com.afqa123.intergalactic.model.FactionMap;
import com.afqa123.intergalactic.model.FactionMapSector;
import com.afqa123.intergalactic.model.Galaxy;
import com.afqa123.intergalactic.model.Range;
import com.afqa123.intergalactic.model.Sector;
import com.afqa123.intergalactic.model.SectorStatus;
import com.afqa123.intergalactic.model.Session;
import com.badlogic.gdx.utils.Json;
import java.util.ArrayList;
import java.util.List;

public class SimpleStrategy extends BaseStrategy implements Json.Serializable {

    SimpleStrategy() {
        
    }
    
    public SimpleStrategy(String factionName) {
        this.factionName = factionName;        
    }
    
    @Override
    protected void updateGoals(Session session) {
        final Faction faction = session.getFactions().get(factionName);
        final Galaxy galaxy = session.getGalaxy();

        HexCoordinate exploreTarget = null;
        List<HexCoordinate> stationCandidates = new ArrayList<>();
        int colonyCandidates = 0;
        
        // Add goals for sectors within faction range
        List<FactionMapSector> tmp = faction.getMap().findSectors(SectorStatus.UNKNOWN, Range.LONG);
        for (FactionMapSector s : tmp) {
            if (s.getStatus() == SectorStatus.UNKNOWN) {
                // mark first unknown sector for exploration
                if (exploreTarget == null) {
                    exploreTarget = s.getCoordinate();
                }
                continue;
            }
            // can only colonize at short / medium range
            if (s.getRange() == Range.LONG) {
                continue;
            }

            HexCoordinate c = s.getCoordinate();
            Sector sector = galaxy.getSector(c);
            // Check if this sector would be a good candidate for an outpost
            if (s.getRange() == Range.SHORT && sector.canBuildOutpost()) {
                // Don't build outposts towards the outer rim
                int d = c.getDistance(HexCoordinate.ORIGIN);
                if (d < (galaxy.getRadius() - Range.LONG.getDistance())) {
                    stationCandidates.add(sector.getCoordinates()); // mark outpost candidate for later use
                }
            }
            
            if (sector.canColonize()) {
                colonyCandidates++;
                addGoal(new Goal(Type.COLONIZE_SECTOR, s.getCoordinate()));
            } else if (sector.isColony(faction)) {
                checkColonyProduction(sector);
                if (sector.getBuildQueue().isEmpty()) {
                    addGoal(new Goal(Type.BUILD_STRUCTURES, s.getCoordinate()));
                }                
            }
        }
        
        // Add EXPLORE goals for unexplored sectors within visible range
        if (exploreTarget != null) {
            addGoal(new Goal(Type.EXPLORE, exploreTarget));
        }
        
        // Add BUILD_STATION goals if there are no more candidates for colonies available
        if (colonyCandidates == 0) {
            // check that no other plans to build station are active
            if (getGoalsByType(Type.BUILD_STATION).isEmpty()) {
                addGoal(new Goal(Type.BUILD_STATION, evaluteStationCandidates(session, stationCandidates)));
            }
        }
        
        // TODO: Add DESTROY goals for enemy units within visible range        
    }
    
    private void checkColonyProduction(Sector sector) {
        // First, check for food shortage
        if (sector.getNetFoodOutput() < 0.0f) {
            while (sector.getNetFoodOutput() < 0.0f) {
                // Always take from science first, then industry
                if (sector.getScienceProducers() > 0) {
                    sector.changeScienceProducers(-1);
                    sector.changeFoodProducers(1);
                } else if (sector.getIndustrialProducers() > 0) {
                    sector.changeIndustrialProducers(-1);
                    sector.changeFoodProducers(1);
                } else {
                    // no other produces available - this is bad!
                    // TODO: construct food surplus building?
                    break;
                }
            }
        // Food surplus: see if we can adjust producers for better industry / science
        // output
        } else {
            int free = 0;
            while (sector.getNetFoodOutput() > 0.0f) {
                free++;
                sector.changeFoodProducers(-1);
            }
            // at this point, we've removed 1 producer too many
            free--;
            sector.changeFoodProducers(1);
            // Add to science if system is idle
            // TODO: revise - this is too simplistic
            if (sector.getBuildQueue().isEmpty()) {
                sector.changeScienceProducers(free);
            } else {
                sector.changeIndustrialProducers(free);
            }
        }        
    }

    /**
     * Evaluates a set of candidate coordinates to find the best location for
     * a station. For the purpose of the evaluation, best is defined as 
     * providing the largest gain in territory.
     * 
     * @param session The session.
     * @param candidates The list of candidates to evaluate.
     * @return The best location.
     */
    private HexCoordinate evaluteStationCandidates(Session session, List<HexCoordinate> candidates) {
        FactionMap map = session.getFactions().get(factionName).getMap();
        Galaxy galaxy = session.getGalaxy();

        int bestScore = 0;
        HexCoordinate bestCoord = null;
        for (HexCoordinate c : candidates) {
            int score = 0;
            
            // 2 point for new SHORT RANGE
            HexCoordinate[] ring = c.getRing(Range.SHORT.getDistance());
            for (HexCoordinate cn : ring) {
                FactionMapSector ms = map.getSector(cn);
                if (ms == null) {
                    continue;
                }
                Range range = ms.getRange();
                if (range == null || range.getDistance() > Range.SHORT.getDistance()) {
                    score += 2;
                }
                if (galaxy.getSector(cn).getType() != null) {
                    score++;
                }
            }
            
            // 1 point for new LONG RANGE
            ring = c.getRing(Range.LONG.getDistance());
            for (HexCoordinate cn : ring) {
                FactionMapSector ms = map.getSector(cn);
                if (ms == null) {
                    continue;
                }
                Range range = ms.getRange();
                if (range == null) {
                    score += 1;
                }
                if (galaxy.getSector(cn).getType() != null) {
                    score++;
                }
            }
            
            // TODO: Any additional factors
            
            if (score > bestScore) {
                bestScore = score;
                bestCoord = c;
            }
        }
        return bestCoord;
    }
        
    private List<Goal> getGoalsByType(Goal.Type type) {
        List<Goal> res = new ArrayList<>();
        for (Goal goal : allGoals) {
            if (type.equals(goal.getType())) {
                res.add(goal);
            }
        }
        return res;
    }
    
    @Override
    public void write(Json json) {
        json.writeValue("class", SimpleStrategy.class.getName());
        super.write(json);
    }
}