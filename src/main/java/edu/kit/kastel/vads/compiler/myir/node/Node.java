package edu.kit.kastel.vads.compiler.myir.node;

import edu.kit.kastel.vads.compiler.myir.node.visitor.Visitable;
import edu.kit.kastel.vads.compiler.myir.node.visitor.Visitor;

import java.util.Collection;
import java.util.List;

public interface Node extends Visitable {

    List<? extends Node>  children();
}
