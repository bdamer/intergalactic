package com.afqa123.intergalactic.ui;

import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.Payload;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.Source;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.Target;

/**
 * Target for worker in production group.
 */
public class WorkerTarget extends Target {

    private final ProductionGroup targetGroup;
    
    public WorkerTarget(ProductionGroup group) {
        super(group);
        this.targetGroup = group;
    }
    
    @Override
    public boolean drag(Source source, Payload pld, float x, float y, int pointer) {
        return (source instanceof WorkerSource);
    }

    @Override
    public void drop(Source source, Payload pld, float x, float y, int pointer) {
        if (!(source instanceof WorkerSource)) {
            return;
        }
        // Perform worker transfer
        int count = (Integer)pld.getObject();
        ProductionGroup srcGroup = (ProductionGroup)source.getActor().getParent();
        srcGroup.take(count);
        targetGroup.receive(count);
    }   
}