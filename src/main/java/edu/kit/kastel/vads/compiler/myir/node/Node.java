package edu.kit.kastel.vads.compiler.myir.node;

import edu.kit.kastel.vads.compiler.myir.node.visitor.Visitable;

import java.util.List;

public interface Node extends Visitable {

    List<? extends Node>  children();
}
