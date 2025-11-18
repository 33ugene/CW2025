package com.comp2042;

import com.comp2042.logic.bricks.Brick;

public class HoldManager {
    private Brick heldBrick;
    private boolean canHold;

    public HoldManager() {
        this.heldBrick = null;
        this.canHold = true;
    }

    // FIXED: Return type should be BrickResult, not Brick
    public BrickResult holdBrick(Brick currentBrick) {
        if (!canHold) {
            return new BrickResult(currentBrick, false); // can't hold the current brick
        }

        Brick brickToUse;
        boolean shouldGetNewBrick;

        if (heldBrick == null) {
            // holds first brick as the current brick
            heldBrick = currentBrick;
            brickToUse = null; // sends new brick
            shouldGetNewBrick = true;
        } else {
            // swap brick with held brick
            brickToUse = heldBrick;
            heldBrick = currentBrick;
            shouldGetNewBrick = false;
        }

        canHold = false; // can only hold once per piece
        return new BrickResult(brickToUse, shouldGetNewBrick);
    }

    public static class BrickResult {
        public final Brick brick;
        public final boolean shouldGetNewBrick;

        // FIXED: Removed the comma typo
        public BrickResult(Brick brick, boolean shouldGetNewBrick) {
            this.brick = brick;
            this.shouldGetNewBrick = shouldGetNewBrick; // FIXED: was "this,shouldGetNewBrick"
        }
    }

    public void resetHold() {
        canHold = true; // reset hold ability after placing block
    }

    public Brick getHeldBrick() {
        return heldBrick;
    }

    public boolean canHold() {
        return canHold;
    }

    public void reset() {
        heldBrick = null;
        canHold = true;
    }
}