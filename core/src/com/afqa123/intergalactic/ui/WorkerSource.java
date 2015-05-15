package com.afqa123.intergalactic.ui;

import com.afqa123.intergalactic.asset.Assets;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.Payload;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.Source;
import com.badlogic.gdx.utils.SnapshotArray;

public class WorkerSource extends Source {
    
    private final TextureRegion texture;
    
    public WorkerSource(Actor actor, TextureRegion texture) {
        super(actor);
        this.texture = texture;
    }
    
    @Override
    public Payload dragStart(InputEvent event, float x, float y, int pointer) {
        Actor actor = getActor();
        Group parent = actor.getParent();

        // Determine how many workers we are moving
        int count = 0;
        SnapshotArray<Actor> children = parent.getChildren();
        for (int i = 0; i < children.size; i++) {
            if (children.get(i) == actor) {
                count = children.size - i;
                break;
            }
        }
       
        Payload payload = new Payload();
        payload.setObject(count);        

        // TODO: make configurable and fix alignment if possible
        // create icon used for dragging
        Image dragActor = new Image(texture);
        dragActor.setWidth(25.0f);
        dragActor.setHeight(25.0f);
        payload.setDragActor(dragActor);
        payload.setInvalidDragActor(dragActor);
        payload.setValidDragActor(dragActor);

        return payload;
    }
}