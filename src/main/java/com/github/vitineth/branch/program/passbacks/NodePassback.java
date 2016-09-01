package com.github.vitineth.branch.program.passbacks;

import com.github.vitineth.branch.program.node.Node;
import com.github.vitineth.branch.program.window.primary.BranchComponent;
import com.github.vitineth.branch.program.window.DetailWindow;

/**
 * An interface to facilitate the passing of nodes between the {@link DetailWindow} and the {@link BranchComponent}.
 * <p/>
 * File created by Ryan (vitineth).<br>
 * Created on 31/08/2016.
 *
 * @author Ryan (vitineth)
 * @since 31/08/2016
 */
public interface NodePassback {

    /**
     * A callback to be used when the node is saved
     * @param oldNode {@link Node} The original node so it can be used in a lookup to determine an index.
     * @param newNode {@link Node} The newly edited node.
     */
    void detailsAltered(Node oldNode, Node newNode);
}
