package com.comp2042;

import javax.swing.text.View;

public interface InputEventListener {

    DownData onDownEvent(MoveEvent event);

    ViewData onLeftEvent(MoveEvent event);

    ViewData onRightEvent(MoveEvent event);

    ViewData onRotateEvent(MoveEvent event);

    ViewData onHardDropEvent(MoveEvent event);

    ViewData onHoldEvent(MoveEvent event);

    ViewData onPauseEvent(MoveEvent event);


    void createNewGame();
}
