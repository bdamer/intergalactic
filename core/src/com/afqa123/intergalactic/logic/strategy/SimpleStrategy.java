package com.afqa123.intergalactic.logic.strategy;

import com.afqa123.intergalactic.logic.strategy.Goal.Type;
import com.afqa123.intergalactic.logic.strategy.Plan.Status;
import com.afqa123.intergalactic.math.HexCoordinate;
import com.afqa123.intergalactic.model.Faction;
import com.afqa123.intergalactic.model.FactionMapSector;
import com.afqa123.intergalactic.model.Galaxy;
import com.afqa123.intergalactic.model.Range;
import com.afqa123.intergalactic.model.Sector;
import com.afqa123.intergalactic.model.SectorStatus;
import com.afqa123.intergalactic.model.Ship;
import com.afqa123.intergalactic.model.Session;
import com.afqa123.intergalactic.model.Station;
import com.afqa123.intergalactic.model.Unit;
import com.afqa123.intergalactic.util.PriorityList;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class SimpleStrategy implements Strategy, Json.Serializable {

    // Helper class containing precomputed data for plan execution
    public class FactionState {
        
        public final Faction faction;
        public final Galaxy galaxy;
        public final List<Ship> idleShips;
        public final List<Station> idleStations;
        public final List<Sector> idleSectors;

        public FactionState(Faction faction, Galaxy galaxy) {
            this.faction = faction;
            this.galaxy = galaxy;
            this.idleShips = new ArrayList<>();
            this.idleStations = new ArrayList<>();
            this.idleSectors = new ArrayList<>();
        }
    }
    
    private String factionName;
    // Active plans
    private final List<Plan> plans = new LinkedList<>();
    // Goals for which we have not yet created a plan
    private final List<Goal> goals = new PriorityList<>(new Comparator<Goal>() {
        @Override
        public int compare(Goal o1, Goal o2) {
            return (o1.getType().getPriority() - o2.getType().getPriority());
        }        
    });
    // All currently active goals
    private final Set<Goal> allGoals = new HashSet<>();

    SimpleStrategy() {
        
    }
    
    public SimpleStrategy(String factionName) {
        this.factionName = factionName;        
    }
    
    @Override
    public void nextTurn(Session state) {        
        Gdx.app.debug(SimpleStrategy.class.getName(), "Computing turn for: " + factionName);
        FactionState fs = updateFactionState(state);
        
        // Create new plans for goals on queue 
        for (Goal goal : goals) {
            Gdx.app.debug(SimpleStrategy.class.getName(), String.format("Adding plan for: %s", goal.toString()));
            plans.add(PlanFactory.newPlan(goal));
        }
        goals.clear();
        
        // evaluate plans
        Gdx.app.log(SimpleStrategy.class.getName(), String.format("Active plans: %d", plans.size()));
        int i = 0;
        while (i < plans.size()) {
            Plan plan = plans.get(i);
            // Update plan as long as status is active
            Status status;
            while (Status.ACTIVE == (status = plan.update(state, fs))) ;
            if (status == Status.COMPLETE) {
                onGoalCompleted(plan.getGoal());
                plans.remove(i);
            } else if (status == Status.INVALID) {
                // cancel plan and return goal to queue
                onGoalCancelled(plan.getGoal());
                plans.remove(i);
            } else {
                i++;
            }
        } 
    }

    private FactionState updateFactionState(Session state) {
        final Faction faction = state.getFactions().get(factionName);
        final Galaxy galaxy = state.getGalaxy();
        FactionState fs = new FactionState(faction, galaxy);

        // Find idle ships
        for (Unit u : faction.getUnits()) {
            if (u instanceof Ship && u.getTarget() == null) {
                fs.idleShips.add((Ship)u);
            }
        }

        HexCoordinate exploreTarget = null;
        HexCoordinate outpostCandidate = null;
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
            // TODO: improve - the distribution feels fairly random
            if (outpostCandidate == null && s.getRange() == Range.MEDIUM && sector.canBuildOutpost()) {
                // Don't build outposts towards the outer rim
                int d = c.getDistance(HexCoordinate.ORIGIN);
                if (d < (galaxy.getRadius() - Range.LONG.getDistance())) {
                    outpostCandidate = sector.getCoordinates(); // mark outpost candidate for later use
                }
            }
            
            if (sector.canColonize()) {
                colonyCandidates++;
                addGoal(new Goal(Type.COLONIZE_SECTOR, s.getCoordinate()));
            } else if (sector.isColony(faction)) {
                checkColonyProduction(sector);
                if (sector.getBuildQueue().isEmpty()) {
                    fs.idleSectors.add(sector);
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
                addGoal(new Goal(Type.BUILD_STATION, outpostCandidate));
            }
        }
        
        // TODO: Add DESTROY goals for enemy units within visible range        

        return fs;
    }

    private void addGoal(Goal goal) {
        if (!allGoals.contains(goal)) {
            goals.add(goal);
            allGoals.add(goal);
        }        
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
    
    private void onGoalCompleted(Goal goal) {
        // TODO: evaluate and see if the completion of this goal triggers a new one
        allGoals.remove(goal);
    }
    
    private void onGoalCancelled(Goal goal) {
        // for now, just add goal back to queue
        // TODO: evaluate and see if this goal is in fact still valid
        goals.add(goal);
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
        json.writeValue("factionName", factionName);
        json.writeValue("plans", plans);
        json.writeValue("goals", goals);        
    }

    @Override
    public void read(Json json, JsonValue jv) {
        factionName = json.readValue("factionName", String.class, jv);
        plans.addAll(Arrays.asList(json.readValue("plans", Plan[].class, jv)));
        goals.addAll(Arrays.asList(json.readValue("goals", Goal[].class, jv)));

        // fill all goals with goals from both plans and goal queue
        allGoals.clear();
        for (Plan p : plans) {
            allGoals.add(p.getGoal());
        }
        allGoals.addAll(goals);
    }
}