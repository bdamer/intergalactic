package com.afqa123.intergalactic.ui;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop;
import com.badlogic.gdx.utils.SnapshotArray;

/**
 * UI control to display a list of workers assigned to a specific type of 
 * production. 
 */
public class ProductionGroup extends HorizontalGroup implements ProductionTransferListener {

    private static final float SCALE = 1.2f;
    // count of workers assigned to this production group
    private int count;
    private final TextureRegion texture;
    private final DragAndDrop dnd;
    private ChangeListener<Integer> listener;
    
    /**
     * Creates a new {@code ProductionGroup} with an initial worker count.
     * 
     * @param dnd Drag and drop instance.
     * @param texture The texture to be used to display workers.
     * @param count The number of workers in this group.
     */
    public ProductionGroup(DragAndDrop dnd, TextureRegion texture, int count) {
        this.setSize(texture.getRegionWidth() * 10, texture.getRegionHeight());
        this.dnd = dnd;
        this.count = count;
        this.texture = texture;
        updateWorkers();
        dnd.addTarget(new WorkerTarget(this));
    }

    private void updateWorkers() {
        SnapshotArray<Actor> children = getChildren();
        int delta = count - children.size;
        if (delta < 0) {
            // TODO: do we need to remove dnd sources?
            children.removeRange(count, children.size - 1);
        } else if (delta > 0) {
            for (int i = children.size; i < count; i++) {
                Image img = new Image(texture);
                img.setScale(SCALE);
                img.setPosition(i * img.getWidth(), 0);
                addActor(img);
                dnd.addSource(new WorkerSource(img, texture));
            }
        }        
    }
    
    @Override
    public void take(int amount) {
        count -= amount;
        updateWorkers();
        if (listener != null) {
            listener.valueChanged(count);
        }
    }

    @Override
    public void receive(int amount) {
        count += amount;
        updateWorkers();
        if (listener != null) {
            listener.valueChanged(count);
        }
    }
    
    public void setChangeListener(ChangeListener<Integer> listener) {
        this.listener = listener;
    }
}