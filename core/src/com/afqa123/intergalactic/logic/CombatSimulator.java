package com.afqa123.intergalactic.logic;

import com.afqa123.intergalactic.model.Unit;
import com.badlogic.gdx.Gdx;

public class CombatSimulator {

    public enum CombatResult { 
        VICTORY,
        DEFEAT,
        DRAW
    };

    private final float DAMAGE_VARIANCE = 0.25f;
    
    public CombatResult simulate(Unit attacker, Unit defender) {
        // TODO: include modifiers
        double attackDmg = attacker.getBaseAttack() * (1.0f - DAMAGE_VARIANCE * Math.random());
        double defenseDmg = defender.getBaseDefense() * (1.0f - DAMAGE_VARIANCE * Math.random());
        double attackHealth = attacker.getHealth() - defenseDmg;
        double defenseHealth = defender.getHealth() - attackDmg;

        Gdx.app.log(CombatSimulator.class.getName(), String.format("Attack: %f - %f = %f", defender.getHealth(), attackDmg, defenseHealth));
        Gdx.app.log(CombatSimulator.class.getName(), String.format("Defense: %f - %f = %f", attacker.getHealth(), defenseDmg, attackHealth));

        // both units are dead - unit with least damage stays alive, so adjust damage
        if (attackHealth <= 0.0f && defenseHealth <= 0.0f) {
            double d = attackHealth - defenseHealth;
            if (d >= 0.0f) {
                defenseDmg = attacker.getHealth() - 0.1f;
            } else {
                attackDmg = defender.getHealth() - 0.1f;
            }
        }

        attacker.applyDamage(defenseDmg);
        defender.applyDamage(attackDmg);
        
        CombatResult res;
        if (attackHealth <= 0.0f) {
            res = CombatResult.DEFEAT;
        } else if (defenseHealth <= 0.0f) {
            res = CombatResult.VICTORY;
        } else {
            res = CombatResult.DRAW;
        }
        
        Gdx.app.log(CombatSimulator.class.getName(), String.format("Combat result: %s", res.name()));
        
        return res;
    }    
}