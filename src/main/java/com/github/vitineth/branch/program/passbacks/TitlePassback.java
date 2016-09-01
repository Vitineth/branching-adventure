package com.github.vitineth.branch.program.passbacks;

/**
 * An interface to facilitate the passing of a title between {@link com.github.vitineth.branch.program.window.primary.BranchAdventure}
 * and {@link com.github.vitineth.branch.program.window.primary.BranchComponent}
 * <p/>
 * File created by Ryan (vitineth).<br>
 * Created on 01/09/2016.
 *
 * @author Ryan (vitineth)
 * @since 01/09/2016
 */
public interface TitlePassback {

    void updateTitle(String title);

}
