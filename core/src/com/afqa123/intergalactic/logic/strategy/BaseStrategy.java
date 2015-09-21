package com.afqa123.intergalactic.logic.strategy;

import com.afqa123.intergalactic.model.Faction;
import com.afqa123.intergalactic.model.Session;
import com.afqa123.intergalactic.util.PriorityList;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public abstract class BaseStrategy implements Strategy, Json.Serializable {
 
    protected String factionName;
    // Active plans
    protected final List<Plan> plans = new LinkedList<>();
    // Goals for which we have not yet created a plan
    protected final List<Goal> goals = new PriorityList<>(new Comparator<Goal>() {
        @Override
        public int compare(Goal o1, Goal o2) {
            return (o1.getType().getPriority() - o2.getType().getPriority());
        }        
    });
    // All currently active goals
    protected final Set<Goal> allGoals = new HashSet<>();
    
    @Override
    public void nextTurn(Session session) {        
        Gdx.app.debug(BaseStrategy.class.getName(), "Computing turn for: " + factionName);
        Faction faction = session.getFactions().get(factionName);
        updateGoals(session);
        
        // Create new plans for goals on queue 
        for (Goal goal : goals) {
            Gdx.app.debug(BaseStrategy.class.getName(), String.format("Adding plan for: %s", goal.toString()));
            plans.add(PlanFactory.newPlan(goal));
        }
        goals.clear();
        
        int updateCounter = 0;
        // evaluate plans
        Gdx.app.log(BaseStrategy.class.getName(), String.format("Active plans: %d", plans.size()));
        int i = 0;
        while (i < plans.size()) {
            Plan plan = plans.get(i);
            // Update plan as long as status is active
            Plan.Status status;
            while (Plan.Status.ACTIVE == (status = plan.update(session, faction))) {
                updateCounter++;
                if (updateCounter >= 1000) {
                    throw new RuntimeException("Excessive plan updates!");
                }
            }
            if (status == Plan.Status.COMPLETE) {
                onGoalCompleted(plan.getGoal());
                plans.remove(i);
            } else if (status == Plan.Status.INVALID) {
                // cancel plan and return goal to queue
                onGoalCancelled(plan.getGoal());
                plans.remove(i);
            } else {
                i++;
            }
        }
        Gdx.app.log(BaseStrategy.class.getName(), String.format("Performed %d plan updates.", updateCounter));
    }
    
    /**
     * Updates the goal queue.
     * 
     * @param session The game session.
     */
    protected abstract void updateGoals(Session session);
    
    protected void addGoal(Goal goal) {
        if (!allGoals.contains(goal)) {
            goals.add(goal);
            allGoals.add(goal);
        }        
    }
    
    protected void onGoalCompleted(Goal goal) {
        // TODO: evaluate and see if the completion of this goal triggers a new one
        allGoals.remove(goal);
    }
    
    protected void onGoalCancelled(Goal goal) {
        // for now, just add goal back to queue
        // TODO: evaluate and see if this goal is in fact still valid
        goals.add(goal);
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
