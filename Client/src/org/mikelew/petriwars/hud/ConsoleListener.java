package org.mikelew.petriwars.hud;


/**
 * This Listener listens to events from the {@link CheatConsole}.
 * 
 * @author Marvin Froehlich (aka Qudus) (edited by Tim Pittman)
 */
public interface ConsoleListener
{
    /**
     * This event is fired when command has been entered in the {@link CheatConsole}.
     * 
     * @param console
     * @param commandLine
     */
    public void onCommandEntered( CheatConsole console, String commandLine );
}
